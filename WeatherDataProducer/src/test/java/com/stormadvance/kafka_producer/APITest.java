/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.stormadvance.kafka_producer;

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
public class APITest {
    
    public APITest() {
    }
    
    @Test
    public void testGetAPI() throws Exception {
        System.out.println("getAPI");
        String[] result = API.getAPI().split(",");
        for(int i = 0; i < 5; i++)
        {
            assertNotNull(result[i]);
        }

    }
    
}
