package com.dataframe.part11.kinesis.producer

/**
  * Created by kalit_000 on 6/13/19.
  */

import com.amazonaws.services.kinesis.AmazonKinesisClient
import com.amazonaws.services.kinesis.model.PutRecordRequest
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import scala.util.Random
import java.nio.ByteBuffer

object KinesisSimpleProducer {

  def main(args: Array[String]): Unit = {

    val stream = "kinesissimpleproducer"
    val endpoint = "kinesis.us-east-1.amazonaws.com"
    val region = "us-east-1"
    val recordsPerSecond = 10
    val wordsPerRecord = 5

    // Generate the records and return the totals
    val totals = generate(stream, endpoint, recordsPerSecond.toInt,
      wordsPerRecord.toInt)

    // Print the array of (word, total) tuples
    println("Totals for the words sent")
    totals.foreach(println(_))

  }

  def generate(stream: String,
               endpoint: String,
               recordsPerSecond: Int,
               wordsPerRecord: Int): Seq[(String, Int)] = {

    val randomWords = List("spark", "you", "are", "my", "father")
    val totals = scala.collection.mutable.Map[String, Int]()

    // Create the low-level Kinesis Client from the AWS Java SDK.
    val kinesisClient = new AmazonKinesisClient(new DefaultAWSCredentialsProviderChain())
    kinesisClient.setEndpoint(endpoint)

    println(s"Putting records onto stream $stream and endpoint $endpoint at a rate of" +
      s" $recordsPerSecond records per second and $wordsPerRecord words per record")

    // Iterate and put records onto the stream per the given recordPerSec and wordsPerRecord
    for (i <- 1 to 10) {
      // Generate recordsPerSec records to put onto the stream

      val records = (1 to recordsPerSecond.toInt).foreach {
        recordNum =>
          // Randomly generate wordsPerRecord number of words
          val data = (1 to wordsPerRecord.toInt).map(x => {
            // Get a random index to a word
            val randomWordIdx = Random.nextInt(randomWords.size)
            val randomWord = randomWords(randomWordIdx)

            // Increment total count to compare to server counts later
            totals(randomWord) = totals.getOrElse(randomWord, 0) + 1

            randomWord
          }).mkString(" ")

          println("Sample Data:-"+data)

          println(recordNum)

          // Create a partitionKey based on recordNum
          val partitionKey = s"partitionKey-$recordNum"

          // Create a PutRecordRequest with an Array[Byte] version of the data
          val putRecordRequest = new PutRecordRequest().withStreamName(stream)
          .withPartitionKey(partitionKey)
          .withData(ByteBuffer.wrap(data.getBytes()))

          // Put the record onto the stream and capture the PutRecordResult
          val putRecordResult = kinesisClient.putRecord(putRecordRequest)
      }

      // Sleep for a second
      Thread.sleep(1000)
      println("Sent " + recordsPerSecond + " records")
    }
    // Convert the totals to (index, total) tuple
    totals.toSeq.sortBy(_._1)

  }
}
