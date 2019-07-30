package com.dataframe.part19.SimpleMKTData

import org.apache.spark._
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.rdd.RDD
import java.text.SimpleDateFormat
import java.util.Date
import java.io._

object SimpleMktData {

  def main(args: Array[String]): Unit =
  {
    val starttime=System.currentTimeMillis()
    val jobname="SimpleMktData_APP"
    var logger = Logger.getLogger(this.getClass())

    println(s"At least 7 argument need to be passed")

    if (args.length < 7)
    {
      logger.error("=> wrong parameters number")
      System.err.println("Usage: " +
        "SimpleMkDataFlow Source File:- data.txt " +
        "Status Dest:- /status/ " +
        "Luld Dest:- /luld/ " +
        "Quote Dest:- /quote/ " +
        "SparkMaster:- Local[2] or yarn-cluster " +
        "numberofpartitions:- (Based on no of nodes in a cluster * 2) or (no fo cores in a laptop * 2)" +
        "shufflepartitions:- (Based on no of rows in a cluster * 2) or (no of cores in a laptop * 2)" +
        "businessdate:- business date"
      )


      System.exit(1)
    }

    if (args.length >= 7)
    {
      println("Argument 1:Source File        :- %s".format(args(0)))
      println("Argument 2:Status Path        :- %s".format(args(1)))
      println("Argument 3:Luld   Path        :- %s".format(args(2)))
      println("Argument 4:Quote  Path        :- %s".format(args(3)))
      println("Argument 5:Deploy Mode        :- %s".format(args(4)))
      println("Argument 6:NumberofPartitions :- %s".format(args(5)))
      println("Argument 6:ShuffleParitions   :- %s".format(args(6)))
      println("Argument 7:BusinessDate       :- %s".format(args(7)))
    }

    /*filter out too many logs and warnings just get that are usefull*/
    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val Sourcepath=args(0)
    val StatusPath=args(1)
    val Luldpath=args(2)
    val Quotepath=args(3)
    val deploymode=args(4)
    val numberofpartitions=args(5)
    val shufflepartitions=args(6)
    val businessdate=args(7)

    val conf = new SparkConf().setMaster(deploymode).setAppName("SimpleMktData_APP").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)


    /*Source File*/
    def SourceFile(file:String): RDD[String] = {
      sc.textFile(file,numberofpartitions.toInt)
    }

    //http://0x0fff.com/spark-architecture/
    //val filesHere=(new File(Sourcepath)).listFiles

    //val test : String=for (file <- filesHere if file.getName.endsWith(".gz")) yield file


    val sourceFile=SourceFile(Sourcepath)

    println("Source File Name:- %s".format(Sourcepath))

    def FilterDataSet(value:String,len:Int,flag:String): RDD[Array[String]] = {
      if (flag == "FULL")
      {   sourceFile.map(x => x.split("\\|")).
        filter(line => line(0).contains(value))//.coalesce(shufflepartitions.toInt,false)
      }
      else
      {
        sourceFile.map(x => x.split("\\|")).
          filter(line => line(0).contains(value)).filter(line => {
          line.length >= len})
      }
    }

    def ConvertToMap (value: RDD[(String,Any)]): RDD[String]  = {
      value
        //.coalesce(shufflepartitions.toInt,false) //we might need to use after filter large amount of data need to redistriubte (false will not shuffle the date across nodes)
        .partitionBy(new HashPartitioner(shufflepartitions.toInt))
        .persist()
        .reduceByKey((_, v) => v)
        .values.map(x => x.toString.replace("(","").replace(")",""))
    }





    /* sort and coalesce ....
     def ConvertToMap (value: RDD[(String,Any)]): RDD[String]  = {
       value.partitionBy(new HashPartitioner(numberofpartitions.toInt))
         .persist()
         .reduceByKey((_, v) => v).sortBy(_._1,false)
         .values.map(x => x.toString.replace("(","").replace(")","")).coalesce(1)
     }*/

    /*def ConvertToMap (value: RDD[(String,Any)]): RDD[String]  = {
      value
        .reduceByKey((_, v) => v)
        .values.map(x => x.toString.replace("(","").replace(")",""))
    }*/


    def replacebrack (value: RDD[(Any)]): RDD[String]  = {
      value.map(x => x.toString.replace("(","").replace(")",""))
    }



    val format = new SimpleDateFormat("yyyy-M-dd hh:mm:ss.ssssss")
    //println("Todays date in format YYYY-M-DD HH:MM:SS:-%s".format(format.format(new Date())))

    //format.format(new Date())
    /*CREATE STATUS DATA SET*/
    val StatusRDD=ConvertToMap(FilterDataSet("1013",6,"").map(x => ((x(5)+x(4)),(x(5),x(4),x(1),if (x.length >=7) x(6) else "" ,if (x.length >=8) x(7) else "",if (x.length >=9) x(8) else "",x(0),if (x.length >=10) x(9) else  ""))))
    //println("Count of rows in Source Status Data file filtered for (1013):- %s".format(FilterDataSet("1013",8,"").countApproxDistinct(0.001)))
    //println("Count of rows in Status Output Data Set:- %s".format(StatusRDD.countApproxDistinct(0.001)))
    replacebrack(StatusRDD
      .map(x => x.split(","))
      .map(x => (format.format(new Date()),x(6),x(1),x(0),x(3),x(4),x(5),if (x.length >=8) x(7) else "","ETL_HALTS",format.format(new Date()),businessdate))
      .map{ case (trd_date,msgtype,vendortime,symbol,reasoncode,statuscode,updatetime,message,created_by,created_date,businessdate)
      => (trd_date,msgtype,vendortime,symbol,reasoncode,statuscode,updatetime,message,created_by,created_date,businessdate)})
      .saveAsTextFile(StatusPath)

