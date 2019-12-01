package com.dataframe.part31.OldSparkExamplesJson.kali.datastructures

/**
  * Created by kjfg254 on 11/20/2016.
  */
import antlr.StringUtils
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}
import  scala.collection.mutable.Stack
import StringUtils._
//stack is similar to plate stack in restaurants , LIFO
//stack has push and pop methods
//push inserts data
//pop deletes data from stack

//methods on stack :- isEmpty,size and clear

//Infix notation:-
// 5+2

//Postfix notation:-

//52*+1-

case class Person(var name:String)

object StackObject {

  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[1]").setAppName("YOUR_APP_NAME_USER").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    var ints=Stack[Int]()
    var fruits=Stack[String]()

    var perople=Stack[String]()

    //add element to stack

    fruits.push("apple","melons","bananna")
    //Stack(bananna, melons, apple)
    println(fruits)

    //get top fruit from fruit stack
    //bananna
    println(fruits.top)

    //remove one element
    fruits.pop()

    //Stack(melons, apple)
    println(fruits)

    //size of stack
    fruits.size

    //isempty stack
    fruits.isEmpty

    //clear the stack
    fruits.clear()

    //postfix calculator

    //Postfix notation:-

    //52*+1-

    val values=Stack[Int]()

    val input="5 6 7 * + 1 -"

    for (token <- input.replaceAll(" ",""))
    {

      if (token.isDigit)
      {

        values.push(token)

        println("Numeric values:-%s".format(token))

      }
      else
      {
        var rhs:Int=values.pop()
        var lhs:Int=values.pop()

        println("Non Numeric Values:-%s".format(token))

        token match
        {
          case '+' => values.push(lhs+rhs)
          case '*' => values.push(lhs*rhs)
          case '-' => values.push(lhs-rhs)
          case '/' => values.push(lhs/rhs)
          case '%' => values.push(lhs%rhs)
          case  ' ' => "looks like shit value please go fuck your self!"

        }
      }
    }

    println("final result:-%s".format(values.pop()))

  }


}
