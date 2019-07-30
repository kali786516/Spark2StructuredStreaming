package com.dataframe.part12.kafka.consumer

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Item}
import com.dataframe.RealTimeFraudDetection.fraudDetection.creditcard.Schema
import com.dataframe.part18.FixMessageAnalytics.kafka.consumer.dstream.FixMessageKafkaConsumer.{getClass, parseFixEvent, schema}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{from_json, lit}
import org.apache.spark.sql.types.{DoubleType, TimestampType}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

import scala.collection.mutable.Map

object SimplaKafkaConsumer {

  def main(args: Array[String]): Unit = {


    val logger = Logger.getLogger(getClass.getName)

    //val dynamodDBClinet      = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build
    //val dynamoDBCon          = new DynamoDB(dynamodDBClinet)
    //val dynamoDBTable:String = dynamoDBCon.getTable("fixdata").getTableName
    //val dynamoDBTable   = dynamoDBCon.getTable("fixdata")

    val ssc = new StreamingContext("local[*]", "KafkaFixConsumer", Seconds(1))
    val conf = new SparkConf().setAppName("Sample")
    val spark = SparkSession.builder.config(conf).getOrCreate()
    import spark.implicits._

    val kafkaParams = Map[String, String](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
      ConsumerConfig.GROUP_ID_CONFIG -> "KafkaFixConsumer"
    )

    val topics = Set("creditcardTransaction")

    val stream = KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent, Subscribe[String, String](topics, kafkaParams))

    val broadcastSchema = spark.sparkContext.broadcast(schema)

    //stream.map(record=>(record.value().toString)).print

    val transactionStream = stream.map(cr => (cr.value(), cr.partition(), cr.offset()))

    transactionStream.foreachRDD(rdd => {
      if (!rdd.isEmpty()) {

        val kafkaTransactionDF = rdd.toDF("transaction", "partition", "offset")
          .withColumn(Schema.kafkaTransactionStructureName, // nested structure with our json
            from_json($"transaction", Schema.kafkaTransactionSchema)) //From binary to JSON object
          .select("transaction.*", "partition", "offset")
          .withColumn("amt", lit($"amt") cast (DoubleType))
          .withColumn("merch_lat", lit($"merch_lat") cast (DoubleType))
          .withColumn("merch_long", lit($"merch_long") cast (DoubleType))
          .withColumn("trans_time", lit($"trans_time") cast (TimestampType))


        kafkaTransactionDF.registerTempTable("test")

        spark.sql("select distinct cc_num,first,last,trans_num,trans_time,category,merchant,amt,merch_lat,merch_long from test where " +
          "cc_num != 'cc_num' and cc_num = '5132731018032805' ").show(false)

        kafkaTransactionDF.printSchema()

        spark.sql("select distinct cc_num,first,last,trans_num,trans_time,category,merchant,amt,merch_lat,merch_long from test where " +
          "cc_num != 'cc_num' and cc_num = '5132731018032805' ").write.mode(SaveMode.Append)
          .csv("/Users/kalit_000/Downloads/gitCode/Spark2StructuredStreaming/src/main/resources/kafkaOutput/")

        //kafkaTransactionDF.write.csv("/Users/kalit_000/Downloads/gitCode/Spark2StructuredStreaming/src/main/resources/kafkaOutput/")

      }
    })

    ssc.start()
    ssc.awaitTermination()
  }


}
