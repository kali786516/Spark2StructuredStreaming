package com.dataframe.part28.JavaHadoopExperiments.s3ToHdfs;

import org.apache.commons.math.ode.IntegratorException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.io.InterruptedIOException;

public class S3Mapper extends Mapper<LongWritable,Text,Void,Text> {

    //map hadoop map method
    @Override
    public void map(LongWritable Key,Text Value,Context context) throws IOException,InterruptedException {

        String[] infor=Value.toString().split("\",\"");

        for(int i=0; i < infor.length;i++) {
            context.write(null, new Text(infor[i]));
        }
    }
}
