package com.yumu.hexie.web.task;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.web.BaseController;

@RestController
@RequestMapping(value = "/task")
public class TaskController extends BaseController {

	@Autowired
	private Executor taskExecutor;

	@RequestMapping(value = "/threadInfo", method = RequestMethod.GET)
	private Map<String, String> threadInfo() {

		Map<String, String> map = new HashMap<>();
		Object[] myThread = { taskExecutor };
		for (Object thread : myThread) {
			ThreadPoolTaskExecutor threadTask = (ThreadPoolTaskExecutor) thread;
			ThreadPoolExecutor threadPoolExecutor = threadTask.getThreadPoolExecutor();
			
			map.put("任务数-->", String.valueOf(threadPoolExecutor.getTaskCount()));
			map.put("完成任务数-->", String.valueOf(threadPoolExecutor.getCompletedTaskCount()));
			map.put("当前活跃的线程数-->", String.valueOf(threadPoolExecutor.getActiveCount()));
			map.put("队列等待数-->", String.valueOf(threadPoolExecutor.getQueue().size()));
			map.put("当前可用队列长度-->", String.valueOf(threadPoolExecutor.getQueue().remainingCapacity()));
			map.put("当前时间-->", String.valueOf(LocalDate.now()));
		}
		return map;
	}

}
