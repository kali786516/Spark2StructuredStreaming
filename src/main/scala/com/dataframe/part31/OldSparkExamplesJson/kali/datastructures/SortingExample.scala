package com.dataframe.part31.OldSparkExamplesJson.kali.datastructures

/**
  * Created by kjfg254 on 11/20/2016.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}

//arranging data in a collection based on comparision
//two general sorting algor linear and divide and conquer
// linear treat problem of sorting as single large operation, ex:- array of data is done in one go
// Divide and conquer partition data into smaller sets that can be independently sorted

//comparisions :- when two values of input array are compared for relative equality , Equal to ,grater then ,less then

//swaps:- when two values stored in the input array are swapped ,Eg [1,0] => [0,1]

//----------------------
// Linear
//----------------------

/*
//bubble sort:- on each pass , compare each array item to its right neighbor
// if neighbor is smaller then swap right and left
//very bad
// 2|4|5|3|7
// 2|5|3|4|7
// worst performance :- o(n2)  for n items of arrays it takes n squared comparision and swaps for data to become sorted
// 10 items would require 100 operations
*/

/*
* Insertion sort:- sort each item in the array as they are encountered
* everything left of item is sorted , everything to the right is unsorted
* worst performance:- o(n2), not appropiate for large unsorted data sets
* Space required:- o(n)
* */


/*
* selecttion sort:- sorts data by finding the smallest item and swapping it into the array in the first unsorted location
* enumerate the array from the first unsroted item to the end
* identify the smalled item
* swap the smallest item with first unsorted item
* worst case:- o(n2)
* * */

//----------------------
// Divide and conquer
//----------------------

/*
* Merge sort:- the array is recursively slit into two halfs
* example:- 3|8|2|1|5|4|6|7
*
* Divide Phase:-
* 3|8|2|1  5|4|6|7
* 3|8 2|1 5|4 6|7
* 3 8 2 1 5 4 6 7
*
* Reconstruction Phase:-
* 3|8 1|2 4|5 6|7
* 1|2|3|8  4|5|6|7
* 1|2|3|4|5|6|7|8
*
*worst case performance:- o(n log n)
* */

/*
* Quick Sort:- Pick a pivot value and partition the array
* worst case performance:- o(n2)
* Average:- o(n log n)
*
* */

object SortingExample {

  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("YOUR_APP_NAME_USER").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)

    def bubblesort[A <% Ordered[A]](list: List[A]): List[A] = {
      def sort(as: List[A], bs: List[A]): List[A] =
        if (as.isEmpty) bs
        else bubble(as, Nil, bs)

      def bubble(as: List[A], zs: List[A], bs: List[A]): List[A] = as match {
        case h1 :: h2 :: t =>
          if (h1 > h2) bubble(h1 :: t, h2 :: zs, bs)
          else bubble(h2 :: t, h1 :: zs, bs)
        case h1 :: Nil => sort(zs, h1 :: bs)
      }

      sort(list, Nil)
    }



  }

}
