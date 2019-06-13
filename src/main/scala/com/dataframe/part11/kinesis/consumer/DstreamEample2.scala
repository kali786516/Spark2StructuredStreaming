package com.dataframe.part11.kinesis.consumer

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.RegionUtils
import com.amazonaws.services.kinesis.AmazonKinesisClient
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kinesis.KinesisUtils
import org.apache.spark.streaming.{Milliseconds, Minutes, Seconds, StreamingContext}

/**
  * Created by kalit_000 on 6/13/19.
  */
case class SensorData(id: String, currentTemp: Int, status: String)

object DstreamEample2 {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("Kinesis Read Sensor Data")
    conf.setIfMissing("spark.master", "local[*]")

    val appName      = "test123"
    val streamName   = "tweets-stream"
    val endpointUrl  = "kinesis.us-east-1.amazonaws.com"

    val credentials = new DefaultAWSCredentialsProviderChain().getCredentials()
    require(credentials != null,
      "No AWS credentials found. See http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html")
    val kinesisClient = new AmazonKinesisClient(credentials)
    kinesisClient.setEndpoint(endpointUrl)
    val numShards = kinesisClient.describeStream(streamName).getStreamDescription().getShards().size

    println(numShards)

    val numStreams = numShards

    val batchInterval = Milliseconds(2000)

    val kinesisCheckpointInterval = batchInterval

    // Get the region name from the endpoint URL to save Kinesis Client Library metadata in
    // DynamoDB of the same region as the Kinesis stream
    val regionName = "us-east-1"

    val ssc = new StreamingContext(conf, batchInterval)

    // Create the Kinesis DStreams
    val kinesisStreams = (0 until numStreams).map { i =>
      KinesisUtils.createStream(ssc, appName, streamName, endpointUrl, regionName,
        InitialPositionInStream.LATEST, kinesisCheckpointInterval, StorageLevel.MEMORY_AND_DISK_2)
    }

    // Union all the streams (in case numStreams > 1)
    val unionStreams = ssc.union(kinesisStreams)


    val sensorData = unionStreams.map { byteArray =>
      val Array(sensorId, temp, status) = new String(byteArray).split(",")
      SensorData(sensorId, temp.toInt, status)
    }


    val hotSensors: DStream[SensorData] = sensorData.filter(_.currentTemp > 100)

    hotSensors.print(1) // remove me if you want... this is just to spit out timestamps

    println(s"Sensors with Temp > 100")
    hotSensors.map { sd =>
      println(s"Sensor id ${sd.id} has temp of ${sd.currentTemp}")
    }

    // Hotest sensors over the last 20 seconds
    hotSensors.window(Seconds(20)).foreachRDD { rdd =>
      val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
      import spark.implicits._

      val hotSensorDF = rdd.toDF()
      hotSensorDF.createOrReplaceTempView("hot_sensors")

      val hottestOverTime = spark.sql("select * from hot_sensors order by currentTemp desc limit 5")
      hottestOverTime.show(2)
    }

    // To make sure data is not deleted by the time we query it interactively
    ssc.remember(Minutes(1))

    ssc.start()
    ssc.awaitTermination()


  }


}
