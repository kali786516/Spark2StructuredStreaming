package com.dataframe.part33.SparkExcelRead

import org.apache.log4j.{Level, Logger}
import org.apache.spark
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types._
import org.apache.spark.sql.types.{DecimalType, IntegerType, StringType, StructField, StructType, TimestampType}
import org.apache.spark.sql.functions._

object SparkExcel {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setAppName("Test").setMaster("local[*]").set("spark.driver.memory","4g")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val df=sqlContext
      .read
      .format("com.databricks.spark.csv")
      .option("header", "false")
      .option("codec", "org.apache.hadoop.io.compress.GzipCodec")
      .option("escape", ":")
      .option("parserLib", "univocity")
      .option("delimiter", "~")
      .option("inferSchema", "false")
      .load("file:///C:/Users/test.xlsx")

    // df.show(10)


    val dfNew1 = sqlContext.read.format("com.crealytics.spark.excel")
      .option("header","false")
      .option("sheetName", "Source File")
      .option("useHeader", "true")
      .option("treatEmptyValuesAsNulls", "false")
      .option("inferSchema","false")
      .option("addColorColumns", "false")
      .option("startColumn", 0)
      .option("endColumn", 28)
      .option("timestampFormat", "MM-dd-yyyy HH:mm:ss")
      .option("maxRowsInMemory", 20)
      .option("excerptSize", 10)
      .load("file:///C:/Userstest.xlsx")

    val dfNew2 = sqlContext.read.format("com.crealytics.spark.excel")
      .option("header","false")
      .option("sheetName", "Source File")
      .option("useHeader", "false")
      .option("treatEmptyValuesAsNulls", "false")
      .option("inferSchema","false")
      .option("addColorColumns", "false")
      .option("startColumn", 29)
      .option("endColumn", 200)
      .option("timestampFormat", "MM-dd-yyyy HH:mm:ss")
      .option("maxRowsInMemory", 20)
      .option("excerptSize", 10)
      .load("file:///C:/test.xlsx")

    dfNew1.show(10)

    dfNew2.show(1)

    dfNew1.registerTempTable("blah_data")

    import sqlContext.implicits._

    //dfNew2.select(concat_ws(",",dfNew2.columns.map(c => col(c)): _*))

    //println(dfNew2.columns.toList.map(c => (c)))

    //dfNew2.select(concat_ws(",",dfNew2.columns.map(c => col(c)): _*))

    // val test=Seq(dfNew2.schema.fieldNames)

    dfNew2.registerTempTable("kali")
    val test2=sqlContext.sql("select * from kali limit 1")

    // val result=test2.withColumn("newCol", split(concat_ws(",",  test2.schema.fieldNames.map(c=> col(c)):_*), ";"))

    val dfResults = dfNew2.select(concat_ws(",",test2.columns.map(c => col(c)): _*).alias("joined_cols"))

    dfResults.printSchema()

    dfResults.registerTempTable("kali2")

    dfResults.show(1,false)

    sqlContext.sql("with cte as ( select explode(split(joined_cols, ',')) as product from kali2 ) select * from payers_data cross join cte").show(10)


    sc.stop()



  }

}
