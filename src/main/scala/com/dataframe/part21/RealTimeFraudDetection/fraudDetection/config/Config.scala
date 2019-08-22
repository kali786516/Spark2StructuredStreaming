package com.dataframe.part21.RealTimeFraudDetection.fraudDetection.config

/**
  * Created by kalit_000 on 5/31/19.
  */

import java.io.File

import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.cassandra.CassandraConfig
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.kafka.KafkaConfig
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.spark.SparkConfig
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.cassandra.CassandraConfig
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.kafka.KafkaConfig
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.spark.SparkConfig
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.log4j.Logger

object Config {
  val logger                        = Logger.getLogger(getClass.getName)

  var applicationConf: Config       = _

  var runMode                       = "local"
  var localProjectDir               = ""

  def parseArgs(args:Array[String]) = {

    if(args.size == 0 ) {
       defaultSetting()
    } else {
      applicationConf   = ConfigFactory.parseFile(new File(args(0)))
      val runMode       = applicationConf.getString("config.mode")
      if (runMode == "local" ) {
        localProjectDir = s"file:///${System.getProperty("user.home")}/"
      }
      loadConfig()
    }
  }

  def loadConfig() = {
    CassandraConfig.load()
    KafkaConfig.load()
    SparkConfig.load()
  }


  def defaultSetting() = {
    CassandraConfig.defaultSettng()
    KafkaConfig.defaultSetting()
    SparkConfig.defaultSetting()

  }

}
