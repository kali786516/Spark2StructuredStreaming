package com.dataframe.part1.JustinPihonyJsonDataSetExamples

/**
  * Created by kalit_000 on 5/15/19.
  */

import org.apache.spark.sql._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.expressions.scalalang.typed

case class Account(number:String,firstName:String,lastName:String)
case class Transaction(id:Long,account:Account,date:java.sql.Date,amount:Double,description:String)
case class TransactionForAverage(accountNumber:String,amount:Double,description:String,date:java.sql.Date)

object DetectorDataSet {
  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark = SparkSession.builder
      .master("local")
      .appName("Fraud Detector")
      .config("spark.driver.memory","2g")
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    val financiesDS = spark.read.json("Data/finances-small.json")
                           .withColumn("date",to_date(unix_timestamp($"Date","MM/dd/yyyy")
                                             .cast("timestamp")
                           )).as[Transaction]

    val accountNumberPrevioud4Windowsspec=Window.partitionBy($"AccountNumber")
                                                .orderBy($"Date").rowsBetween(-4,0)

    val rollimngavgForPrevious4PerAccount = avg($"Amount").over(accountNumberPrevioud4Windowsspec)

    financiesDS
               .na.drop("all",Seq("ID","Account","Amount","Description","Date"))
               .na.fill("Unknown",Seq("Description")).as[Transaction]
               .filter(tx => (tx.amount != 0 || tx.description == "Unknown"))
               .select($"Account.Number".as("AccountNumber").as[String],$"Amount".as[Double],
                       $"Date".as[java.sql.Date](Encoders.DATE),$"Description".as[String])
               .withColumn("RollingAverage",rollimngavgForPrevious4PerAccount)
               .write.mode(SaveMode.Overwrite).parquet("Output/finances-small")


    financiesDS
               .select($"account.number".as("accountNumber").as[String],$"amount".as[Double],
                       $"description".as[String],
                       $"date".as[java.sql.Date](Encoders.DATE)).as[TransactionForAverage]
               .groupByKey(_.accountNumber)
               .agg(
                    typed.avg[TransactionForAverage](_.amount).as("AverageTransaction").as[Double],
                    typed.sum[TransactionForAverage](_.amount),
                    typed.count[TransactionForAverage](_.amount),
                    max($"Amount").as("MaxTransaction").as[Double]
               ).coalesce(5)
               .write.mode(SaveMode.Overwrite).json("Output/finances-small-account-details")

  }

  implicit class DataFrameHelper[T](ds:Dataset[T]) {
    import scala.util.Try // org.apache.spark.sql.AnalysisException
    def hasColumn(columnName:String) = Try(ds(columnName)).isSuccess
  }

}
