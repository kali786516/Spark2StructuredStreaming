package com.dataframe.part31.OldSparkExamplesJson.ETLTOOL

/**
  * Created by kjfg254 on 11/20/2016.
  */
import java.util
import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark._
import java.sql.{ResultSet, DriverManager, Connection}
import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.springframework.context.support.ClassPathXmlApplicationContext
import scala.collection.mutable.ListBuffer

case class SqlMulti(driver:String,url:String,username:String,password:String,sql:String)

object SaprkMultiExample {
  def main (args: Array[String]):Unit= {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[1]").setAppName("MultipleSqlColumns").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    //read the application context file
    val ctx = new ClassPathXmlApplicationContext("multiplecolumns.xml")
    val DBinfo = ctx.getBean("SqlTest").asInstanceOf[SqlMulti]

    /*assign class values to variables*/
    val driver = DBinfo.driver
    val url = DBinfo.url
    val username = DBinfo.username
    val password = DBinfo.password
    val query = DBinfo.sql
    var connection: Connection = null
    val sqlquery = DBinfo.sql

    println("DB Driver:-%s".format(driver))
    println("DB Url:-%s".format(url))
    println("Username:-%s".format(username))
    println("Password:-%s".format(password))
    println("Query:-%s".format(query))


    try {
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(query)

      resultSet.setFetchSize(10);
      val columnnumber = resultSet.getMetaData().getColumnCount.toInt

      /*OP COLUMN NAMES*/
      var i = 0.toInt;
      for (i <- 1 to columnnumber.toInt) {
        val columnname = resultSet.getMetaData().getColumnName(i)
        println("Column Names are:- %s".format(columnname))
      }


      /*OP DATA*/
      while (resultSet.next()) {
        var list = new java.util.ArrayList[String]()
        for (i <- 1 to columnnumber.toInt) {
          list.add(resultSet.getString(i))
          println(list)
          sc.parallelize(list.toString.replace("null", "N/A")).saveAsTextFile("C:\\Users\\kalit_000\\Desktop\\typesafe\\scaladbop\\op.txt")
        }
      }


      /* val myRDD = new JdbcRDD(sc, () =>
         DriverManager.getConnection(url, username, password), query + " where ? = ? ", 1, 1, 1,
         r => r.getString(1)+","+r.getString(2)+","+r.getString(3))
           //getString(columnname))
       print(myRDD.count())
       myRDD.saveAsTextFile("C:\\Users\\kalit_000\\Desktop\\typesafe\\scaladbop\\op.txt")
     */

      val sqlContext = new org.apache.spark.sql.SQLContext(sc)
      // val querytest=sqlContext.sql(query)
      //val prop=new Properties()
      //val url2="jdbc:sqlserver://localhost;user=admin;password=oracle;database=AdventureWorks2014"
      //prop.setProperty("user","admin")
      //prop.setProperty("password","oracle")
      //val test=sqlContext.read.format(url2,"`Customer",prop)
      //sqlContext.load()
      val df = sqlContext.read.format("jdbc").options(Map("url" -> url,"dbtable" -> "customer","query"-> query,"driver" -> driver)).load()

      //df.show(10)

      df.registerTempTable("Test")

      val test=sqlContext.sql("select  * from Test limit 10")

      sc.parallelize(test.collect()).saveAsTextFile("C:\\Users\\kalit_000\\Desktop\\typesafe\\scaladbop\\op.txt")

      //test2.foreach(println)

      //df.saveAsParquetFile("C:\\Users\\kalit_000\\Desktop\\typesafe\\scaladbop\\op.txt")

      //df.show(10)
      //test.show(10)

    } catch {
      case e: Exception => e.printStackTrace
    }
    connection.close()
    sc.stop()
  }
}
