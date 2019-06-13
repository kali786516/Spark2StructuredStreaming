package com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.workflow

import java.util

import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.exception._
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.utils.{EmailUtils, SparkUtils}
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.utils.SparkUtils._
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.components.ComponentUtil
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConversions._
/**
  * Created by kalit_000 on 6/11/19.
  */
object WorkFlowUtils {

  val resultsq: util.Queue[String] = new util.LinkedList[String]

  def checkNull(compName:String):Boolean ={
    compName.toLowerCase() match {
      case "null" => true
      case _      => false
    }
  }

  def getClassName(compName:String,enum: ApplicationConfig.type):String={

    val readS3ClassName          = getEnumClassVariableValue(enum,enum.READS3)
    val sqlEtlClassName          = getEnumClassVariableValue(enum,enum.SQLETL)
    val completeAuditClassName   = getEnumClassVariableValue(enum,enum.COMPLETEAUDIT)
    val extractAndEmailClassName = getEnumClassVariableValue(enum,enum.EXTRACTANDEMAIL)
    val readKafkaClassName       = getEnumClassVariableValue(enum,enum.READKAFKA)
    val writeAzureClassName      = getEnumClassVariableValue(enum,enum.WRITEAZURE)
    val workFlow                 = getEnumArgVariableValue(enum,enum.WORKFLOWNAME)

    compName match {
      case "read_s3"           =>  readS3ClassName
      case "sql_etl"           =>  sqlEtlClassName
      case "complete_audit"    =>  completeAuditClassName
      case "extract_and_email" =>  extractAndEmailClassName
      case "read_kafka"        =>  readKafkaClassName
      case "write_azure"       =>  writeAzureClassName
      case _                   =>  EmailUtils.sendFailureAlertEmail("Component Class Name not found "+compName,
        "com.dataframe.part10.IngestionPOC.workflow.getClassName:"+workFlow.toUpperCase()+"","getClassName")
                                  throw new CompNameClassNotFoundException("Records in metadata file are Zero")
    }

  }

  def getCompNames(sparksession: SparkSession,metaDataTableName:String):util.Queue[String]={
    for ( row <- sparksession.sql("select distinct seq_id,comp_name from " + metaDataTableName + " order by 1 asc").collect())
    {
      var seqId    = row.mkString(",").split(",")(0)
      var compName = row.mkString(",").split(",")(1)

      if(!checkNull(compName)) resultsq.add(compName)
    }
    resultsq
  }

  def executeWorkFlowSeq(enum: ApplicationConfig.type, sparksession: SparkSession,comp:String,metaDataTableName:String)={

        SparkUtils.createCheckPointingFolder(sparksession, enum, comp)

        val component = ComponentUtil.getComponent(getClassName(comp, enum))
        component.init(enum: ApplicationConfig.type, metaDataTableName: String)

  }

  def executeWorkFlows(enum: ApplicationConfig.type, sparksession: SparkSession,metaDataTableName:String)={

    val compNamesQ = getCompNames(sparksession,metaDataTableName)

    for ( compName <- compNamesQ) {
        executeWorkFlowSeq(enum, sparksession, compName, metaDataTableName)
    }

  }



}
