package com.dataframe.part16.ScalaTypeSystem.basics.demos

import java.time.LocalDateTime
import java.util.UUID

sealed abstract class TransactionType

trait Online extends TransactionType
trait Offline extends TransactionType

case object Card extends Online
case object wallet extends Online
case object NetBanking extends Online

case object Cash extends Offline
case object Cheque extends Offline

case class Amount(value:Double,currency:String)

//Variance annotations
// +T means users can create a string or Int type which is a error so filtering users just to type Transaction but using <: type bound you are allowed only TransactionType
case class Transaction[+T <: TransactionType](id:UUID,amount: Amount,txnTime:LocalDateTime,txnType:T)

object TransactionUtils {
  def compensate[A <: Online](transaction:Transaction[A]):Boolean = {
    println(s"Transaction is being compensated")
    true
  }
}

object TransactionDemo extends App{
  import TransactionUtils._

  val transaction =
    Transaction(UUID.randomUUID(),Amount(2,"USD"),LocalDateTime.now(),Card)

  val offlineTxn=
    Transaction(UUID.randomUUID(),Amount(2,"USD"),LocalDateTime.now(),Cash)

  println(s"${transaction.txnType} Transaction: $transaction")

  val compensationTxn = compensate(transaction)


}
