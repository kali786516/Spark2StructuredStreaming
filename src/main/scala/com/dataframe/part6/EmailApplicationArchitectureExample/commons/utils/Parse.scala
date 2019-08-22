package com.dataframe.part6.EmailApplicationArchitectureExample.commons.utils

/**
  * Created by kalit_000 on 5/26/19.
  */


import com.dataframe.part6.EmailApplicationArchitectureExample.commons.exception
import com.dataframe.part6.EmailApplicationArchitectureExample.commons.exception.EmailException
import org.apache.commons.cli._
import org.slf4j.LoggerFactory

sealed trait InputArgs {

  val logger = LoggerFactory.getLogger(this.getClass)

  def commandLine(args:Array[String],options:Options): CommandLine  = {
    val parser:CommandLineParser = new BasicParser
    try {
      parser.parse(options,args)
    } catch {
      case e: ParseException => {
        val ex = new EmailException(s"INVALID_ARGUMENT: ${exception.Error.INVALID_ARGUMENT}")
        logger.error(ex.message,ex)
        throw ex
      }
    }
  }
}

case object Parse extends InputArgs
