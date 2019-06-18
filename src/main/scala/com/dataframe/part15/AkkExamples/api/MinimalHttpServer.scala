package com.dataframe.part15.AkkExamples.api

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.settings.ServerSettings
import com.typesafe.config.ConfigFactory
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import scala.io.StdIn

object MinimalHttpServer {

  // needed to run the route
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.dispatcher
  def main(args: Array[String]) {

    def route =
      pathPrefix("v1") {
        path("id" / Segment) { id =>
          get {
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Hello $id from Akka Http!</h1>"))
          } ~
            post {
              entity(as[String]) { entity =>
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<b>Thanks $id for posting                    your message <i>$entity</i></b>"))
              }
            }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ ⇒ system.terminate()) // and shutdown when done
    //MinimalHttpServer.startServer("0.0.0.0", 8088,ServerSettings(ConfigFactory.load))

    //GET
    //curl http://localhost:8088/v1/id/ALICE <h1>Hello ALICE from Akka Http!</h1>

    //POST
    //curl -X POST --data 'Akka Http is Cool' http://localhost:8088/v1/id/ALICE
  }
}


