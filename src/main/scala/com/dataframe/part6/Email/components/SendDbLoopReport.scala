package com.dataframe.part6.Email.components

import java.io.{BufferedReader, StringReader}
import java.util
import java.util.ArrayList

import com.dataframe.part6.Email.commons.exception._
import com.dataframe.part6.Email.commons.utils.EmailUtils
import com.dataframe.part6.Email.workflow.ApplicationConfig

/**
  * Created by kalit_000 on 5/28/19.
  */
class SendDbEmailAndResultsReport extends OutputComponent {

  def sendGenericHtmlFromDB(enum:ApplicationConfig.type,dbValues:util.Queue[StringBuilder]):Int = {

    var counter: Int                    = 0

    import scala.collection.JavaConversions._
    for (item <- dbValues) {
      val bufReader                     = new BufferedReader(new StringReader(item.toString))
      val oneRowData                    = null
      var line:String                   = null
      val columnHeaders                 = null
      /*loop through lines*/
      while ( {
        (line                           = bufReader.readLine) != null
      })
      {
        counter                         = counter + 1
        if (counter > 1)
        {
          val oneRowArray               = line.split("\\~")
          if (oneRowArray(0).isEmpty)
            System.out.println("Bad Record Line")

          else
          {
            val emailIdValueFromDBTable = oneRowArray(0).replaceAll("\"", "")
            val htmlValueFromDBTable    = oneRowArray(1).replaceAll("\"", "")
            var returnstatus            = 0
            val appHtml                 = htmlValueFromDBTable.toString
            returnstatus                = EmailUtils.sendEmailWithoutAttachment(enum = enum,htmlContent = htmlValueFromDBTable,emailID = emailIdValueFromDBTable)
            returnstatus match {
              case 0 => return 0
              case 1 => throw new SendDbLoopReportException("class SendExcelReport.sendExcelReport failed to send excel report")
            }
          }
        }
      }
    }

    return 0
  }

  override def init(enum:ApplicationConfig.type)={

    val dbValues:util.Queue[StringBuilder] = GetFromDB.getDBResults(enum)
    sendGenericHtmlFromDB(enum,dbValues)

  }

}
