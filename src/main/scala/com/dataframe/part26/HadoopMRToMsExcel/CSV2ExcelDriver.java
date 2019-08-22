package com.dataframe.part26.HadoopMRToMsExcel;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.zuinnote.hadoop.office.format.common.dao.SpreadSheetCellDAO;
import org.zuinnote.hadoop.office.format.common.dao.TextArrayWritable;
import org.zuinnote.hadoop.office.format.mapreduce.ExcelFileOutputFormat;


public class CSV2ExcelDriver extends Configured implements Tool {

    public int run(String[] args)
            throws Exception
    {
        Job job = Job.getInstance(getConf(), "example-hadoopoffice-CSV2Excel-job");
        job.setNumReduceTasks(1);
        job.setJarByClass(CSV2ExcelDriver.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(TextArrayWritable.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(SpreadSheetCellDAO.class);

        job.setMapperClass(HadoopOfficeExcelMap.class);
        job.setReducerClass(HadoopOfficeExcelReducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(ExcelFileOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args)
            throws Exception
    {
        Configuration conf = new Configuration();
        conf.set("hadoopoffice.read.locale.bcp47", "de");
        conf.set("sheetname", "testkali");

        int res = ToolRunner.run(conf, new CSV2ExcelDriver(), args);
        System.exit(res);
    }


}
