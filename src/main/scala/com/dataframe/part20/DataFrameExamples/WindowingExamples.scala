package com.dataframe.part20.DataFrameExamples

/**
  * Created by kalit_000 on 5/14/19.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

case class Status(id:Int,customer:String,status:String)

object WindowingExamples {

  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._

    val statuses = spark.createDataFrame(List(
      Status(1,"Justin","New"),
      Status(2,"Justin","Open"),
      Status(3,"Laura","New"),
      Status(4,"Unknown","New"),
      Status(5,"Justin","Open"),
      Status(6,"Justin","Pending"),
      Status(7,"Laura","Pending"),
      Status(8,"Laura","Resolved"),
      Status(9,"Unknown","Invalid"),
      Status(10,"Justin","Resolved")
    ))

    statuses.show(10,false)

    val windowedId=Window.partitionBy($"customer").orderBy($"id")

    //statuses.select($"id",$"customer",$"status",lag($"status",1).over(Window.orderBy($"id").partitionBy($"customer")).as("prevStatus")).show(false)

    statuses.select($"id",$"customer",$"status",lag($"status",1).over(windowedId).as("prevStatus")).show(false)

    statuses.select($"id",$"customer",$"status",lag($"status",1).over(windowedId).as("prevStatus"),avg($"id").over(windowedId).as("test")).show(false)

    statuses.createOrReplaceTempView("test")

    spark.sql("select id,customer,status,avg(id) OVER (PARTITION BY customer order by id) as avg_status from test group by id,customer,status").show(false)

    spark.sql("select id,customer,status," +
      "avg(id) OVER (PARTITION BY customer order by id) as avg_status," +
      "row_number() over (PARTITION BY customer order by id) as row_number_test," +
      "lead(status) over (PARTITION BY customer order by id) as lead_Status," + //Next value
      "lag(status,1) over (PARTITION BY customer order by id) as lag_Status," + //Previous value
      "first(status) over (PARTITION BY customer order by id) as first_status " +
      "from test group by id,customer,status").show(false)

    //different windows function

    val financesDF=spark.read.parquet("Output/finances-small")

    val financesDFWindowed=financesDF.select($"*",window($"Date","30 days","30 days","15 minutes").as("windowed"))

    financesDF.select($"*",window($"Date","30 days","30 days","15 minutes").as("windowed")).show(false)

    financesDFWindowed.groupBy($"windowed",$"AccountNumber").count().show(false)


  }

}
