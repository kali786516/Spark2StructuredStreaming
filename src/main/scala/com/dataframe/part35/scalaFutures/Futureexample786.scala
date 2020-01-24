package com.dataframe.part35.scalaFutures

import java.util.Calendar

import scala.concurrent._

object Futureexample786 {

  def main(args: Array[String]): Unit = {

    import java.util.concurrent.Executors

    import scala.concurrent.ExecutionContext
    implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool(3))

    val rand = scala.util.Random

    for (x <- 1 to 100) {
      val f = Future {
        //val sleepTime =  rand.nextInt(1000)
        val sleepTime =  2000
        Thread.sleep(sleepTime)

        val today = Calendar.getInstance().getTime()
        println("Future: " + x + " - sleep was: " + sleepTime + " - " + today)

        println("Future: " + x + " - " + today)
        //1;
      }
    }

    Thread.sleep(10000)

  }



}
