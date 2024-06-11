package edu.stanford.slac.core_build_system.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Clock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Profile("test")
@Configuration
@EnableScheduling
public class ThreadPoolTestConfig {
    @Bean
    @Primary
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);  // Set the pool size to the desired number of concurrent threads
        taskScheduler.setThreadNamePrefix("processing-task-");
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);  // Wait for tasks to complete on shutdown
        taskScheduler.setAwaitTerminationSeconds(30);
        return taskScheduler;
    }
}
