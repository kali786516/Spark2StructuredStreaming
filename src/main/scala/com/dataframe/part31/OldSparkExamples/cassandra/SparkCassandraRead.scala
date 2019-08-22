package com.dataframe.part31.OldSparkExamples.cassandra

/**
  * Created by kjfg254 on 11/21/2016.
  */
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.datastax.spark.connector._

object SparkCassandraRead {

  def main(args: Array[String]){

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)


    val conf=new SparkConf(true).set("spark.cassandra.connection.host", "127.0.0.1")

    val sc= new SparkContext("local","test",conf)

    print("Test kali Spark Cassandra")

    val personRDD = sc.cassandraTable("people","person")

    personRDD.count()

    personRDD.collect().foreach(println)

    val firstrow=personRDD.first()

    //firstrow.columnNames

    //val cc = new org.apache.spark.sql.cassandra.CassandraSQLContext(sc)

    //val p=cc.sql("select * from people.person")

    //p.collect().foreach(println)

  }

}
