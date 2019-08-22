package com.dataframe.part21.RealTimeFraudDetection.fraudDetection.spark

/**
  * Created by kalit_000 on 6/2/19.
  */
import org.apache.log4j.Logger
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.sql.{DataFrame, SparkSession}

object DataBalancing {

  val logger = Logger.getLogger(getClass.getName)
  /*
  There will be more non-fruad transaction then fraund transaction. So non-fraud transactions must be balanced
  Kmeans Algorithm is used to balance non fraud transatiion.
  No. of non-fruad transactions  are balanced(reduced) to no. of fraud transaction
  */

  def createBalancedDataframe(df:DataFrame, reductionCount:Int)(implicit sparkSession:SparkSession) = {

    println("-" * 100)
    println("createBalancedDataframe input Data Frame")
    df.show(10,false)

    val kMeans = new KMeans().setK(reductionCount).setMaxIter(30)
    val kMeansModel = kMeans.fit(df)

    import sparkSession.implicits._

    val testDf = kMeansModel.clusterCenters.toList.map(v => (v, 0)).toDF("features", "label")

    println("-" * 100)
    //println(kMeansModel.clusterCenters.toList)

    println("createBalancedDataframe testDf after KMeans Model")
    testDf.show(10,false)

    kMeansModel.clusterCenters.toList.map(v => (v, 0)).toDF("features", "label")
  }




}
