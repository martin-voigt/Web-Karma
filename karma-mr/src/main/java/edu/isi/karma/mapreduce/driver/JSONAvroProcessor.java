package edu.isi.karma.mapreduce.driver;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class JSONAvroProcessor extends Configured implements Tool {

	 public Job configure(Properties p ) throws Exception
	 {
		
		Configuration conf = getConf();
		if(p.getProperty("model.uri") != null)
		{
			conf.setIfUnset("model.uri", p.getProperty("model.uri"));
		}
		if(p.getProperty("model.file") != null)
		{
			conf.setIfUnset("model.file", p.getProperty("model.file"));
		}
		if(p.getProperty("karma.input.type") != null)
		{
			conf.setIfUnset("karma.input.type", p.getProperty("karma.input.type"));
		}
		if(p.getProperty("context.uri") != null)
		{
			conf.setIfUnset("context.uri", p.getProperty("context.uri"));
		}
		Job job = Job.getInstance(conf);
        job.setInputFormatClass(AvroKeyInputFormat.class);
        job.setJarByClass(JSONAvroProcessor.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        job.setMapperClass(JSONAvroMapper.class);
        job.setReducerClass(JSONReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.setInputPaths(job, new Path(p.getProperty("input.directory")));
        FileOutputFormat.setOutputPath(job, new Path(p.getProperty("output.directory")));
        
        job.setNumReduceTasks(0);
        return job;
	 }
	 
	 public int run(String[] args) throws Exception {
		 Properties p = new Properties();
		 p.load(new FileInputStream(new File(args[0])));
		 
         
         Job job = configure(p);
         
         if(!job.waitForCompletion(false))
         {
        	 System.err.println("Unable to finish job");
        	 return -1;
         }
        
         return 0;
       }
       
       public static void main(String[] args) throws Exception {
    	   System.exit(ToolRunner.run(new Configuration(), new JSONAvroProcessor(), args));
       }

}
