package com.dataframe.part35.scalaFutures

object Futres2Example {

  def test(a:Long):Long = {
    a
  }

  def main(args: Array[String]): Unit = {

    val numJobs = 4
    var numThreads = 1

    // customize the execution context to use the specified number of threads
    //implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(numThreads))

    /*

    // define the tasks
    val tasks = for (i <- 1 to numJobs) yield Future {
      // do something more fancy here
      println(test(Thread.currentThread().getId))
      i
    }

    // aggregate and wait for final result
    val aggregated = Future.sequence(tasks)
    val oneToNSum = Await.result(aggregated, 1.seconds).sum

    println(aggregated)

    println(oneToNSum)

*/
  }
}
