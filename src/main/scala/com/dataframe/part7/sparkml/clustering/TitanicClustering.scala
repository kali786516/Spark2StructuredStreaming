package com.dataframe.part7.sparkml.clustering

import com.google.common.collect.ImmutableMap
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.evaluation.ClusteringEvaluator
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
/**
  * Created by kalit_000 on 5/30/19.
  */
object TitanicClustering {

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("BikeBuyersDecisionTrees")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val spark = SparkSession.builder().appName("Examine data about passensgers on the titanic").master("local[*]").getOrCreate()
    val sc = spark.sparkContext

    val rawData=spark.read.format("csv")
      .option("header","true")
      .load("sparkMLDataSets/titanic.csv")

    println("-" * 100)

    println("Sample Data 10 Records")
    rawData.show(10)

    println(rawData.count())

    println("-" * 100)

    println("cast data sets")

    rawData.createOrReplaceTempView("rawDataTable")

    val dataSet = spark.sql("select cast(Survived as double) as Survived ,cast(Pclass as float) as Pclass," +
      "Sex,cast(Age as double) as Age ,cast(Fare as double) as Fare," +
      "Embarked from rawDataTable")

    val cleanedDataSet=dataSet.na.replace("*", ImmutableMap.of("?", "null")).na.drop()

    cleanedDataSet.show(10)

    println(cleanedDataSet.count())

    val Array(traindata,testdata) = cleanedDataSet.randomSplit(Array(.8, .2))

    println("String Indexer Step 1 ....................................................")

    val stringIndexColumns = Seq("Sex","Embarked")

    val stringIndexer = stringIndexColumns.map { colName =>
      new StringIndexer().setInputCol(colName).setOutputCol(colName + "_indexed").fit(cleanedDataSet)
    }

    val pipeline = new Pipeline().setStages(stringIndexer.toArray)

    val StringIndexedDF = pipeline.fit(cleanedDataSet).transform(cleanedDataSet)

    print("-" * 100)

    print("Data Set After StringIndexer addinging _indexex")

    StringIndexedDF.show(10)

    val opDF = StringIndexedDF.drop("Sex","Embarked").withColumnRenamed("Sex_indexed","Gender").withColumnRenamed("Embarked_indexed","Boarded")

    println("Vector Assembler Step  2....................................................")

    val assembler = new VectorAssembler()
      .setInputCols(Array("Survived","Pclass","Age","Fare", "Gender","Boarded"))
      .setOutputCol("features")

    val assemberDF = assembler.transform(opDF)

    assemberDF.show(10)

    assemberDF.printSchema()

    println("Build Model Step  3....................................................")

    val kmeans = new KMeans().setK(5).setSeed(1)

    val model = kmeans.fit(assemberDF)

    val clusteredData = model.transform(assemberDF)

    println("Evaluate Model Step  4....................................................")

    val evaluator = new ClusteringEvaluator()

    val silhouetter = evaluator.evaluate(clusteredData)

    println("Silhouette with squared euclidean distance =" + silhouetter)

    println("Print Cluster Centers Step  5....................................................")

    model.clusterCenters.foreach(println)

    println("Print Clustered Data Step  5....................................................")

    clusteredData.show(10)

    println("Print Clustered Data Averages Step  6....................................................")

    clusteredData.groupBy("prediction").agg(avg("Survived"),avg("Pclass"),avg("Age"),
                        avg("Fare"),avg("Gender"),avg("Boarded"),count("prediction")).orderBy("prediction").show(10)




    model.write.overwrite()
      .save("SparkMLModels/TitanicClustering/TitanicClustering.model_v1")


    sc.stop()

  }

}
