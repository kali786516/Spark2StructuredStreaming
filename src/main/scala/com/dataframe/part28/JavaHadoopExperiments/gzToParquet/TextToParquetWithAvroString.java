package com.dataframe.part28.JavaHadoopExperiments.gzToParquet;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import parquet.avro.AvroParquetOutputFormat;
import parquet.example.data.Group;


public class TextToParquetWithAvroString extends Configured implements Tool  {

    private static final Schema SCHEMA = new Schema.Parser().parse(
            "{\n" +
                    "  \"type\": \"record\",\n" +
                    "  \"name\": \"ParquetFile\",\n" +
                    "  \"fields\": [\n" +
                    "    {\"name\": \"empname\", \"type\": \"string\"},\n" +
                    "    {\"name\": \"designation\", \"type\": \"string\"},\n" +
                    "    {\"name\": \"salary\", \"type\": \"string\"}\n"	 +
                    "  ]\n" +
                    "}");

    public static class TextToParquetMapper
            extends Mapper<LongWritable, Text, Void, GenericRecord> {

        private GenericRecord record = new GenericData.Record(SCHEMA);

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String line = value.toString();
            StringTokenizer infor = new StringTokenizer(line, ",");

            int cnt = 0;
            while (infor.hasMoreTokens()) {
                String val = infor.nextToken();
                record.put(cnt, val);
                cnt++;
                if (cnt > 2) break;
            }
            context.write(null, record);
        }
    }


    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.printf("Usage: %s [generic options] <input> <output>\n",
                    getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        Job job = new Job(getConf(), "Text to Parquet");
        job.setJarByClass(getClass());

        /*
        job.getConfiguration().set("fs.s3n.awsAccessKeyId", "");
        job.getConfiguration().set("fs.s3n.awsSecretAccessKey","");
        job.getConfiguration().set("fs.defaultFS","");
        job.getConfiguration().set("fs.s3a.proxy.host","");
        job.getConfiguration().set("fs.s3a.proxy.port","");
        job.getConfiguration().set("fs.s3n.impl","org.apache.hadoop.fs.s3native.NativeS3FileSystem");
         */

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.out.println("Input path " + args[0]);
        System.out.println("Oupput path " + args[1]);

        job.setMapperClass(TextToParquetMapper.class);
        job.setNumReduceTasks(0);

        job.setOutputFormatClass(AvroParquetOutputFormat.class);
        AvroParquetOutputFormat.setSchema(job, SCHEMA);

        job.setOutputKeyClass(Void.class);
        job.setOutputValueClass(Group.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new TextToParquetWithAvroString(), args);
        System.exit(exitCode);
    }


}
