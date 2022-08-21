package com.gmail.derevets.artem.sagaorchestrator.configuration;

import com.gmail.derevets.artem.sagaorchestrator.saga.handler.CreateRouteSagaCommandHandler;
import io.eventuate.tram.reactive.commands.consumer.ReactiveCommandDispatcher;
import io.eventuate.tram.reactive.commands.consumer.ReactiveCommandDispatcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteSagaConfiguration {

    @Bean
    public ReactiveCommandDispatcher commandDispatcher(
            ReactiveCommandDispatcherFactory commandDispatcherFactory,
            CreateRouteSagaCommandHandler routeSagaEventConsumer) {
        return commandDispatcherFactory.make("routeSagaCommandDispatcher",
                routeSagaEventConsumer.getCommandHandlers());
    }
}
