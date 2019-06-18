package com.dataframe.part15.AkkExamples.ScalingMasterWorker

import akka.actor.{ActorRef, ActorSystem,Props}
import akka.routing.RoundRobinPool
import scala.concurrent.duration._

object ScalingOuterWorker extends App {
  val actorSystem = ActorSystem("WorkerActorSystem")
  implicit val dispatcher = actorSystem.dispatcher
  val selection = actorSystem.actorSelection(
    "akka.tcp://MasterActorSystem@127.0.0.1:2552/user/masterActor"
  )
 selection.resolveOne(3 seconds).onSuccess( {
   case masterActor: ActorRef =>
     println("we got the ActorRef for the master actor")
     val pool       = RoundRobinPool(10)
     val workerPool = actorSystem.actorOf(
       Props[WorkerActor].withRouter(pool),"workerActor")
       masterActor ! RegisterWorker(workerPool)
 })

}

object ScalingOutMaster extends App {
  val actorSystem = ActorSystem("MasterActorSyste")
  val masterActor = actorSystem.actorOf(Props[MasterActor],"masterActor")
  (1 to 100).foreach(i => {
    masterActor ! Work(s"$i")
    Thread.sleep(5000)
     // Simulates sending work to the master actor every 5 seconds
  })
}

//sbt -Dconfig.resource=application-1.conf "runMain
//      com.packt.chapter7.ScalingOutMaster"

//https://learning.oreilly.com/library/view/akka-cookbook/9781785288180/81375730-b664-40d0-ae63-de7f026de389.xhtml

