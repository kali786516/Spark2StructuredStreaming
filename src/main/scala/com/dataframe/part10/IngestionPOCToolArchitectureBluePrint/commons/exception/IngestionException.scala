package com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.exception

import java.sql.SQLException

/**
  * Created by kalit_000 on 6/8/19.
  */
case class IngestionException(val message:String,th:Throwable = new Exception) extends Exception(message,th)

sealed trait IngestionExceptionFunctions {
  def printWorkFlowException(exception: Exception): Unit = {
    if (exception.isInstanceOf[Exception]) {
      System.err.println("-" * 150)
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

case object IngestionWorkFlowException extends IngestionExceptionFunctions

case class CommandLineArgumentException(message:String) extends Exception(message)

case class RunModuleNotFound(message:String) extends Exception(message)

case class SqlExecutionEngineNotFound(message:String) extends Exception(message)

case class ReadS3Exception(message:String) extends Exception(message)

case class ReadKafkaException(message:String) extends Exception(message)

case class ReadJDBCException(message:String) extends Exception(message)

case class ReadGCPException(message:String) extends Exception(message)

case class ReadAZUREException(message:String) extends Exception(message)

case class ReadSalesForceException(message:String) extends Exception(message)

case class SqlETLException(message:String) extends Exception(message)

case class WriteS3Exception(message:String) extends Exception(message)

case class WriteKafkaException(message:String) extends Exception(message)

case class WriteJDBCException(message:String) extends Exception(message)

case class WriteGCPException(message:String) extends Exception(message)

case class WriteAZUREException(message:String) extends Exception(message)

case class WriteSalesForceException(message:String) extends Exception(message)

case class MetaDataFileNotExistsException(message:String) extends Exception(message)

case class MetaDataFileRecordsZero(message:String) extends Exception(message)

case class DeleteLocalRunningFolder(message:String) extends Exception(message)

case class DeleteHdfsRunningFolder(message:String) extends Exception(message)

case class CreateLocalRunningFolder(message:String) extends Exception(message)

case class CreateHdfsRunningFolder(message:String) extends Exception(message)

case class CompNameClassNotFoundException(message:String) extends Exception(message)