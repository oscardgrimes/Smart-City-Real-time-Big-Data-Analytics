package com.stormadvance.kafka_producer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.simple.parser.ParseException;

public class WeatherProducer {
	
	public static void main(String[] args) throws ParseException {
		
		API api = new API();
		// Build the configuration required for connecting to Kafka
		Properties props = new Properties();

		// List of kafka borkers. Complete list of brokers is not required as
		// the producer will auto discover the rest of the brokers.
		props.put("bootstrap.servers", "localhost:9092");
		props.put("batch.size", 1);
		// Serializer used for sending data to kafka. Since we are sending string,
		// we are using StringSerializer.
		props.put("key.serializer",
				"org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer",
				"org.apache.kafka.common.serialization.StringSerializer");

		props.put("producer.type", "sync");
		
		// Create the producer instance
		Producer<String, String> producer = new KafkaProducer<String, String>(
				props);
		
		String API_data;
		
		while(true)
		{
			try {
				API_data = api.getAPI();
			} catch (IOException e) {
				API_data = "API Call Failed";
				e.printStackTrace();
			}
			System.out.println("data : " + API_data);
			ProducerRecord<String, String> data = new ProducerRecord<String, String>(
					"PredictionTopic",API_data, API_data);
			producer.send(data);
			try 
			{
				Thread.sleep(300000);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
}
