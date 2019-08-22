package com.dataframe.part1.JustinPihonyJsonDataSetExamples

/**
  * Created by kalit_000 on 5/14/19.
  */

import org.apache.spark.sql.{DataFrame,SaveMode,SparkSession}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object Detector {
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

    val financiesDF = spark.read.json("Data/finances-small.json")

    val accountNumberPrevious4WindowSpec = Window.partitionBy($"AccountNumber")
                                                 .orderBy($"Date").rowsBetween(-4,0)//previous 4 rows and end at the current row

    val rollingAvgForPrevious4TransactionPerAccount = avg($"Amount").over(accountNumberPrevious4WindowSpec)

      financiesDF
         // not available (cleaning all empty rows)
         .na.drop("all", Seq("ID","Account","Amount","Description","Date"))
         .na.fill("Unknown", Seq("Description"))  // fill empty value for description and fill with value unknown
         .where(($"Amount" =!= 0 ) || $"Description" === "Unknown")
         .selectExpr("Account.Number as AccountNumber","Amount",
           "to_Date(CAST(unix_timestamp(Date,'MM/dd/yyyy') as TIMESTAMP)) as Date",
           "Description")
         .withColumn("RollingAverage",rollingAvgForPrevious4TransactionPerAccount)
         .write.mode(SaveMode.Overwrite).parquet("Output/finances-small")

    /*spark.read.json("Data/finances-small.json")
         .na.drop("all")
         .na.fill("Unknown" , Seq("Description"))
         .where($"Description" === "Unknown").show(10,false)*/

    /* na.replace(Seq("Description"),Map("Movies" -> "Entertainment","Groacery Store" -> "Food") */

    /*
      spark.read.option("mode","DROPMALFORMED").json("PATH")
      spark.read.option("mode","FAILFAST").json("PATH")
      spark.read.option("mode","PERMISSIVE").json("PATH")
    */

    spark.read.parquet("Output/finances-small").show(10,false)

    if (financiesDF.hasColumn("_corrupt_record")){
      financiesDF.where($"_corrupt_record".isNotNull).select($"_corrupt_record")
                 .write.mode(SaveMode.Overwrite).text("Output/corrupt_finances")
    }

    financiesDF.select(concat($"Account.FirstName", lit(" "),$"Account.LastName").as("FullName"),
                         $"Account.Number".as("AccountNumber"))
               .distinct
               .coalesce(5)
               .write.mode(SaveMode.Overwrite).json("Output/finances-small-accounts")

    /*financiesDF.select(concat($"Account.FirstName",lit(" "),$"Account.Lastname").as("FullName"),
                         $"Account.Number".as("AccountNumber"))
               .dropDuplicates()
               .write.mode(SaveMode.Overwrite).json("Output/finances-small-accounts")*/

    financiesDF
               .select($"Account.Number".as("AccountNumber"),$"Amount",$"Description",$"Date")
               .groupBy($"AccountNumber")
               .agg(avg($"Amount").as("AverageTransaction"),sum($"Amount").as("TotalTransactions"),
                 count($"Amount").as("NumberOfTransactions"),max($"Amount").as("MaxTransactions"),
                 min($"Amount").as("MinTransaction"),stddev($"Amount").as("StandardDeviationAmount"),
                 collect_set($"Description").as("UniqueTransactionDescrioptions"))
               .coalesce(5)
               .write.mode(SaveMode.Overwrite).json("Output/finances-small-account-details")

    financiesDF
      .select($"Account.Number".as("AccountNumber"),$"Amount",$"Description",$"Date")
      .groupBy($"AccountNumber")
      .agg(avg($"Amount").as("AverageTransactionAmount"),sum($"Amount").as("TotalTransactionAmount"),
        count($"Amount").as("NumberOfTransactions"),max($"Amount").as("MaxTransactionAmount"),
        min($"Amount").as("MinTransactionAmount"),stddev($"Amount").as("StandardDeviationAmount"),
        collect_set($"Description").as("UniqueTransactionDescrioptions")).show(false)

  }

  implicit class DataFrameHelper(df:DataFrame) {
    import scala.util.Try // org.apache.spark.sql.AnalysisException
    def hasColumn(columnName:String) = Try(df(columnName)).isSuccess
  }

}
