package com.dataframe.part20.DataFrameExamples

/**
  * Created by kalit_000 on 5/14/19.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

case class Person(firstName:String,lastName:String,age:Int,weightInLbs:Option[Double],jobType:Option[String])

object FunctionsTest {
  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._

    val peopleDF = spark.createDataFrame(List(
      Person("Justin","Pihony",32,None,Some("Programmer")),
      Person("John","Smith",22,Some(176.7),None),
      Person("Jane","Doe",62,None,None),
      Person("jane","smith",42,Some(125.3),Some("Chemical Engineer")),
      Person("John","Doe",25,Some(222.2),Some("Teacher"))
    ))

    peopleDF.show(false)

    peopleDF.groupBy($"firstName").agg(first($"weightInLbs")).collect()

    val correctedPeopleDF = peopleDF.withColumn("firstName",trim(initcap($"firstName")))

    correctedPeopleDF.groupBy(lower($"firstName")).agg(first($"weightInLbs")).collect()

    correctedPeopleDF.groupBy(lower($"firstName")).agg(first($"weightInLbs")).show()

    correctedPeopleDF.withColumn("weightInLbs",coalesce($"weightInLbs",lit(0))).show(10)

    correctedPeopleDF.filter(lower($"jobType").contains("engineer")).show()

    correctedPeopleDF.filter(lower($"jobType").isin(List("Chemical engineer","teacher"):_*)).show() //isin variable argument method slot _*

  }

}
