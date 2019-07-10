package com.dataframe.part16.ScalaTypeSystem.basics.demos


abstract class Passenger

class BusinessPassenger extends Passenger
class CorporatePassenger extends BusinessPassenger
class EconomyPassenger extends Passenger

// -T means users can create a string or Int type AirplaceSeat but using <: type bound you are allowed only PassengerType
class AirplaneSeat[-T >: CorporatePassenger <: Passenger]
object PassengerContraVairance extends App {

  def reserverForCorpaote(airplaceSeat:AirplaneSeat[CorporatePassenger]):Unit =
    println("Reserved seat")

    reserverForCorpaote(new AirplaneSeat[CorporatePassenger])

  val corporatePassengerSeat = new AirplaneSeat[CorporatePassenger]()
  val businessPassengerSeat = new AirplaneSeat[BusinessPassenger]()
  val passengerSeat = new AirplaneSeat[Passenger]()

  // this will not work
  //val economyPassengerSeat = new AirplaneSeat[EconomyPassenger]


}
