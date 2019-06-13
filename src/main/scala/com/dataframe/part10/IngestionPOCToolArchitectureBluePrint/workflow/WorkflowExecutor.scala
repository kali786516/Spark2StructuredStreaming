package com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.workflow

import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.utils.SparkUtils
import org.apache.spark.sql._



/**
  * Created by kalit_000 on 6/9/19.
  * */

object WorkflowExecutor {


  def executeWorkflowSteps(enum: ApplicationConfig.type, sparksession: SparkSession) = {

    println("step 1 Create Metadata Table")
    val metaDataTableName = SparkUtils.createMetadataTable(sparksession, enum)


    println("step 2 Create CheckPointing Folders and Execute WorkFlows")
    WorkFlowUtils.executeWorkFlows(enum,sparksession,metaDataTableName)


    println("step 3 Delete Running Folder")
    SparkUtils.deleteRunningFolders(enum,sparksession)

  }


}
