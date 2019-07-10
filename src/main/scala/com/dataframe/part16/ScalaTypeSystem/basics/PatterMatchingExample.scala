package com.dataframe.part16.ScalaTypeSystem.basics

object PatterMatchingExample extends App {

  sealed trait TaskStatus
  case object Pending extends TaskStatus
  case object InProgres extends TaskStatus
  case object Completed extends TaskStatus

  case class Task(name:String,status:TaskStatus,isPriority:Boolean)

  def manageTask(task:Task):Unit = {
    val taskStatus = task.status

    taskStatus match {
      case Pending if task.isPriority == true => println(s"${task.name}")
      case Pending => println("Task is pending")
      case InProgres => println("Task is in progress")
      case Completed => println("Task is completed")
    }
  }

  def labelPriority(task:Task,labelString:String):Task = {
    task match {
      case Task(name,status,true) => Task(s"[$labelString] - $name", status,true)
      case task: Task => task
    }
  }

  val task = Task("Decide the title",Pending,true)

  manageTask(task)
  println(labelPriority(task,"Important").name)

}
