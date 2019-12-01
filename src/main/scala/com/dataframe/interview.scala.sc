sc.textFile("purplecow.txt").map(line => line.toUpperCase()).filter(line => line.startsWith("I")).count()
mydata_filt.take(2)
def toUpper(s: String): String ={ s.toUpperCase }
val mydata = sc.textFile("purplecow.txt")
mydata.map(toUpper).take(2)
myData = ["Alice","Carlos","Frank","Barbara"]
myRdd = sc.parallelize(myData)
myRdd.take(2)
import scala.util.parsing.json.JSON
val myrdd1 = sc.wholeTextFiles(mydir)
val myrdd2 = myrdd1.map(pair => JSON.parseFull(pair._2).get.asInstanceOf[Map[String,String]])
for (record <- myrdd2.take(2)) println(record.getOrElse("firstName",null))
sc.textFile(file).flatMap(line => line.split(' ')).distinct()
rdd1.subtract(rdd2)
rdd1.zip(rdd2)
sc.textFile(logfile).keyBy(line => line.split(' ')(2))
sc.textFile(file).map(line => line.split('\t')).map(fields => (fields(0),(fields(1),fields(2))))
0003,sku888:sku022:sku010:sku594]
[00004,sku411]
(00001,sku010:sku933:sku022)
//output
(00001,sku010)
(00001,sku933)
sc.textFile(file).map(line => line.split('\t')).map(fields => (fields[0],fields[1])).flatMapValues(skus => skus.split(':'))
val counts = sc.textFile(file).flatMap(_.split("\\W")).map((_,1)).reduceByKey(_+_)
val x = sc.parallelize(Array(("USA", 1), ("USA", 2), ("India", 1),
  | ("UK", 1), ("India", 4), ("India", 9),
  | ("USA", 8), ("USA", 3), ("India", 4),
  | ("UK", 6), ("UK", 9), ("UK", 5)), 3)
val y = x.groupByKey
val employee_rdd = sc1.textFile(csv_file_path).map(line => line.split(',')).sortByKey(ascending= False)
val sortedreqs = userreqs.map(pair => pair.swap).transform(rdd => rdd.sortByKey(false))
def reverse(s:String):String = {
  val buf= new StringBuilder
  var len = s.length
  for (i <- 0 until le

    n){
    buf.append(s.charAt(len-i-1))
  }
  buf.toString
}
def isEven(number: Int) = number % 2 == 0
def isOdd(number: Int) = !isEven(number)
val evens = Seq.range(0, 10).filter(isEven(_))
println(evens)
val odds = Seq.range(0, 10).filter(isOdd(_))
println(odds)
val a = List(10, 20, 30, 40, 10)      # List(10, 20, 30, 40, 10) a.distinct, a.drop(2), a.dropRight(2), a.dropWhile(_ < 25), a.filter(_ < 25) a.filter(_ > 100), a.filterNot(_ < 25), a.find(_ > 20),
a.head,
a.headOption
a.init
a.intersect(List(19,20,21))
a.last
a.lastOption
a.slice(2,4)
a.tail
a.take(3)
a.takeRight(2)
a.takeWhile(_ < 30)
x.partition(_ > 10)
x.goupBy(_ > 10)
(1 to 5).toArray.sliding(2).toList
val a = Array(1,2,3,4,5).reduceLeft(_+_) or reduceLeft(_ * _) or reduceLeft(_ min _),a.foldLeft(20)(_+_) val women = List("Wilma","Betty") val men = List("Fred","Barney") women zip men.toMap0