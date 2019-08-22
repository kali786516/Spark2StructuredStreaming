package com.dataframe.part26.HadoopMRToMsExcel;

import org.apache.avro.generic.GenericData;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.ReduceContext;
import org.zuinnote.hadoop.office.format.common.dao.TextArrayWritable;
import java.io.IOException;
import java.io.InterruptedIOException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class HadoopOfficeExcelMap extends Mapper<LongWritable, Text, Text, TextArrayWritable> {
    private static final String CSV_SEPARATOR = ",";
    private static final Log LOG = LogFactory.getLog(HadoopOfficeExcelMap.class);

    public void setup(Mapper<LongWritable, Text, Text, TextArrayWritable>.Context context)
            throws IOException, InterruptedException
    {
        //context.write(new Text("column1,column2,column3"), new TextArrayWritable());
    }

    public void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, TextArrayWritable>.Context context)
            throws IOException, InterruptedException
    {
        //if (key.get() == 0 && value.toString().contains("header"))
        //{
        //return ;
        // }
        //else
        //{
        StringTokenizer myTokenizer = new StringTokenizer(value.toString(), ",");
        Text[] textArray = new Text[myTokenizer.countTokens()];
        int i = 0;
        while (myTokenizer.hasMoreTokens()) {
            textArray[(i++)] = new Text(myTokenizer.nextToken());
        }
        TextArrayWritable valueOutArrayWritable = new TextArrayWritable();
        valueOutArrayWritable.set(textArray);
        Configuration conf = context.getConfiguration();
        String sheetname = conf.get("sheetname");
        context.write(new Text(sheetname), valueOutArrayWritable);
        //}
    }

    public void cleanup(Mapper<LongWritable, Text, Text, TextArrayWritable>.Context context) {}


}
