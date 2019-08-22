package com.dataframe.part6.EmailApplicationArchitectureExample.commons.exception

/**
  * Created by kalit_000 on 5/26/19.
  */
object Error extends Enumeration {
  val INVALID_ARGUMENT = Value("Invalid input arguments encountered")
  val MISSING_ARGUMENT = Value("Missing required input argument")
}
