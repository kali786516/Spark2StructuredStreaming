package com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.workflow

import org.apache.log4j.{Level, Logger}
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.exception._
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.utils.{IngestionUtils, Parse, SparkUtils}
import org.apache.commons.cli
import org.apache.commons.cli.{Option, Options}
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.exception._
import com.dataframe.part6.EmailApplicationArchitectureExample.commons.exception.CommandLineArgumentException

/**
  * Created by kalit_000 on 6/8/19.
  */

object ApplicationConfig extends Enumeration {
  type Name = Value
  val
  APPNAME,JOBNAME,
  WORKFLOWNAME,TOEMAIL,FROMEMAIL,BUSDATE,AUDITTABLE,METADATAFILENAME,
  READKAFKA,READJDBC,READSALESFORCE,READS3,READFTP,READAZURE,READGCP,
  SQLETL,
  WRITEKAFKA,WRITES3,WRITEJDBC,WRITESALESFORCE,WRITESFTP,WRITEFTP,WRITEAZURE,WRITEGCP,
  ACESSID,SECRETKEY,SOURCES3PATH,
  ETLSQL,COMPUTESTATS,SQLOPTIONS,
  EXTRACTANDEMAIL,EXTRACTANDEMAILSQL,
  COMPLETEAUDITSQL,COMPLETEAUDIT,
  SOURCEKAFAKATOPIC,SOURCEKAFKABOOTSTRAPSERVER,SOURCEKAFAKASCHEMA,
  TARGETKAFAKATOPIC,TARGETKAFKABOOTSTRAPSERVER,TARGETKAFAKASCHEMA,
  EMAILSUBJECT,BCCRECIEVER,
  TEMPFOLDER,MODE,
  ENV
  = Value

  var param         =  collection.mutable.Map[Name,String]()
  var argValueMap   =  collection.mutable.Map[Name,String]()
  var compNamesMap  =  collection.mutable.Map[Name,String]()
}

class IngestionEngineDriverApplication {
  val logMessageCosmetic = " :- "
  val logger             = Logger.getLogger("IngestionEngineDriver")
  val akkLogger          = Logger.getLogger("akka")
  val orgLogger          = Logger.getLogger("org")

