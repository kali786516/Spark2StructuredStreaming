package com.dataframe.part16.ScalaTypeSystem.basics

object SomeExampleAndEither extends App {

  import scala.util.control.NonFatal

  def saferNumber(possibleNumber:String):Option[Int] = {
    try {
      Some(possibleNumber.toInt)
    } catch {
      case NonFatal(_) => println(s"Cound not convert $possibleNumber to a Number");None
    }
  }

  def double(number:Int):Int = number * 2

  val number = saferNumber("12").get
  println(s"Converted number is $number")

  saferNumber("12") match {
    case Some(number) => println(s"The number:$number")
    case None => println("Could not convert this number")
  }

  def saferNumber2(possibleNumber:String):Either[String,Int] = {
    try {
      Right(possibleNumber.toInt)
    } catch {
      case e:Throwable =>
        Left(s"${e.getMessage}")
    }
  }

  saferNumber2("12") match {
    case Right(number) => println(s"The number:$number")
    case Left(exceptionMsg)  => println(s"Something went wrong:$exceptionMsg")
   }

}
