package com.dataframe.part15.AkkExamples.stockUpdateCassandraExample

import akka.actor.{ActorLogging,Props}
import akka.persistence.{PersistentActor, RecoveryCompleted}
import com.dataframe.part15.AkkExamples.stockUpdateCassandraExample._

object StockPersistenceActor {
  def props(stockId : String) = Props(new StockPersistenceActor(stockId))
}

class StockPersistenceActor(stockId:String) extends  PersistentActor with ActorLogging {
  override val persistenceId                = stockId
  var state                                 = StockHistory()
  def updateState(event: ValueAppend) = state = state.update(event)

  val receiveRecover: Receive = {
    case evt: ValueAppend  => updateState(evt)
    case RecoveryCompleted => log.info(s"Recovery completed. Current state: $state")
  }

  val receiveCommand: Receive = {
    case ValueUpdate(value) => persist(ValueAppend(StockValue(value))) (updateState)
    case "print"            => log.info(s"Current state: $state")
  }


}