    println("No of paritions of StatusRDD:-%s".format(StatusRDD.partitions.size))

    //ConvertToMap(FilterDataSet("1041",9,"").take(2).foreach(println)
    /*CREATE LULD DATA SET*/
    val LuldRDD=ConvertToMap(FilterDataSet("1041",6,"").map(x => ((x(5)+x(4)),(x(5),x(4),x(1),if (x.length >=7) x(6) else "",if (x.length >=8) x(7) else "",if (x.length >=9) x(8) else "",if (x.length >=10) x(9) else  "",x(0),x(1)))))
    //println("Count of rows in Source Luld Data file filtered for (1041):- %s".format(FilterDataSet("1041",9,"").countApproxDistinct(0.001)))
    //println("Count of rows in Luld Output Data Set:- %s".format(LuldRDD.countApproxDistinct(0.001)))
    replacebrack(LuldRDD.map(x => x.split(","))
      .map(x => (format.format(new Date()),x(7),x(1),x(0),x(3),x(4),x(5),x(6),"ETL_LULD",format.format(new Date()),businessdate))
      .map{ case (trd_date,msgtype,vendortime,symbol,limit_down_price,limit_ip_price,luld_price_band_indicator,effective_time,created_by,created_date,businessdate)
      => (trd_date,msgtype,vendortime,symbol,limit_down_price,limit_ip_price,luld_price_band_indicator,effective_time,created_by,created_date,businessdate)})
      .saveAsTextFile(Luldpath)

    println("No of paritions of LuldRDD:-%s".format(LuldRDD.partitions.size))

    /*CREATE Quote DATA SET*/
    val QuoteRDD=ConvertToMap(FilterDataSet("1017",25,"").
      map(x => ((x(5)+x(4)) , (x(5),x(4),x(1),x(0),
        if (x.length >= 15) if (x(15) =="B")
          (
            if (x.length >= 25) {if (x(25) == "") x(9)   else x(25)},
            if (x.length >= 37) {if (x(37) == "") x(11)  else x(37)}
          )
        else if (x(15) =="C" )
          (
            if (x.length >= 24) {if (x(24) == "") (x(9))  else x(24)},
            if (x.length >= 30) {if (x(30) == "") (x(11)) else x(30)}
          )
        else if (x(15) =="A")
        {(x(9),x(11))}
      ))))

    //println("Count of rows in Source Quote Data file filtered for (1017):- %s".format(FilterDataSet("1017",17,"").countApproxDistinct(0.001)))
    //println("Count of rows in Quote Output Data Set:- %s".format(QuoteRDD.countApproxDistinct(0.001)))
    replacebrack(QuoteRDD.map(x => x.split(","))
      .map(x => (format.format(new Date()),x(3),x(2),x(1),x(0),if (x.length >=5) x(4) else "",if (x.length >=6) x(5) else "","ETL_NBBO",format.format(new Date()),businessdate))
      .map{ case (trd_date,msgtype,timeindex,vendortime,symbol,nationalbbidprice,nationalbofferprice,created_by,created_date,businessdate) => (trd_date,msgtype,timeindex,vendortime,symbol,nationalbbidprice,nationalbofferprice,created_by,created_date,businessdate)})
      .saveAsTextFile(Quotepath)

    println("No of paritions of QuoteRDD:-%s".format(QuoteRDD.partitions.size))

    //println("Bad Data Count :-%s".format(Linecounter))

    println("FLUSH JVM CACHE MEMEORY FOR STATUS DATA SET ....")
    StatusRDD.unpersist()

    /*FLUSH JVM CACHE MEMORY*/
    println("FLUSH JVM CACHE MEMEORY FOR LULD DATA SET ....")
    LuldRDD.unpersist()

    /*FLUSH JVM CACHE MEMORY*/
    println("FLUSH JVM CACHE MEMEORY FOR sourceFile DATA SET ....")
    sourceFile.unpersist()


    /*FLUSH JVM CACHE MEMORY*/
    println("FLUSH JVM CACHE MEMEORY FOR QUOTE DATA SET ....")
    QuoteRDD.unpersist()

    //println("Count of Bad Status Rows:-%s".format(FilterDataSet("1013",0,"FULL").filter(_.length < 4).count()))
    //println("Count of Bad Luld Rows  :-%s".format(FilterDataSet("1041",0,"FULL").filter(_.length < 4).count()))
    //println("Count of Bad Quote Rows :-%s".format(FilterDataSet("1017",0,"FULL").filter(_.length <30).count()))


    /*calculate end time of scala class*/
    val endTime=System.currentTimeMillis()
    val totalTime=endTime-starttime

    println("JobName "+ jobname +" took (%s".format(totalTime/1000d) + ") seconds to process ")

    sc.stop()
    /*Return exit code 0 if all the above steps are successful*/
    System.exit(0)

  }

}

