package com.dataframe.part31.OldSparkExamplesJson.ETLTOOL

/**
  * Created by kjfg254 on 11/20/2016.
  */
import java.util.Properties
import org.apache.spark.SparkConf
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark._
import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.apache.spark.sql.DataFrame
import org.springframework.context.support.ClassPathXmlApplicationContext

case class sourcedb(driver:String,url:String,username:String,password:String,table:String,lowerbound:String,upperbound:String,numberofparitions:String,parallelizecolumn:String)

case class targetdb(driver:String,url:String,username:String,password:String,table:String)

object SourceDBToTargetDB {
  def main (args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("DBSourceToTargetBulkLoad").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    //read the application context file
    val ctx = new ClassPathXmlApplicationContext("SourceToTargetBulkLoad.xml")
    val SourceDBinfo = ctx.getBean("SourceDB").asInstanceOf[sourcedb]
    val TargetDBinfo = ctx.getBean("TargetDB").asInstanceOf[targetdb]

    println("--###########################################################--")
    println("SourceDB Driver:-%s".format(SourceDBinfo.driver))
    println("SourceDB Url:-%s".format(SourceDBinfo.url))
    println("SourceUsername:-%s".format(SourceDBinfo.username))
    println("SourcePassword:-%s".format(SourceDBinfo.password))
    println("SourceTable:-%s".format(SourceDBinfo.table))
    println("SourceLowerbound:-%s".format(SourceDBinfo.lowerbound.toInt))
    println("SourceUpperbound:-%s".format(SourceDBinfo.upperbound.toInt))
    println("SourceNumberofpartitions:-%s".format(SourceDBinfo.numberofparitions.toInt))
    println("SourceParallelizecolumn:-%s".format(SourceDBinfo.parallelizecolumn))
    println("--###########################################################--")
    println("TargetDriver:-%s".format(TargetDBinfo.driver))
    println("TargetUrl:-%s".format(TargetDBinfo.url))
    println("TargetUsername:-%s".format(TargetDBinfo.username))
    println("TargetPassword:-%s".format(TargetDBinfo.password))
    println("TargetTable:-%s".format(TargetDBinfo.table))
    println("--###########################################################--")

    try {

      val sourceprops=new Properties()
      sourceprops.put("user",SourceDBinfo.username)
      sourceprops.put("password",SourceDBinfo.password)
      sourceprops.put("driver",SourceDBinfo.driver)

      val targetprops=new Properties()
      targetprops.put("user",SourceDBinfo.username)
      targetprops.put("password",SourceDBinfo.password)
      targetprops.put("driver",SourceDBinfo.driver)

      val sqlContext = new org.apache.spark.sql.SQLContext(sc)
      val sourcedf = sqlContext.read.jdbc(SourceDBinfo.url,SourceDBinfo.table,SourceDBinfo.parallelizecolumn,SourceDBinfo.lowerbound.toInt,SourceDBinfo.upperbound.toInt,SourceDBinfo.numberofparitions.toInt,sourceprops)
      sourcedf.show(10)

      //println(sourcedf.first())

      //create table and inser
      //sourcedf.createJDBCTable(TargetDBinfo.url,TargetDBinfo.table,true)

      //truncate and insert
      //sourcedf.insertIntoJDBC(TargetDBinfo.url,TargetDBinfo.table,true)

      //just insert dont truncate
      val sourcedfmode=sourcedf.write.mode("append")
      sourcedfmode.jdbc(TargetDBinfo.url,TargetDBinfo.table,targetprops)


    } catch {
      case e: Exception => e.printStackTrace
    }

    sc.stop()
  }
}
