package com.dataframe.part2.kafkaJustinPihony.consumer

/**
  * Created by kalit_000 on 5/16/19.
  */
import java.nio.file.{Files, Paths}
import java.util.concurrent._

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, Encoders, SaveMode, SparkSession}
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{StreamingContext, _}

case class Account5(number:String,firstName:String,lastName:String)
case class Transaction5(id:Long,account:Account5,date:java.sql.Date,amount:Double,description:String)
case class TransactionForAverage5(accountNumber:String,amount:Double,description:String,date:java.sql.Date)

case class SimpleTransaction2(id: Long, account_number: String, amount: Double,
                             date: java.sql.Date, description: String)

case class UnparsableTransaction2(id: Option[Long], originalMessage: String, exception: Throwable)

object FraudDetectorKafkaConsumerSimple {
  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("HbIngestion")
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val startTimeMillis = System.currentTimeMillis()

    val spark = SparkSession.builder
      .master("local[*]")
      .appName("Fraud Detector")
      .config("spark.driver.memory","2g")
      .config("spark.cassandra.connection.host","localhost")
      .config("spark.streaming.stopGracefullyOnShutdown","true")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    val financesDS = spark.read.json("Data/finances-small.json")
                               .withColumn("date", to_date(unix_timestamp($"Date","MM/dd/yyyy")
                               .cast("timestamp"))).as[Transaction4]

    val accountNumberPrevioud4WindowSpec = Window.partitionBy($"AccountNumber").orderBy($"Date").rowsBetween(-4,0)
    val rollingAvgForPrevious4PerAccount = avg($"Amount").over(accountNumberPrevioud4WindowSpec)

    financesDS
            .na.drop("all", Seq("ID","Account","Amount","Description","Date"))
            .na.fill("Unknown", Seq("Description")).as[Transaction4]
            //.filter(tx=>(tx.amount != 0 || tx.description == "Unknown"))
            .where($"Amount" =!= 0 || $"Description" === "Unknown")
            .select($"Account.Number".as("AccountNumber").as[String], $"Amount".as[Double],
            $"Date".as[java.sql.Date](Encoders.DATE), $"Description".as[String])
            .withColumn("RollingAverage", rollingAvgForPrevious4PerAccount)
            .write.mode(SaveMode.Overwrite).parquet("Output/finances-small")

    if(financesDS.hasColumn("_corrupt_record")) {
      financesDS.where($"_corrupt_record".isNotNull).select($"_corrupt_record")
        .write.mode(SaveMode.Overwrite).text("Output/corrupt_finances")
    }

    financesDS
            .map(tx => (s"${tx.account.firstName} ${tx.account.lastName}", tx.account.number))
            .distinct
            .toDF("FullName", "AccountNumber")
            .coalesce(5)
            .write.mode(SaveMode.Overwrite).json("Output/finances-small-accounts")

    financesDS
            .select($"account.number".as("accountNumber").as[String], $"amount".as[Double],
            $"description".as[String],
            $"date".as[java.sql.Date](Encoders.DATE)).as[TransactionForAverage4]
           .groupBy($"AccountNumber")
           .agg(avg($"Amount").as("average_transaction"), sum($"Amount").as("total_transactions"),
           count($"Amount").as("number_of_transactions"), max($"Amount").as("max_transaction"),
           min($"Amount").as("min_transaction"), stddev($"Amount").as("standard_deviation_amount"),
           collect_set($"Description").as("unique_transaction_descriptions"))
           .withColumnRenamed("accountNumber", "account_number")
           .coalesce(5).show(false)
           //.write
           //.mode(SaveMode.Overwrite)
           //.cassandraFormat("account_aggregates", "finances")
           //.save

    val checkpointDir = "StreamCheckPoint/file:///checkpoint"

    val streamingContext = StreamingContext.getOrCreate(checkpointDir, () => {

        val ssc = new StreamingContext(spark.sparkContext,Seconds(1))
        ssc.checkpoint(checkpointDir)

        runConcurrentFunction(ssc,logger)

        val kafkaStream = KafkaUtils.createStream(ssc,"localhost:2181","test",Map("spark_avro_topic" -> 1))

        kafkaStream.map(keyVal => tryConversionToSimpleTransaction(keyVal._2))
          .flatMap(x => x.right.toOption)
          .foreachRDD( rdd => {
            if(!rdd.isEmpty()) {
              println(rdd.toString())
            }
          })
        //.saveToCassandra("transactions","finances")
          ssc
    })


    streamingContext.start()
    streamingContext.awaitTermination()

  }

  def stopStreaming(stream:StreamingContext,logger:Logger):Boolean = {
    var streamStopFile=true
    if(Files.exists(Paths.get("/Users/kalit_000/Downloads/Spark2StructuredStreaming/StreamStopCheckPoint/stop3.txt"))) {
      logger.info("Gracefully stopping Spark Streaming Application")
      stream.stop(true, true)
      logger.info("Application stopped")
      sys.exit(0)
      streamStopFile=false
    }
    return streamStopFile
  }


  def runConcurrentFunction(stream:StreamingContext,logger:Logger,timeInSeconds:Int=1)={
     val ex = new ScheduledThreadPoolExecutor(1)
     val task = new Runnable {
       def run() = stopStreaming(stream,logger)
     }

    ex.scheduleAtFixedRate(task,0,timeInSeconds,TimeUnit.SECONDS)
  }


  import scala.util.{Either, Left, Right}
  def tryConversionToSimpleTransaction(logLine:String):Either[UnparsableTransaction2,SimpleTransaction2] = {
    import java.text.SimpleDateFormat

    import scala.util.control.NonFatal

    logLine.split(',') match {
      case Array(id,date,acctNum,amt,desc) =>
        var parseId:Option[Long] = None
        try {
          parseId = Some(id.toLong)
          Right(SimpleTransaction2(parseId.get,acctNum,amt.toDouble,
          new java.sql.Date((new SimpleDateFormat("MM/dd/yyyy")).parse(date).getTime),desc))
        } catch {
          case NonFatal(exception) => Left(UnparsableTransaction2(parseId,logLine,exception))
        }
      case _ => Left(UnparsableTransaction2(None, logLine,
        new Exception("Log split on comma does not result in a 5 element array.")))
    }

  }


  implicit class DatasetHelper[T](ds: Dataset[T]) {
    import scala.util.Try //org.apache.spark.sql.AnalysisException
    def hasColumn(columnName: String) = Try(ds(columnName)).isSuccess
  }

}
