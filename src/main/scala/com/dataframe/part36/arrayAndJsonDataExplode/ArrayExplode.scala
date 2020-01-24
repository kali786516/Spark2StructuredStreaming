package com.dataframe.part36.arrayAndJsonDataExplode


import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.sql.functions.{explode, expr, posexplode, when,explode_outer,posexplode_outer}

//https://sparkbyexamples.com/spark/explode-spark-array-and-map-dataframe-column/

object ArrayExplode {

  def main(args: Array[String]): Unit = {

    val logger = Logger.getLogger("HbIngestion")
    //Logger.getLogger("org").setLevel(Level.WARN)
    //Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark=SparkSession.builder()
      .appName("Test2").master("local").config("spark.driver.memory","2g").enableHiveSupport().getOrCreate()

    import spark.implicits._

    val arrayData = Seq(
      Row("James",List("Java","Scala"),Map("hair"->"black","eye"->"brown")),
      Row("Michael",List("Spark","Java",null),Map("hair"->"brown","eye"->null)),
      Row("Robert",List("CSharp",""),Map("hair"->"red","eye"->"")),
      Row("Washington",null,null),
      Row("Jefferson",List(),Map())
    )

    val arraySchema = new StructType()
      .add("name",StringType)
      .add("knownLanguages", ArrayType(StringType))
      .add("properties", MapType(StringType,StringType))

    val df = spark.createDataFrame(spark.sparkContext.parallelize(arrayData),arraySchema)
    df.printSchema()
    df.show(false)

    df.select($"name",explode($"knownLanguages"))
      .show(false)

    df.select($"name",explode($"properties"))
      .show(false)


    /*
      +-------+----+-----+
      |name   |key |value|
      +-------+----+-----+
      |James  |hair|black|
      |James  |eye |brown|
      |Michael|hair|brown|
      |Michael|eye |null |
      |Robert |hair|red  |
      |Robert |eye |     |
      +-------+----+-----+
    */


    //gets keys and values
    df.select($"name",explode_outer($"knownLanguages"))
      .show(false)

    /*
      +----------+------+
      |name      |col   |
      +----------+------+
      |James     |Java  |
      |James     |Scala |
      |Michael   |Spark |
      |Michael   |Java  |
      |Michael   |null  |
      |Robert    |CSharp|
      |Robert    |      |
      |Washington|null  |
      |Jeferson  |null  |
      +----------+------+
    */

    df.select($"name",explode_outer($"properties")).show(false)

    /*
        explode outer on map
        +----------+----+-----+
        |name      |key |value|
        +----------+----+-----+
        |James     |hair|black|
        |James     |eye |brown|
        |Michael   |hair|brown|
        |Michael   |eye |null |
        |Robert    |hair|red  |
        |Robert    |eye |     |
        |Washington|null|null |
        |Jeferson  |null|null |
        +----------+----+-----+
      */

    df.select($"name",posexplode($"knownLanguages"))
      .show(false)

    /*
      pos explode
      +-------+---+------+
      |name   |pos|col   |
      +-------+---+------+
      |James  |0  |Java  |
      |James  |1  |Scala |
      |Michael|0  |Spark |
      |Michael|1  |Java  |
      |Michael|2  |null  |
      |Robert |0  |CSharp|
      |Robert |1  |      |
      +-------+---+------+
    * */

    df.select($"name",posexplode($"properties"))
      .show(false)

    /*
    pos explode on map
    +-------+---+----+-----+
    |name   |pos|key |value|
    +-------+---+----+-----+
    |James  |0  |hair|black|
    |James  |1  |eye |brown|
    |Michael|0  |hair|brown|
    |Michael|1  |eye |null |
    |Robert |0  |hair|red  |
    |Robert |1  |eye |     |
    +-------+---+----+-----+
    * */

    df.select($"name",posexplode_outer($"knownLanguages"))
      .show(false)


    /*
      +----------+----+------+
      |name      |pos |col   |
      +----------+----+------+
      |James     |0   |Java  |
      |James     |1   |Scala |
      |Michael   |0   |Spark |
      |Michael   |1   |Java  |
      |Michael   |2   |null  |
      |Robert    |0   |CSharp|
      |Robert    |1   |      |
      |Washington|null|null  |
      |Jeferson  |null|null  |
      +----------+----+------+
    * */




  }

}
