package com.dataframe.part35.scalaFutures

import scala.concurrent.{Future => ConcurrentTask}           // rename
//import scala.concurrent.ExecutionContext.Implicits.global  // number iof threads based on cpu cores
import scala.util.{Failure, Success}

object FutureAsConcurrentTask {

  def sleep(time:Long) = { Thread.sleep(1000)}

  def test(a:Long):Long = {

    a
  }

  def main(args: Array[String]): Unit = {

    //implicit val ec = ExecutionContext.global //want to avoid this
    import scala.concurrent.ExecutionContext.Implicits.global //want to avoid this
    //implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(64))
    //implicit val ec = ExecutionContext.fromExecutor(Executors.newForkJoinThreadPool(64))

    /*
     implicit val ec = new ExecutionContext {
      val threadPool = Executors.newFixedThreadPool(1000)
      def execute(runnable: Runnable) {threadPool.submit(runnable)}
      def reportFailure(t: Throwable) {throw t}
    }
    */


    def debug(message: String): Unit = {
      val now = java.time.format.DateTimeFormatter.ISO_INSTANT
        .format(java.time.Instant.now)
        .substring(11, 23) // keep only time component
      val thread = Thread.currentThread.getName()

      println(thread)
      println(s"$now [$thread] $message")
    }
    //implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool(3)) // 35 threads
    //implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(6)) // 35 threads

    //val pool: ExecutorService = Executors.newFixedThreadPool(1)

    //import java.util.concurrent.Executors
    //Executors.newFixedThreadPool(5)

    // run some long-running task (task has type Future[Int] in this example)

    var n = 10

    var x = 0


    for (i <- 1 to n) {
      println("number of jobs "+i)
        blah()
      }


    def blah() = {
      debug("Starting Thread")

      println("number of jobs completed "+x)

      println("active count of threads " + java.lang.Thread.activeCount())


      val task = ConcurrentTask {

        test(Thread.currentThread().getId)
      }

      // whenever the task completes, execute this code
      task.onComplete {
        case Success(value) => println(s"Got the callback, value = $value")
        case Failure(e) => println(s"D'oh! The task failed: ${e.getMessage}")
        //case _ => ec.shutdownNow()
      }

      debug("ending Thread")

      //Await.result(task, Duration.Inf)
    }






    //sleep(2000)

    //debug("Finished Main")


  }

}