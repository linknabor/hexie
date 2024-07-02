package com.yumu.hexie;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.yumu.hexie.common.config.AppConfig;
import com.yumu.hexie.service.ScheduleService;

import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppConfig.class)
public class BkTest extends TestCase {
	
	@Resource
	private ScheduleService scheduleService;
	
	@Test
	public void testSendMiniProgramData() {
		
		scheduleService.westData2Beyondsoft();
	}

}
