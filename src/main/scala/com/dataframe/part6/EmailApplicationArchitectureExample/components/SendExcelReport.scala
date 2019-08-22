package com.dataframe.part6.EmailApplicationArchitectureExample.components
/**
  * Created by kalit_000 on 5/26/19.
  */
//companion object

import java.util

import com.dataframe.part6.EmailApplicationArchitectureExample.workflow.ApplicationConfig
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.IOException
import java.io.StringReader
import java.util
import java.util.{ArrayList, Queue}

import com.dataframe.part6.EmailApplicationArchitectureExample.commons.exception.EmailWorkFlowException
import org.apache.poi.hssf.record.chart.ObjectLinkRecord
import org.apache.poi.hssf.usermodel._
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.AreaReference
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel._
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo
import com.dataframe.part6.EmailApplicationArchitectureExample.commons.exception._
import com.dataframe.part6.EmailApplicationArchitectureExample.commons.utils._
import scala.collection.JavaConversions._

class SendExcelReport extends OutputComponent{


  @throws[IOException]
  /**
    * This method query's JDBC DB and creates excep file
    */
  def createExcelOp(dbValues: util.Queue[StringBuilder],outputFileName:String,enum:ApplicationConfig.type):Int = {

    var allRowAndColData: util.ArrayList[util.ArrayList[String]] = new util.ArrayList[util.ArrayList[String]]
    var columnNumber                                             = 0
    val delimeter                                                = "~"
    var oneRowData:util.ArrayList[String]                        = null
    val records:StringBuilder                                    = new StringBuilder
    var count:Int                                                = 0
    val excelSheetName                                           = enum.argValueMap.get(enum.EXCELSHEETNAME).get.toString

    /*loop through lines*/
    for (item <- dbValues) {
      //create a bufreader
      val bufReader = new BufferedReader(new StringReader(item.toString))
      var line:String = null
      var columnHeaders: Array[String] = null
      /*loop through lines*/
      while ( { (line = bufReader.readLine) != null})
      {
        oneRowData = new util.ArrayList[String]
        count = count + 1
        if (count == 1) {
          columnHeaders = line.split("~")
          columnNumber = columnNumber + columnHeaders.length
        }
        val oneRowArray = line.split(delimeter)
        var j = 0
        while ( {
          j < oneRowArray.length
        }) {
          oneRowData.add(oneRowArray(j))

          {
            j += 1; j - 1
          }
        }
        allRowAndColData.add(oneRowData)
      }
    }

    try {
      val workBook: HSSFWorkbook                                  = new HSSFWorkbook();
      val sheet: HSSFSheet                                        = workBook.createSheet(excelSheetName);
      val font: HSSFFont                                          = workBook.createFont()



    } catch {
      case e: Exception => throw new SendExcelReportException("class SendExcelReport.sendExcelReport failed to send excel report:-" + outputFileName)
    }

    return 0
  }

  /**
    * This method send excel file via email
    * @param enum
    * @param dbValues
    * @return
    */
  def sendExcelReport(enum:ApplicationConfig.type,dbValues: util.Queue[StringBuilder]): Int = {



    val emailSubject                                             = enum.argValueMap.get(enum.EMAILSUBJECT).get.toString
    val bccReciver                                               = enum.argValueMap.get(enum.BCCRECIEVER).get.toString
    val fromEmail                                                = enum.argValueMap.get(enum.FROMEMAIL).get.toString
    val htmlEmailContent                                         = enum.argValueMap.get(enum.HTMLEMAILCONTENT).get.toString
    val outputFileName                                           = enum.argValueMap.get(enum.OUTPUTFOLDER).get.toString+ enum.argValueMap.get(enum.EXCELSHEETNAME).get.toString+".xls"
    val createExcelopReturnStatus                                = createExcelOp(dbValues,outputFileName,enum)
    var sendEmailReturnStatus                                    = 1

    createExcelopReturnStatus  match {
      case 0 => sendEmailReturnStatus = EmailUtils.sendEmailWithAttachement(enum,htmlEmailContent,outputFileName)
    }

    sendEmailReturnStatus match {
      case _ => throw new SendExcelReportException("class SendExcelReport.sendExcelReport failed to send excel report:-"+outputFileName)
      case 0 => return 0
    }

  }

  /**
    *
    * @param enum
    */
  override def init(enum:ApplicationConfig.type)={

    val dbValues:util.Queue[StringBuilder] = GetFromDB.getDBResults(enum)
    sendExcelReport(enum,dbValues)

  }

}
