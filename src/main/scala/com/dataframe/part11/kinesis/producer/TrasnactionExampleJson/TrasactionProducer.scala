package com.dataframe.part11.kinesis.producer.TrasnactionExampleJson

/**
  * Created by kalit_000 on 6/13/19.
  */
import java.io.File
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.{Date, Random, TimeZone}

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.kinesis.AmazonKinesisClient
import com.amazonaws.services.kinesis.model.PutRecordRequest
import com.google.gson.{Gson, JsonObject}
import org.apache.commons.csv.{CSVFormat, CSVParser}


object TrasactionProducer {

  def main(args: Array[String]): Unit = {

    //val Array(stream, endpoint, recordsPerSecond, wordsPerRecord) = args

    val stream           = "creditcardTransaction"
    val endpoint         = "kinesis.us-east-1.amazonaws.com"
    val region           = "us-east-1"
    val recordsPerSecond = 10
    val wordsPerRecord   = 5
    val fileName         = "/Users/kalit_000/Downloads/Spark2StructuredStreaming/src/main/resources/data/transactions.csv"

    publishJsonMsg(fileName,endpoint,10,stream)

  }

  def getCsvIterator(fileName:String) = {
    val file      = new File(fileName)
    val csvParser = CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.DEFAULT)
    csvParser.iterator()
  }

  def publishJsonMsg(fileName:String,endpoint:String,recordsPerSecond: Int,stream:String) = {
    val gson:Gson         = new Gson
    val csvIterator       = getCsvIterator(fileName)
    val rand: Random      = new Random
    var count             = 0

    while (csvIterator.hasNext) {
      val record          = csvIterator.next()

      val obj: JsonObject = new JsonObject
      val isoFormat       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      isoFormat.setTimeZone(TimeZone.getTimeZone("IST"));
      val d               = new Date()
      val timestamp       = isoFormat.format(d)
      val unix_time       = d.getTime

      // Create the low-level Kinesis Client from the AWS Java SDK.
      val kinesisClient = new AmazonKinesisClient(new DefaultAWSCredentialsProviderChain())
      kinesisClient.setEndpoint(endpoint)

      println("Transaction Details:" + record.get(0),record.get(1),record.get(2),record.get(3),timestamp, record.get(7),record.get(8),record.get(9), record.get(10), record.get(11))

      /*
      Example Data
       Transaction Record:
       {"cc_num":"cc_num","first":"first","last":"last","trans_num":"trans_num","trans_time":"2019-06-13 01:45:24","category":"category","merchant":"merchant","amt":"amt","merch_lat":"merch_lat","merch_long":"merch_long"}
       Sent data to partition: 1 and offset: 395
       Transaction Record: {"cc_num":"180094108369013","first":"John","last":"Holland","trans_num":"80f5177be11f0bcd768e06a0b1b294c8",
       "trans_time":"2019-06-13 01:45:26","category":"personal_care","merchant":"Hills-Boyer","amt":"64","merch_lat":"39.011566","merch_long":"-119.937831"}
       Sent data to partition: 0 and offset: 395
      */

      obj.addProperty(TransactionKinesisEnum.cc_num, record.get(0))
      obj.addProperty(TransactionKinesisEnum.first, record.get(1))
      obj.addProperty(TransactionKinesisEnum.last, record.get(2))
      obj.addProperty(TransactionKinesisEnum.trans_num, record.get(3))
      obj.addProperty(TransactionKinesisEnum.trans_time, timestamp)
      //obj.addProperty(TransactionKafkaEnum.unix_time, unix_time)
      obj.addProperty(TransactionKinesisEnum.category, record.get(7))
      obj.addProperty(TransactionKinesisEnum.merchant, record.get(8))
      obj.addProperty(TransactionKinesisEnum.amt, record.get(9))
      obj.addProperty(TransactionKinesisEnum.merch_lat, record.get(10))
      obj.addProperty(TransactionKinesisEnum.merch_long, record.get(11))

      val json: String = gson.toJson(obj)

      println("Transaction Record: " + json)

      println("Transaction Record to Kinesis Format: " + json.getBytes())


      for(recordNum <- 1 to recordsPerSecond.toInt){

        // Create a partitionKey based on recordNum
        val partitionKey = s"partitionKey-$recordNum"

        // Create a PutRecordRequest with an Array[Byte] version of the data
        val putRecordRequest = new PutRecordRequest().withStreamName(stream)
          .withPartitionKey(partitionKey)
          .withData(ByteBuffer.wrap(json.getBytes()))

        // Put the record onto the stream and capture the PutRecordResult
        val putRecordResult = kinesisClient.putRecord(putRecordRequest)
      }

      // Sleep for a second
      Thread.sleep(1000)
      println("Sent " + recordsPerSecond + " records")
    }

  }

}
