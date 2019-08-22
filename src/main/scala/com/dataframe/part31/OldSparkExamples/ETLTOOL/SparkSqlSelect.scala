package com.dataframe.part31.OldSparkExamples.ETLTOOL

/**
  * Created by kjfg254 on 11/20/2016.
  */
import org.apache.spark.SparkConf
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark._
import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.apache.spark.sql.DataFrame
import org.springframework.context.support.ClassPathXmlApplicationContext

case class SparkSqlValueClass(driver:String,url:String,username:String,password:String,sql:String,table:String,opdelimeter:String)


object SparkSqlSelect {
  def main (args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("SparkSqlConfigurable").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    def opfile(value:DataFrame,delimeter:String):RDD[String]=
    {
      value.map(x => x.toString.replace("[","").replace("]","").replace(",",delimeter))
    }


    //read the application context file
    val ctx = new ClassPathXmlApplicationContext("sparksql.xml")
    val DBinfo = ctx.getBean("SparkSQLInst").asInstanceOf[SparkSqlValueClass]

    val driver = DBinfo.driver
    val url = DBinfo.url
    val username = DBinfo.username
    val password = DBinfo.password
    val query = DBinfo.sql
    val sqlquery = DBinfo.sql
    val table = DBinfo.table
    val opdelimeter=DBinfo.opdelimeter

    println("DB Driver:-%s".format(driver))
    println("DB Url:-%s".format(url))
    println("Username:-%s".format(username))
    println("Password:-%s".format(password))
    println("Query:-%s".format(query))
    println("Table:-%s".format(table))
    println("Opdelimeter:-%s".format(opdelimeter))

    try {
      val sqlContext = new org.apache.spark.sql.SQLContext(sc)
      val df = sqlContext.read.format("jdbc").options(Map("url" -> url,"dbtable" -> table,"driver" -> driver)).load()
      df.registerTempTable(table)
      val OP=sqlContext.sql(query)

      opfile(OP,opdelimeter).saveAsTextFile("C:\\Users\\kalit_000\\Desktop\\typesafe\\scaladbop\\op.txt")

    } catch {
      case e: Exception => e.printStackTrace
    }
    sc.stop()

  }
}
