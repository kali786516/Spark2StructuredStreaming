package com.dataframe.part6.EmailApplicationArchitectureExample.components

import com.dataframe.part6.EmailApplicationArchitectureExample.workflow.ApplicationConfig

/**
  * Created by kalit_000 on 5/27/19.
  */
abstract class Component {

  def init(enum:ApplicationConfig.type)


}
