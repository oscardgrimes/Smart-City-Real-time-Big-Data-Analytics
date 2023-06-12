package com.stormadvance.kafka;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;

public class PredictionTopology {
	public static void main(String[] args) {
		try {
			// zookeeper hosts for the Kafka cluster
			BrokerHosts zkHosts = new ZkHosts("localhost:2181");

			// Create the KafkaSpout configuartion
			// Second argument is the topic name
			// Third argument is the zookeepr root for Kafka
			// Fourth argument is consumer group id
			SpoutConfig kafkaConfig = new SpoutConfig(zkHosts, "PredictionTopic", "",
					"id1");

			// Specify that the kafka messages are String
			// We want to consume all new messages 
			kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
			kafkaConfig.startOffsetTime = kafka.api.OffsetRequest
					.LatestTime();

			// Now we create the topology
			TopologyBuilder builder = new TopologyBuilder();

			// set the kafka spout class
			builder.setSpout("WeatherSpout", new KafkaSpout(kafkaConfig), 2);

			// set the prediction bolt class
			builder.setBolt("PredictionBolt", new PredictionBolt(), 1).globalGrouping(
					"WeatherSpout");

			LocalCluster cluster = new LocalCluster();
			Config conf = new Config();
			conf.setDebug(true);
			if (args.length > 0) {
				conf.setNumWorkers(2);
				conf.setMaxSpoutPending(5000);
				StormSubmitter.submitTopology("PredictionTopology", conf,
						builder.createTopology());

			} else {
				cluster.submitTopology("PredictionBolt", conf,
						builder.createTopology());
			}

		} catch (Exception exception) {
			System.out.println("Thread interrupted exception : " + exception);
		}
	}
}
