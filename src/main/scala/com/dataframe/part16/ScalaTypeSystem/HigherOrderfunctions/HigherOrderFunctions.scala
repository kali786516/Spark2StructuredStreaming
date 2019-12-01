package com.dataframe.part16.ScalaTypeSystem.HigherOrderfunctions

object HigherOrderFunctions {

  def main(args: Array[String]): Unit = {

    val someNumbers = List(10,20,30,40,50,60)

    println(someNumbers.scanRight(0)(_ - _))

    // scan right start with right
    // calculation
    // 0,60-0,50-60,40(-)(-)10,30-50,20(-)(-)20,10-40
    // result List(-30, 40, -20, 50, -10, 60, 0)

    println(someNumbers.scanRight(10)(_ - _))
    //calculation
    //10,10-60,50-50,40-0,30-40,20(-)(-)10,10-30
    //result List(-20, 30, -10, 40, 0, 50, 10)

    println(someNumbers.scanLeft(0)(_ - _))
    //calculation
    // 0,0-10,-10-20,-30-30,-60-40,-100-50,-150-60
    //result List(0, -10, -30, -60, -100, -150, -210)

    println(someNumbers.foldRight(0)(_-_))
    // result -30

    println(someNumbers.foldLeft(0)(_-_))
    // result -210

    println(someNumbers.reduceRight(_-_))

    println(someNumbers.reduceLeft(_-_))
    
  }

}
