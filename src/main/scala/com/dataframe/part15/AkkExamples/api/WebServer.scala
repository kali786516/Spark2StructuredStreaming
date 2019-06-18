package com.dataframe.part15.AkkExamples.api

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.server.HttpApp
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.http.scaladsl.server
import com.dataframe.part14.bankingExchangeRates.api.Forex

object WebServer {
  /*
  extends HttpApp
def routes:server.Route =
    path("rates") {
      get {
        complete(HttpEntity(ContentTypes.`application/json`, Forex.getRatesAsJson()))
      }
    }
}

object WebServerServerApplication  extends App {
  val port: Int = sys.env.getOrElse("PORT", "8080").toInt
  WebServer.startServer("0.0.0.0", port)
*/
}

