package part19.SimpleMKTestCase

import org.apache.spark._
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest._
import org.apache.spark.rdd.RDD

@RunWith(classOf[JUnitRunner])
class SimpleMktDataTest extends FunSuite with Matchers with BeforeAndAfterAll {

  /*filter out too many logs and warnings just get that are usefull*/
  Logger.getLogger("org").setLevel(Level.WARN)
  Logger.getLogger("akka").setLevel(Level.WARN)

  val starttime=System.currentTimeMillis()
  val jobname="CitiMarketData_APP_Scala_Test"

  val conf = new SparkConf().setMaster("local[*]").setAppName("SimpleMktDataTest_APP").set("spark.hadoop.validateOutputSpecs", "false")
  val sc = new SparkContext(conf)

  val status = List("1013|1|N|CT04|114501821|CSLT|1|105|114501821||A|||114501821|2729429|")
  val duplicate=List("1013|1|N|CT04|114501821|CSLT|1|105|114501821||A|||114501821|2729429|")
  val quote=List("1017|1|N|AQ01|080000014|ADK-A|P|R|0|0|100|26.45|ARCA||8|B||0|A|A|A|0|B|P|0|0||||P|26.45|100||||A|||||||||||A|080000014|173014|")
  val baddataone=List("1017|1|N|CT03|||||||A|||074502736|")
  val baddatatwo=List("1013|1|")
  val baddatathree=List("1041|1|")
  val maptestone=List("1017|1|N|AQ01|080141349|BTI|P|R|400|105.5|900|105.62|ARCA||0|A|A|080141350|189213|")
  val maptesttwo=List("1017|1|N|AQ01|080141349|BTI|P|R|400|108.5|900|106.62|ARCA||0|B|A|080141350|189213|")
  val fullataset=status:::duplicate:::quote:::baddataone:::baddatatwo:::maptestone:::maptesttwo:::baddatathree

  override def afterAll() {
    sc.stop()
  }

  def SourceFile(file: String): RDD[String] = {
    sc.textFile(file)
  }

  val testRdd=sc.parallelize(fullataset)
  val sourceFile = testRdd//.map(x => x)


  def FilterDataSet(value:String,len:Int,flag:String): RDD[Array[String]] = {
    if (flag == "FULL")
    {   sourceFile.map(x => x.split("\\|")).
      filter(line => line(0).contains(value))
    }
    else
    {
      sourceFile.map(x => x.split("\\|")).
        filter(line => line(0).contains(value)).filter(line => {
        line.length >= len})
    }
  }

  def ConvertToMap (value: RDD[(String,Any)]): RDD[String]  = {
    value.reduceByKey((_, v) => v).values.map(x => x.toString.replace("(","").replace(")",""))
  }


  test("Test Function FilterDateSet"){
    println("Test Function FilterDateSet")
    val results=FilterDataSet("1013",8,"").map(x => x(0)) //should contain ("1013")
    results.take(1) should contain ("1013")
  }

  test("Test Function ConvertToMap"){
    println("Test Function ConvertToMap")
    val StatusRDD=ConvertToMap(FilterDataSet("1013",8,"").map(x => ((x(5)+x(4)),(x(5),x(4),x(1),x(6),x(7),x(8)))))
    StatusRDD.take(2) should not contain ("1013")
  }

  test("Test Quote RDD"){
    println("Test Quote RDD")
    val QuoteRDD=ConvertToMap(FilterDataSet("1017",17,"").
      map(x => ((x(5)+x(4)) , (x(5),x(4),x(1) ,
        if (x.length >= 15) if (x(15) =="B")
          (
            if (x.length >= 25) {if (x(25) == "") x(9)   else x(25)},
            if (x.length >= 37) {if (x(37) == "") x(11)  else x(37)}
          )
        else if (x(15) =="C" )
          (
            if (x.length >= 25) {if (x(24) == "") (x(9))  else x(24)},
            if (x.length >= 30) {if (x(30) == "") (x(11)) else x(30)}
          )
        else if (x(15) =="A")
        {(x(9),x(11))}
      ))))

    QuoteRDD.take(1) should not contain ("26.45")
    QuoteRDD.foreach(println)
  }

  test("test :- find whether data contains 1013") {
    println("test :- find whether data contains 1013")
    val inputRDD: RDD[String] = sc.parallelize[String](status)
    val results = inputRDD.map(x => x.split("\\|")).filter(line => line(0).contains("1013")).collect
    //results.foreach(println)
    results.map(x => x(0)) should contain("1013")
  }


  test("test :- find whether key of status file = CSLT114501821") {
    println("test :- find whether key of status = CSLT114501821")
    val inputRDD: RDD[String] = sc.parallelize[String](status)
    val keyrdd = inputRDD.map(x => x.split("\\|")).map(line => (line(5)) + (line(4))).collect
    keyrdd.map(x => x) should contain("CSLT114501821")
  }

  test("test :- test filter bad data records for 1013") {
    println("test :- test  filter bad data records 1013")
    val baddatarecords=FilterDataSet("1013",0,"FULL").filter(_.length < 4).count()
    assert(baddatarecords == 1)
    println("1013 bad data record count :-%s".format(baddatarecords))

  }

  test("test :- test filter bad data records for 1017") {
    println("test :- test  filter bad data records 1017")
    val baddatarecords=FilterDataSet("1017",0,"FULL").filter(_.length < 15).count()
    assert(baddatarecords == 1)
    println("1017 bad data record count :-%s".format(baddatarecords))

  }

  test("test :- test filter bad data records for 1041") {
    println("test :- test  filter bad data records 1041")
    val baddatarecords=FilterDataSet("1041",0,"FULL").filter(_.length < 4).count()
    assert(baddatarecords == 1)
    println("1041 bad data record count :-%s".format(baddatarecords))

  }

  test("HashMap Test for QuoteRDD"){
    println("HashMap Test for QuoteRDD")
    val QuoteRDD=ConvertToMap(FilterDataSet("1017",17,"").
      map(x => ((x(5)+x(4)) , (x(5),x(4),x(1) ,
        if (x.length >= 15) if (x(15) =="B")
          (
            if (x.length >= 25) {if (x(25) == "") x(9)   else x(25)},
            if (x.length >= 37) {if (x(37) == "") x(11)  else x(37)}
          )
        else if (x(15) =="C" )
          (
            if (x.length >= 25) {if (x(24) == "") (x(9))  else x(24)},
            if (x.length >= 30) {if (x(30) == "") (x(11)) else x(30)}
          )
        else if (x(15) =="A")
        {(x(9),x(11))}
      ))))

    assert(QuoteRDD.count() == 2)
    println("Number of records in quote hashmap rdd :-%s".format(QuoteRDD.count()))
  }

  test("HashMap Test for StatusRDD"){
    println("HashMap Test for StatusRDD")
    val StatusRDD=ConvertToMap(FilterDataSet("1013",8,"").map(x => ((x(5)+x(4)),(x(5),x(4),x(1),x(6),x(7),x(8)))))
    assert(StatusRDD.count() == 1)
    println("Number of records in Status hashmap rdd :-%s".format(StatusRDD.count()))
  }


  /*calculate end time of scala class*/
  val endTime=System.currentTimeMillis()
  val totalTime=endTime-starttime

  println("JobName "+ jobname +" took (%s".format(totalTime/1000d) + ") seconds to process ")
  println("Test Case for SimpleMktDataFlowTest was successful")

}
