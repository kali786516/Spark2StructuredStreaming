package com.dataframe.part28.JavaHadoopExperiments.gzToParquet;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
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

public class GzToParquet extends Configured implements Tool {

        /*
       final Schema.Parser parser = new Schema.Parser();
        parser.parse(new File("PersonSchema.avsc"));
 */
    //private static final Schema SCHEMA = new Schema.Parser().parse("file.avsc");


    //account table vct
    private static final Schema SCHEMA = new Schema.Parser().parse(
            "{\n" +
                    "  \"type\": \"record\",\n" +
                    "  \"name\": \"ParquetFile\",\n" +
                    "  \"fields\": [\n" +
                    "   {\"name\":\"firstcolumn\",\"type\":\"string\"},\n" +
                    "   {\"name\":\"lastcolumn\",\"type\":\"string\"}\n" +
                    "  ]\n" +
                    "}");

    /*
    approved_document
    private static final Schema SCHEMA = new Schema.Parser().parse(
            "{\n" +
                    "  \"type\": \"record\",\n" +
                    "  \"name\": \"parquetfile\",\n" +
                    "  \"fields\": [\n" +
                    "{\"name\":\"column\",\"type\":\"string\"},\n"+
                    "{\"name\":\"lastcolumn\",\"type\":[\"string\",\"null\"]}\n"+
                    "  ]\n" +
                    "}"); */

    public static class TextToParquetMapper
            extends Mapper<LongWritable, Text, Void, GenericRecord> {

        String headervalue="y";
        String delimetervalue=",";

        public int headermethod(String headersyesno){
            if (headersyesno.equals("n")) {
                headervalue="n";
                return 1;
            }
            return 0;
        }

        public int setdelimetermethod(String delim){
            if (delim.equals(",")){
                delimetervalue=",";
                return 1;
            }
            if (delim.equals("~")){
                delimetervalue="~";
                return 1;
            }
            if (delim.equals("|")){
                delimetervalue="|";
                return 1;
            }
            if (delim.equals("\001")){
                delimetervalue="\001";
                return 1;
            }
            if (delim.equals("\",\"")){
                delimetervalue="\",\"";
                return 1;
            }
            return 0;
        }


        private GenericRecord record = new GenericData.Record(SCHEMA);
        // private GenericRecord record = new GenericData.Record("file.avsc");

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            if (headervalue.equals("y"))
            {
                if (key.get() == 0){
                    return;
                }
                else {
                    String line = value.toString();
                    int cnt = 0;
                    String[] infor = line.split("\",\"");

                    for (int i = 0; i < infor.length; i++) {
                        String val = infor[i];
                        val = val.trim();
                        val = val.replaceAll("(\\\\r|\\\\n|\\\\r\\\\n|\\\\)+", " ");
                        String val2 = val.replace("\\/", " ");
                        String val3 = val2.replace("\\", "");
                        record.put(cnt, val3);
                        cnt++;
                        if (cnt > infor.length - 1) break;
                    }
                    context.write(null, record);
                }
            }
            else
            {
                String line = value.toString();
                int cnt = 0;
                String[] infor = line.split(delimetervalue);
                for (int i = 0; i < infor.length; i++) {
                    String val = infor[i];
                    val = val.trim();
                    val = val.replaceAll("(\\\\r|\\\\n|\\\\r\\\\n|\\\\)+", " ");
                    String val2 = val.replace("\\/", " ");
                    record.put(cnt, val2);
                    cnt++;
                    if (cnt > infor.length - 1) break;
                }

                context.write(null, record);
            }
        }
    }


    public int run(String[] args) throws Exception {
        if (args.length != 5) {
            System.err.printf("Usage: %s [generic options] <input> <output> <local<mr/local>> <header<Y/N>> <delimeter<,~|001>>\n",
                    getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }
        Configuration conf=new Configuration();
        conf.setBoolean("mapreduce.input.fileinputformat.input.dir.recursive", true);
        Job job=new Job(conf,"GZ to Parquet");
        conf.set("mapreduce.jobtracker.address", "local");
        //conf.set("fs.defaultFS","file:///");
        conf.set("io.compression.codecs","org.apache.hadoop.io.compress.DefaultCodec,nl.basjes.hadoop.io.compress.SplittableGzipCodec,org.apache.hadoop.io.compress.BZip2Codec");

        job.setJarByClass(getClass());


        Path inputPath=null;

        if (args[2] == "local" ) {
            FileSystem fs = FileSystem.getLocal(conf).getRawFileSystem();

            //FileSystem fs = FileSystem.get(inputPath.toUri(),conf);
            inputPath = fs.makeQualified(new Path(args[0]));
        } else
        {
            //FileSystem fs = FileSystem.get(conf);
            inputPath = new Path(args[0]);

        }

        //FileInputFormat.addInputPath(job, new Path(args[0]));
        FileInputFormat.addInputPath(job,inputPath);
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.out.println("Input path  " + args[0]);
        System.out.println("Output path " + args[1]);
        System.out.println("File System " + args[2]);
        System.out.println("Header " + args[3]);
        System.out.println("Delimeter " + args[4]);

        String header=args[3];
        String delimeter=args[4];

        TextToParquetMapper setArguments=new TextToParquetMapper();

        setArguments.headermethod(header);
        setArguments.setdelimetermethod(delimeter);


        job.setMapperClass(TextToParquetMapper.class);
        job.setNumReduceTasks(0);

        job.setOutputFormatClass(AvroParquetOutputFormat.class);
        AvroParquetOutputFormat.setSchema(job, SCHEMA);

        job.setOutputKeyClass(Void.class);
        job.setOutputValueClass(Group.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new GzToParquet(), args);
        System.exit(exitCode);
    }


}
