package com.dataframe.part17.DynamoDBRead

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import org.apache.hadoop.dynamodb.DynamoDBItemWritable
import org.apache.hadoop.dynamodb.read.DynamoDBInputFormat
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{from_json, lit, _}
import org.apache.spark.sql.types.{StringType, StructType}

object DynamoDBRead {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      .setAppName("Sample")
    val spark = SparkSession.builder.config(conf).master("local[*]").getOrCreate()
    import spark.implicits._

    val transactionStructureName = "transaction"
    val kinesisTransactionStructureName = transactionStructureName
    val kinesisTransactionSchema = new StructType().add("studentId", StringType,true).add("lastName", StringType, true).add("address", StringType, true).add("age", StringType, true).add("firstName", StringType, true)

    val addressStructureName = "address"
    val addressStructureName2 = addressStructureName
    val addressStructureName2Schema = new StructType().add("zipCode", StringType,true)


    val credentialsProvider = new DefaultAWSCredentialsProviderChain
    val credentials = credentialsProvider.getCredentials

    val ddbConf = new JobConf(spark.sparkContext.hadoopConfiguration)
    //ddbConf.set("dynamodb.output.tableName", "student")
    ddbConf.set("dynamodb.input.tableName", "student")
    ddbConf.set("dynamodb.throughput.write.percent", "1.5")
    ddbConf.set("dynamodb.endpoint", "dynamodb.us-east-1.amazonaws.com")
    ddbConf.set("dynamodb.regionid", "us-east-1")
    ddbConf.set("dynamodb.servicename", "dynamodb")
    ddbConf.set("dynamodb.throughput.read", "1")
    ddbConf.set("dynamodb.throughput.read.percent", "1")
    ddbConf.set("mapred.input.format.class", "org.apache.hadoop.dynamodb.read.DynamoDBInputFormat")
    ddbConf.set("mapred.output.format.class", "org.apache.hadoop.dynamodb.write.DynamoDBOutputFormat")
    //ddbConf.set("dynamodb.awsAccessKeyId", credentials.getAWSAccessKeyId)
    //ddbConf.set("dynamodb.awsSecretAccessKey", credentials.getAWSSecretKey)


    val data = spark.sparkContext.hadoopRDD(ddbConf, classOf[DynamoDBInputFormat], classOf[Text], classOf[DynamoDBItemWritable])

    val simple2: RDD[(String)] = data.map { case (text, dbwritable) => (dbwritable.toString)}

    spark.read.json(simple2).registerTempTable("gooddata")

    spark.sql("select replace(replace(split(cast(address as string),',')[0],']',''),'[','') as housenumber," +
      "replace(replace(split(cast(address as string),',')[1],']',''),'[','') as streetname," +
      "replace(replace(split(cast(city as string),',')[2],']',''),'[','') as city from gooddata").show(false)


    /*

    def extractValue : (String => String) = (aws:String) => {
      val pat_value = "\\s(.*),".r
      val matcher = pat_value.findFirstMatchIn(aws)
      matcher match {
        case Some(number) => number.group(1).toString
        case None => ""
      }
    }

    val simple: RDD[(String, String,String)] = data.map { case (text, dbwritable) => (dbwritable.getItem().get("studentId").toString,dbwritable.getItem().get("lastName").toString,dbwritable.toString)}



    simple.toDF("studentId","lastName","address").show(10,false)
    simple.toDF("studentId","lastName","address").printSchema()

    simple.toDF("studentId","lastName","address").registerTempTable("JsonData")

    spark.sql("select studentId,lastName,to_json(address) as address from JsonData").show(false)

    simple.toDF("studentId","lastName","address").withColumn(kinesisTransactionStructureName, from_json($"address", kinesisTransactionSchema)).select("address.*").show(false)


    simple.toDF("studentId","lastName","address").withColumn("address", explode(array($"studentId.s.*"))).show(false)

    simple.toDF("studentId","lastName","address")



    spark.read.json(simple2).show(false)



    val col_extractValue = udf(extractValue)

    simple.toDF().withColumn("studentId", col_extractValue($"_1")).withColumn("lastName", col_extractValue($"_2")).withColumn("address", to_json(column("_3"))).select("studentId","lastName","address").show(false)


    val dynamodDF  = simple.toDF("text","data").withColumn(kinesisTransactionStructureName, from_json($"data", kinesisTransactionSchema)).select("data.*").withColumn("studentId", lit($"studentId") cast (StringType)).withColumn("lastName", lit($"lastName") cast (StringType)).withColumn("address", lit($"address") cast (StringType)).withColumn("age", lit($"age") cast (StringType)).withColumn("firstName", lit($"firstName") cast (StringType))



    simple.toDF("studentId","lastName","address").withColumn(addressStructureName2, from_json($"address", addressStructureName2Schema)).select("address.*").withColumn("zipCode", lit($"zipCode") cast (StringType)).show(false)


    simple.toDF().withColumn("studentId", col_extractValue($"_1")).withColumn("lastName", col_extractValue($"_2")).withColumn("address", col_extractValue($"_3")).select("studentId","lastName","address").withColumn(addressStructureName2, from_json($"address", addressStructureName2Schema)).select("address.*").withColumn("zipCode", lit($"zipCode") cast (StringType)).show(false)

    simple.toDF("text","data").withColumn(kinesisTransactionStructureName, from_json($"data", kinesisTransactionSchema)).select("data.*").withColumn("studentId", lit($"studentId") cast (StringType)).withColumn("lastName", lit($"lastName") cast (StringType)).withColumn("address", lit($"address") cast (StringType)).withColumn("age", lit($"age") cast (StringType)).withColumn("firstName", lit($"firstName") cast (StringType))

    simple.toDF("text","data").withColumn(kinesisTransactionStructureName, from_json($"data", kinesisTransactionSchema)).show(false)

    simple.toDF().withColumn("studentId", col_extractValue($"_1")).withColumn("lastName", col_extractValue($"_2")).withColumn("address", col_extractValue($"_3")).select("studentId","lastName","address").registerTempTable("test")

    spark.sql("select studentId,lastName,concat('{',address) as address from test").withColumn(addressStructureName2, from_json($"address", addressStructureName2Schema)).select("address.*").withColumn("zipCode", lit($"zipCode") cast (StringType)).show(false)

    spark.sql("select studentId,lastName,concat('{',address) as address from test").printSchema()

    val df1 = spark.sparkContext.parallelize("{{zipCode={S: 1234,}, city={S: LA,}, addressLine1={S: 123A,}, addressLine2={S: second street,}, state={S: CA,}}")

    import spark.implicits._
    val jsonStr = """{zipCode={S: 1234,}, city={S: LA,}, addressLine1={S: 123A,}, addressLine2={S: second street,}, state={S: CA,}"""
    val df2 = spark.read.json(Seq(jsonStr).toDS)

    df2.printSchema()




    dynamodDF.show(false)

    println(data.count())

    data.map(x => x).foreach(println)

    data.map(x => x._2)


    val sqlContext = new org.apache.spark.sql.SQLContext(spark.sparkContext)
    import sqlContext.implicits._

    data.map(x => x._2).foreach(println)

    data.toDF

    spark.createDataFrame(data).show(10)

    data.toDF("studentId","lastName","address","age","firstname").show(10,false)

    data.toDF().show(10,false)

     */

    spark.stop()



  }

}
