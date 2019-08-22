package com.dataframe.part22.strcuturedStreamingExamples

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.streaming.Trigger

object TriggerOnceExample {

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("TriggerOncetest")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark = SparkSession.builder
      .master("local[*]")
      .appName("TriggerOncetest")
      //.enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    /*
    val schemaUntyped = new StructType()
      .add("lsoa_code", "string")
      .add("borough", "string")
      .add("major_category", "string")
      .add("minor_category", "string")
      .add("value", "string")
      .add("year", "string")
      .add("month", "string")
*/

    val schmeaOp = spark.read.load("TitanicParquetData/op")

    schmeaOp.printSchema()

    schmeaOp.show(10,false)

    val fileStreamDF = spark.readStream.format("parquet").option("maxFilesPerTrigger",1)
      .schema(schmeaOp.schema).parquet("TitanicParquetData/op")

    println(fileStreamDF.isStreaming)

    println(fileStreamDF.printSchema())


    val query = fileStreamDF.writeStream.trigger(Trigger.Once).format("parquet")
      .option("checkpointLocation", "StreamCheckPoint2")
      .start("StructuresStreamingOP/data/")


    query.stop



  }

}
