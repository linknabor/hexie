package com.yumu.hexie.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HexieConfigApplication.class)
public class AppTest extends TestCase {

	@Value(value = "${eureka.instance.instance-id}")
	String port;
	
	@Test
	public void testValue() {
		
		System.out.println("~~~~" + port);
	}
	
}
