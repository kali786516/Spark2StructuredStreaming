package com.dataframe.part34.schemaEvolution

import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession

object JsonSchemaPrasing {
  def main(args: Array[String]): Unit = {

        val logger = Logger.getLogger("FlattenTest")
    //Logger.getLogger("org").setLevel(Level.WARN)
    //Logger.getLogger("akka").setLevel(Level.WARN)

    val spark = SparkSession.builder()
      .appName("FlattenTest")
      //.config("spark.sql.warehouse.dir", "C:\\Temp\\hive")
      .master("local[2]")
      .enableHiveSupport()
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


    gitHubDF.registerTempTable("JsonTable")

    /*nested structure*/

    spark.sql("with cte as" +
      "(" +
      "select explode(sen) as senArray  from JsonTable" +
      "), cte_2 as" +
      "(" +
      "select senArray.ses_id,senArray.ses_id,senArray.columns.* from cte" +
      ")" +
      "select * from cte_2"
    ).show()


    val schemadf = spark.sql("desc JsonTable")

    schemadf.show(1000,false)

   spark.sql("with cte as (select explode(sen) as senArray  from JsonTable) select senArray.columns.* from cte").registerTempTable("senArrayTable")

    val schemadf2 = spark.sql("desc senArrayTable")

    schemadf2.show(1000,false)


    spark.stop()


  }

}
