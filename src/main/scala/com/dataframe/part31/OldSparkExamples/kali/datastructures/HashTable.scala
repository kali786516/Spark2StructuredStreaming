package com.dataframe.part31.OldSparkExamples.kali.datastructures

/**
  * Created by kjfg254 on 11/20/2016.
  */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}
import scala.collection.SortedMap
import scala.collection.mutable.LinkedHashMap

/* Hashtable usses associateive arrays behind the scenes
storage of key/value pairs
Each key is unique
get the index of array ans store the object data
ex:- Adding Jane,Kelly,kali,steve
int index=GetIndex(Jane.Name);
Aditive Hash Algorithm:-
hashing a string using naive implementation
summs ASCII value of each character example ascii value for foo:- foo --> 102|111|111=324 is the has key value
cons foo hash will be same as oof , so not secure
folding Hash:-
somewhat better algorithm folds bytes , which every four characters into an integer
kali charan tummala :- every four characters
kali :- 1223112432123   char :- 797962362 an :-999 tumm:- 98732623 ala:- 6876
SHA-2 algorithm is stable , secure but less efficient
Handling Collisions:-
Open Addressing :- Moving to next index in hash table if there is a collision
Chaining        :- storing items in a linked list if there is a collision
growing table:-
grow index length
Removing items:-
HashTable.remove("Jane");
get index of the key
if the value at the index is non null
*/

object HashTable {

  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("YOUR_APP_NAME_USER").set("spark.hadoop.validateOutputSpecs", "false")
    val sc = new SparkContext(conf)


    val grades=SortedMap("kim" -> 90,"Al" -> 85,"Melisa" -> 95,"Emily" -> 91,"Hannah" -> 92)

    println(grades)

    var states=LinkedHashMap("IL" -> "illinois")

    states += ("ky" -> "kentuchy")

    states += ("TX" -> "Texas")

    println(states)

  }

}
