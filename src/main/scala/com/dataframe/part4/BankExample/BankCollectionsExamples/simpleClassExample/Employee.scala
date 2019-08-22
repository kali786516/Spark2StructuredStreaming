package com.dataframe.part4.BankExample.BankCollectionsExamples.simpleClassExample

/**
  * Created by kalit_000 on 5/21/19.
  */
abstract class Employee {
  val first:String
  val last:String
}


abstract class DepartmentEmployee extends Employee {
  private val secret = "Big Secreet!"
  val department:String
  val departmentCode:String
  val numberOfStocks:Int

  override def toString: String = "[" + first + "," + department + "," + numberOfStocks + "]"

}

class RnDEmployee(f:String,l:String) extends DepartmentEmployee {
  val first = f
  val last = l
  val department = "Research and Development"
  val departmentCode = "R&D"
  val numberOfStocks = 100
}

class MartketingEmployee(f:String,l:String) extends DepartmentEmployee {
  val first = l
  val last = l
  val department = "Marketing"
  val departmentCode = "MKT"
  val numberOfStocks = 85
}


