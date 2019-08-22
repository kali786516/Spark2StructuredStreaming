package com.dataframe.part6.EmailApplicationArchitectureExample.workflow

/**
  * Created by kalit_000 on 5/26/19.
  */


import com.dataframe.part6.EmailApplicationArchitectureExample.components.SendExcelReport
import com.dataframe.part6.EmailApplicationArchitectureExample.components.ComponentUtil

object WorkflowExecutor {

  /**
    * This method executes workflow's(classes)
    * @param runModule
    * @param workFlowNode
    * @param enum
    */
  def executeWorkflow(runModule:String,workFlowNode:String,enum:ApplicationConfig.type) ={

    val sendExcelReportClassName         = enum.classNamesMap.get(enum.SENDEXCELREPORT).get.toString
    val sendDbEmailAndResultsReport      = enum.classNamesMap.get(enum.SENDDBEMAILANDRESULTSREPORT).get.toString
    val sendHtmlReport                   = enum.classNamesMap.get(enum.SENDHTMLREPORT).get.toString

    (runModule,workFlowNode) match {

      case ("excelonly",sendExcelReportClassName)             => val component = ComponentUtil.getComponent(workFlowNode)
                                                                 component.init(enum:ApplicationConfig.type)
      case ("htmlloopfromdb", sendDbEmailAndResultsReport)    => val component = ComponentUtil.getComponent(workFlowNode)
                                                                 component.init(enum:ApplicationConfig.type)
      case ("htmltableonly", sendHtmlReport)                  => val component = ComponentUtil.getComponent(workFlowNode)
                                                                 component.init(enum:ApplicationConfig.type)
      case (_,_) => println("Some Error")

    }

  }

}
