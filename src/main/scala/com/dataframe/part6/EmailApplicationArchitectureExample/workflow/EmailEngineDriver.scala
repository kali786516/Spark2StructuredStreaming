package com.dataframe.part6.EmailApplicationArchitectureExample.workflow

/**
  * Created by kalit_000 on 5/26/19.
  */
import java.util.logging.Logger
import com.dataframe.part6.EmailApplicationArchitectureExample.commons.utils._
import org.apache.commons.cli
import org.apache.commons.cli.{Options,Option}
import com.dataframe.part6.EmailApplicationArchitectureExample.commons.exception._
import com.dataframe.part6.EmailApplicationArchitectureExample.workflow._


object ApplicationConfig extends Enumeration {
  type Name = Value
  val APPLICATIONNAME,ENV,APPNAME,JOBNAME,SOURCEJDBCCONNECTION,FROMEMAIL,
  BCCRECIEVER,EMAILSUBJECT,SOURCEFILEDELIMETER,SOURCESQLQUERY,EXCELSHEETNAME,
  RUNMODULE,OUTPUTFOLDER,TOEMAIL,HTMLEMAILCONTENT,SQLEXECUTIONEGINE,SENDEXCELREPORT,SENDDBEMAILANDRESULTSREPORT,SENDHTMLREPORT = Value

  var param         =  collection.mutable.Map[Name,String]()
  var argValueMap   =  collection.mutable.Map[Name,String]()
  var classNamesMap =  collection.mutable.Map[Name,String]()
}

class EmailEngineDriverApplication {
  val logMessageCosmetic = " :- "
  val logger             = Logger.getLogger("EmailEngineDriver")

  /**
    *
    * @param args
    */
  def run(args: Array[String]): Unit = {

    /*
    -j "jdbc:impala://localhost.net:21050/default;user=xxxxx;password=xxxx;AuthMech=3;SSL=1;transportMode=sasl;SSLTrustStore=/jssecacerts;SSLTrustStorePwd=changeit" -f "srtummal@amazon.com" -b "srtummal@amazon.com" -s "HBEmail" -d "~" -q "select * from table" -e "test" -r "EXCELONLY" -o "/tmp" -t "srtummal@amazon.com" -h "blah blah"

    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
      "-j SourceDBJDBConnectionString   :- jdbc:impala://localhost.net:21050/default;user=xxxxx;password=xxxx;AuthMech=3;SSL=1;transportMode=sasl;SSLTrustStore=/jssecacerts;SSLTrustStorePwd=changeit" + "\n" +
      "-f Bcc reciept                   :- {YOUR_EMAIL_BCC} " + "\n" +
      "-b Subject                       :- {YOUR_EMAIL_SUBJECT}" + "\n" +
      "-d Source File Delimeter         :- ~ {YOUR_DELIMETER}" + "\n" +
      "-q SqlQuery                      :- select * from sourcetable" + "\n" +
      "-e SqlExecutionEngine            :- HIVE|IMPALA|SQLServer|Oracle" + "\n" +
      "-r Excel Sheet Name              :- Excel Sheet Name" + "\n" +
      "-o RunModule                     :- EXCELONLY|HTMLTABLEONLY|EXCELPLUSHTMLTABLE|HTMLLOOPFROMDB" + "\n" +
      "-t OutputFolder                  :- /tmp" + "\n" +
      "-h toemail                       :- {TO_EMAIL}" + "\n" +
      "-h htmleemailcontenet            :- HTMLEMAILCONTENET"
    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    */

    val startTime:Long          = System.currentTimeMillis()
    val jobName                 = "EmailEngine"
    try {
      if (args.length < 10) {
        throw new CommandLineArgumentException("command line arguments which are passed to the scala applicaion are less than 10")
       }
    } catch {
      case e: Exception => EmailUtils.workFlowUsage(args,e)
    }
    val cmd                         = Parse.commandLine(args,setOptions())
    val sourceJdbcConnection        = if(cmd.hasOption("j")) cmd.getOptionValue("j") else "sourcedbconnection"
    val fromEmail                   = if(cmd.hasOption("f")) cmd.getOptionValue("f") else "fromemail"
    val bccReciever                 = if(cmd.hasOption("b")) cmd.getOptionValue("b") else "bccemaillist"
    val emailSubject                = if(cmd.hasOption("s")) cmd.getOptionValue("s") else "emailsubject"
    val sourceFileDelimeter         = if(cmd.hasOption("d")) cmd.getOptionValue("d") else "sourcefiledelim"
    val sourceSqlQuery              = if(cmd.hasOption("q")) cmd.getOptionValue("q") else "sourcesqlquery"
    val excelSheetName              = if(cmd.hasOption("e")) cmd.getOptionValue("e") else "excelsheetname"
    val runModule                   = if(cmd.hasOption("r")) cmd.getOptionValue("r") else "runmodule"
    val outputFolder                = if(cmd.hasOption("o")) cmd.getOptionValue("o") else "outputfolder"
    val toEmail                     = if(cmd.hasOption("t")) cmd.getOptionValue("t") else "toemail"
    val htmlEmailContent            = if(cmd.hasOption("h")) cmd.getOptionValue("h") else "htmlemailcontent"
    val sqlExecutionEngine          = if(cmd.hasOption("x")) cmd.getOptionValue("x") else "sqlexecutionengine"
    val appName                     = jobName+"-"+runModule
    val lowercaseRunModule          = runModule.toLowerCase()

    ApplicationConfig.param         += ApplicationConfig.APPLICATIONNAME               -> appName
    ApplicationConfig.param         += ApplicationConfig.ENV                           -> "Dev"

    ApplicationConfig.argValueMap   += ApplicationConfig.APPNAME                       -> appName
    ApplicationConfig.argValueMap   += ApplicationConfig.JOBNAME                       -> jobName
    ApplicationConfig.argValueMap   += ApplicationConfig.SOURCEJDBCCONNECTION          -> sourceJdbcConnection
    ApplicationConfig.argValueMap   += ApplicationConfig.FROMEMAIL                     -> fromEmail
    ApplicationConfig.argValueMap   += ApplicationConfig.BCCRECIEVER                   -> bccReciever
    ApplicationConfig.argValueMap   += ApplicationConfig.EMAILSUBJECT                  -> emailSubject
    ApplicationConfig.argValueMap   += ApplicationConfig.SOURCEFILEDELIMETER           -> sourceFileDelimeter
    ApplicationConfig.argValueMap   += ApplicationConfig.SOURCESQLQUERY                -> sourceSqlQuery
    ApplicationConfig.argValueMap   += ApplicationConfig.EXCELSHEETNAME                -> excelSheetName
    ApplicationConfig.argValueMap   += ApplicationConfig.RUNMODULE                     -> runModule
    ApplicationConfig.argValueMap   += ApplicationConfig.OUTPUTFOLDER                  -> outputFolder
    ApplicationConfig.argValueMap   += ApplicationConfig.TOEMAIL                       -> toEmail
    ApplicationConfig.argValueMap   += ApplicationConfig.HTMLEMAILCONTENT              -> htmlEmailContent
    ApplicationConfig.argValueMap   += ApplicationConfig.SQLEXECUTIONEGINE             -> sqlExecutionEngine

    ApplicationConfig.classNamesMap += ApplicationConfig.SENDEXCELREPORT             -> "SendExcelReport"
    ApplicationConfig.classNamesMap += ApplicationConfig.SENDDBEMAILANDRESULTSREPORT -> "SendDbEmailAndResultsReport"
    ApplicationConfig.classNamesMap += ApplicationConfig.SENDHTMLREPORT              -> "SendHtmlReport"

    logger.info("Executing workflow" +logMessageCosmetic + ApplicationConfig.param(ApplicationConfig.APPLICATIONNAME))

    try {
      EmailUtils.printArgumentValues(ApplicationConfig)

      lowercaseRunModule match {
        case "excelonly"      => WorkflowExecutor.executeWorkflow(lowercaseRunModule,"SendExcelReport",ApplicationConfig)
        case "htmltable"      => WorkflowExecutor.executeWorkflow(lowercaseRunModule,"SendHtmlReport",ApplicationConfig)
        case "htmlloopfromdb" => WorkflowExecutor.executeWorkflow(lowercaseRunModule,"SendDbEmailAndResultsReport",ApplicationConfig)
        case _                => Thread.sleep(100)
                                 throw new RunModuleNotFound("runner class not found for argument -r (runmodule):-"+runModule)
      }
    } catch {
      case e: Exception => println(e)
        EmailWorkFlowException.printWorkFlowException(e)
        sys.exit(1)
    }
  }

