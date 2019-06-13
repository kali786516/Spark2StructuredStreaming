package com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.components

import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.components.Component
import com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.commons.exception._

/**
  * Created by kalit_000 on 6/11/19.
  */
object ComponentUtil {

  def getComponent(compName:String):Component = {

    var component:Option[Component] = None

    val classLoader                 = Thread.currentThread().getContextClassLoader

    try {

      val clazz                     = classLoader.loadClass(s"com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.components.${compName}")
      component                     = Some(clazz.newInstance().asInstanceOf[Component])
    } catch {
      case _: ClassNotFoundException =>
        try {
          val clazz                 = classLoader.loadClass(compName + "$")

          component                 = Some(clazz.getField("MODULE$").get().asInstanceOf[Component])
        } catch {
          case _: ClassNotFoundException =>
            try {
              val clazz             = classLoader.loadClass(s"com.dataframe.part10.IngestionPOCToolArchitectureBluePrint.components.$compName")

              component             = Some(clazz.newInstance().asInstanceOf[Component])
            } catch {
              case _: ClassNotFoundException =>
                try {
                  val clazz         = classLoader.loadClass(compName)

                  component         = Some(clazz.newInstance().asInstanceOf[Component])
                } catch {
                  case e: ClassNotFoundException =>
                    throw IngestionException(s"Component $compName Not Found in Classpath", e)
                }

            }
        }

    }
    component.get
  }

}
