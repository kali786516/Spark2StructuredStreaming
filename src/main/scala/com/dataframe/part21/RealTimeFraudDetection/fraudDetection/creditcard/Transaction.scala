package com.dataframe.part21.RealTimeFraudDetection.fraudDetection.creditcard

/**
  * Created by kalit_000 on 6/6/19.
  */

import java.sql.Timestamp


case class Transaction(cc_num:String,
                       first:String,
                       last:String,
                       trans_num:String,
                       trans_time: Timestamp,
                       //unix_time:String,
                       category:String,
                       merchant:String,
                       amt:String,
                       merch_lat: String,
                       merch_long:String)

case class DstreamTransaction(cc_num:String,
                              first:String,
                              last:String,
                              trans_num:String,
                              trans_time: Timestamp,
                              category:String,
                              merchant:String,
                              amt:Double,
                              merch_lat: Double,
                              merch_long:Double)


/* Spark Dataset case class for mapping messages received from Kafka in Structured Streaming*/
case class TransactionKafka(topic: String, partition: Int, offset: Long, timestamp: Timestamp, timestampType:Int, transaction:Transaction)
