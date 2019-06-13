package com.dataframe.part7.sparkml.clustering

/**
  * Created by kalit_000 on 6/13/19.
  */

import org.apache.spark._
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.sql._

import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.clustering.KMeans

object UberClustering {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Uber Train")
    conf.setIfMissing("spark.master", "local[*]")

    val spark = SparkSession
      .builder()
      .config(conf)
      .getOrCreate()

    import spark.sqlContext.implicits._
    import org.apache.spark.sql.functions._

    val df = spark.read.option("header", "false")
      .csv("sparkMLDataSets/uber.csv")
      .withColumnRenamed("_c0", "dt")
      .withColumnRenamed("_c1", "lat")
      .withColumnRenamed("_c2", "lon")
      .withColumnRenamed("_c3", "base")
      .withColumn("dt", $"dt".cast("string"))
      .withColumn("lat", $"lat".cast("decimal"))
      .withColumn("lon", $"lon".cast("decimal"))
      .withColumn("base", $"base")
      .as[Uber]

    df.cache
    df.show
    df.schema

    val featureCols = Array("lat", "lon")
    val assembler = new VectorAssembler().setInputCols(featureCols).setOutputCol("features")
    val df2 = assembler.transform(df)
    val Array(trainingData, testData) = df2.randomSplit(Array(0.7, 0.3), 5043)

    val kmeans = new KMeans().setK(10).setFeaturesCol("features").setMaxIter(3)
    val model = kmeans.fit(trainingData)

    println("Final Centers: ")
    model.clusterCenters.foreach(println)

    val categories = model.transform(testData)

    categories.show
    categories.createOrReplaceTempView("uber")

    //Which cluster had highest number of pickups by month, day, hour?
    categories.select(month($"dt").alias("month"), dayofmonth($"dt")
      .alias("day"), hour($"dt").alias("hour"), $"prediction")
      .groupBy("month", "day", "hour", "prediction").
      agg(count("prediction").alias("count")).orderBy("day", "hour", "prediction").show

    //Which cluster had highest number of pickups by hour?
    categories.select(hour($"dt").alias("hour"), $"prediction")
      .groupBy("hour", "prediction").agg(count("prediction")
      .alias("count")).orderBy(desc("count")).show

    // number of pickups per cluster
    categories.groupBy("prediction").count().show()

    spark.sql("select * from uber").show(10,false)

    // pick your preference DataFrame API above or can use SQL directly
    spark.sql("select prediction, count(prediction) as count from uber group by prediction").show
    spark.sql("SELECT hour(uber.dt) as hr,count(prediction) as ct FROM uber group By hour(uber.dt)").show

    spark.sql("SELECT hour(uber.dt) as hr,uber.lat,uber.lon,count(prediction) as ct FROM uber group By hour(uber.dt),uber.lat,uber.lon").show

    // to save the categories dataframe as json data
    //  categories.select("dt", "base", "prediction").write.format("json").save("uberclusterstest")
    //  to save the model
    //  model.write.overwrite().save("/user/user01/data/savemodel")
    //  to re-load the model
    //  val sameModel = KMeansModel.load("/user/user01/data/savemodel")

  }

}
case class Uber(dt: java.sql.Timestamp, lat: BigDecimal, lon: BigDecimal, base: String)