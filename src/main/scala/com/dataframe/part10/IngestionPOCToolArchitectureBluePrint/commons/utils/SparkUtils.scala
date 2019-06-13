package com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.utils

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.types.StructType
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.exception._
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.workflow.ApplicationConfig
import java.nio.file.Files
import java.io.File
import java.util
import scala.collection.JavaConversions._
import org.apache.commons.io.FileUtils
import java.util
import java.util.{LinkedList, Queue}

import sys.process._

/**
  * Created by kalit_000 on 6/9/19.
  */
object SparkUtils {

  var sparkSession:SparkSession    = _
  var booleanValue:Boolean         = _
  var someStringValue:String       = _

  def checkLocal(mode:String):Boolean ={
    mode match {
      case "local" => true
      case  _      => false
    }
  }

  def getHadoopFileSystem(sc:SparkSession,enum:ApplicationConfig.type):FileSystem ={
    import sc.implicits._
    val fs = FileSystem.get(sc.sparkContext.hadoopConfiguration)
    fs
  }

  def getEnumArgVariableValue(enum:ApplicationConfig.type, variable:ApplicationConfig.Value):String ={
    val variableValue = enum.argValueMap.get(variable).get.toString
    variableValue
  }

  def getEnumClassVariableValue(enum:ApplicationConfig.type, variable:ApplicationConfig.Value):String ={
    val variableValue = enum.compNamesMap.get(variable).get.toString
    variableValue
  }

  def getSparkContext(appName:String,mode:String):SparkSession = {

    if(checkLocal(mode)) {
      sparkSession = SparkSession.builder().config(SparkConfig.sparkConf).config(SparkConfig.defaultSetting()).getOrCreate()
      sparkSession
    }

    if(!checkLocal(mode)) {
      sparkSession = SparkSession.builder()
        .config(SparkConfig.sparkConf).config(SparkConfig.load())
        .getOrCreate()
      sparkSession
    }
    sparkSession
  }

  def createMetadataTable(sc:SparkSession,enum:ApplicationConfig.type):String = {
    val fs                = getHadoopFileSystem(sc,enum)
    val workFlow          = getEnumArgVariableValue(enum,enum.WORKFLOWNAME)
    val metadataFileName  = getEnumArgVariableValue(enum,enum.METADATAFILENAME)

    val schemaUntyped = new StructType()
      .add("pk_id", "int")
      .add("seq_id","int")
      .add("work_flow", "string")
      .add("comp_name", "string")
      .add("comp_param", "string")
      .add("comp_value", "string")
      .add("created_by", "string")
      .add("created_date", "string")

    if(!fs.exists(new Path(metadataFileName))){
      EmailUtils.sendFailureAlertEmail("file "+metadataFileName+" doesnt exists workflow:-"+workFlow,
        "com.dataframe.part10.IngestionPOC.commons.utils.createMetaDataTable:"+workFlow.toUpperCase()+"","createMetadataTable")
      throw new MetaDataFileNotExistsException("MetaData file doesn't exists file name:-" +metadataFileName)
    }
    else {
      val metaDataDf = sc
        .read
        .format("com.databricks.spark.csv")
        .option("header", "false")
        .option("codec", "org.apache.hadoop.io.compress.GzipCodec")
        .option("escape", ":")
        .option("parserLib", "univocity")
        .option("delimiter", "~")
        .option("inferSchema", "false")
        .schema(schemaUntyped)
        .load(metadataFileName)

      metaDataDf.createOrReplaceTempView("metaData" + workFlow)

      val metaDataCount=sc.sql("select count(1) as count_of_records from metaData" + workFlow + " where work_flow='" + workFlow + "'").collect().mkString("").replace("[", "").replace("]", "")

      if (metaDataCount.toInt <= 0)
      {
        EmailUtils.sendFailureAlertEmail("0 metadata records found in file "+metadataFileName+" workflow:-"+workFlow,
          "com.dataframe.part10.IngestionPOC.commons.utils.createMetaDataTable:"+workFlow.toUpperCase()+"","createMetadataTable")
        throw new MetaDataFileRecordsZero("Records in metadata file are Zero")

      }

      sc.sql("select * from metaData" + workFlow + " where work_flow='" + workFlow + "' order by 2 asc").show(false)
      return "metaData" + workFlow
    }

  }

  def checkHdfsPathExists(fs:FileSystem,path:String,workFlow:String,component:String):Boolean ={
    if(!fs.exists(new Path(path+"/"+workFlow+"/"+component))){
      false
    }
    else {
      true
    }
  }

  def createLocalRunningFolder(path:String,workFlow:String,component:String) ={
    val checkLocalFile = new File(path+"/"+workFlow+"/"+component)
    if(!checkLocalFile.exists()){
      try {
        checkLocalFile.mkdirs()
      }
      catch {
        case e: Exception => println(e)
          IngestionWorkFlowException.printWorkFlowException(e)
          EmailUtils.sendFailureAlertEmail(e.toString,component,"createLocalRunningFolder")
          throw new CreateLocalRunningFolder("Failed to create local running folder")

      }
    }
  }

  def createHdfsRunningFolder(fs:FileSystem,path:String,workFlow:String,component:String) ={
    val hdfsPath       = new Path(path+"/"+workFlow+"/"+component)
    val hdfsFullPath   = fs.resolvePath(hdfsPath)
    if(!fs.exists(hdfsFullPath)){
      try {
        fs.mkdirs(hdfsFullPath)
      }
      catch {
        case e: Exception => println(e)
          IngestionWorkFlowException.printWorkFlowException(e)
          EmailUtils.sendFailureAlertEmail(e.toString,component,"createHdfsRunningFolder")
          throw new CreateHdfsRunningFolder("Failed to create hdfs running folder")
      }
    }
  }

