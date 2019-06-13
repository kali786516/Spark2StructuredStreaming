package com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.exception

/**
  * Created by kalit_000 on 6/8/19.
  */
object Error extends Enumeration {
  val INVALID_ARGUMENT = Value("Invalid input arguments encountered")
  val MISSING_ARGUMENT = Value("Missing required input argument")
}
