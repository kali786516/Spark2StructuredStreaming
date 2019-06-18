package com.dataframe.part15.AkkExamples.stockUpdateCassandraExample


case class ValueUpdate(newValue:Double)
case class StockValue(value:Double, timestamp:Long = System.currentTimeMillis())
case class ValueAppend(stockValue: StockValue)

case class StockHistory (values:Vector[StockValue] = Vector.empty[StockValue]) {
  def update(evt: ValueAppend)         = copy(values :+ evt.stockValue)

  override def toString: String                    = s"$values"
}



