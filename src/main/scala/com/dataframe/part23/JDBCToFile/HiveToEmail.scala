package com.dataframe.part23.JDBCToFile

import java.io._
import java.sql._
import java.io.FileWriter
import java.sql.DriverManager
import java.sql.SQLException

object HiveToEmail {

  /*

  def main(args: Array[String]): Unit = {

    var connection:Connection = null
    var statement:Statement = null

    try { // Register the Hive (Impala) JDBC driver
      Class.forName("org.apache.hive.jdbc.HiveDriver")
      // Open the connection

      connection = DriverManager.getConnection("jdbc:hive2://localhost:10000/default;principal=hive/_HOST@blah.NET")
      // Execute the query
      statement = connection.createStatement
      val sqlQuery = "select * from table LIMIT 5"
      val rs = statement.executeQuery(sqlQuery)
      rs.setFetchSize(1000)
      val columnnumber = rs.getMetaData.getColumnCount
      val list = new java.util.ArrayList[String]
      //PrintWriter writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("/home/bin/poc/records_2.txt")), "UTF-8"));
      val fw = new FileWriter("/home/bin/poc/records_2.txt")
      // Iterate through the result set
      while ( {
        rs.next
      }) {
        var i = 1
        while ( {
          i <= columnnumber
        }) {
          if (i < columnnumber) {
            //list.add(rs.getObject(i).toString());
            fw.append(rs.getObject(i).toString)
            fw.append(',')
            list.add(rs.getObject(i).toString)
          }
          else {
            fw.append(rs.getObject(i).toString)
            list.add(rs.getObject(i).toString)
          }

          {
            i += 1; i - 1
          }
        }
        fw.append('\n')
      }
      fw.flush()
      fw.close()
      rs.close
    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally {
      try
          if (statement != null) statement.close
      catch {
        case e: SQLException =>
          e.printStackTrace()
      }
      try
          if (connection != null) connection.close
      catch {
        case e: SQLException =>
          e.printStackTrace()
      }
    }


  }

   */

}
