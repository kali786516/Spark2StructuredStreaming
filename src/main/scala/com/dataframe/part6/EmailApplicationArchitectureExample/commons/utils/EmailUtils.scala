package com.dataframe.part6.EmailApplicationArchitectureExample.commons.utils

import com.dataframe.part6.EmailApplicationArchitectureExample.workflow.EmailEngineDriverApplication

/**
  * Created by kalit_000 on 5/26/19.
  */

import com.dataframe.part6.EmailApplicationArchitectureExample.workflow.ApplicationConfig
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.Message
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import com.amazonaws.regions.Regions
import com.dataframe.part6.EmailApplicationArchitectureExample.commons.exception._


sealed trait EmailUtilsFunctions {
  def workFlowUsage(args:Array[String],exception: Exception)={
    System.err.println("@" * 150)
    System.err.println("At least 10 argument need to be passed")
    System.err.println("Number of Arguments passed:"+args.length)
    System.err.println("Usage:" + "\n" +
      "-j (jdbcConnection)           :- jdbc:impala://localhost.net:21050/default;user=xxxxx;password=xxxx;AuthMech=3;SSL=1;transportMode=sasl;SSLTrustStore=/jssecacerts;SSLTrustStorePwd=changeit" + "\n" +
      "-f (fromEmail)                :- {fromemail}" + "\n" +
      "-b (bccemaillist)             :- {bccemaillist}" + "\n" +
      "-s (emailSubject)             :- {emailsubject}" + "\n" +
      "-d (sourceFileDelim)          :- {sourcefiledelim}" + "\n" +
      "-q (sqlQuery)                 :- select * from sourcetable" + "\n" +
      "-e (excelSheetName)           :- Excel Sheet Name" + "\n" +
      "-r (runModule)                :- EXCELONLY|HTMLTABLEONLY|EXCELPLUSHTMLTABLE|HTMLLOOPFROMDB" + "\n" +
      "-o (tempFolder)               :- /tmp" + "\n" +
      "-t (toEmail)                  :- {TO_EMAIL}" + "\n" +
      "-h (htmlEmailContent)         :- HTMLEMAILCONTENET"
    )
    System.err.println("Exception:"+exception.getMessage)
    System.err.println("@" * 150)
    System.exit(1)
  }

  def printArgumentValues(enum:ApplicationConfig.type) ={
     println("@" * 150)
     println("List Of Arguments passed to the application")
    for (x <- enum.argValueMap.keys){
      //println("Args Key:-"+x+" Value:-"+enum.argValueMap.get(x).get)
      /*println("Args Key:-"+x+" Value:-"+enum.argValueMap.get(x) match {
        case Some(x) => x.toString
        case None => ""
      })*/
      //val value = ""+x+" Value:- "+enum.argValueMap.get(x).getOrElse("None")
      val key=String.format("%-25s", x)
      val value=String.format("%-25s", enum.argValueMap.get(x).getOrElse("None"))
      println(key+":- "+value)
    }
    println("@" * 150)
  }

  def sendEmailWithoutAttachment (enum:ApplicationConfig.type,htmlContent:String,emailSubject:String="",emailID:String=""):Int ={

    var emailSubject2    = ""
    var toEmailID2       = ""

    if(emailID           == ""){
      toEmailID2         = enum.argValueMap.get(enum.TOEMAIL).get.toString
    }
    else
      {
        toEmailID2       =  emailID
      }

    //val toEmail          = enum.argValueMap.get(enum.TOEMAIL).get.toString
    val fromEmail        = enum.argValueMap.get(enum.FROMEMAIL).get.toString

    if(emailSubject      =="") {
      emailSubject2      = enum.argValueMap.get(enum.EMAILSUBJECT).get.toString
    }
    else
      {
        emailSubject2    = emailSubject
      }
    val bccEmailReciever = enum.argValueMap.get(enum.BCCRECIEVER).get.toString

    try {
      val client:AmazonSimpleEmailService =
        AmazonSimpleEmailServiceClientBuilder.standard()
          // Replace US_WEST_2 with the AWS Region you're using for
          // Amazon SES.
          .withRegion(Regions.US_EAST_1).build();

      val request:SendEmailRequest = new SendEmailRequest()
        .withDestination(
          new Destination().withToAddresses(toEmailID2)
            .withBccAddresses(bccEmailReciever))
        .withMessage(new Message()
          .withBody(new Body()
            .withHtml(new Content()
              .withCharset("UTF-8").withData(htmlContent)))
          .withSubject(new Content()
            .withCharset("UTF-8").withData(emailSubject2)))
        .withSource(fromEmail)
      client.sendEmail(request);
      System.out.println("Email sent!");

    } catch {
      case ex:Exception => EmailWorkFlowException.printWorkFlowException(ex)
    }
    return 0
  }

  def sendEmailWithAttachement(enum:ApplicationConfig.type,htmlContent:String,attachmentFileWithLocation:String):Int ={
    val toEmail          = enum.argValueMap.get(enum.TOEMAIL).get.toString
    val fromEmail        = enum.argValueMap.get(enum.FROMEMAIL).get.toString
    val emailSubject     = enum.argValueMap.get(enum.EMAILSUBJECT).get.toString
    val bccEmailReciever = enum.argValueMap.get(enum.BCCRECIEVER).get.toString

    try {
      val client:AmazonSimpleEmailService =
        AmazonSimpleEmailServiceClientBuilder.standard()
          // Replace US_WEST_2 with the AWS Region you're using for
          // Amazon SES.
          .withRegion(Regions.US_EAST_1).build();


      //https://stackoverflow.com/questions/7963439/example-of-sending-an-email-with-attachment-via-amazon-in-java

    } catch {
      case ex:Exception => EmailWorkFlowException.printWorkFlowException(ex)
    }
    return 0
  }

}

case object EmailUtils  extends EmailUtilsFunctions
