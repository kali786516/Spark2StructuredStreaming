package com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.utils

import org.apache.log4j.Logger
import org.apache.spark.SparkConf

/**
  * Created by kalit_000 on 6/9/19.
  */
object SparkConfig {

  val logger                        = Logger.getLogger(getClass.getName)
  val sparkConf                     = new SparkConf

  def load() = {
    logger.info("Loading Spark Setttings")
    sparkConf.set("spark.streaming.stopGracefullyOnShutdown","true")
      .set("spark.debug.maxToStringFields","200000")
  }

  def defaultSetting():SparkConf = {
    sparkConf
      .set("spark.master", "local[*]")
      .set("spark.streaming.stopGracefullyOnShutdown","true")
      .set("spark.debug.maxToStringFields","200000")
    sparkConf
  }


}
