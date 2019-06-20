package com.dataframe.part7.sparkml.Recomendations.explicitRatings

import com.google.common.collect.ImmutableMap
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.sql.functions._

/**
  * Created by kalit_000 on 5/30/19.
  */

object RatingsCollaborativeFiltering {

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("BikeBuyersDecisionTrees")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val spark = SparkSession.builder().appName("Use Collaborative Filtering for movie Recommendations").master("local[*]").getOrCreate()
    val sc = spark.sparkContext

    import spark.implicits._

    val rawData=spark.read.format("csv")
      .option("header","true")
      .load("sparkMLDataSets/movielens/ratings.csv")

    println("-" * 100)

    println("Sample Data 10 Records")
    rawData.show(10)

    println(rawData.count())

    println("-" * 100)

    println("cast data sets")

    rawData.createOrReplaceTempView("rawDataTable")

    val dataSet = spark.sql("select cast(userID as int) as userID ,cast(movieId as int) as movieId," +
      "cast(rating as float) as rating from rawDataTable")

    val cleanedDataSet=dataSet.na.replace("*", ImmutableMap.of("?", "null")).na.drop()

    cleanedDataSet.show(10)

    println(cleanedDataSet.count())

    println("-" * 100)

    println("Lets Describe the ratings columns")

    cleanedDataSet.describe("rating").show(100)

    println("Split the Data Step  1....................................................")

    val Array(traindata,testdata) = cleanedDataSet.randomSplit(Array(.8, .2))

    println("Build ALS Step  2....................................................")

    //cold strategy if the algorithm encounters new productid or userid while buildint the model it will just drop the record (streaming data)
    val als = new ALS().setMaxIter(5).setRegParam(0.1).setUserCol("userID").setItemCol("movieId").setRatingCol("rating").setColdStartStrategy("drop")

    val model = als.fit(traindata)

    println("Get Predictions Step  3....................................................")

    testdata.show(10,false)

    val predictions = model.transform(testdata)

    predictions.show(10)

    predictions.describe("rating","prediction").show(100)

    println("Evaluate ALS Step  4....................................................")

    val evaluator = new RegressionEvaluator().setMetricName("rmse").setLabelCol("rating").setPredictionCol("prediction")

    val rmse = evaluator.evaluate(predictions)

    println("rmse :-" + rmse)

    println("Get top 3 movie recommendations for all users Step  5....................................................")

    val userRecsAll = model.recommendForAllUsers(3)

    userRecsAll.show(10,false)

    println("Get top 3 users for each movie Step  6....................................................")

    val movieRecsAll = model.recommendForAllItems(3)

    movieRecsAll.show(10,false)

    println("Lets test recommendations for 3 users with top 5 movies Step  7....................................................")

    val df=spark.createDataFrame(List((148,"sri"),(463,"hari"),(267,"kali"))).toDF("userID","name")

    println("-" * 100 )

    println("note if below code should work cahnge the mllb to <artifactId>spark-mllib_2.11</artifactId> <version>2.3.1</version>")


    model.write.overwrite()
      .save("SparkMLModels/RatingCollaborative/Recomendation.model_v1")

    /*

            <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-mllib_2.11</artifactId>
            <version>2.3.1</version>
        </dependency>


    val userRecs = model.recommendForUserSubset(df.selectExpr("userID"),5)

    userRecs.show(10,false)

    println("Lets get recommendations for singe user and split the data after exploding and print the results Step  8....................................................")

    userRecs.createOrReplaceTempView("userRecs")

    val moveisDFforOneUSer148 = spark.sql("select userrecs.recommendations from userRecs where userrecs.userID = '148' ")

    moveisDFforOneUSer148.show(10,false)

    userRecs.registerTempTable("tobeexploded")

    val testDf1 = spark.sql("select explode(recommendations) as recommendations from tobeexploded")

    testDf1.createOrReplaceTempView("test2")

    val finalDF = spark.sql("select trim(split(cast(recommendations as string),',')[0]) as movie_id ,trim(split(cast(recommendations as string),',')[1]) as movie_ratings from test2")
    .select(translate($"movie_id","[","").alias("movieId"),translate($"movie_ratings","]","").alias("movie_ratings_new"))

    finalDF.show(10)

    println("Lets join the data sets Step 9....................................................")

    val movieData=spark.read.format("csv")
      .option("header","true")
      .load("sparkMLDataSets/movielens/movies.csv")

    movieData.show(10)

    val recommnendedMovies = movieData.join(finalDF,"movieId").orderBy("movie_ratings_new").selectExpr("title","genres","movie_ratings_new")

    movieData.join(finalDF,"movieId").orderBy($"movie_ratings_new".desc).selectExpr("title","genres","movie_ratings_new").show(10,false)

     */

    //spark.sql("select split(cast(explode(recommendations) as string),',')[0] as movie_id,split(cast(explode(recommendations) as string),',')[1] as rating from tobeexploded").show()

    /*
    userRecs.selectExpr("explode(recommendations) as recommendations").show()

    userRecs.withColumn("ExplodedField", explode($"recommendations")).drop("recommendations").show()

    userRecs.withColumn("ExplodedField", explode(explode($"recommendations"))).printSchema()

    //userRecs.withColumn("ExplodedField", explode(explode($"recommendations"))).show()

    userRecs.withColumn("ExplodedField", explode($"recommendations"))
                  .selectExpr("recommendations[0] as movieID","recommendations[1] as rating").show(10)

    userRecs.withColumn("ExplodedField", explode(explode($"recommendations")))
      .select(
        struct($"userRecs.recommendations[0].alias('movieid')"),
          struct($"userRecs.recommendations[0].alias('ratings')")).show(10)*/

    /* collect list example */
    //val result = df.select($"Id", array($"Name", $"Number", $"Comment") as "List")

    println("Lets join the data with movieLens data set to get movie name Step  9....................................................")


    sc.stop()


  }

}
