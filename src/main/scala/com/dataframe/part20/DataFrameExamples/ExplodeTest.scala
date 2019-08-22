package com.dataframe.part20.DataFrameExamples

/**
  * Created by kalit_000 on 5/14/19.
  */

import com.google.common.collect.ImmutableMap
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object ExplodeTest {

  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

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

    println("-" * 100)

    println("Check the note :- spark-mllib_2.11:2.3.2")

    /*
    Below code will work with
            <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-mllib_2.11</artifactId>
            <version>2.3.2</version>
        </dependency>

    val userRecs = model.recommendForUserSubset(df.selectExpr("userID"),5)

    userRecs.show(10,false)

    println("Lets get recommendations for singe user and print the results Step  8....................................................")

    userRecs.createOrReplaceTempView("userRecs")

    val moveisDFforOneUSer148 = spark.sql("select userrecs.recommendations from userRecs where userrecs.userID = '148' ")

    moveisDFforOneUSer148.show(10,false)

    userRecs.selectExpr("explode(recommendations) as recommendations").show()

    userRecs.withColumn("ExplodedField", explode($"recommendations")).drop("recommendations").show()

    userRecs.withColumn("ExplodedField", explode($"recommendations")).selectExpr("recommendations[0] as movieID","recommendations[1] as rating").show(10)

    */

    /*

    //val mySchema = StructType(StructField("keyValueMap",ArrayType(MapType(StringType,IntegerType))))

    /*
    val test = StructType(
      StructField(comments,ArrayType(StructType(StructField(comId,StringType,true),StructField(content,StringType,true)),true),true),
      StructField(createHour,StringType,true),
      StructField(gid,StringType,true),
      StructField(replies,ArrayType(StructType(StructField(content,StringType,true),StructField(repId,StringType,true)),true),true),
      StructField(revisions,ArrayType(StructType(StructField(modDate,StringType,true),StructField(revId,StringType,true)),true),true)
    )*/

    //val df = spark.createDataFrame([[[['a','b','c'], ['d','e','f'], ['g','h','i']]]],["col1"])


    val jsonCompanies = List(
      """{"company":"NewCo","employees":[{"firstName":"Justin","LastName":"Pihony"},{"firstName":"Jane","LastName":"Doe"}]}""",
      """{"company":"FamilyCo","employees":[{"firstName":"Rigel","LastName":"Pihony"},{"firstName":"Rory","LastName":"Pihony"}]}""",
      """{"company":"OldCo","employees":[{"firstName":"Mary","LastName":"Louise"},{"firstName":"Joe","LastName":"Bob"}]}"""
    )

    val companiesRdd = spark.sparkContext.makeRDD(jsonCompanies)

    val companiesDF = spark.read.json(companiesRdd)

    companiesDF.show(false)

    companiesDF.printSchema()

    println(companiesDF.schema)

    //println(companiesDF.schema.fields.mkString(",\n"))

    //val testScheam=StructType(StructField("company",StringType,true), StructField("employees",ArrayType(StructType(StructField("LastName",StringType,true), StructField("firstName",StringType,true)),true),true))

    //spark.read.schema(testScheam).json(companiesRdd).show(10)

    val comapaniesDFTemp=companiesDF.select($"company",explode($"employees").as("employees"))

    comapaniesDFTemp.show(10)

    val employeeDF=comapaniesDFTemp.select($"company",expr("employees.firstName as firstName"))

    employeeDF.select($"*",when($"company" === "FamilyCo","Premium").when($"company" === "OldCo","Legacy").otherwise("Standard")).show(10)

    companiesDF.select($"company",posexplode($"employees").as(Seq("employeesPosition","employee"))).show(10)
    */




  }

}
