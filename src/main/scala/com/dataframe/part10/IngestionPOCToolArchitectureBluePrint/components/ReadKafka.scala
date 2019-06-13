package com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.components

import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.workflow.ApplicationConfig

/**
  * Created by kalit_000 on 6/11/19.
  */
class ReadKafka extends OutputComponent {

  println("I am here at ReadKafka Class")

  override def init(enum: ApplicationConfig.type, metaDataTableName: String): Unit = {

  }

}
