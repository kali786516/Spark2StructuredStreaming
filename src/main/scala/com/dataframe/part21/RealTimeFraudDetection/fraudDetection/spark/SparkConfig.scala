package com.dataframe.part21.RealTimeFraudDetection.fraudDetection.spark

/**
  * Created by kalit_000 on 6/1/19.
  */

import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.config.Config
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.cassandra.CassandraConfig
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.config.Config

object SparkConfig {

  val logger                        = Logger.getLogger(getClass.getName)
  val sparkConf                     = new SparkConf
  var transactionDatasouce:String   = _
  var customerDatasource:String     = _
  var modelPath:String              = _
  var preprocessingModelPath:String = _
  var shutdownMarker:String         = _
  var batchInterval:Int             = _

  def load() = {
    logger.info("Loading Spark Setttings")
    sparkConf.set("spark.streaming.stopGracefullyOnShutdown", Config.applicationConf.getString("config.spark.gracefulShutdown"))
      .set("spark.sql.streaming.checkpointLocation", Config.applicationConf.getString("config.spark.checkpoint"))
      .set("spark.cassandra.connection.host", Config.applicationConf.getString("config.cassandra.host"))
      .set("spark.debug.maxToStringFields","200000")
    shutdownMarker         = Config.applicationConf.getString("config.spark.shutdownPath")
    batchInterval          = Config.applicationConf.getString("config.spark.batch.interval").toInt
    transactionDatasouce   = Config.localProjectDir + Config.applicationConf.getString("config.spark.transaction.datasource")
    customerDatasource     = Config.localProjectDir + Config.applicationConf.getString("config.spark.customer.datasource")
    modelPath              = Config.localProjectDir + Config.applicationConf.getString("config.spark.model.path")
    preprocessingModelPath = Config.localProjectDir + Config.applicationConf.getString("config.spark.model.preprocessing.path")
  }

  def defaultSetting() = {
    sparkConf.setMaster("local[*]")
      .set("spark.cassandra.connection.host", CassandraConfig.cassandrHost)
      .set("spark.sql.streaming.checkpointLocation", "/tmp/checkpoint")
      .set("spark.debug.maxToStringFields","10000")
    shutdownMarker         = "/tmp/shutdownmarker"
    transactionDatasouce   = "/Users/kalit_000/Downloads/Spark2StructuredStreaming/src/main/resources/data/transactions.csv"
    customerDatasource     = "/Users/kalit_000/Downloads/Spark2StructuredStreaming/src/main/resources/data/customer.csv"
    modelPath              = "/Users/kalit_000/Downloads/Spark2StructuredStreaming/src/main/resources/spark/training/RandomForestModel"
    preprocessingModelPath = "/Users/kalit_000/Downloads/Spark2StructuredStreaming/src/main/resources/spark/training/PreprocessingModel"
    batchInterval          = 5000
  }



}
