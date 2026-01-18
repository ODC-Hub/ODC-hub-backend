package com.odc.hub.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;

@TestConfiguration
public class TestcontainersConfiguration {

    static final MongoDBContainer mongo = new MongoDBContainer("mongo:7");

    static {
        mongo.start();
        System.setProperty("spring.data.mongodb.uri", mongo.getReplicaSetUrl());
    }

    @Bean
    public MongoDBContainer mongoDBContainer() {
        return mongo;
    }
}