  def checkLocalRunningFolderExists(path:String,workFlow:String):Boolean={
    val checkLocalFile = new File(path+"/"+workFlow)
    if(checkLocalFile.exists()) true else false
  }

  def deleteLocalRunningFolder(path:String,workFlow:String) ={
    val checkLocalFile = new File(path+"/"+workFlow)
    if(checkLocalFile.exists()){
      try {
        val p = "rm -r "+path+"/"+workFlow+"/"
            p.!
      }
      catch {
        case e: Exception => println(e)
          IngestionWorkFlowException.printWorkFlowException(e)
          EmailUtils.sendFailureAlertEmail(e.toString,"EndStep","deleteLocalRunningFolder")
          throw new DeleteLocalRunningFolder("Failed to delete local running folder")

      }
    }
  }

  def checkHdfsRunningFolderExists(fs:FileSystem,path:String,workFlow:String):Boolean={
    val hdfsPath       = new Path(path+"/"+workFlow)
    val hdfsFullPath   = fs.resolvePath(hdfsPath)
    if(fs.exists(hdfsFullPath)) true else false
  }

  def deleteHdfsRunningFolder(fs:FileSystem,path:String,workFlow:String) ={
    val hdfsPath       = new Path(path+"/"+workFlow)
    val hdfsFullPath   = fs.resolvePath(hdfsPath)
    if(fs.exists(hdfsFullPath)){
      try {
        fs.delete(hdfsFullPath)
      }
      catch {
        case e: Exception => println(e)
          IngestionWorkFlowException.printWorkFlowException(e)
          EmailUtils.sendFailureAlertEmail(e.toString,"EndStep","deleteHdfsRunningFolder")
          throw new DeleteLocalRunningFolder("Failed to delete hdfs running folder")
      }
    }
  }


  def funcCreateFolderRunner(mode:String)(fs:FileSystem,path:String,workFlow:String,component:String):Boolean ={
    if(checkLocal(mode)) createLocalRunningFolder(path,workFlow,component)
    true
    if(!checkLocal(mode)) createHdfsRunningFolder(fs,path,workFlow,component)
    true
  }

  def funcDeleteFolderRunner(mode:String)(fs:FileSystem,path:String,workFlow:String):Boolean ={
    if(checkLocal(mode)) deleteLocalRunningFolder(path,workFlow)
    true
    if(!checkLocal(mode)) deleteHdfsRunningFolder(fs,path+"/"+workFlow+"/*",workFlow)
    true
  }

  def createRunningFolder(fs:FileSystem,path:String,workFlow:String,component:String,mode:String):Boolean ={
    funcCreateFolderRunner(mode)(fs,path,workFlow,component)
  }

  def deleteRunningFolders(enum:ApplicationConfig.type,sc:SparkSession):Boolean ={
    val fs                           = getHadoopFileSystem(sc,enum)
    val mode                         = getEnumArgVariableValue(enum,enum.MODE)
    val workFlow                     = getEnumArgVariableValue(enum,enum.WORKFLOWNAME)
    val tmpFolder                    = getEnumArgVariableValue(enum,enum.TEMPFOLDER)

    funcDeleteFolderRunner(mode)(fs,tmpFolder,workFlow)
  }


  def checkIngestionCheckpointing(sc:SparkSession,enum:ApplicationConfig.type,component:String):(Boolean,String)= {
    import sc.implicits._
    val fs                           = getHadoopFileSystem(sc,enum)
    val ingestionCheckPointingFolder = getEnumArgVariableValue(enum,enum.TEMPFOLDER)
    val mode                         = getEnumArgVariableValue(enum,enum.MODE)
    val workFlow                     = getEnumArgVariableValue(enum,enum.WORKFLOWNAME)

    if(checkLocal(mode)) {
      val status = createRunningFolder(fs,ingestionCheckPointingFolder,workFlow,component,mode)
      if(status) { booleanValue = true; someStringValue = ingestionCheckPointingFolder + "/" + workFlow + "/" + component; } else {booleanValue = false; someStringValue = ingestionCheckPointingFolder + "/" + workFlow + "/" + component}
    }

    if(!checkLocal(mode)) {
      val status = createRunningFolder(fs,ingestionCheckPointingFolder,workFlow,component,mode)
      if(status) { booleanValue = true; someStringValue = ingestionCheckPointingFolder + "/" + workFlow + "/" + component; } else {booleanValue = false; someStringValue = ingestionCheckPointingFolder + "/" + workFlow + "/" + component}
    }
    (booleanValue,someStringValue)
  }

  def createCheckPointingFolder(sc:SparkSession,enum:ApplicationConfig.type,compName:String)={
    checkIngestionCheckpointing(sc,enum,compName)
  }

  def checkCheckPointingFoldersExists(sc:SparkSession,enum:ApplicationConfig.type ):Boolean = {
    val fs                           = getHadoopFileSystem(sc,enum)
    val mode                         = getEnumArgVariableValue(enum,enum.MODE)
    val workFlow                     = getEnumArgVariableValue(enum,enum.WORKFLOWNAME)
    val ingestionCheckPointingFolder = getEnumArgVariableValue(enum,enum.TEMPFOLDER)

    if(checkLocal(mode))  checkLocalRunningFolderExists(ingestionCheckPointingFolder,workFlow) else checkHdfsRunningFolderExists(fs,ingestionCheckPointingFolder,workFlow)
  }


}
