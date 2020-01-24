package com.dataframe.part35.scalaFutures

import scala.concurrent.Future
// customize the execution context to use the specified number of threads
//implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(numThreads))
import scala.util.{Failure, Success}


object MultipleFutures {

  def main(args: Array[String]): Unit = {

    def sleep(time: Long): Unit = Thread.sleep(time)

    import java.util.concurrent.Executors

    import scala.concurrent.ExecutionContext
    //implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool(3)) // 35 threads
    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(3)) // 35 threads

    // (a) create the futures




    val f1 = Future { sleep(800); 1 }
    val f2 = Future { sleep(200); 2 }
    val f3 = Future { sleep(400); 3 }

    println("active count of threads " + java.lang.Thread.activeCount())

    // (b) run them simultaneously in a for-comprehension
    val result = for {
      r1 <- f1
      r2 <- f2
      r3 <- f3
    } yield (r1 + r2 + r3)

    // (c) do whatever you need to do with the result
    result.onComplete {
      case Success(x) => println(s"\nresult = $x")
      case Failure(e) => e.printStackTrace
    }

    // important for a little parallel demo: keep the jvm alive
    //sleep(3000)




  }

}
