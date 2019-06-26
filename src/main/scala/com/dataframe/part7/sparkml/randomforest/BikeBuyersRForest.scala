package com.dataframe.part7.sparkml.randomforest

/**
  * Created by kalit_000 on 5/28/19.
  */

import org.apache.spark.SparkContext
import org.apache.spark.annotation.Since
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.tree.RandomForest
import com.dataframe.part7.sparkml.DecisionTrees.BikeBuyerADW.common.PrintUtils.printMetrics
import com.dataframe.part7.sparkml.DecisionTrees.BikeBuyerADW.common.Stats
import com.dataframe.part7.sparkml.DecisionTrees.BikeBuyerADW.common.Stats.confusionMatrix
import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}
import com.dataframe.part7.sparkml.DecisionTrees.BikeBuyerADW.BikeBuyerModel

object BikeBuyersRForest {

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("BikeBuyersRForest")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val spark = SparkSession.builder().appName("Classification of Bike Buyers with Random Forest").master("local[*]").getOrCreate()
    val sc = spark.sparkContext

    val bbFile = sc.textFile(args.headOption.getOrElse("sparkMLDataSets/") + "bike-buyers.txt")

    val data = bbFile.map { row =>
      BikeBuyerModel(row.split("\\t")).toLabeledPoint
    }

    val Array(train, test) = data.randomSplit(Array(.9, .1), 102059L)
    train.cache()
    test.cache()

    val numClasses = 2
    val numTrees = 10
    val featureSubsetStrategy = "auto"
    val impurity = "entropy"
    val maxDepth = 20
    val maxBins = 34

    val model = RandomForest.trainClassifier(train, numClasses, BikeBuyerModel.categoricalFeaturesInfo, numTrees, featureSubsetStrategy,
      impurity, maxDepth, maxBins)

    /*
      Briefly, feature is input; label is output.
      A feature is one column of the data in your input set. For instance,
      if you're trying to predict the type of pet someone will choose,
      your input features might include age, home region, family income, etc.
      The label is the final choice, such as dog, fish, iguana, rock, etc.

      Once you've trained your model, you will give it sets of new input containing those features;
      it will return the predicted "label" (pet type) for that person.
    */

    test.take(5).foreach {
      x => println(s"Predicted: ${model.predict(x.features)}, Label: ${x.label}")
    }

    val predictionsAndLabels = test.map {
      point => (model.predict(point.features), point.label)
    }

    val stats = Stats(confusionMatrix(predictionsAndLabels))
    println(stats.toString)

    val metrics = new BinaryClassificationMetrics(predictionsAndLabels)
    printMetrics(metrics)


    spark.stop()




  }

}
