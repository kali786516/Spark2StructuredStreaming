package com.dataframe.part2.kafkaJustinPihony.consumer

/**
  * Created by kalit_000 on 5/15/19.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.expressions.scalalang.typed
import org.apache.spark.sql.functions._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._

case class Account3(number:String,firstName:String,lastName:String)
case class Transaction3(id:Long,account:Account3,date:java.sql.Date,amount:Double,description:String)
case class TransactionForAverage3(accountNumber:String,amount:Double,description:String,date:java.sql.Date)


object CassandraKafkaSimpleTest {
  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark = SparkSession.builder
      .master("local[*]")
      .appName("Fraud Detector")
      .config("spark.driver.memory","2g")
      .config("spark.cassandra.connection.host","localhost")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    val financiesDS = spark.read.json("Data/finances-small.json")
      .withColumn("date",to_date(unix_timestamp($"Date","MM/dd/yyyy")
        .cast("timestamp")
      )).as[Transaction3]

    val accountNumberPrevioud4Windowsspec=Window.partitionBy($"AccountNumber")
      .orderBy($"Date").rowsBetween(-4,0)

    val rollimngavgForPrevious4PerAccount = avg($"Amount").over(accountNumberPrevioud4Windowsspec)

    financiesDS
      .na.drop("all",Seq("ID","Account","Amount","Description","Date"))
      .na.fill("Unknown",Seq("Description")).as[Transaction3]
      .filter(tx => (tx.amount != 0 || tx.description == "Unknown"))
      .select($"Account.Number".as("AccountNumber").as[String],$"Amount".as[Double],
        $"Date".as[java.sql.Date](Encoders.DATE),$"Description".as[String])
      .withColumn("RollingAverage",rollimngavgForPrevious4PerAccount)
      .write.mode(SaveMode.Overwrite).parquet("Output/finances-small")


    financiesDS
      .select($"account.number".as("accountNumber").as[String],$"amount".as[Double],
        $"description".as[String],
        $"date".as[java.sql.Date](Encoders.DATE)).as[TransactionForAverage3]
      .groupByKey(_.accountNumber)
      .agg(
        typed.avg[TransactionForAverage3](_.amount).as("AverageTransaction").as[Double],
        typed.sum[TransactionForAverage3](_.amount),
        typed.count[TransactionForAverage3](_.amount),
        max($"Amount").as("MaxTransaction").as[Double]
      ).coalesce(5).show(false)
      //.write.mode(SaveMode.Overwrite)
      //.format("org.apache.spark.sql.cassandra")
      //.options(Map("Keyspace" -> "finances","table" -> "account_aggregates"))
      //.cassandraFormat("account_aggregates","finances")
      //.save
    //.json("Output/finances-small-account-details")

    val streamingContext = new StreamingContext(spark.sparkContext,Seconds(1))
    val kafkaStream = KafkaUtils.createStream(streamingContext,"localhost:2181","test-group",Map("spark_avro_topic" -> 1))

    kafkaStream.print()
    streamingContext.start()
    streamingContext.awaitTermination()

  }

  implicit class DataFrameHelper[T](ds:Dataset[T]) {
    import scala.util.Try // org.apache.spark.sql.AnalysisException
    def hasColumn(columnName:String) = Try(ds(columnName)).isSuccess
  }

}
