package com.dataframe.part18.FixMessageAnalytics.kafka.consumer.dstream

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Item}
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.kafka.KafkaConfig
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.spark.jobs.realTimeFraudDetection.DstreamFraudDetection.getClass
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.{Assign, Subscribe}
import org.apache.spark.sql.{Row, SQLContext}

import scala.collection.mutable.Map
import org.apache.spark.sql.types._
import org.apache.spark.{SparkConf, SparkContext}
//import com.audienceproject.spark.dynamodb.implicits._
import com.amazonaws.services.dynamodbv2.document.{DynamoDB,Table,Item}

object FixMessageKafkaConsumer {

  def parseFixEvent(eventString:String):Row = {
    var fixElements = scala.collection.mutable.Map[String,String]()
    val pattern     = """(\d*)=(.*?)(?:[\001])""".r
    pattern.findAllIn(eventString).matchData.foreach{
      m => fixElements. += (m.group(1) -> m.group(2))
    }
    try {
      Row(
        fixElements("55") /* stocksymbol = 55: symbol */,
        fixElements("60").toLong /* transacttime = 60: transacttime */,
        fixElements("11") /* clordid = 11: clordid */,
        fixElements("35") /* msgtype = 35: msgtype */,
        if (fixElements("35").equals("D")) fixElements("38").toInt else null /* orderqty = 38: orderqty */,
        if (fixElements.contains("151")) fixElements("151").toInt else null /* leavesqty = 151: leavesqty */,
        if (fixElements.contains("14")) fixElements("14").toInt else null /* cumqty = 14: cumqty */,
        if (fixElements.contains("6")) fixElements("6").toDouble else null /* avgpx = 6: avgpx */,
        System.currentTimeMillis()
      )
    } catch {
      case e:Exception => e.printStackTrace()
       null
    }
  }

  val schema = StructType(
      StructField("stocksymbol", StringType, false) ::
      StructField("transacttime", LongType, false) ::
      StructField("clordid", StringType, false) ::
      StructField("msgtype", StringType, false) ::
      StructField("orderqty", IntegerType, true) ::
      StructField("leavesqty", IntegerType, true) ::
      StructField("cumqty", IntegerType, true) ::
      StructField("avgpx", DoubleType, true) ::
      StructField("lastupdated", LongType, false) :: Nil)

   def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger(getClass.getName)

     val dynamodDBClinet      = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build
     val dynamoDBCon          = new DynamoDB(dynamodDBClinet)
     //val dynamoDBTable:String = dynamoDBCon.getTable("fixdata").getTableName
     val dynamoDBTable   = dynamoDBCon.getTable("fixdata")

    val ssc = new StreamingContext("local[*]", "KafkaFixConsumer", Seconds(1))
    val conf = new SparkConf().setAppName("Sample")
    val spark = SparkSession.builder.config(conf).getOrCreate()
    import spark.implicits._

    val kafkaParams        =  Map[String, String](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG         -> "localhost:9092",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG    -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG  -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG         -> "earliest",
      ConsumerConfig.GROUP_ID_CONFIG                  ->  "KafkaFixConsumer"
    )

    val topics             = Set("fixmessagetopic")

    val stream = KafkaUtils.createDirectStream[String, String](ssc,PreferConsistent, Subscribe[String, String](topics, kafkaParams))
    val broadcastSchema = spark.sparkContext.broadcast(schema)

    //stream.map(record=>(record.value().toString)).print

    val parsedMessages =  stream.map(cr => parseFixEvent(cr.value()))

     parsedMessages.foreachRDD( rdd => {
       if (!rdd.isEmpty()) {

         val dynamoDBDF = spark.createDataFrame(rdd, schema)
         dynamoDBDF.show(false)
         /*lets write to DynamoDB*/
         dynamoDBDF.registerTempTable("dyntable")

         //spark.sql("select distinct stocksymbol,transacttime,clordid,msgtype,orderqty,leavesqty,cumqty,avgpx,lastupdated from dyntable").write.dynamodb(dynamoDBTable)
         val dyJson     = spark.sql("select distinct stocksymbol,transacttime,clordid,msgtype,orderqty,leavesqty,cumqty,avgpx,lastupdated from dyntable").toJSON.collect()
          for (element <- dyJson) {
           val item = Item.fromJSON(element)
           dynamoDBTable.putItem(item)
            println("Data Inserted")
         }

       }
    })

    ssc.start()
    ssc.awaitTermination()


  }

}
