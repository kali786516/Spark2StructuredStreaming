package com.dataframe.part27.SparkExperiments

import org.apache.hadoop.fs.FileSystem
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}
import com.sforce.soap.partner._
import com.sforce.soap.partner.sobject._
import com.sforce.ws._
import org.apache.avro.generic.GenericRecord
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.SchemaBuilder
//import scala.annotation.target

object SalesForceGet {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val startTimeMillis=System.currentTimeMillis()

    val conf = new SparkConf()
      .setMaster(args(3))
      .setAppName("test")
      .set("spark.hadoop.validateOutputSpecs", "false")
      .set("mapreduce.output.basename","blah")
    //.set("spark.authenticate.enableSaslEncryption","true")
    //.set("spark.ssl.protocol", "TLS")
    //.set("javax.net.ssl.truststore",args(2))
    //.set("javax.net.ssl.keyStore",args(3))

    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val fs = FileSystem.get(sc.hadoopConfiguration);

    System.setProperty("http.proxyHost", args(4));
    System.setProperty("http.proxyPort", args(5));
    System.setProperty("http.protocols", args(7));

    System.setProperty("https.proxyHost", args(4));
    System.setProperty("https.proxyPort", args(5));
    System.setProperty("https.protocols", args(7));


    println("htthost "+System.getProperty("http.proxyHost"))
    println("httport "+System.getProperty("http.proxyPort"))
    println("httpprotocols "+System.getProperty("http.protocols"))

    println("httpshost "+System.getProperty("https.proxyHost"))
    println("httpsport "+System.getProperty("https.proxyPort"))
    println("httpsprotocols "+System.getProperty("https.protocols"))


    val SCHEMA: Schema = new Schema.Parser().parse(
      "{\n" +
        "  \"type\": \"record\",\n" +
        "  \"name\": \"ParquetFile\",\n" +
        "  \"fields\": [\n" +
        "   {\"name\":\"column1\",\"type\":\"string\"},\n" +
        "   {\"name\":\"column2\",\"type\":\"string\"}\n" +
        "  ]\n" +
        "}"
    )



    val userName: String = args(0)
    val password: String = args(1)
    val url: String = args(2)
    val soqlquery=args(6)
    val config:ConnectorConfig  = new ConnectorConfig
    config.setUsername(userName)
    config.setPassword(password)
    config.setAuthEndpoint(url)
    config.setProxy(args(4), args(5).toInt)
    println("config set")
    var connection: PartnerConnection = null

    connection = Connector.newConnection(config)

    println("connection set")



    /*
    def query(q: String) = new Iterator[SObject] {

      private var queryResult = connection.query(q)
      private var batch = queryResult.getRecords.toIterator

      override def hasNext: Boolean =
        if (batch.hasNext) true
        else if (queryResult.isDone) false
        else {
          queryResult = connection.queryMore(queryResult.getQueryLocator)
          batch = queryResult.getRecords.toIterator
          true
        }
      override def next(): SObject = batch.next()
    } */

    println("query ......")
    var queryResult = connection.query(soqlquery)

    println(queryResult)

    //val results = query(soqlquery)

    //println(results)

    println("print sf records")
    //printSFRecords(results)


    def printSFRecords(records: Iterator[SObject]):Unit ={
      records.zipWithIndex.foreach({ case (o, i) => println("records %i,%o",o,i)})
    }




    /*
    private def storeSFRecords(records: Iterator[SObject], target: Dataset[GenericRecord]): Unit = {
      val writer = target.newWriter()
      val schema = target.getDescriptor.getSchema
      records.zipWithIndex.foreach { case (o, i) =>
        if(i % 2000 == 0) logger.info(s"Processed $i records")
        writer.write(sfRecord2AvroRecord(o, schema))
      }
      writer.close()
    }*/

    val endTimeMillis=System.currentTimeMillis()
    val durationSeconds=(endTimeMillis-startTimeMillis)/1000

    println("Time Taken :-"+durationSeconds )

    sc.stop()
    System.exit(0)

  }


}
