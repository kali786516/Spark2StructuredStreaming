package com.dataframe.part31.OldSparkExamplesJson.xmlparsing

/**
  * Created by kjfg254 on 11/20/2016.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

object SimpleXmlParser {
  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("YOUR_APP_NAME_USER").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    /*XML parsing*/
    val sqlcontext=new SQLContext(sc)

    val file=sqlcontext.read.format("xml").option("rowtag","book").load("C:\\Users\\kalit_000\\Desktop\\2016\\scalasqlconvertcode\\books.xml")

    file.printSchema()

    file.registerTempTable("xmltable")

    sqlcontext.sql("select * from xmltable where to_date(publish_date) > to_date('2000-11-17')").show()


    sc.stop()


  }
}
