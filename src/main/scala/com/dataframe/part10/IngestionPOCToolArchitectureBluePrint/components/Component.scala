package com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.components

/**
  * Created by kalit_000 on 6/11/19.
  */

import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.workflow.ApplicationConfig

abstract class Component {

  def init(enum:ApplicationConfig.type,metaDataTableName:String)

}
