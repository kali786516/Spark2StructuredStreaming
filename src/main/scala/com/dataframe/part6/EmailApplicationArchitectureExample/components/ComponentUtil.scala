package com.dataframe.part6.EmailApplicationArchitectureExample.components

/**
  * Created by kalit_000 on 5/27/19.
  */

import com.dataframe.part6.EmailApplicationArchitectureExample.commons.exception._
import com.dataframe.part6.EmailApplicationArchitectureExample.components.Component
import com.dataframe.part6.EmailApplicationArchitectureExample.components.SendExcelReport
import java.util.Properties

object ComponentUtil {

  /**
    * This method takes in component name and returns a component class loader class
    * @param compName
    * @return
    */
  def getComponent(compName:String):Component ={

    var component:Option[Component] = None

    val classLoader                 = Thread.currentThread().getContextClassLoader

    try {

      val clazz                     = classLoader.loadClass(s"com.dataframe.part6.Email.components.${compName}")
      component                     = Some(clazz.newInstance().asInstanceOf[Component])
    } catch {
      case _: ClassNotFoundException =>
        try {
          val clazz                 = classLoader.loadClass(compName + "$")

          component                 = Some(clazz.getField("MODULE$").get().asInstanceOf[Component])
        } catch {
          case _: ClassNotFoundException =>
            try {
              val clazz             = classLoader.loadClass(s"com.dataframe.part6.Email.components.$compName")

              component             = Some(clazz.newInstance().asInstanceOf[Component])
            } catch {
              case _: ClassNotFoundException =>
                try {
                  val clazz         = classLoader.loadClass(compName)

                  component         = Some(clazz.newInstance().asInstanceOf[Component])
                } catch {
                  case e: ClassNotFoundException =>
                    throw EmailException(s"Component $compName Not Found in Classpath", e)
                }

            }
        }

    }
    component.get

  }

}
