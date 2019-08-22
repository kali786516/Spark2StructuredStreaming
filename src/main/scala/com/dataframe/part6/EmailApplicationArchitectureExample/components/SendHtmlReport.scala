package com.dataframe.part6.EmailApplicationArchitectureExample.components

import java.io.{BufferedReader, StringReader}
import java.util
import java.util.ArrayList

import com.dataframe.part6.EmailApplicationArchitectureExample.commons.exception._
import com.dataframe.part6.EmailApplicationArchitectureExample.commons.utils.EmailUtils
import com.dataframe.part6.EmailApplicationArchitectureExample.workflow.ApplicationConfig

import scala.collection.JavaConversions._

/**
  * Created by kalit_000 on 5/28/19.
  */
class SendHtmlReport extends OutputComponent {

  var commonHtmlBuilder                      = new StringBuilder

  def sendGenericHtmlReport(enum:ApplicationConfig.type,dbValues:util.Queue[StringBuilder]):Int ={
    /*html string*/
    commonHtmlBuilder.append("<!DOCTYPE html> <html> <head> <style> table {     font-family: arial, sans-serif;     border-collapse: collapse;     width: 100%; }  td, th {     border: 1px solid #dddddd;     text-align: left;     padding: 8px; }  tr:nth-child(even) {     background-color: #dddddd; } </style> </head> <body>    <table>     <tr>         ")
    var count                                = 0


    for (item <- dbValues)
    {
      val bufReader                          = new BufferedReader(new StringReader(item.toString))
      val oneRowData: util.ArrayList[String] = null
      var line:String                        = null
      val headerValues                       = new StringBuilder
      //loop through lines
      //String[] columnHeaders;
      /*loop through lines*/
      while ({(line                          = bufReader.readLine) != null })
      {
        val oneRowArray                      = line.split("\\~")
        count                                = count + 1
        if (count                            == 1) {
          var j = 0
          while ({ j < oneRowArray.length })
          {
            //headers
            //System.out.println(oneRowArray[j]);
            commonHtmlBuilder.append("<th style=background-color:rgb(0,161,234)>" + oneRowArray(j) + "</th>")
            {
              j += 1; j - 1
            }
          }
          commonHtmlBuilder.append("</tr>  ")
          commonHtmlBuilder.append("<tr>")
        }
        else {
          //String oneRowArray[] = line.split("~");
          var j = 0
          while ( {
            j < oneRowArray.length
          }) {
            commonHtmlBuilder.append("<td style=background-color:rgb(242,242,242)>" + oneRowArray(j) + "</td>")
            {
              j += 1; j - 1
            }
          }
          commonHtmlBuilder.append("</tr>")
        }
      }
    }
    commonHtmlBuilder.append("  </table> </body> </html>")

    val htmlReport            = new StringBuilder
    htmlReport.append(enum.argValueMap.get(enum.HTMLEMAILCONTENT).get.toString)
    htmlReport.append("<br><br>")
    htmlReport.append(commonHtmlBuilder)
    val finalHtml              = htmlReport.toString
    var sendEmailReturnStatus  = 1
    sendEmailReturnStatus      = EmailUtils.sendEmailWithoutAttachment(enum,finalHtml)
    sendEmailReturnStatus match {
      case 0 => return 0
      case 1 => throw new SendHtmlReportException("class SendExcelReport.sendExcelReport failed to send excel report")
    }
    return 0
  }


  override def init(enum:ApplicationConfig.type)={

    val dbValues:util.Queue[StringBuilder] = GetFromDB.getDBResults(enum)
    sendGenericHtmlReport(enum,dbValues)

  }
}
