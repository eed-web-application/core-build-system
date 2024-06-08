package edu.stanford.slac.core_build_system.config;


import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Log4j2
@Configuration
@EnableScheduling
@Profile("async-build-processing")
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);  // Set the pool size to the desired number of concurrent threads
        taskScheduler.setThreadNamePrefix("processing-task-");
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);  // Wait for tasks to complete on shutdown
        taskScheduler.setAwaitTerminationSeconds(30);  // Maximum wait time in seconds
        return taskScheduler;
    }
}
