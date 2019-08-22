package com.dataframe.part6.EmailApplicationArchitectureExample.commons.exception

/**
  * Created by kalit_000 on 5/26/19.
  */

import java.sql.SQLException

import scala.collection.JavaConversions._

case class EmailException(val message:String,th:Throwable = new Exception) extends Exception(message,th)

sealed trait EmailExceptionFunctions {
  def printWorkFlowException(exception: Exception): Unit = {
    if (exception.isInstanceOf[Exception]) {
      //exception.printStackTrace(System.err)
      System.err.println("-" * 150)
      //System.err.println("StackTrace: " + exception.asInstanceOf[Exception].getStackTrace)
      //System.err.println("Cause: " + exception.asInstanceOf[Exception].getCause)
      System.err.println("Message: " + exception.getMessage)
      System.err.println("-" * 150)
      var t = exception.getCause
      while ( {
        t != null
      }) {
        System.out.println("Cause: " + t)
        t = t.getCause
      }
    }
    sys.exit(1)
  }

  def printSQLException(ex:SQLException)={
    import scala.collection.JavaConversions._
    for (e <- ex) {
      if (e.isInstanceOf[SQLException]) {
        e.printStackTrace(System.err)
        System.err.println("SQLState: " + e.asInstanceOf[SQLException].getSQLState)
        System.err.println("Error Code: " + e.asInstanceOf[SQLException].getErrorCode)
        System.err.println("Message: " + e.getMessage)
        var t = ex.getCause
        while ( {
          t != null
        }) {
          System.out.println("Cause: " + t)
          t = t.getCause
        }
      }
    }
    sys.exit(1)
  }

}

case object EmailWorkFlowException extends EmailExceptionFunctions

case class CommandLineArgumentException(message:String) extends Exception(message)

case class RunModuleNotFound(message:String) extends Exception(message)

case class SqlExecutionEngineNotFound(message:String) extends Exception(message)

case class SendExcelReportException(message:String) extends Exception(message)

case class CreateExcepReportException(message:String) extends Exception(message)

case class SendHtmlReportException(message:String) extends Exception(message)

case class SendDbLoopReportException(message:String) extends Exception(message)