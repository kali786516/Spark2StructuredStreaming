package com.dataframe.part17.DynamoDBRead

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.dynamodb.DynamoDBItemWritable
import org.apache.hadoop.dynamodb.read.DynamoDBInputFormat
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.dynamodb.DynamoDBItemWritable
import org.apache.hadoop.io.Text

object DynamoDBReadSparkConnector {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      .setAppName("Sample")
    val spark = SparkSession.builder.config(conf).master("local[*]").getOrCreate()
    import spark.implicits._

    val credentialsProvider = new DefaultAWSCredentialsProviderChain
    val credentials = credentialsProvider.getCredentials

    val ddbConf = new JobConf(spark.sparkContext.hadoopConfiguration)
    ddbConf.set("dynamodb.output.tableName", "student")
    ddbConf.set("dynamodb.input.tableName", "student")
    ddbConf.set("dynamodb.throughput.write.percent", "1.5")
    ddbConf.set("dynamodb.endpoint", "dynamodb.us-east-1.amazonaws.com")
    ddbConf.set("dynamodb.regionid", "us-east-1")
    ddbConf.set("dynamodb.servicename", "dynamodb")
    ddbConf.set("dynamodb.throughput.read", "1")
    ddbConf.set("dynamodb.throughput.read.percent", "1")
    ddbConf.set("mapred.input.format.class", "org.apache.hadoop.dynamodb.read.DynamoDBInputFormat")
    ddbConf.set("mapred.output.format.class", "org.apache.hadoop.dynamodb.write.DynamoDBOutputFormat")
    ddbConf.set("dynamodb.awsAccessKeyId", credentials.getAWSAccessKeyId)
    ddbConf.set("dynamodb.awsSecretAccessKey", credentials.getAWSSecretKey)


    val data = spark.sparkContext.hadoopRDD(ddbConf, classOf[DynamoDBInputFormat], classOf[Text], classOf[DynamoDBItemWritable])
    data.count()

    data.toDF("studentId","lastName","address","age","firstname").show(10,false)

   spark.stop()

  }

}
