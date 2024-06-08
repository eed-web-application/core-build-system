package edu.stanford.slac.core_build_system;

import edu.stanford.slac.core_build_system.config.CoreBuildProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(CoreBuildProperties.class)
@ComponentScan({"edu.stanford.slac"})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
