package com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.utils

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.{AmazonSimpleEmailService, AmazonSimpleEmailServiceClientBuilder}
import com.amazonaws.services.simpleemail.model._
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.exception._
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.workflow.ApplicationConfig

/**
  * Created by kalit_000 on 6/8/19.
  */

sealed trait IngestionUtilsFunctions {

  def workFlowUsage(args:Array[String],exception: Exception)={
    System.err.println("@" * 150)
    System.err.println("At least 10 argument need to be passed")
    System.err.println("Number of Arguments passed:"+args.length)
    System.err.println("Usage:" + "\n" +
      "-j (workFlow)                 :- workFlowName" + "\n" +
      "-t (toEmail)                  :- srtummal@amazon.com" + "\n" +
      "-f (fromEmail)                :- srtummal@amazon.com" + "\n" +
      "-b (busDate)                  :- 20190101" + "\n" +
      "-a (audiTable)                :- audit_db.audit_table" + "\n" +
      "-m (metadataFileName)         :- metadata_file" + "\n" +
      "-e (emailSubject)             :- email_subject" + "\n" +
      "-g (bccReciever)              :- bcc_reciever" + "\n" +
      "-t (tempFolder)               :- temp_folder" + "\n" +
      "-o (mode)                     :- mode" + "\n"
    )
    System.err.println("Exception:"+exception.getMessage)
    System.err.println("@" * 150)
    System.exit(1)
  }

  def printArgumentValues(enum:ApplicationConfig.type) ={
    println("@" * 150)
    println("List Of Arguments passed to the application")
    for (x <- enum.argValueMap.keys){
      val key=String.format("%-25s", x)
      val value=String.format("%-25s", enum.argValueMap.get(x).getOrElse("None"))
      println(key+":- "+value)
    }
    println("@" * 150)
  }


}

case object IngestionUtils extends IngestionUtilsFunctions
