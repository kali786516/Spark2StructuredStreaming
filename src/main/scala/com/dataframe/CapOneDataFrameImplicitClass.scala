package com.dataframe


import org.apache.spark.sql.DataFrame

object CapOneDataFrameImplicitClass {


  implicit class DataFrame(df:DataFrame){
    def someFuncName()={
      println("blah")
    }
  }
  //
  val ds = spark.createDateSet("....")
  //
  ds.someFuncName


}
