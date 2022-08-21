package com.gmail.derevets.artem.sagaorchestrator;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
@EnableR2dbcRepositories
public class SagaOrchestratorApplication {

    public static void main(String[] args) {
        ReactorDebugAgent.init();
        SpringApplication.run(SagaOrchestratorApplication.class, args);
    }
}
