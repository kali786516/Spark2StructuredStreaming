package com.dataframe.part8.sparkDeepLearning.XGBoost

import com.google.common.collect.ImmutableMap
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{DateType, DoubleType, IntegerType, LongType, StringType, StructField, StructType}
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.feature.ChiSqSelector
import ml.dmlc.xgboost4j.scala.spark.XGBoostClassifier
import ml.dmlc.xgboost4j.scala.spark.XGBoostClassifier
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import ml.dmlc.xgboost4j.scala.Booster
import ml.dmlc.xgboost4j.java.Booster

object TelcoCustomerCurnXGBoost {

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("TelcoCustomerChurn")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val spark = SparkSession.builder().appName("Examine data about passensgers on the titanic").master("local[*]").getOrCreate()
    val sc = spark.sparkContext
    import spark.implicits._

    val rawData=spark.read.format("csv")
      .option("header","false")
      .load("sparkMLDataSets/telcocustomerCurndata.txt")
      .filter(_.get(0) != null)
      .withColumn("state",$"_c0")
      .withColumn("account_length", $"_c1".cast(DoubleType))
      .withColumn("area_code", $"_c2")
      .withColumn("phone", $"_c3")
      .withColumn("intl_plan",$"_c4")
      .withColumn("voice_mail_plan",$"_c5")
      .withColumn("number_vmail_messages",$"_c6".cast(DoubleType))
      .withColumn("total_day_minutes", $"_c7".cast(DoubleType))
      .withColumn("total_day_calls",$"_c8".cast(DoubleType))
      .withColumn("total_day_charge",$"_c9".cast(DoubleType))
      .withColumn("total_eve_minutes",$"_c10".cast(DoubleType))
      .withColumn("total_eve_calls",$"_c11".cast(DoubleType))
      .withColumn("total_eve_charge",$"_c12".cast(DoubleType))
      .withColumn("total_night_minutes",$"_c13".cast(DoubleType))
      .withColumn("total_night_calls",$"_c14".cast(DoubleType))
      .withColumn("total_night_charge",$"_c15".cast(DoubleType))
      .withColumn("total_intl_minutes",$"_c16".cast(DoubleType))
      .withColumn("total_intl_calls",$"_c17".cast(DoubleType))
      .withColumn("total_intl_charge",$"_c18".cast(DoubleType))
      .withColumn("number_customer_service_calls",$"_c19".cast(DoubleType))
      .withColumn("churned",$"_c20")


    println("-" * 100)

    println("Sample Data 10 Records")
    rawData.show(10)

    println(rawData.count())

    println("-" * 100)

    println("cast data sets")

    rawData.createOrReplaceTempView("rawDataTable")

    spark.sql("select _c0 as state,cast(_c1 as double) as account_length,_c2 as area_code,_c3 as phone,_c4 as intl_plan,_c5 as voice_mail_plan," +
      "cast(_c6 as double) as number_vmail_messages,cast(_c7 as double) as total_day_minutes,cast(_c8 as double ) as total_day_calls," +
      "cast(_c9 as double) as total_day_charge ,cast(_c10 as double) as total_eve_minutes,cast(_c11 as double) as total_eve_calls," +
      "cast(_c12 as double) as total_eve_charge,cast(_c13 as double) as total_night_minutes,cast(_c14 as double) as total_night_calls," +
      "cast(_c15 as double) as total_night_charge,cast(_c16 as double) as total_intl_minutes,cast(_c17 as double) as total_intl_calls," +
      "cast(_c18 as double) as total_intl_charge,cast(_c19 as double) as number_customer_service_calls,_c20  as churned from rawDataTable").show(10,false)

