package com.gmail.derevets.artem.routeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
@EnableReactiveCouchbaseRepositories(basePackages = "com.gmail.derevets.artem.routeservice.repository")
public class RouteServiceApplication {

    public static void main(String[] args) {
        ReactorDebugAgent.init();
        SpringApplication.run(RouteServiceApplication.class, args);
    }
}


