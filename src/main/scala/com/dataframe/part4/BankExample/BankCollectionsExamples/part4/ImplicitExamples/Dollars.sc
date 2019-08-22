case class Dollars(amount:Double) {
  def withTax(dollars: Dollars, taxRate: Double) = {
    Dollars(dollars.amount * (1 + taxRate))

  }

  def pretify: String = "$" + amount
}

  implicit def doubleToDollrs(d: Double): Dollars = Dollars(d)
  val cusomterDollars = Dollars(200)
  //this works
  cusomterDollars.withTax(Dollars(200),0.10)

  // this doesnt work without implicit becuase the
  // account should be a dollar first
  // ex:- val test = Dollars(100)
  // test.withTax(test.amount,0.10) --> this will work
   cusomterDollars.withTax(200.0,0.10)

   val test = Dollars(100)
   test.withTax(test.amount,0.10)

   cusomterDollars.pretify



  /*
  Implicit Conversion has 4 rules
   Marking Rule
   Scope Rule
   once-at-a-time Rule
   Expliciti-First Rule

  */