    val dataSet = spark.sql("select _c0 as state,cast(_c1 as double) as account_length,_c2 as area_code,_c3 as phone,_c4 as intl_plan,_c5 as voice_mail_plan," +
      "cast(_c6 as double) as number_vmail_messages,cast(_c7 as double) as total_day_minutes,cast(_c8 as double ) as total_day_calls," +
      "cast(_c9 as double) as total_day_charge ,cast(_c10 as double) as total_eve_minutes,cast(_c11 as double) as total_eve_calls," +
      "cast(_c12 as double) as total_eve_charge,cast(_c13 as double) as total_night_minutes,cast(_c14 as double) as total_night_calls," +
      "cast(_c15 as double) as total_night_charge,cast(_c16 as double) as total_intl_minutes,cast(_c17 as double) as total_intl_calls," +
      "cast(_c18 as double) as total_intl_charge,cast(_c19 as double) as number_customer_service_calls,_c20  as churned from rawDataTable")

    val cleanedDataSet=dataSet.na.replace("*", ImmutableMap.of("?", "null")).na.drop()

    cleanedDataSet.show(10)

    println(cleanedDataSet.count())

    println("String Indexer Step 1 ....................................................")

    val XGBoost_pipeline = new Pipeline()

    val featureAttributeArray=Array("account_length","intl_plan_indexed",
      "voice_mail_plan_indexed","number_vmail_messages","total_day_minutes",
      "total_day_calls","total_day_charge",
      "total_eve_minutes", "total_eve_calls",
      "total_eve_charge","total_night_minutes",
      "total_night_calls","total_night_charge",
      "total_intl_minutes", "total_intl_calls",
      "total_intl_charge","number_customer_service_calls")

    val churnIndexer = new StringIndexer().setInputCol("churned").setOutputCol("label")

    val indexedChurnDS = churnIndexer.fit(dataSet).transform(dataSet)

    val intlPlanIndexer = new StringIndexer().setInputCol("intl_plan").setOutputCol("intl_plan_indexed")

    val vMailPlanindexer = new StringIndexer().setInputCol("voice_mail_plan").setOutputCol("voice_mail_plan_indexed")

    val vectorAssembler = new VectorAssembler().setInputCols(featureAttributeArray).setOutputCol("features")



    val selector = new ChiSqSelector()
      .setNumTopFeatures(12)
      .setFeaturesCol("features")
      .setLabelCol("label")
      .setOutputCol("selectedFeatures")

    val xgbParam =Map[String, Any](
      "num_round" -> 5,
      "objective" -> "binary:logistic",
      "nworkers" -> 16,
      "nthreads" -> 4
    )

    val xgbClassifier = new XGBoostClassifier(xgbParam).
      setFeaturesCol("selectedFeatures").
      setLabelCol("label")


    // Define stages of transformation that will yield a trained Model when the model is fitted using training data.
    XGBoost_pipeline.setStages(Array(intlPlanIndexer,vMailPlanindexer,churnIndexer,vectorAssembler, selector, xgbClassifier))

    // Split the data into training and test sets (30% held out for testing).
    val Array(training, test) = dataSet.randomSplit(Array(0.7, 0.3))

    val xgBoostModel = XGBoost_pipeline.fit(training)

    println("I am here")

    val xgBoostPredictions = xgBoostModel.transform(test)


    def initClassificationMetrics(dataset: Dataset[_]) : BinaryClassificationMetrics = {
      val scoreAndLabels =
        dataset.select(col("probability"), col("label").cast(DoubleType)).rdd.map {
          case Row(prediction: org.apache.spark.ml.linalg.Vector, label: Double) => ( prediction(1), label)
          case Row(prediction: Double, label: Double) => (prediction, label)
        }
      val metrics = new BinaryClassificationMetrics(scoreAndLabels)
      metrics
    }

    val xgBoostMetrics = initClassificationMetrics(xgBoostPredictions)
    val aurocXG = xgBoostMetrics.areaUnderROC
    val auprcXG =  xgBoostMetrics.areaUnderPR

    println("aurocXG Area Under ROC:-"+aurocXG)
    println("auprcXG Area Under PR:-"+auprcXG)

    xgBoostModel.write.overwrite()
      .save("SparkMLModels/XgBoostCustomerChurn/telco_churn/telco_churn_xg.model_v1")


    spark.stop()
  }

}
