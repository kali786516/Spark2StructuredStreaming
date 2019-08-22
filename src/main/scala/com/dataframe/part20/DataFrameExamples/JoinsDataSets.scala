package com.dataframe.part20.DataFrameExamples

/**
  * Created by kalit_000 on 5/15/19.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

case class Person2(id:Int,first:String,last:String)
case class Role(id:Int,role:String)

object JoinsDataSets {
  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._

    val personDS=List(Person2(1,"justin","Pihony"),Person2(2,"John","Doe"),Person2(3,"Jane","Smith")).toDS()

    val roleDs=List(Role(1,"Manager"),Role(3,"CEO"),Role(4,"huh")).toDS()

    val innerJoinDS = personDS.joinWith(roleDs,personDS("id") === roleDs("id"))

    innerJoinDS.show(false)



  }
}
