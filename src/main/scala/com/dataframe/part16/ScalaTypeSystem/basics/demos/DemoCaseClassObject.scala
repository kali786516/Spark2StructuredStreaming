package com.dataframe.part16.ScalaTypeSystem.basics.demos

object DemoCaseClassObject extends App{

  class Amount(val value:Double,val currency:String){
    override def toString: String = s"Amount(${this.value},${this.currency})"
  }

  object AmountUtils {
    def convert(from:Amount,to:String):Amount ={
      val conversionRate = 69.45
      new Amount(from.value*conversionRate,to)
    }
  }

  import AmountUtils._

  val twoDollars = new Amount(2,"USD")

  val twoDollarsEqINT = convert(twoDollars,"INR")

  println(twoDollarsEqINT)


}
