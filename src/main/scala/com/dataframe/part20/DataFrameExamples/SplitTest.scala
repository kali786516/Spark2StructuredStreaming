package com.dataframe.part20.DataFrameExamples

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType

/**
  * Created by kalit_000 on 5/30/19.
  */
object SplitTest {

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark = SparkSession.builder().appName("Use Collaborative Filtering for movie Recommendations").master("local[*]").getOrCreate()
    val sc = spark.sparkContext

    import spark.implicits._

    val df = Seq(("Sri","[123,456]"),("Hari","[786,787]")).toDF("Name","ID")

    val schemaUntyped = new StructType()
      .add("name1", "string")
      .add("id2", "string")

    val df2=spark.createDataFrame(df.rdd,schema = schemaUntyped)

    df2.createOrReplaceTempView("test")

    spark.sql("select split(id2,',')[0] as id1,split(id2,',')[1] as id2 from test").show(10)


    df2.printSchema()







  }

}
