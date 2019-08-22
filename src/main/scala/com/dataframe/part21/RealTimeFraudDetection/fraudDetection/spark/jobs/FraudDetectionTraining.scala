package com.dataframe.part21.RealTimeFraudDetection.fraudDetection.spark.jobs

/**
  * Created by kalit_000 on 6/2/19.
  */
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.cassandra.CassandraConfig
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.config.Config
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.spark.algorithms.Algorithms
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.spark.{DataBalancing, DataReader, SparkConfig}
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.spark.pipeline.BuildPipeline
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.cassandra.CassandraConfig
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.config.Config
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.spark.algorithms.Algorithms
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.spark.pipeline.BuildPipeline
import com.dataframe.part21.RealTimeFraudDetection.fraudDetection.spark.{DataBalancing, DataReader, SparkConfig}
import org.apache.spark.ml.Pipeline


object FraudDetectionTraining extends SparkJob("Balancing Fraud & Non-Fraud Dataset"){
  def main(args: Array[String]): Unit = {

    Config.parseArgs(args)

    import sparkSession.implicits._

    println("-" * 100)
    println("Read from Cassandra tables fraud and non fraud tables")

    val fraudTransactionDF            = DataReader.readFromCassandra(CassandraConfig.keyspace , CassandraConfig.fraudTransactionTable)
      .select("cc_num", "category", "merchant", "distance", "amt", "age", "is_fraud")

    val nonFraudTransactionDF         = DataReader.readFromCassandra(CassandraConfig.keyspace, CassandraConfig.nonFraudTransactionTable)
      .select("cc_num" , "category", "merchant", "distance", "amt", "age", "is_fraud")

    val transactionDF                 = nonFraudTransactionDF.union(fraudTransactionDF)
    transactionDF.cache()

    val coloumnNames                  = List("cc_num", "category", "merchant", "distance", "amt", "age")

    val pipelineStages                = BuildPipeline.createFeaturePipeline(transactionDF.schema, coloumnNames)
    val pipeline                      = new Pipeline().setStages(pipelineStages)
    val PreprocessingTransformerModel = pipeline.fit(transactionDF)
    PreprocessingTransformerModel.save(SparkConfig.preprocessingModelPath)

    val featureDF                     = PreprocessingTransformerModel.transform(transactionDF)

    val fraudDF = featureDF
      .filter($"is_fraud" === 1)
      .withColumnRenamed("is_fraud", "label")
      .select("features", "label")

    val nonFraudDF                    = featureDF.filter($"is_fraud" === 0)
    val fraudCount                    = fraudDF.count()

    /*
      There will be very few fraud transaction and more normal transaction. Models created  from such
      imbalanced data will not have good prediction accuracy. Hence balancing the dataset. K-means is used for balancing
    */

    val balancedNonFraudDF            = DataBalancing.createBalancedDataframe(nonFraudDF, fraudCount.toInt)
    val finalfeatureDF                = fraudDF.union(balancedNonFraudDF)

    val randomForestModel             = Algorithms.randomForestClassifier(finalfeatureDF)
    randomForestModel.save(SparkConfig.modelPath)


  }

}
