package com.dataframe.part31.OldSparkExamples.JsonPraser

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by kjfg254 on 11/20/2016.
  */


object JsonParserExample {
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("test").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val people = sqlContext.jsonFile("C:\\Users\\kjfg254\\Desktop\\AZ_Projects\\veeva\\json_poc\\suggestion_vod__c.json")

    people.printSchema()
    people.registerTempTable("json_temp_table")

    val ddl=sqlContext.sql("describe json_temp_table")

    ddl.registerTempTable("test")

    val test2=ddl.sqlContext.sql("select * from test")

    ddl.rdd.saveAsTextFile("C:\\Users\\kjfg254\\Desktop\\AZ_Projects\\veeva\\json_poc\\file")

    test2.foreach(println)
    sc.stop()

  }

}
