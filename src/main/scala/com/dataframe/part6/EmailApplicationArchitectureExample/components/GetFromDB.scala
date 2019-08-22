package com.dataframe.part6.EmailApplicationArchitectureExample.components

/**
  * Created by kalit_000 on 5/27/19.
  */

import java.sql._
import java.util
import java.util._
import com.dataframe.part6.EmailApplicationArchitectureExample.workflow.ApplicationConfig
import com.dataframe.part6.EmailApplicationArchitectureExample.commons.exception._
import com.dataframe.part6.EmailApplicationArchitectureExample.commons.utils._

sealed trait DBFunctions{

  val delim="~"
  val dbResultsQ:util.Queue[StringBuilder] = new util.LinkedList[StringBuilder];

  /**
    * This method gets results from database and places the results in a Queue of StringBuilder
    * @param enum
    * @return
    */
  def getDBResults(enum:ApplicationConfig.type):util.Queue[StringBuilder]= {

    val sqlExecutionEngine    = enum.argValueMap.get(enum.SQLEXECUTIONEGINE).get.toString
    val sqlQuery              = enum.argValueMap.get(enum.SOURCESQLQUERY).get.toString
    val sqlConnection         = enum.argValueMap.get(enum.SOURCEJDBCCONNECTION).get.toString
    val runModule             = enum.argValueMap.get(enum.RUNMODULE).get.toString
    var connection:Connection = null;
    var statement:Statement   = null;

    try {
      /*create string builder*/
      // Register the Impala JDBC driver
      sqlExecutionEngine match {
        case "hive" => Class.forName("org.apache.hive.jdbc.HiveDriver")
          connection = DriverManager.getConnection(sqlConnection)
        case "impala" => Class.forName("com.cloudera.impala.jdbc41.Driver")
          connection = DriverManager.getConnection(sqlConnection)
        case _ => throw new SqlExecutionEngineNotFound("sql execution engine which is passed not found or available for Execution Engine which was passed:-" + sqlExecutionEngine)
      }
      // Open the connection
      statement                       = connection.createStatement();
      /*execute and get the results*/
      val rs: ResultSet               = statement.executeQuery(sqlQuery);
      /*set result set fetch size*/
      rs.setFetchSize(1000);
      /*get number of columns returned back from query*/
      val columnNumber:Int            = rs.getMetaData().getColumnCount();
      /*result set*/
      val rsmd: ResultSetMetaData     = rs.getMetaData
      /*store header names in string builder and add to resultsQ*/
      var headerValues: StringBuilder = new StringBuilder

      /*loop through number of column names and add to string builder and add string builder to q*/
      /*
      var a: Int = 1
      for (x <- columnNumber) {
        if (a < columnNumber) {
          headerValues.append(rs.getMetaData.getColumnName(a) + delim)
        }
        else {
          headerValues.append(rs.getMetaData().getColumnName(a));
        }
      }*/

      var i = 1
      while ( { i <= columnNumber }) {
        /*if column is not last column 1 < 2 for example add delimeter to end*/
        if (i < columnNumber)
          headerValues.append(rs.getMetaData.getColumnName(i) + delim)
        else
          headerValues.append(rs.getMetaData.getColumnName(i))

        {
          i += 1; i - 1
        }
      }

      /*
      var i: Int = 1
      while
      ( {
        i <= columnNumber
      })
      {
        /*if column is not last column 1 < 2 for example add delimeter to end*/
        if (i < columnNumber)
        {
          headerValues.append(rs.getMetaData.getColumnName(i) + delim)
        }
        else {
          headerValues.append(rs.getMetaData.getColumnName(i))
        }

        {
          i += 1; i - 1
        }
      }*/

      /*add new line to the end of line*/
      headerValues.append('\n')

      /*add string builder values to dbResultsQ*/
      dbResultsQ.add(headerValues)

      /*check for empty result set*/
      var empty                 = true
      /*get data from result set*/
      while ( { rs.next})
      {
        empty                   = false
        val valuesStringBuilder = new StringBuilder
        /*assign a variable row*/
        var row                 = ""
        var i                   = 1
        while ( { i <= columnNumber}) {
          if (i < columnNumber)
            row                 += rs.getObject(i) + delim
          else
            row                 += rs.getObject(i)

          {
            i += 1;
            i - 1
          }
        }
        valuesStringBuilder.append(row)
        valuesStringBuilder.append('\n')
        dbResultsQ.add(valuesStringBuilder)
      }
      if (empty) {
        System.out.println("No Data in DATABASE for SQL Query <" + sqlQuery + "> for run module < " + runModule + " > ")
        val alertHtml = "Hi Support,<br>Email Engine for Run module <b>[" + runModule.toUpperCase + "]</b> failed due to no records found in database for sql query <b>[" + sqlQuery + "]</b>"
        val alertEmailSubject = "FAILURE:-[EMAIL ENGINE FOR RUN MODULE " + runModule.toUpperCase + " FAILED DUE TO NO RECORDS FOUND IN DATABASE ]"
        EmailUtils.sendEmailWithoutAttachment(ApplicationConfig, alertHtml, alertEmailSubject)
        System.exit(7)
      }
      /*close the result ser*/
      rs.close()
    } catch {
      case e:SQLException => EmailWorkFlowException.printSQLException(e)
      case e:Exception    => EmailWorkFlowException.printWorkFlowException(e)
    }
    try
        if (statement != null) statement.close()
    catch {
      case e: SQLException => EmailWorkFlowException.printSQLException(e)
    }
    try
        if (connection != null) connection.close()
    catch {
      case e: SQLException => EmailWorkFlowException.printSQLException(e)
    }

    return dbResultsQ
  }
}

case object GetFromDB extends DBFunctions

