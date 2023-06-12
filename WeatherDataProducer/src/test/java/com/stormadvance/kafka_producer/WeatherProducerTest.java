/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.stormadvance.kafka_producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.StreamSupport;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class WeatherProducerTest {
    
    public WeatherProducerTest() {
    }
    public static String Consume()
    {
        //List to hold data in topic
        List<String> result = new ArrayList<String>();
        //Kafka properties
        Properties props = new Properties();
        props.put( "bootstrap.servers", "RT01:9092" );
        props.put( "key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer" );
        props.put( "value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer" );
        props.put( "auto.offset.reset", "earliest" );
        props.put( "enable.auto.commit", "false" );
        props.put( "group.id", "Test" );
        
        //Create consumer
        try( KafkaConsumer<String, String> consumer = new KafkaConsumer<>( props ) ){
            //subscribe to topic
            consumer.subscribe( Collections.singletonList( "PredictionTopic" ) );
            
            //Store all values in list break when done
            while( true ){
                // poll with a 100 ms timeout
                ConsumerRecords<String, String> records = consumer.poll( 100 );
                if( records.isEmpty() ) continue;
                StreamSupport.stream( records.spliterator(), false ).forEach( 
                        r -> result.add(r.value()));
                break;
            }
        }
        //get last record in list, last produced message in topic
        return result.get(result.size() - 1);
    }

    /**
     * Test of main method, of class WeatherProducer.
     */
    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = null;
        //Produce data
        WeatherProducer.main(args);
        String Produced = WeatherProducer.tData;
        //Wait 5 seconds to allow kafka topic to receive data
        try 
        {
            Thread.sleep(5000);
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
        //Consume data
        String Consumed = Consume();
        //Assert data produced is the same as data consumed
        assertEquals(Produced,Consumed);
    }
    
}
