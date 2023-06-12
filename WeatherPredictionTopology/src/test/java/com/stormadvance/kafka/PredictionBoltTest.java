/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit3TestClass.java to edit this template
 */
package com.stormadvance.kafka;

import junit.framework.TestCase;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.datastax.driver.core.Cluster;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.cql.DefaultRow;
import static com.stormadvance.kafka.PredictionBolt.makePrediction;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.simple.parser.ParseException;


/**
 *
 * @author Administrator
 */
public class PredictionBoltTest extends TestCase {
    
    private static final long serialVersionUID = -5353547217135922477L;
    private static String contactPoint = "localhost";
    private static int port = 9042;
    private static String keySpace = "test";
    private static String dataCenter = "datacenter1";
    
    public PredictionBoltTest(String testName) {
        super(testName);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    public void Produce(String tData)
    {
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

        ProducerRecord<String, String> data = new ProducerRecord<String, String>(
                        "PredictionTopic",tData, tData);
        producer.send(data);
    }
    public double[] Select()
    {
        double[] fResult = new double[7];
        try (CqlSession session = CqlSession.builder().addContactPoint(new InetSocketAddress(contactPoint, port))
                        .withLocalDatacenter(dataCenter).withKeyspace(keySpace).build()) {
            String query = "SELECT * FROM predictions WHERE city_id = 1 ORDER by timeuuid DESC limit 1;";
            ResultSet result = session.execute(query);
            Row row = result.all().get(0);
            double[] doubles = new double[7];
            for(int i = 2; i <= 8; i++)
            {
                System.out.println("i:" + i + ", i-2:" + (i-2) + ", Data: " + row.getDouble(i));
                //doubles[i - 2] = row.getDouble(i);
                fResult[i-2] = row.getDouble(i); 
            }
        } catch (Exception e) {
            System.out.println("error");  
            //System.out.println(e.getMessage());
        }
        double temp = fResult[2];
        fResult[2] = fResult[4];
        fResult[4] = temp;
        temp = fResult[5];
        fResult[5] = fResult[6];
        fResult[6] = temp;
        return fResult;
    }
    /**
     * Test of execute method, of class PredictionBolt.
     */
    public void testExecute() {
        System.out.println("execute");
        //Dummy data for test
        String data = "17.49,1014,64,0.5,320";
        double[] expected = new double[7];
        for(int i = 2; i <= 6 ; i++)
        {
            System.out.println("First");
            System.out.println(Double.parseDouble(data.split(",")[i - 2]));
            expected[i] = Double.parseDouble(data.split(",")[i - 2]);
        }
        //Make prediction to get part of expected result
        double[] prediction = makePrediction(data);
        //Rest of expected result is dummy data
        expected[0] = prediction[0];
        expected[1] = prediction[1];
        //Produce data so execute function runs
        Produce(data);
        //Wait for execute to insert data into cassandra
        try 
        {
            Thread.sleep(10000);
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
        //Get actual result by selecting top record from prediction table
        double[] result = Select();
        for(int i = 0; i <= 6; i++)
        {
            System.out.println("Expected: " + expected[i] + "   Actual: " + result[i]);
            assertEquals(expected[i], result[i]);
        }
    }

    /**
     * Test of makePrediction method, of class PredictionBolt.
     */
    public void testMakePrediction() {
        System.out.println(Select());
        System.out.println("makePrediction");
        //Sample data
        String data = "9.52, 1014, 78, 1.34, 209";
        double[] expResult = {0,0};
        double[] result = PredictionBolt.makePrediction(data);
        //Assert predictor is giving correct output of 1s and 0s
        assertTrue((result[0] == 0 || result[0] == 1) && (result[1] == 0 || result[1] == 1));
    }  

}
