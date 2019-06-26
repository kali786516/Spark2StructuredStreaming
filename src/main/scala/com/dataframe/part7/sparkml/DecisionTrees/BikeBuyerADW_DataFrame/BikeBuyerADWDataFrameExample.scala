package com.dataframe.part7.sparkml.DecisionTrees.BikeBuyerADW_DataFrame

import com.google.common.collect.ImmutableMap
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator

object BikeBuyerADWDataFrameExample {
  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("BikeBuyersDecisionTrees_DataFrameExample")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val spark = SparkSession.builder().appName("Examine data about passensgers on the titanic").master("local[*]").getOrCreate()
    val sc = spark.sparkContext

    val rawData=spark.read.format("csv")
      .option("header","false")
      .option("delimiter","\t") //delimeter tab
      .load("sparkMLDataSets/bike-buyers.txt")

    println("-" * 100)

    println("Sample Data 10 Records")
    rawData.show(10)

    println(rawData.count())

    println("-" * 100)

    println("cast data sets")

    rawData.createOrReplaceTempView("rawDataTable")

    spark.sql("select cast(_c0 as int) as customerKey ,cast(_c1 as int) as age,cast(_c2 as int) as bikeBuyer,cast(_c3 as string) as commuteDistance," +
      "cast(_c4 as string) as englishEducation,cast(_c5 as string) as gender,cast(_c6 as int) as houseOwnerFlag,cast(_c7 as string) as maritalStatus ," +
      "cast(_c8 as int) as numberCarsOwned,cast(_c9 as int) as numberChildrenAtHome,cast(_c10 as string) as englishOccupation,cast(_c11 as string) as region," +
      "cast(_c12 as int) as totalChildren,cast(split(_c13,',')[0] as double) as yearlyIncome from rawDataTable").show(10,false)

    val dataSet = spark.sql("select cast(_c0 as int) as customerKey ,cast(_c1 as int) as age,cast(_c2 as int) as bikeBuyer,cast(_c3 as string) as commuteDistance," +
      "cast(_c4 as string) as englishEducation,cast(_c5 as string) as gender,cast(_c6 as int) as houseOwnerFlag,cast(_c7 as string) as maritalStatus ," +
      "cast(_c8 as int) as numberCarsOwned,cast(_c9 as int) as numberChildrenAtHome,cast(_c10 as string) as englishOccupation,cast(_c11 as string) as region," +
      "cast(_c12 as int) as totalChildren,cast(split(_c13,',')[0] as double) as yearlyIncome from rawDataTable")

    val cleanedDataSet=dataSet.na.replace("*", ImmutableMap.of("?", "null")).na.drop()

    cleanedDataSet.show(10)

    println(cleanedDataSet.count())

    println("String Indexer Step 1 ....................................................")

    val stringIndexColumns = Seq("englishEducation","gender","maritalStatus","englishOccupation","region","commuteDistance")

    val stringIndexer = stringIndexColumns.map { colName =>
      new StringIndexer().setInputCol(colName).setOutputCol(colName + "_indexed").fit(cleanedDataSet)
    }

    val pipeline = new Pipeline().setStages(stringIndexer.toArray)

    val StringIndexedDF = pipeline.fit(cleanedDataSet).transform(cleanedDataSet)

    print("-" * 100)

    print("Data Set After StringIndexer addinging _indexex")

    StringIndexedDF.show(10)

    val opDF = StringIndexedDF.drop("englishEducation","gender","maritalStatus","englishOccupation","region","commuteDistance")

    println("Vector Assembler Step  2....................................................")

    val assembler = new VectorAssembler()
      .setInputCols(Array("customerKey","bikeBuyer","commuteDistance_indexed","englishEducation_indexed","gender_indexed","houseOwnerFlag","maritalStatus_indexed",
        "numberCarsOwned","numberChildrenAtHome","englishOccupation_indexed","region_indexed","totalChildren","yearlyIncome"))
      .setOutputCol("features")

    val assemberDF = assembler.transform(opDF)

    println("show assember DF ....................................................")

    assemberDF.show()

    val Array(traindata,testdata) = assemberDF.randomSplit(Array(.8, .2))

    println("DefineDecisionTreeClassifier Step  3....................................................")

    val dtree = new DecisionTreeClassifier().setLabelCol("bikeBuyer").setFeaturesCol("features").setMaxDepth(3).setImpurity("gini")

    val model = dtree.fit(traindata)

    println("Show Predictions Step  4....................................................")

    val predictions = model.transform(testdata)

    predictions.show(10,false)

    predictions.selectExpr("features","rawPrediction","probability","prediction","bikeBuyer").show(10,false)


    println("Show Metric Values Step  5....................................................")

    // Select (prediction, true label) and compute test error.
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("bikeBuyer")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")

    val accuracy = evaluator.evaluate(predictions)
    println("Test Error = " + (1.0 - accuracy))


   spark.stop()

  }

}
