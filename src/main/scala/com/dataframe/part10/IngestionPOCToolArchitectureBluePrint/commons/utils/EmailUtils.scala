package com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.utils

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.{AmazonSimpleEmailService, AmazonSimpleEmailServiceClientBuilder}
import com.amazonaws.services.simpleemail.model._
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.exception.IngestionException
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.utils.SparkUtils.getEnumArgVariableValue
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.workflow.ApplicationConfig
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.exception._


/**
  * Created by kalit_000 on 6/11/19.
  */

sealed trait EmailUtilsFunctions {

  def sendFailureAlertEmail(failureReason: String,compName:String,step:String):Int ={

    var reason = ""
    if (failureReason.length >= 200) {
      reason = failureReason.substring(0, 200)
    }
    else {
      reason = failureReason
    }

    val enum             = ApplicationConfig

    val workFlow         = getEnumArgVariableValue(enum,enum.WORKFLOWNAME)
    val env              = getEnumArgVariableValue(enum,enum.ENV)
    val toEmail          = getEnumArgVariableValue(enum,enum.TOEMAIL)
    val fromEmail        = getEnumArgVariableValue(enum,enum.FROMEMAIL)
    val bccReciver       = getEnumArgVariableValue(enum,enum.BCCRECIEVER)
    val busDate          = getEnumArgVariableValue(enum,enum.BUSDATE)

    val emailSubject     = "Ingestion POC JOB ALERT [FAILURE]:- SPARK INGESTION FAILED FOR WORK_FLOW [" + workFlow.toUpperCase() + "] IN ENV [" + env.toUpperCase() + "]"

    val emailContent     = "Hi AZ,<br /><br />" +
      "<!DOCTYPE html> <html> <head> <style> table " +
      "{     font-family: arial, sans-serif;     border-collapse: collapse;     width: 100%; }  td, th " +
      "{     border: 1px solid #dddddd;     text-align: left;     padding: 8px; }  tr:nth-child(even) " +
      "{     background-color: #dddddd; } </style> </head> <body>    <table>     <tr>     <th>work_flow</th> <th>comp_name</th>" +
      "<th>bus_date</th><th>step</th><th>failure_reason</th> </tr>  <tr>    <td>" + workFlow + "</td><td>" + compName + "</td><td>" + busDate + "</td>" +
      "<td>" + step + "</td><td><p style=\"background-color:Tomato;\">" + reason + "</td> </tr></table></body></html><br /> Warmest Regards,<br /> Spark Ingestion POC team  "

    try {
      val client:AmazonSimpleEmailService =
        AmazonSimpleEmailServiceClientBuilder.standard()
          // Replace US_WEST_2 with the AWS Region you're using for
          // Amazon SES.
          .withRegion(Regions.US_EAST_1).build();

      val request:SendEmailRequest = new SendEmailRequest()
        .withDestination(
          new Destination().withToAddresses(toEmail)
            .withBccAddresses(bccReciver))
        .withMessage(new Message()
          .withBody(new Body()
            .withHtml(new Content()
              .withCharset("UTF-8").withData(emailContent)))
          .withSubject(new Content()
            .withCharset("UTF-8").withData(emailSubject)))
        .withSource(fromEmail)
      client.sendEmail(request);
      System.out.println("Email sent!");

    } catch {
      case ex:Exception => IngestionWorkFlowException.printWorkFlowException(ex)
    }
    return 0
  }
}
case object EmailUtils extends EmailUtilsFunctions
