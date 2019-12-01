package com.dataframe.part31.OldSparkExamplesJson.cassandra

/**
  * Created by kjfg254 on 11/20/2016.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{SaveMode, Row}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.apache.spark.{SparkContext, SparkConf}

object SprakHivePercentile {

  def main(args: Array[String]) {

    val starttime=System.currentTimeMillis()
    var logger = Logger.getLogger(this.getClass())
    val jobname="SimpleMktData_APP"

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

    val deploymode=args(0)
    val cassandrahost=args(1)
    val sourcefile=args(2)

    val conf = new SparkConf().setMaster(deploymode).setAppName("comcast_test").set("spark.cassandra.connection.host", cassandrahost).set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)
    val hiveContext = new HiveContext(sc)



    val customSchema = StructType(Array(
      StructField("column1",DataTypes.DoubleType, true),
      StructField("column2",DataTypes.DoubleType, true),
      StructField("column3",DataTypes.DoubleType, true),
      StructField("column4",DataTypes.DoubleType, true),
      StructField("column5",DataTypes.DoubleType, true)))

    val df = hiveContext.read
      .format("com.databricks.spark.csv")
      .option("header", "false")
      .schema(customSchema)
      .load(sourcefile)

    val sorteddata=df.sort(asc("column1"),asc("column2"),asc("column3"),asc("column4"),asc("column5"))

    sorteddata.registerTempTable("table")


    val op=hiveContext.sql("" +
      "SELECT 1 as column_order,min(column1) as min_value,percentile(cast(column1 as BIGINT), 0.25) as quartile1,percentile(cast(column1 as BIGINT), 0.5) as median,percentile(cast(column1 as BIGINT), 0.75) as quartile3,regexp_replace(max(column1), \"NaN\", 0) as max_value,current_timestamp as created_ts FROM table " +
      "union all " +
      "SELECT 2 as column_order,min(column2) as min_value,percentile(cast(column2 as BIGINT), 0.25) as quartile1,percentile(cast(column2 as BIGINT), 0.5) as median,percentile(cast(column2 as BIGINT), 0.75) as quartile3,regexp_replace(max(column2), \"NaN\", 0) as max_value,current_timestamp as created_ts FROM table " +
      "union all " +
      "SELECT 3 as column_order,min(column3) as min_value,percentile(cast(column3 as BIGINT), 0.25) as quartile1,percentile(cast(column3 as BIGINT), 0.5) as median,percentile(cast(column3 as BIGINT), 0.75) as quartile3,regexp_replace(max(column3), \"NaN\", 0) as max_value,current_timestamp as created_ts FROM table " +
      "union all " +
      "SELECT 4 as column_order,min(column4) as min_value,percentile(cast(column4 as BIGINT), 0.25) as quartile1,percentile(cast(column4 as BIGINT), 0.5) as median,percentile(cast(column4 as BIGINT), 0.75) as quartile3,regexp_replace(max(column4), \"NaN\", 0) as max_value,current_timestamp as created_ts FROM table " +
      "union all " +
      "SELECT 5 as column_order,min(column5) as min_value,percentile(cast(column5 as BIGINT), 0.25) as quartile1,percentile(cast(column5 as BIGINT), 0.5) as median,percentile(cast(column5 as BIGINT), 0.75) as quartile3,regexp_replace(max(column5), \"NaN\", 0) as max_value,current_timestamp as created_ts FROM table  " +
      "")

    op.show(5)

    op.write.format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "comcast_test","keyspace" -> "comcast"))
      .mode(SaveMode.Append).save()

    /*calculate end time of scala class*/
    val endTime=System.currentTimeMillis()
    val totalTime=endTime-starttime

    println("JobName "+ jobname +" took (%s".format(totalTime/1000d) + ") seconds to process ")

    sc.stop()
    /*Return exit code 0 if all the above steps are successful*/
    System.exit(0)
  }
}
