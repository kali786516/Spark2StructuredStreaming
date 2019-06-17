package part14.bankingExchangeRatesTestCase

import org.scalatest.{FlatSpec, Matchers}
import com.dataframe.part14.bankingExchangeRates.calculators.CompoundInterest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CompoundInterestSpec  extends FlatSpec with Matchers {
  "A Compound Interest Calculator" should "return more than 62000 in return" in {
    CompoundInterest.calculate(5000, 5, 10) should be(6.0466175E10)
  }

}
