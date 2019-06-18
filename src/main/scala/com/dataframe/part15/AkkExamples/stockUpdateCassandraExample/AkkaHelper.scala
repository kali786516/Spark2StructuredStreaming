package com.dataframe.part15.AkkExamples.stockUpdateCassandraExample

trait AkkaHelper {
  lazy val system          = akka.actor.ActorSystem("example")
  lazy val teslaStockActor = system.actorOf(StockPersistenceActor.props(("TLSA")))
  system.actorOf(StockPersistenceActor.props("TLSA"))
}


object StockApp extends App with AkkaHelper {
  teslaStockActor ! ValueUpdate(305.12)
  teslaStockActor ! ValueUpdate(305.12)
  teslaStockActor ! "print"
  Thread.sleep(5000)
  system.terminate()
}


object StockRecoveryApp extends App with AkkaHelper {
  teslaStockActor ! ValueUpdate(305.20)
  teslaStockActor ! "print"
  Thread.sleep(2000)
  system.terminate()
}

//sbt -Dconfig.resource=application-cassandra.conf "runMain      com.packt.chapter6.StockApp"
//https://learning.oreilly.com/library/view/akka-cookbook/9781785288180/1c709020-51b3-4bd9-a79e-bc3f5a083e3a.xhtml
//sbt -Dconfig.resource=application-cassandra.conf "runMain  com.packt.chapter6.StockRecoveryApp"
