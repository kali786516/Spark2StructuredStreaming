package com.dataframe.part20.DataFrameExamples

/**
  * Created by kalit_000 on 5/14/19.
  */
import java.lang.management.ManagementFactory

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.StructType
import org.apache.spark.{SparkConf, SparkContext}

object Test {

  def main(args: Array[String]): Unit = {

    val isIDE = {
      ManagementFactory.getRuntimeMXBean.getInputArguments.toString.contains("IntelliJ IDEA")
    }
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    logger.info("[INFO] :- s3_to_hdfs Data Pipeline Arguments")

    val conf = new SparkConf().setAppName("Test").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val schemaUntyped = new StructType()
      .add("pk_id", "int")
      .add("work_flow", "string")
      .add("comp_name", "string")
      .add("comp_param", "string")
      .add("comp_value", "string")
      .add("user_name", "string")
      .add("created_date", "string")

    val metaDataDf = sqlContext
      .read
      .format("com.databricks.spark.csv")
      .option("header", "false")
      .option("codec", "org.apache.hadoop.io.compress.GzipCodec")
      .option("escape", ":")
      .option("parserLib", "univocity")
      .option("delimiter", "~")
      .option("inferSchema", "false")
      .schema(schemaUntyped)
      .load("/Users/kalit_000/Downloads/Spark2StructuredStreaming/Data/lg_mcm_adb_cntnt_metadata.txt")

    metaDataDf.printSchema()

    metaDataDf.registerTempTable("Test")

    sqlContext.sql("select * from test").show(10,false)

  }

}
