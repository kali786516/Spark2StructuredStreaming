package com.dataframe.part18.FixMessageAnalytics.kafka.producer

import java.util.Properties

import com.dataframe.part21.RealTimeFraudDetection.CreditCardProducer.TrasactionProducer.{applicationConf, props, topic}
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import com.google.common.collect.Sets
import scala.collection.mutable
import com.dataframe.part18.FixMessageAnalytics.kafka.producer.classes.Order
import scala.collection.JavaConversions._
import com.google.common.util.concurrent.RateLimiter.SleepingStopwatch

object FixGenerator {

  val props                                  = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  var producer:KafkaProducer[String, String] = new KafkaProducer[String, String](props)

  def main(args: Array[String]): Unit = {
    val openOrders:java.util.Set[Order]     = com.google.common.collect.Sets.newHashSet[Order]
    val completedOrder:java.util.Set[Order] = com.google.common.collect.Sets.newHashSet[Order]

    while (true){

      while (openOrders.size() < 100 ){
        val newOrder:Order = new Order
        val newOrderSingleFIX = newOrder.newOrderSingleFIX()
        println("newOrderSingleFIX :-"+newOrderSingleFIX)
        producer.send(new ProducerRecord[String,String]("fixmessagetopic",newOrderSingleFIX))
        openOrders.add(newOrder)
      }

      for (order <- openOrders) {
       if(!order.isComplete()){
         if(order.hasFurtherExecuted()){
           Thread.sleep(1)
           val executionReportFIX = order.nextExecutionReportFIX()
           println("executionReportFIX :-"+executionReportFIX)
           producer.send(new ProducerRecord[String,String]("fixmessagetopic",executionReportFIX))
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