  def run(args: Array[String]): Unit  = {

    akkLogger.setLevel(Level.WARN)
    orgLogger.setLevel(Level.WARN)


/*
  -w "ingestion_poc" -t "srtummal@amazon.com" -f "srtummal@amazon.com" -b "20190101" -a "audit_db.audit_table" -m "file:///Users/kalit_000/Downloads/Spark2StructuredStreaming/ingestionPocMetadata/ingestion_poc_metadata.txt" -t "/temp"

  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    "-w WORKFLOW                      :- WORK_FLOW_NAME
    "-t TO_EMAIL                      :- {TO_EMAIL} " + "\n" +
    "-f FROM_EMAIL                    :- {FROM_EMAIL}" + "\n" +
    "-b BUS_DATE                      :- {BUS_DATE}" + "\n" +
    "-a AUDIT_TABLE                   :- audit_db.audit_table" + "\n" +
    "-m METADATA_FILE_NAME            :- metadata_file" + "\n" +
    "-e EMAIL_SUBJECT                 :- email_subject"
    "-g BCC_RECIEVER                  :- email_bcc_reciever"
    "-t TEMP_FOLDER                   :- temp_folder"
    "-o MODE                          :- local,cluster"
  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

*/

    val startTime:Long          = System.currentTimeMillis()
    val jobName                 = "IngestionPOC"
        try {
      if (args.length < 10) {
        throw new CommandLineArgumentException("command line arguments which are passed to the scala application are less than 10")
       }
    } catch {
      case e: Exception => IngestionUtils.workFlowUsage(args,e)
    }
    val cmd                         = Parse.commandLine(args,setOptions())
    val workFlow                    = if(cmd.hasOption("w")) cmd.getOptionValue("w") else "workflow"
    val fromEmail                   = if(cmd.hasOption("f")) cmd.getOptionValue("f") else "fromemail"
    val toEmail                     = if(cmd.hasOption("t")) cmd.getOptionValue("t") else "toemail"
    val busDate                     = if(cmd.hasOption("b")) cmd.getOptionValue("b") else "busdate"
    val auditTable                  = if(cmd.hasOption("a")) cmd.getOptionValue("a") else "audittable"
    val metadataFileName            = if(cmd.hasOption("m")) cmd.getOptionValue("m") else "metadatafilename"
    val emailSubject                = if(cmd.hasOption("e")) cmd.getOptionValue("e") else "emailsubject"
    val bccReciever                 = if(cmd.hasOption("g")) cmd.getOptionValue("g") else "bccreciever"
    val tempFolder                  = if(cmd.hasOption("p")) cmd.getOptionValue("p") else "tempfolder"
    val mode                        = if(cmd.hasOption("o")) cmd.getOptionValue("o") else "mode"
    val env                         = if(cmd.hasOption("q")) cmd.getOptionValue("q") else "env"
    val appName                     = jobName+":"+workFlow

    ApplicationConfig.argValueMap   += ApplicationConfig.APPNAME                       -> appName
    ApplicationConfig.argValueMap   += ApplicationConfig.JOBNAME                       -> jobName
    ApplicationConfig.argValueMap   += ApplicationConfig.WORKFLOWNAME                  -> workFlow
    ApplicationConfig.argValueMap   += ApplicationConfig.FROMEMAIL                     -> fromEmail
    ApplicationConfig.argValueMap   += ApplicationConfig.TOEMAIL                       -> toEmail
    ApplicationConfig.argValueMap   += ApplicationConfig.BUSDATE                       -> busDate
    ApplicationConfig.argValueMap   += ApplicationConfig.AUDITTABLE                    -> auditTable
    ApplicationConfig.argValueMap   += ApplicationConfig.METADATAFILENAME              -> metadataFileName
    ApplicationConfig.argValueMap   += ApplicationConfig.BCCRECIEVER                   -> bccReciever
    ApplicationConfig.argValueMap   += ApplicationConfig.EMAILSUBJECT                  -> emailSubject
    ApplicationConfig.argValueMap   += ApplicationConfig.TEMPFOLDER                    -> tempFolder
    ApplicationConfig.argValueMap   += ApplicationConfig.MODE                          -> mode
    ApplicationConfig.argValueMap   += ApplicationConfig.ENV                           -> env

    ApplicationConfig.compNamesMap += ApplicationConfig.READKAFKA                      -> "ReadKafka"
    ApplicationConfig.compNamesMap += ApplicationConfig.READJDBC                       -> "ReadJDBC"
    ApplicationConfig.compNamesMap += ApplicationConfig.READSALESFORCE                 -> "ReadSalesForce"
    ApplicationConfig.compNamesMap += ApplicationConfig.READS3                         -> "ReadS3"
    ApplicationConfig.compNamesMap += ApplicationConfig.READFTP                        -> "ReadFTP"
    ApplicationConfig.compNamesMap += ApplicationConfig.READAZURE                      -> "ReadAzure"
    ApplicationConfig.compNamesMap += ApplicationConfig.READGCP                        -> "ReadGCP"
    ApplicationConfig.compNamesMap += ApplicationConfig.SQLETL                         -> "SqlETL"
    ApplicationConfig.compNamesMap += ApplicationConfig.WRITEKAFKA                     -> "WriteKafka"
    ApplicationConfig.compNamesMap += ApplicationConfig.WRITES3                        -> "WriteS3"
    ApplicationConfig.compNamesMap += ApplicationConfig.WRITEJDBC                      -> "WriteJDBC"
    ApplicationConfig.compNamesMap += ApplicationConfig.WRITESALESFORCE                -> "WriteSalesForce"
    ApplicationConfig.compNamesMap += ApplicationConfig.WRITESFTP                      -> "WriteSFTP"
    ApplicationConfig.compNamesMap += ApplicationConfig.WRITEFTP                       -> "WriteFTP"
    ApplicationConfig.compNamesMap += ApplicationConfig.WRITEAZURE                     -> "WriteAzure"
    ApplicationConfig.compNamesMap += ApplicationConfig.WRITEGCP                       -> "WriteGCP"
    ApplicationConfig.compNamesMap += ApplicationConfig.COMPLETEAUDIT                  -> "CompleteAudit"
    ApplicationConfig.compNamesMap += ApplicationConfig.EXTRACTANDEMAIL                -> "ExtractAndEmail"

    logger.info("Executing workflow" +logMessageCosmetic + ApplicationConfig.argValueMap.get(ApplicationConfig.APPNAME).get.toString)
    try {
      IngestionUtils.printArgumentValues(ApplicationConfig)
      val sparksession = SparkUtils.getSparkContext(appName,mode)
      WorkflowExecutor.executeWorkflowSteps(ApplicationConfig,sparksession)

    } catch {
      case e: Exception => println(e)
        IngestionWorkFlowException.printWorkFlowException(e)
        sys.exit(1)
    }
  }

  /**
    *
    * @return
    */
  def setOptions():Options   = {
    val options              = new cli.Options
    val workFlow             = new Option("w","workflow",true,"workflow name")
    val toEmail              = new Option("t","srtummal@amazon.com",true,"to email")
    val fromEmail            = new Option("f","srtummal@amazon.com",true,"from email")
    val busDate              = new Option("b","20190101",true,"bus date")
    val auditTable           = new Option("a","audit_db.audit_table",true,"audit table")
    val metadataFileName     = new Option("m","metadatafilename",true,"meta data file name")
    val emailSubject         = new Option("e","",true,"email subject")
    val bccReciever          = new Option("g","srtummal@amazon.com",true,"bcc reciever")
    val tempFolder           = new Option("p","/tmp",true,"temp folder")
    val mode                 = new Option("o","local",true,"local,cluster or yarn client")
    val env                  = new Option("q","env",true,"dev")

    workFlow.setRequired(true)
    fromEmail.setRequired(true)
    toEmail.setRequired(true)
    busDate.setRequired(true)
    auditTable.setRequired(true)
    metadataFileName.setRequired(true)
    tempFolder.setRequired(true)
    mode.setRequired(true)
    env.setRequired(true)

    options.addOption(workFlow)
    options.addOption(fromEmail)
    options.addOption(toEmail)
    options.addOption(busDate)
    options.addOption(auditTable)
    options.addOption(metadataFileName)
    options.addOption(emailSubject)
    options.addOption(bccReciever)
    options.addOption(tempFolder)
    options.addOption(mode)
    options.addOption(env)
    options
  }
}


object IngestionEngineDriver {

  def main(args: Array[String]): Unit = {
    new IngestionEngineDriverApplication().run(args)
  }

}