  /**
    *
    * @return
    */
  def setOptions():Options   = {
    val options              = new cli.Options
    val sourceJdbcConnection = new Option("j","sourcedbconnection",true,"source jdbc connection string")
    val fromEmail            = new Option("f","fromemail",true,"from email")
    val bccReciever          = new Option("b","bccemaillist",true,"email bcc group")
    val emailSubject         = new Option("s","emailsubject",true,"email subject")
    val sourceFileDelimeter  = new Option("d","sourcefiledelim",true,"source file delimeter")
    val sourceSqlQuery       = new Option("q","sourcesqlquery",true,"source sql query")
    val excelSheetName       = new Option("e","excelsheetname",true,"excel sheet name")
    val runModule            = new Option("r","runmodule",true,"run module")
    val outputFolder         = new Option("o","outputfolder",true,"output folder")
    val toEmail              = new Option("t","toemail",true,"to email")
    val htmlEmailContent     = new Option("h","htmlemailcontent",true,"html email content")
    val sqlExecutionEngine   = new Option("x","sqlexecutionengine",true,"sql execution engine")

    sourceJdbcConnection.setRequired(true)
    fromEmail.setRequired(true)
    bccReciever.setRequired(true)
    emailSubject.setRequired(true)
    runModule.setRequired(true)
    sqlExecutionEngine.setRequired(true)

    options.addOption(sourceJdbcConnection)
    options.addOption(fromEmail)
    options.addOption(bccReciever)
    options.addOption(emailSubject)
    options.addOption(sourceFileDelimeter)
    options.addOption(sourceSqlQuery)
    options.addOption(excelSheetName)
    options.addOption(runModule)
    options.addOption(outputFolder)
    options.addOption(toEmail)
    options.addOption(htmlEmailContent)
    options.addOption(sourceJdbcConnection)
    options.addOption(sqlExecutionEngine)
    options
  }
}

object EmailEngineDriver{
  /**
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {
    new EmailEngineDriverApplication().run(args)
  }
}