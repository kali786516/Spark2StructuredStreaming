package com.dataframe.part36.arrayAndJsonDataExplode


import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.sql.functions.{explode, expr, posexplode, when,explode_outer,posexplode_outer}
import org.apache.spark.sql.types.DataTypes

object MapDataFrame {

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("HbIngestion")
    //Logger.getLogger("org").setLevel(Level.WARN)
    //Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._
    val arrayType = DataTypes.createArrayType(BooleanType)
    val mapType = DataTypes.createMapType(StringType, LongType)







  }

}
