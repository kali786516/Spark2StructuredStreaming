package com.dataframe.part31.OldSparkExamples.ETLTOOL

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

case class SparkSqlValueClassMPP(driver:String,url:String,username:String,password:String,table:String,opdelimeter:String,lowerbound:String,upperbound:String,numberofparitions:String,parallelizecolumn:String)

object SparkDBMPPExtractor {
  def main (args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("SparkDBExtractorMPP").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    /*
    def opfile(value:DataFrame,delimeter:String):RDD[String]=
    {
      value.map(x => x.toString.replace("[","").replace("]","").replace(",",delimeter))
    }*/

    //read the application context file
    val ctx = new ClassPathXmlApplicationContext("sparkDBExtractorMpp.xml")
    val DBinfo = ctx.getBean("SparkSQLDBExtractorMPP").asInstanceOf[SparkSqlValueClassMPP]

    val driver = DBinfo.driver
    val url = DBinfo.url
    val username = DBinfo.username
    val password = DBinfo.password
    val table = DBinfo.table
    val opdelimeter=DBinfo.opdelimeter
    val lowerbound=DBinfo.lowerbound.toInt
    val upperbound=DBinfo.upperbound.toInt
    val numberofpartitions=DBinfo.numberofparitions.toInt
    val parallelizecolumn=DBinfo.parallelizecolumn


    println("DB Driver:-%s".format(driver))
    println("DB Url:-%s".format(url))
    println("Username:-%s".format(username))
    println("Password:-%s".format(password))
    println("Table:-%s".format(table))
    println("Opdelimeter:-%s".format(opdelimeter))
    println("Lowerbound:-%s".format(lowerbound))
    println("Upperbound:-%s".format(upperbound))
    println("Numberofpartitions:-%s".format(numberofpartitions))
    println("Parallelizecolumn:-%s".format(parallelizecolumn))

    try {
      val props=new Properties()
      props.put("user",username)
      props.put("password",password)
      props.put("driver",driver)

      val sqlContext = new org.apache.spark.sql.SQLContext(sc)
      val df = sqlContext.read.jdbc(url,table,parallelizecolumn,lowerbound,upperbound,numberofpartitions,props)
      df.show(10)

      //opfile(df,opdelimeter).saveAsTextFile("C:\\Users\\kalit_000\\Desktop\\typesafe\\scaladbop\\op.txt")

    } catch {
      case e: Exception => e.printStackTrace
    }
    sc.stop()
  }
}
