package com.dataframe.part31.OldSparkExamples.sql.olapfunctions

/**
  * Created by kjfg254 on 11/20/2016.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object RunningAverageRunningSum {

  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("YOUR_APP_NAME_USER").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

        val data1=List("786~20160710~234")
        val data2=List("786~20160709~-128")
        val data3=List("786~20160711~-457")
        val data4=List("987~20160812~456")
        val data5=List("987~20160812~567")
        val fulldata=data1:::data2:::data3:::data4:::data5


   fulldata.map(x => x.split("\\~"))
     .map(x => (x(0), x(1), x(2).toDouble))
     .groupBy(_._1)
     .mapValues{(x => x.sortBy(_._2).zip(Stream from 1).scanLeft(("","",0.0,0.0,0.0,0.0))
     { (a,b) => (b._1._1,b._1._2,b._1._3,(b._1._3.toDouble + a._3.toDouble),(b._1._3.toDouble + a._3.toDouble)/b._2,b._2)}.tail)}
        //.toArray.map(x => (x._2)).flatMap(x => x.sortBy(_._1))
        .foreach(println)


      sc.textFile("C:\\Users\\kalit_000\\Desktop\\Hadoop_IMP_DOC\\spark\\data.txt")
        .map(x => x.split("\\~"))
      .map(x => (x(0), x(1), x(2).toDouble))
      .groupBy(_._1)
      .mapValues{(x => x.toList.sortBy(_._2).zip(Stream from 1).scanLeft(("","",0.0,0.0,0.0,0.0))
      { (a,b) => (b._1._1,b._1._2,b._1._3,(b._1._3.toDouble + a._3.toDouble),(b._1._3.toDouble + a._3.toDouble)/b._2,b._2)}.tail)}
      .flatMapValues(x => x.sortBy(_._1))
      .foreach(println)


    val sqlContext = new SQLContext(sc)
    val df = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("header", "true") // Use first line of all files as header
      .option("inferSchema", "true") // Automatically infer data types
      .load("C:\\Users\\kalit_000\\Desktop\\az_spark\\csv_file.csv")

    df.registerTempTable("kali")

    val df2=sqlContext.sql("select column1,column2 from kali")

    df2.write.mode("append").saveAsTable("edh_raw.test")



    /*
    sc.textFile("C:\\Users\\kalit_000\\Desktop\\Hadoop_IMP_DOC\\spark\\data.txt")
      .map(x => x.split("\\~"))
      .map(x => (x(0), x(1), x(2).toDouble))
      .groupBy(_._1)
      .mapValues{(x => x.toList.sortBy(_._2).zip(Stream from 1).scanLeft(("","",0.0,0.0,0.0,0.0))
      { (a,b) => (b._1._1,b._1._2,b._1._3,(b._1._3.toDouble + a._3.toDouble),(b._1._3.toDouble + a._3.toDouble)/b._2,b._2)}.tail)}
      .flatMapValues(x => x.sortBy(_._1))
      .foreach(println)
*/

    /*
      sc.textFile("C:\\Users\\kalit_000\\Desktop\\Hadoop_IMP_DOC\\spark\\data.txt")
      .map(x => x.split("\\~"))
      .map(x => (x(0), x(1), x(2).toDouble))
      .groupBy(_._1)
      .mapValues{(x => x.toList.sortBy(_._2).scanLeft(("","",0.0,0.0,0.0)){ (a,b) => (b._1,b._2,b._3,b._3.toDouble + a._3.toDouble,0.0)}.tail)}
      .flatMapValues(x => x)
      .foreach(println)
    */



  }
}
