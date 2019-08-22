package com.dataframe.part21.RealTimeFraudDetection.fraudDetection.utils

/**
  * Created by kalit_000 on 6/2/19.
  */
object Utils {

  def getDistance (lat1:Double,lon1:Double,lat2:Double,lon2:Double) = {

    val r : Int              = 6371
    val latDistance : Double = Math.toRadians(lat2 - lat1)
    val lonDistance : Double = Math.toRadians(lon2 - lon1)
    val a : Double           = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
    val c : Double           = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    val distance : Double    = r * c
    distance

  }


}
