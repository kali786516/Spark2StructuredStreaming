package com.dataframe.strcuturedStreamingExamples.KafkaStrcutredStreaming

import com.dataframe.RealTimeFraudDetection.fraudDetection.creditcard.Schema
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.from_json
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql._

object ParseKafkaJsonMessage {

  def praseJsonMessage(df:DataFrame,colName:String = "value",spark:SparkSession):DataFrame ={
    import spark.implicits._
    val data: DataFrame = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
    data.select(from_json(data.col(colName), Schema.kafkaTransactionSchema).as(colName))
  }

  def dfShow(df:DataFrame)={
    df.show(10,false)
  }

  def splitDataFrame(df:DataFrame,spark:SparkSession) ={
    df.createOrReplaceTempView("test1")
    spark.sql("select value.* from test1").createOrReplaceTempView("test2")
    spark.sql("select cc_num,first,last,trans_num from test2")
    //spark.sql("select cc_num,first,value.last,value.trans_num from test1")

    /*spark.sql("select split(value,',')[0] as cc_num,split(value,',')[1] as first,split(value,',')[3] as last,split(value,',')[4] as trans_num " +
      ",split(value,',')[5] as trans_num ,split(value,',')[6] as trans_time,split(value,',')[7] as category," +
      "split(value,',')[8] as merchant,split(value,',')[9] as amt,split(value,',')[10] as merch_lat,split(value,',')[11] as merch_long  from test1")*/

  }


}
