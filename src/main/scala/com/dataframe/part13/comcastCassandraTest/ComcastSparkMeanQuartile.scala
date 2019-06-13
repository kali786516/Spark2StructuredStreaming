package com.dataframe.part13.comcastCassandraTest

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.apache.commons.math3.stat.descriptive._
import org.apache.spark.sql.functions._

/**
  * Created by kalit_000 on 6/13/19.
  */
object ComcastSparkMeanQuartile {

  def main(args: Array[String]): Unit = {


    val starttime = System.currentTimeMillis()
    var logger    = Logger.getLogger(this.getClass())
    val jobname   = "SimpleMktData_APP"

    if (args.length < 3)
    {
      logger.error("=> wrong parameters number")
      System.err.println("Usage: " +
        "SparkDeployMode:-Local[*]/yarn-client/yarn-cluster " +
        "Cassandra Host:- 127.0.0.1:9042" +
        " SourceCSVFile:-hdfs://data/testdata.csv" )
      System.exit(1)
    }

    if (args.length >= 3)
    {
      println("Argument 1:Deply Mode            :- %s".format(args(0)))
      println("Argument 1:Cassandra Host        :- %s".format(args(1)))
      println("Argument 1:Source CSV File       :- %s".format(args(2)))
    }

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val deploymode    = args(0)
    val cassandrahost = args(1)
    val sourcefile    = args(2)

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    //https://stackoverflow.com/questions/37032689/scala-first-quartile-third-quartile-and-iqr-from-spark-sqlcontext-dataframe

    import spark.implicits._

    val customSchema  = StructType(Array(
      StructField("column1",DataTypes.DoubleType, true),
      StructField("column2",DataTypes.DoubleType, true),
      StructField("column3",DataTypes.DoubleType, true),
      StructField("column4",DataTypes.DoubleType, true),
      StructField("column5",DataTypes.DoubleType, true)))

    val df = spark.read
      .format("com.databricks.spark.csv")
      .option("header", "false")
      .schema(customSchema)
      .load(sourcefile)

    val sorteddata = df.sort(asc("column1"),asc("column2"),asc("column3"),asc("column4"),asc("column5"))

    sorteddata.createOrReplaceTempView("table")

    val data = sorteddata.select(avg(df("column1")) as "column1mean",avg(df("column2")) as "column2mean",avg(df("column3")) as "column3mean",avg(df("column4")) as "column4mean" )

    // Turn dataframe column into an Array[Long]
    val mean1 = data.select("column1mean").rdd.map(row => row(0).asInstanceOf[Long]).collect()
    val mean2 = data.select("column2mean").rdd.map(row => row(0).asInstanceOf[Long]).collect()
    val mean3 = data.select("column3mean").rdd.map(row => row(0).asInstanceOf[Long]).collect()
    val mean4 = data.select("column4mean").rdd.map(row => row(0).asInstanceOf[Long]).collect()

    // Create the math3 object and add values from the
    // mean array to the descriptive statistics array
    val arrMean1 = new DescriptiveStatistics()
    genericArrayOps(mean1).foreach(v => arrMean1.addValue(v))

    val arrMean2 = new DescriptiveStatistics()
    genericArrayOps(mean2).foreach(v => arrMean2.addValue(v))

    val arrMean3 = new DescriptiveStatistics()
    genericArrayOps(mean3).foreach(v => arrMean3.addValue(v))

    val arrMean4 = new DescriptiveStatistics()
    genericArrayOps(mean4).foreach(v => arrMean4.addValue(v))

    // Get first and third quartiles and then calc IQR
    val meanQ11 = arrMean1.getPercentile(25)
    val meanQ31 = arrMean1.getPercentile(75)
    val meanIQR1 = meanQ31 - meanQ11

    val meanQ12 = arrMean2.getPercentile(25)
    val meanQ32 = arrMean2.getPercentile(75)
    val meanIQR2 = meanQ32 - meanQ12

    val meanQ13 = arrMean3.getPercentile(25)
    val meanQ33 = arrMean3.getPercentile(75)
    val meanIQR3 = meanQ33 - meanQ13

    val meanQ14 = arrMean4.getPercentile(25)
    val meanQ34 = arrMean4.getPercentile(75)
    val meanIQR4 = meanQ34 - meanQ14

  }

}
