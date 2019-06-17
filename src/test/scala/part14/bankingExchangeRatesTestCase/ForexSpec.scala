package part14.bankingExchangeRatesTestCase

import org.scalatest.{FlatSpec, Matchers}
import com.dataframe.part14.bankingExchangeRates.api.Forex
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ForexSpec extends FlatSpec with Matchers {
  "The Forex api" should "fetch 32 currencies" in {
    val exchangeRates = Forex.getExchangeRates()
    exchangeRates.size should be (32)
  }
}
