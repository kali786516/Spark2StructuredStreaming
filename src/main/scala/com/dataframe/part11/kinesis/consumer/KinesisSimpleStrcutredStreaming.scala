package com.dataframe.part11.kinesis.consumer

/**
  * Created by kalit_000 on 6/13/19.
  */

import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession

object KinesisSimpleStrcutredStreaming {

  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger(getClass.getName)

    val conf = new SparkConf()
      .setAppName("Sample").setMaster("local[*]")
    val spark = SparkSession.builder.config(conf).getOrCreate()
    import spark.implicits._

    val Schema = new StructType()
      .add("column1", StringType)

    /*Not Working ......*/


    logger.info("Reading from Kinesis")
    val source = spark
      .readStream
      .format("kinesis")
      .option("streamName", "tweets-stream")
      .option("endpointUrl", "https://kinesis.us-east-1.amazonaws.com")
      .option("startingPosition", "TRIM_HORIZON")
      //.schema(Schema)
      .load
      //.option("awsAccessKeyId", [YOUR_AWS_ACCESS_KEY_ID])
      //.option("awsSecretKey", [YOUR_AWS_SECRET_KEY])
      //.option(startingOption, partitionsAndOffsets) //this only applies when a new query is started and that resuming will always pick up from where the query left off

    println(source.isStreaming)

    println(source.printSchema())

    source.selectExpr("cast (data as STRING) data").show(false)

    val df = source.selectExpr("cast (data as STRING) data")

    val trimmedDF = df.select($"data")

    trimmedDF.createOrReplaceTempView("test")

    spark.sql("select split(data,',')[0] as column1,split(data,',')[1] as column2,split(data,',')[2] as column3 from test").show(false)

    val df2 = spark.sql("select split(data,',')[0] as column1,split(data,',')[1] as column2,split(data,',')[2] as column3 from test")


    val query = df2.writeStream.outputMode("complete")
      .format("console")
      .option("truncate","false")
      .option("numRows",30)
      .start()

    query.awaitTermination()

    spark.streams.awaitAnyTermination()

    //val query = wordsCount.writeStream.format("console").outputMode("complete").start

    //val query = wordsCount.writeStream.format("memory").queryName("simpleCount").outputMode("complete").start

    //spark.sql("select * from simpleCount").show()



    /*

    source
      .selectExpr("CAST(rand() AS STRING) as partitionKey",
        "CAST(data AS STRING)")
      .as[(String,String)]
      .writeStream
      .format("kinesis")
      .outputMode("update")
      .option("streamName", "tweets-stream")
      .option("endpointUrl", "https://kinesis.us-east-1.amazonaws.com")
      //.option("awsAccessKeyId", [YOUR_AWS_ACCESS_KEY])
      //.option("awsSecretKey", [YOUR_AWS_SECRET_KEY])
      .start()
      .awaitTermination()*/




  }

}
