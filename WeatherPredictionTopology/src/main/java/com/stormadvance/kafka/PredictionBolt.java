package com.stormadvance.kafka;

import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.driver.core.utils.UUIDs;

import org.apache.storm.shade.com.google.common.collect.ImmutableList;
import org.apache.storm.shade.org.apache.commons.exec.CommandLine;
import org.apache.storm.shade.org.apache.commons.exec.DefaultExecutor;
import org.apache.storm.shade.org.apache.commons.exec.PumpStreamHandler;
import org.apache.storm.shade.org.apache.commons.lang.StringUtils;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;


public class PredictionBolt extends BaseBasicBolt {

	private static final long serialVersionUID = -5353547217135922477L;
	private static String contactPoint = "localhost";
	private static int port = 9042;
	private static String keySpace = "test";
	private static String dataCenter = "datacenter1";

	public void execute(Tuple input, BasicOutputCollector collector) {
		// Get the word from the tuple
		String data = input.getString(0);
		
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		
		if (StringUtils.isBlank(data)) {
			// ignore blank lines
			return;
		}

		System.out.println("Recieved Data:" + data);
		
		Double[] API_data = new Double[5];
		for(int i = 0; i < 5; i++)
		{
			API_data[i] = Double.parseDouble(data.split(",", 5)[i]);
		}
		try (CqlSession session = CqlSession.builder().addContactPoint(new InetSocketAddress(contactPoint, port))
				.withLocalDatacenter(dataCenter).withKeyspace(keySpace).build()) {
			
			double[] predictions = makePrediction(data);
			
			System.out.println("Fog: " + predictions[0] + " Haze: " + predictions[1]);
			PreparedStatement prepared = session.prepare("insert into predictions (city_id, timeuuid, fog_prediction, haze_prediction, temp, pressure, humidity, wind_speed, wind_deg) values (?,?,?,?,?,?,?,?,?)");
			BoundStatement bound = prepared.bind(1, UUIDs.timeBased(), predictions[0], predictions[1], API_data[0], API_data[1], API_data[2], API_data[3], API_data[4]);
			session.execute(bound);
			

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// here we declare we will be emitting tuples with
		// a single field called "data"
		declarer.declare(new Fields("data"));
	}
	
	public static double[] makePrediction(String data)
	{
	    String pythonPath = "C:/Users/Administrator/AppData/Local/Programs/Python/Python39/python.exe ";
		String analyzeDataPy = "C:/Users/Administrator/Desktop/Code/JythonPickle/analyzeData.py \"";
		String line = pythonPath + analyzeDataPy + data + "\"";
	    CommandLine python = CommandLine.parse(line);
	        
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
	        
	    DefaultExecutor executor = new DefaultExecutor();
	    executor.setStreamHandler(streamHandler);
	
	    try {
			int exitCode = executor.execute(python);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    String[] output = outputStream.toString().replace("[", "").replace("]", "").replace("'", "").split(",");
		double[] prediction = {Double.parseDouble(output[0]), Double.parseDouble(output[1])};
	    return prediction;
	}
}
