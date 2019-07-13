package com.dataframe.part18.FixMessageAnalytics.kafka.producer.classes

import java.util.UUID

import scala.collection.JavaConversions._
import scala.util
import scala.util.Random
import scala.util.Random

class Order {

  val Symbol = List("AAPL", "MSFT", "ORCL", "VMW", "GOOG", "AMZN", "FB", "TWTR")
  private val pairDelimiter = "\001"
  private val kvDelimiter = "="

  private var clordid: String = UUID.randomUUID().toString
  private var orderid: String = UUID.randomUUID().toString
  private var orderqty: Int   = new Random().nextInt(10000)
  private var leavesqty: Int  = orderqty
  val randomSymbolIdx         = Random.nextInt(Symbol.size)
  val randomSymbol            = Symbol(randomSymbolIdx)

  def constrcutKVP(tag:String, value:Any) = {
    tag + kvDelimiter + value + pairDelimiter
  }

  def isComplete() = leavesqty == 0

  def hasFurtherExecuted() = new Random().nextInt(1000) == 0

  def newOrderSingleFIX() = {
    /*
        35: msgtype
        11: clordid
        21: handlinst
        55: symbol
        54: side
        60: transacttime
        38: orderqty
        40: ordtype
        10: checksum
   */
  var message:StringBuilder = new StringBuilder
    message.append(constrcutKVP("35","D"))
    message.append(constrcutKVP("11",clordid))
    message.append(constrcutKVP("21",2))
    message.append(constrcutKVP("55",randomSymbol))
    message.append(constrcutKVP("54",2))
    message.append(constrcutKVP("60",System.currentTimeMillis()))
    message.append(constrcutKVP("38",orderqty))
    message.append(constrcutKVP("40",2))
    message.append(constrcutKVP("10","000"))
    message.toString()
  }

  def nextExecutionReportFIX() = {
       /*
          35: msgtype
          37: orderid
          11: clordid
          17: execid
          20: exectranstype
          150: exectype
          39: ordstatus
          55: symbol
          54: side
          151: leavesqty
          14: cumqty
          6: avgpx
          60: transacttime
          10: checksum
       */

    var execRptQty = new Random().nextInt(3000);
    leavesqty -= execRptQty
    if(leavesqty < 0 ) leavesqty = 0

    var message:StringBuilder = new StringBuilder

    message.append(constrcutKVP("35", "8"))
    message.append(constrcutKVP("37", orderid))
    message.append(constrcutKVP("11", clordid))
    message.append(constrcutKVP("17", UUID.randomUUID))
    message.append(constrcutKVP("20", 0))
    message.append(constrcutKVP("150", 0))
    message.append(constrcutKVP("39", if (leavesqty == 0) 2 else 1))
    message.append(constrcutKVP("55", randomSymbol))
    message.append(constrcutKVP("54", 1))
    message.append(constrcutKVP("151", leavesqty))
    message.append(constrcutKVP("14", orderqty - leavesqty))
    message.append(constrcutKVP("6", new util.Random().nextFloat))
    message.append(constrcutKVP("60", System.currentTimeMillis))
    message.append(constrcutKVP("10", "000"))

    message.toString()
  }

}
