package com.dataframe.part4.BankExample.BankCollectionsExamples.duplicateFunctions

/**
  * Created by kalit_000 on 5/21/19.
  */
class Priority(val value:String) {
  override def toString: String = value
}

class Status(val value:String) {
  override def toString: String = value
}

class Task(val value:String,val priority:Priority,val status:Status){
  override def toString: String = s"[P: $priority][S:$status] - $value"
}

object Priority{

  val high   = new Priority("high")
  val medium = new Priority("medium")
  val low    = new Priority("low")

  val todo       = new Status("todo")
  val inProgress = new Status("inProgress")
  val done       = new Status("done")

  val t1 = new Task("Finish Scala Module",high,inProgress)
  val t2 = new Task("Book Flight tickets",high,todo)
  val t3 = new Task("Order wine for Friday",medium,todo)
  val t4 = new Task("Test Awesome Feature",medium,inProgress)
  val t5 = new Task("Reply to Marc",low,todo)
  val t6 = new Task("Get S** donme",high,todo)

  val taks:Seq[Task] = Seq(t1,t2,t3,t4,t5,t6)

  def getTasksMatching(matcher:Task => Boolean) = {
    for (task <- taks ; if matcher(task))
      yield task
  }

  def highPriotiytTasks = (task:Task) => task.priority == high
  def lowPriotitTasks   = (task:Task) => task.priority == low
  def tasksInProgreas   = (task:Task) => task.priority == inProgress
  def taskToDo          = (task:Task) => task.priority == todo

  getTasksMatching(highPriotiytTasks) foreach(println)
  getTasksMatching(lowPriotitTasks) foreach(println)
  getTasksMatching(tasksInProgreas) foreach(println)
  getTasksMatching(taskToDo) foreach(println)

}

