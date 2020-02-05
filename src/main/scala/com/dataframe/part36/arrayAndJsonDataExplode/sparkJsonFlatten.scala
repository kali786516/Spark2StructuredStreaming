package com.dataframe.part36.arrayAndJsonDataExplode

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{ArrayType, StructType}

object sparkJsonFlatten {
  def main(args: Array[String]): Unit = {

  val logger = Logger.getLogger("FlattenTest")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val spark = SparkSession.builder()
      .appName("FlattenTest")
      //.config("spark.sql.warehouse.dir", "C:\\Temp\\hive")
      .master("local[2]")
      //.enableHiveSupport()
      .getOrCreate()
    import spark.implicits._

    val stringTest =
      """{
                               "total_count": 123,
                               "page_size": 20,
                               "another_id": "gdbfdbfdbd",
                               "sen": [{
                                "id": 123,
                                "ses_id": 12424343,
                                "columns": {
                                    "blah": "blah",
                                    "count": 1234
                                },
                                "class": {},
                                "class_timestamps": {},
                                "sentence": "spark is good"
                               }]
                            }
                             """
    val result = List(stringTest)

    val githubRdd=spark.sparkContext.makeRDD(result)
    val gitHubDF=spark.read.json(githubRdd)
    gitHubDF.show()
    gitHubDF.printSchema()


    def flattenDataframe(df: DataFrame): DataFrame = {

      val fields = df.schema.fields

      val fieldNames = fields.map(x => x.name)
      val length = fields.length

      //df.schema.fields.map(x => println(x))

      for(i <- 0 to fields.length-1){

        val field = fields(i)
        val fieldtype = field.dataType
        val fieldName = field.name

        fieldtype match {

          case arrayType: ArrayType =>

            val fieldNamesExcludingArray = fieldNames.filter(_!=fieldName)

            println("**********************fieldNamesExcludingArray**********************")

            fieldNamesExcludingArray.map(x => println(x))

            val fieldNamesAndExplode = fieldNamesExcludingArray ++ Array(s"explode_outer($fieldName) as $fieldName")

            // val fieldNamesToSelect = (fieldNamesExcludingArray ++ Array(s"$fieldName.*"))

            println("**********************fieldNamesAndExplode**********************")

            fieldNamesAndExplode.map(x => println(x))


            val explodedDf = df.selectExpr(fieldNamesAndExplode:_*)

            explodedDf.show()

            println(1)


            explodedDf.printSchema()

            return flattenDataframe(explodedDf)

          case structType: StructType =>

            val childFieldnames = structType.fieldNames.map(childname => fieldName +"."+childname)

            println("**********************childFieldnames**********************")

            df.show()

            df.printSchema()

            childFieldnames.map(x => println(x))

            val newfieldNames = fieldNames.filter(_!= fieldName) ++ childFieldnames

            println("**********************newfieldNames**********************")

            newfieldNames.map(x => println(x))


            val renamedcols = newfieldNames.map(x => (col(x.toString()).as(x.toString().replace(".", "_"))))

            println("**********************renamedcols**********************")

            renamedcols.map(x => println(x))

            val explodedf = df.select(renamedcols:_*)

            explodedf.show()

            println(2)

            return flattenDataframe(explodedf)

          case _ =>
        }
      }
      df
    }


    //val flattendedJSON = flattenDataframe(gitHubDF)
    //schema of the JSON after Flattening
    //flattendedJSON.schema

    //Output DataFrame After Flattening
    //flattendedJSON.show(false)


    var df1:DataFrame = null

    for (a <- 0 to 3){
      df1 = flattenDataframe(gitHubDF)
    }

    spark.stop()




  }

}
