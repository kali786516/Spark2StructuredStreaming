package com.dataframe.part20.DataFrameExamples

/**
  * Created by kalit_000 on 5/16/19.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

case class Person3(id:Int,first:String,last:String)
case class Role3(id:Int,role:String)

object JoinDF {
  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local")
      .config("spark.driver.memory","2g")
      .config("spark.sql.autoBroadcastJoinThreshold","29929")
      .config("spark.sql.shuffle.partitions","20")
      .config("spark.sql.inMemoryColumnarStorage.batchSize","234")
      .enableHiveSupport().getOrCreate()

    import spark.implicits._

    val personDf=List(Person2(1,"justin","Pihony"),Person2(2,"John","Doe"),Person2(3,"Jane","Smith")).toDF()

    val roleDf=List(Role(1,"Manager"),Role(3,"CEO"),Role(4,"huh")).toDF()

    val innerJoinDf = personDf.joinWith(broadcast(roleDf),personDf("id") === roleDf("id"))

    innerJoinDf.explain()

    innerJoinDf.queryExecution.executedPlan

  }

}
