package com.dataframe.part27.SparkExperiments

//import config.S3Settings
import org.apache.hadoop.fs.FileSystem
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.StructType
import org.apache.spark.{SparkConf, SparkContext}

object SparkSalesForceHTTPProxyTest {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val startTimeMillis=System.currentTimeMillis()

    //val s3 = S3Settings.S3Details

    val conf = new SparkConf()
      .setMaster(args(1))
      .setAppName("test")
    //.set("spark.hadoop.validateOutputSpecs", "false")
    //.set("mapreduce.output.basename","approved_document")
    //.set("spark.authenticate.enableSaslEncryption","true")
    //.set("spark.ssl.protocol", "TLS")
    // .set("javax.net.ssl.truststore",args(2))
    // .set("javax.net.ssl.keyStore",args(3))


    val sc = new SparkContext(conf)

    val sqlContext = new SQLContext(sc)


    val fs = FileSystem.get(sc.hadoopConfiguration);

    //System.setProperty("javax.net.ssl.truststore",args(2));
    // System.setProperty("javax.net.ssl.truststorePassword","changeit");
    //System.setProperty("javax.net.ssl.keyStore",args(3));
    // System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
    //System.setProperty("proxyHost",args(6))
    //System.setProperty("proxyPort",args(7))
    //System.setProperty("http.protocols",args(8));
    // System.setProperty("java.net.useSystemProxies", "true");
    System.setProperty("http.proxyHost", args(6));
    System.setProperty("http.proxyPort", args(7));
    System.setProperty("http.protocols", args(8));

    System.setProperty("https.proxyHost", args(6));
    System.setProperty("https.proxyPort", args(7));
    System.setProperty("https.protocols", args(8));



    println("trust store file "+System.getProperty("javax.net.ssl.truststore"))
    println("trust store file "+System.getProperty("javax.net.ssl.keyStore"))

    println("htthost "+System.getProperty("http.proxyHost"))
    println("httport "+System.getProperty("http.proxyPort"))
    println("httpprotocols "+System.getProperty("http.protocols"))

    println("httpshost "+System.getProperty("https.proxyHost"))
    println("httpsport "+System.getProperty("https.proxyPort"))
    println("httpsprotocols "+System.getProperty("https.protocols"))

    val soql = "select * from table"

    val sfDF = sqlContext.read.
      format("com.springml.spark.salesforce").
      option("username", args(4)).
      option("password", args(5)).
      option("login",args(0)).
      option("soql", soql).
      option("version", "35.0").
      option("proxyhost","local.net").
      option("proxyport","9400").
      load()

    sfDF.show(10)

    sfDF.registerTempTable("blah")

    sqlContext.sql("select count(1) as count_of_records from blah").show()


    val endTimeMillis=System.currentTimeMillis()
    val durationSeconds=(endTimeMillis-startTimeMillis)/1000

    println("Time Taken :-"+durationSeconds )

    sc.stop()
    System.exit(0)






  }

}
