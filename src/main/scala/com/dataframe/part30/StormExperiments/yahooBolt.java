package com.dataframe.part30.StormExperiments;

import org.apache.storm.shade.org.apache.zookeeper.KeeperException;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import java.io.PrintWriter;
import java.util.Map;

public class yahooBolt extends BaseBasicBolt {
    private PrintWriter writer;
    public void prepare(Map stormConf,TopologyContext context){
        /*we will pass the file name via storm configurations*/

        String filename=stormConf.get("fileWrite").toString();
        try {
            this.writer=new PrintWriter(filename,"UTF-8");

        }
        catch (Exception e){
            throw new RuntimeException("Error opening file ["+filename+"]");
        }
    }

    public void execute(Tuple input,BasicOutputCollector collector) {

        /*4 columns and its data types refrenced by array tuple is mapped to variable called input*/

        /*access values using array or field name*/
        String symbol = input.getValue(0).toString();
        String timestamp =  input.getString(1);
        /*access values using field name*/
        Double price = (Double) input.getValueByField("price");
        Double prevClose = input.getDoubleByField("prev_close");

        Boolean gain = true;

        /*business logic for gain */

        if (price<=prevClose) {
            gain = false;
        }

        collector.emit(new Values(symbol, timestamp, price,gain));

        writer.println("\""+symbol+"\",\""+timestamp+"\",\""+price+"\",\""+gain+"\"");
    }

    /*column names in storm they call as fields or schema*/
    /*declare OutputFields is used to declare fields or schema*/
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("company", "timestamp", "price","gain"));
    }

    /*clean up method is used for shutdown connection to database or file*/
    public void cleanup() {
        writer.close();
    }


}
