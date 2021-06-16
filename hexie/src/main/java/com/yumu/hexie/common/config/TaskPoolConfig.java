package com.yumu.hexie.common.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskPoolConfig {
	
	@Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        /*
         * threadcount = tasks/(1/taskcost) =tasks*taskcost =  (500~1000)*0.5 = 50~100 个线程。corePoolSize设置应该大于50。根据8020原则，如果80%的每秒任务数小于800，那么corePoolSize设置为80即可
         * 现假设每秒20-50个任务，每个线程处理时间为0.5秒，那么大概需要10-25哥线程数，取80%为20。
         */
        executor.setCorePoolSize(25);
        /*
         * maxPoolSize = (max(tasks)- queueCapacity)/(1/taskcost)
         */
        executor.setMaxPoolSize(30);
        /*
         * queueCapacity = (coreSizePool/taskcost)*responsetime
         * 计算可得 queueCapacity = 20/0.5*1 = 40。意思是队列里的线程可以等待1s，超过了的需要新开线程来执行切记不能设置为Integer.MAX_VALUE，这样队列会很大，线程数只会保持在corePoolSize大小，当任务陡增时，不能新开线程来执行，响应时间会随之陡增。
         * 假设响应时间为1秒，则
         */
        executor.setQueueCapacity(40);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("taskExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
	
}
