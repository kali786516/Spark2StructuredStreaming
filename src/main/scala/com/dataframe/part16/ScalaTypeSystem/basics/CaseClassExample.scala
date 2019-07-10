package com.dataframe.part16.ScalaTypeSystem.basics

object CaseClassExample {

  case class Amount(value:Double,currency:String)

  val twoDollars = Amount(2,"USD")

  def checkifDollar(amount: Amount):Boolean =
    amount.currency match {
      case "USD" => println("yes its USD");true
      case _ => println("its not USD");false
    }

  checkifDollar(twoDollars)
  checkifDollar(Amount(2,"USD"))

}
