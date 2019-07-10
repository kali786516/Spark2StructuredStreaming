package com.dataframe.part16.ScalaTypeSystem.basics.demos

import java.time.LocalDateTime


case class Channel(name:String)
case class TVPackage(name:String,channels:Set[Channel])

sealed trait Plan
case object Monthly extends Plan
case object BiAnnual extends Plan
case object Annual extends Plan

case class SubscriptionPeriod(startDate:LocalDateTime,endDate:LocalDateTime)

case class Subscription(
                       name:String,
                       defaultTVPackages:Map[TVPackage,Plan],
                       additionalTVPackages:Map[TVPackage,Plan],
                       additionalChannels:Map[Channel,Plan],
                       subscriptionPeriod: SubscriptionPeriod
                       )


object DTHDemo extends App {

  val channelOne = Channel("One")
  val channelTwo = Channel("Two")
  val channelSportsOne = Channel("SportsOne")
  val channelSportsTwo = Channel("SportsTwo")

  val TVPackageGen = TVPackage("GenPack",Set(channelOne,channelTwo,channelSportsOne))
  val tcPacjageSports = TVPackage("SportsPack",Set(channelSportsOne,channelSportsTwo))

  val goldSubscription =
    Subscription(
      "Gold",
      Map(TVPackageGen -> Annual),
      Map(tcPacjageSports -> Monthly),
      Map.empty[Channel,Plan],
      SubscriptionPeriod(
        LocalDateTime.of(2019,5,9,12,0),
        LocalDateTime.of(2020,5,9,12,0))
      )

   println(goldSubscription.subscriptionPeriod)

}
