package com.dataframe.part16.ScalaTypeSystem.basics.demos

abstract class Employee[+T]

case class Developer(name:String) extends Employee
case class SalesAssociate(name:String) extends Employee


object DeveloperEmployeeCovarianceExample extends App {

  val employees = new scala.collection.mutable.ArrayBuffer[Employee[Developer]]()

  val developers:List[Developer] = List(Developer("kali"),Developer("Srithan"))

  val salesassociate:List[SalesAssociate] = List(SalesAssociate("sihi"),SalesAssociate("mayank"))

  def addEmployees(es:List[Employee[Developer]]):Unit = employees.appendAll(es)

  addEmployees(developers)
  addEmployees(salesassociate)

  employees foreach(println)

}
