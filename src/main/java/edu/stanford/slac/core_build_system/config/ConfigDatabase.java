package edu.stanford.slac.core_build_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configuration for the database
 */
@Configuration
@EnableMongoRepositories(basePackages = "edu.stanford.slac.core_build_system.repository")
public class ConfigDatabase {

}

