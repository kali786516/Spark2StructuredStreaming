package com.dataframe.part22.strcuturedStreamingExamples.KafkaStrcutredStreaming

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger

object SparkKafkaConsumer {
  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark = SparkSession.builder
      .master("local[3]")
      .appName("CSVFileStream")
      .config("spark.driver.memory","2g")
      .config("spark.cassandra.connection.host","localhost")
      .config("partition.assignment.strategy","range")
      //.enableHiveSupport()
      .getOrCreate()

    val kafkaStream = spark.readStream.format("kafka").option("kafka.bootstrap.servers","localhost:9092")
                           .option("subscribe","creditcardTransaction")
                           .option("startingOffsets","latest")
                           .option("partition.assignment.strategy","range")
                           .load

    val kafkaDF = kafkaStream.selectExpr("CAST(value as STRING) as ")

    //kafkaDF.show(false)

    val df = ParseKafkaJsonMessage.praseJsonMessage(kafkaStream,"value",spark)

    val df1 = ParseKafkaJsonMessage.splitDataFrame(df,spark)

    //ParseKafkaJsonMessage.dfShow(df)

    val query = df1.writeStream.outputMode("append")
                       .format("console")
                       .option("truncate","false")
                       .trigger(Trigger.ProcessingTime("5 seconds"))
                       .start()
                       .awaitTermination()



  }

}
