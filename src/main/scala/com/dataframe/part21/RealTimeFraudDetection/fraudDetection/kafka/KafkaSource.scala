package com.dataframe.part21.RealTimeFraudDetection.fraudDetection.kafka

import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.creditcard.Schema
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.creditcard.{Schema, TransactionKafka}
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
/**
  * Created by kalit_000 on 6/6/19.
  */
object KafkaSource {

  val logger = Logger.getLogger(getClass.getName)

  def readStream(startingOption :String = "startingOffsets",partitionAndOffsets: String = "earliest")(implicit sparkSession:SparkSession) = {

    logger.info("Reading from Kafka")
    import sparkSession.implicits._
    sparkSession
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers",KafkaConfig.kafkaParams("bootstrap.servers"))
      .option("subscribe", KafkaConfig.kafkaParams("topic"))
      .option("enable.auto.commit",KafkaConfig.kafkaParams("enable.auto.commit").toBoolean) // Cannot be set to true in Spark Strucutured Streaming https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html#kafka-specific-configurations
      .option("group.id", KafkaConfig.kafkaParams("group.id"))
    //.option(startingOption, partitionsAndOffsets) //this only applies when a new query is started and that resuming will always pick up from where the query left off
      .load()
      .withColumn(Schema.kafkaTransactionStructureName,from_json($"value".cast(StringType),Schema.kafkaTransactionSchema))
      .as[TransactionKafka]

  }



}
