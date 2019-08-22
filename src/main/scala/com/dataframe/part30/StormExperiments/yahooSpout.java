package com.dataframe.part30.StormExperiments;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Time;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

public class yahooSpout extends BaseRichSpout {
    private SpoutOutputCollector collector;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    /*Map conf is used to connect databases,twitter,kafka etc ...topology configuration*/
    /*context object keeps track of all running tasks in the topology*/
    /*collector object is used to communicate between objects (actual emmiting of data)*/
    public void open(Map conf,TopologyContext context,SpoutOutputCollector collector){
        this.collector=collector;
    }

    /*nextTuple method where action happens recieves data from source and emits data to Bolt in the form of tuple */
    /* as long as spout is alive this method is run continously */

    public void nextTuple(){
        try
        {
            StockQuote quote=YahooFinance.get("MSFT").getQuote();
            BigDecimal price=quote.getPrice();
            BigDecimal prevClose=quote.getPreviousClose();
            Timestamp timestamp=new Timestamp(System.currentTimeMillis());
            /*emit values using collector object*/
            /*MSFT --> company field
             * timestamp value --> timestampcolumn
             * price --> price
             * prev_close --> prev_close
             * values object */
            collector.emit(new Values("MSFT",sdf.format(timestamp),price.doubleValue(),prevClose.doubleValue()));
        }
        catch (Exception e) {}
    }

    /*column names in storm they call as fields or schema*/
    /*declare OutputFields is used to declare fields or schema*/
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("company", "timestamp", "price","prev_close"));
    }
}
