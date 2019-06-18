package com.dataframe.part14.bankingExchangeRates.api
import scala.collection.immutable
import scala.xml._
import org.json4s.JsonDSL._
//import org.json4s.native.JsonMethods._

/*
        <dependency>
            <groupId>org.json4s</groupId>
            <artifactId>json4s-native_2.11</artifactId>
            <version>3.6.6</version>
        </dependency>

 */

object Forex {
  def getExchangeRates():Map[String,Double] = {
    val apiUrl                                           = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"
    val response                                         =  requests.get(apiUrl)
    val xmlResponse                                      = XML.loadString(response.text)
    val currencyCodes: immutable.Seq[String]             = (xmlResponse \\ "@currency").map(node => node.text)
    val euroToCurrencyMultipliers: immutable.Seq[Double] = (xmlResponse \\ "@rate").map(node => node.text.toDouble)
    val currencyCodeMultipliers                          = (currencyCodes zip euroToCurrencyMultipliers).toMap
    currencyCodeMultipliers
  }

  //def getRatesAsJson():String = compact(render(getExchangeRates()))

  def main(args: Array[String]): Unit = {
    println(getExchangeRates())
  }

  /*
        <gesmes:Envelope xmlns:gesmes="http://www.gesmes.org/xml/2002-08-01" xmlns="http://www.ecb.int/vocabulary/2002-08-01/eurofxref">
      <gesmes:subject>Reference rates</gesmes:subject>
      <gesmes:Sender>
      <gesmes:name>European Central Bank</gesmes:name>
      </gesmes:Sender>
      <Cube>
      <Cube time="2019-06-14">
      <Cube currency="USD" rate="1.1265"/>
      <Cube currency="JPY" rate="121.90"/>
      <Cube currency="BGN" rate="1.9558"/>
      <Cube currency="CZK" rate="25.540"/>
      <Cube currency="DKK" rate="7.4676"/>
      <Cube currency="GBP" rate="0.89093"/>
      <Cube currency="HUF" rate="321.53"/>
      <Cube currency="PLN" rate="4.2534"/>
      <Cube currency="RON" rate="4.7233"/>
      <Cube currency="SEK" rate="10.6390"/>
      <Cube currency="CHF" rate="1.1211"/>
      <Cube currency="ISK" rate="141.50"/>
      <Cube currency="NOK" rate="9.7728"/>
      <Cube currency="HRK" rate="7.4105"/>
      <Cube currency="RUB" rate="72.3880"/>
      <Cube currency="TRY" rate="6.6427"/>
      <Cube currency="AUD" rate="1.6324"/>
      <Cube currency="BRL" rate="4.3423"/>
      <Cube currency="CAD" rate="1.5018"/>
      <Cube currency="CNY" rate="7.7997"/>
      <Cube currency="HKD" rate="8.8170"/>
      <Cube currency="IDR" rate="16128.10"/>
      <Cube currency="ILS" rate="4.0518"/>
      <Cube currency="INR" rate="78.6080"/>
      <Cube currency="KRW" rate="1333.60"/>
      <Cube currency="MXN" rate="21.6073"/>
      <Cube currency="MYR" rate="4.6981"/>
      <Cube currency="NZD" rate="1.7241"/>
      <Cube currency="PHP" rate="58.539"/>
      <Cube currency="SGD" rate="1.5403"/>
      <Cube currency="THB" rate="35.101"/>
      <Cube currency="ZAR" rate="16.6529"/>
      </Cube>
      </Cube>
      </gesmes:Envelope>
   */

}
