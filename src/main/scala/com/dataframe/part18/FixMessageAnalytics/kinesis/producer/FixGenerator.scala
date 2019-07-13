package com.dataframe.part18.FixMessageAnalytics.kinesis.producer

import java.nio.ByteBuffer

import com.dataframe.part18.FixMessageAnalytics.kafka.producer.classes.Order

import scala.collection.JavaConversions._
import com.amazonaws.services.kinesis.AmazonKinesisClient
import com.amazonaws.services.kinesis.model.PutRecordRequest
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain

object FixGenerator {

  def main(args: Array[String]): Unit = {
    val stream = "fixmessagetopic"
    val endpoint = "kinesis.us-east-1.amazonaws.com"
    val region = "us-east-1"
    val recordsPerSecond = 25

    val openOrders:java.util.Set[Order]     = com.google.common.collect.Sets.newHashSet[Order]
    val completedOrder:java.util.Set[Order] = com.google.common.collect.Sets.newHashSet[Order]

    // Create the low-level Kinesis Client from the AWS Java SDK.
    val kinesisClient = new AmazonKinesisClient(new DefaultAWSCredentialsProviderChain())
    kinesisClient.setEndpoint(endpoint)

    while (true){

      while (openOrders.size() < 100 ){
        val newOrder:Order = new Order
        val newOrderSingleFIX = newOrder.newOrderSingleFIX()

        println("newOrderSingleFIX :-"+newOrderSingleFIX)
        for (recordNum <- 1 to recordsPerSecond.toInt) {

          // Create a partitionKey based on recordNum
          val partitionKey = s"partitionKey-$recordNum"

          // Create a PutRecordRequest with an Array[Byte] version of the data
          val putRecordRequest = new PutRecordRequest().withStreamName(stream)
            .withPartitionKey(partitionKey)
            .withData(ByteBuffer.wrap(newOrderSingleFIX.getBytes()))

          // Put the record onto the stream and capture the PutRecordResult
          val putRecordResult = kinesisClient.putRecord(putRecordRequest)
        }
        openOrders.add(newOrder)
      }

      for (order <- openOrders) {
        if(!order.isComplete()){
          if(order.hasFurtherExecuted()){
            Thread.sleep(1)
            val executionReportFIX = order.nextExecutionReportFIX()
            println("executionReportFIX :-"+executionReportFIX)
            for (recordNum <- 1 to recordsPerSecond.toInt) {

              // Create a partitionKey based on recordNum
              val partitionKey = s"partitionKey-$recordNum"

              // Create a PutRecordRequest with an Array[Byte] version of the data
              val putRecordRequest = new PutRecordRequest().withStreamName(stream)
                .withPartitionKey(partitionKey)
                .withData(ByteBuffer.wrap(executionReportFIX.getBytes()))

              // Put the record onto the stream and capture the PutRecordResult
              val putRecordResult = kinesisClient.putRecord(putRecordRequest)
            }
          }
        }
        else {
          completedOrder.add(order)
        }
      }

      for (completedOrder <- completedOrder) {
        openOrders.remove(completedOrder)
      }
      completedOrder.clear()

    }




  }

}
