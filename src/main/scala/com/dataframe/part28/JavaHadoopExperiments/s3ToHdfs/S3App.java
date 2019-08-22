package com.dataframe.part28.JavaHadoopExperiments.s3ToHdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class S3App {

    public static void main(String[] args) throws Exception {

        if (args.length != 2)
        {
            System.err.println("Usage: WorcCount <file input path> <output path>");
        }

        Configuration conf=new Configuration();
        Job job=new Job(conf,"S3App");
        job.setJarByClass(S3App.class);
        job.setJobName("S3tohdfs");

        job.getConfiguration().set("fs.s3n.awsAccessKeyId", "");
        job.getConfiguration().set("fs.s3n.awsSecretAccessKey","");
        job.getConfiguration().set("fs.default.name","s3n://bucket//s3_test.txt");


        FileInputFormat.addInputPath(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        job.setMapperClass(S3Mapper.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        System.exit(job.waitForCompletion(true)?0:1);
    }
}
