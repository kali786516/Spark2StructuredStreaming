package com.dataframe.part11.kinesis.consumer

/**
  * Created by kalit_000 on 6/13/19.
  */

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.kinesis._
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream
import org.apache.spark.sql.SparkSession

object KinesisDStream {

  def main(args: Array[String]): Unit = {

    val ssc = new StreamingContext("local[*]", "KinesisExample", Seconds(1))

    val kinesisStream = KinesisUtils.createStream(
      ssc, "test123", "tweets-stream", "kinesis.us-east-1.amazonaws.com",
      "us-east-1", InitialPositionInStream.LATEST, Duration(2000), StorageLevel.MEMORY_AND_DISK_2)

    /*
     Kinesis Example to Send Data
     aws kinesis list-streams

     aws kinesis put-record --stream-name tweets-stream --partition-key 1 --data 30,4050,51,60

     aws kinesis describe-stream --stream-name tweets-stream
    */

    val lines = kinesisStream.map(x => new String(x))

    val conf = new SparkConf()
      .setAppName("Sample")
    val spark = SparkSession.builder.config(conf).getOrCreate()
    import spark.implicits._

    lines.foreachRDD{
      rdd => rdd.toDF("data").createOrReplaceTempView("test1")
        spark.sql("select split(data,',')[0] as column1,split(data,',')[1] as column2,split(data,',')[2] as column3 from test1").show(false)
    }

    // Kick it off
    //ssc.checkpoint("kinesischeckpoint/")
    ssc.start()
    ssc.awaitTermination()


  }

}
