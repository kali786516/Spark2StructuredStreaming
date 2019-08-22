package com.dataframe.part21.RealTimeFraudDetection.fraudDetection.cassandra

/**
  * Created by kalit_000 on 5/31/19.
  */

import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.config.Config
import org.apache.log4j.Logger

object CassandraConfig {

  val logger                          = Logger.getLogger(getClass.getName)

  var keyspace:String                 = _
  var fraudTransactionTable:String    = _
  var nonFraudTransactionTable:String = _
  var kafkaOffsetTable:String         = _
  var customer:String                 = _
  var cassandrHost:String             = _

  /*Configuration setting are loaded from application.conf when you run Spark Standalone cluster*/
  def load() = {
    logger.info("Loading Cassandra Settings")
    keyspace                 = Config.applicationConf.getString("config.cassandra.keyspace")
    fraudTransactionTable    = Config.applicationConf.getString("config.cassandra.table.fraud.transaction")
    nonFraudTransactionTable = Config.applicationConf.getString("config.cassandra.table.non.fraud.transaction")
    kafkaOffsetTable         = Config.applicationConf.getString("config.cassandra.table.kafka.offset")
    customer                 = Config.applicationConf.getString("config.cassandra.table.customer")
    cassandrHost             = Config.applicationConf.getString("config.cassandra.host")
  }

  /* Default Settings will be used when you run the project from Intellij */
  def defaultSettng() = {
    keyspace                 = "creditcard"
    fraudTransactionTable    = "fraud_transaction"
    nonFraudTransactionTable = "non_fraud_transaction"
    kafkaOffsetTable         = "kafka_offset"
    customer                 = "customer"
    cassandrHost             = "localhost"
  }




}
