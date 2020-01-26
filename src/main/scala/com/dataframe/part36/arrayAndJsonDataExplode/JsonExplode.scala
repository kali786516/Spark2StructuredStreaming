package com.dataframe.part36.arrayAndJsonDataExplode

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.{explode, expr, posexplode, when,explode_outer}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{ArrayType, MapType, StringType, StructType}

object JsonExplode {

  def main(args: Array[String]): Unit = {

    //Logger.getLogger("org").setLevel(Level.OFF)
    //Logger.getLogger("akka").setLevel(Level.OFF)

    val spark =SparkSession.builder
      .master("local[*]")
      .appName("Fraud Detector")
      .config("spark.driver.memory","2g")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._


    val jsoncompaines=List("""{"company":"Newco","employees":[{"firstname":"Justin","lastname":"Pihony"},{"firstname":"Jane","lastname":"Doe"}]}""",
      """{"company":"Familyco","employees":[{"firstname":"Rigel","lastname":"Pihony"},{"firstname":"Rory","lastname":"Doe"}]}""",
      """{"company":"Oldco","employees":[{"firstname":"Mary","lastname":"Louise"},{"firstname":"Joe","lastname":"Bob"}]}"""
    )

    val companiesRdd=spark.sparkContext.makeRDD(jsoncompaines)

    val companiesDF=spark.read.json(companiesRdd)

    companiesDF.show(truncate = false)

    companiesDF.printSchema()

    val employeesDFTemp=companiesDF.select($"company",explode($"employees").as("employees"))

    //companiesDF.select($"company",explode_outer($"employees").as("employees"))

    employeesDFTemp.show()

    val employeeDF=employeesDFTemp.select($"company",expr("employees.firstname as firstname"))


    employeeDF.select($"*",when($"company"==="FamilyCo","Premium").when($"company"==="Oldco","Legacy").otherwise("Standard")).show()


    companiesDF.select($"company",posexplode($"employees").as(Seq("employeePosition","employee"))).show()


  }

}
