package com.dataframe.part12.kafka.producer

object TransactionKafkaEnum extends Enumeration {

  val cc_num = "cc_num"
  val first_column = "first_column"
  val last_column = "last_column"
  val trans_num = "trans_num"
  val trans_date = "trans_date"
  val trans_time = "trans_time"
  val unix_time = "unix_time"
  val category_column = "category_column"
  val merchant_column = "merchant_column"
  val amt_column = "amt_column"
  val merch_lat = "merch_lat"
  val merch_long = "merch_long"
  val distance_column = "distance_column"
  val age_column = "age_column"
  val is_fraud = "is_fraud"

}
