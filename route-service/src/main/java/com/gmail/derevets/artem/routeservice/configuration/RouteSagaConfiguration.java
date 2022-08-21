package com.gmail.derevets.artem.routeservice.configuration;

import com.gmail.derevets.artem.routeservice.workflow.RouteSagaStepHandler;
import io.eventuate.tram.reactive.commands.consumer.ReactiveCommandDispatcher;
import io.eventuate.tram.sagas.reactive.participant.ReactiveSagaCommandDispatcherFactory;
import io.eventuate.tram.spring.optimisticlocking.OptimisticLockingDecoratorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
@Import(OptimisticLockingDecoratorConfiguration.class)
public class RouteSagaConfiguration {

    @Bean
    public Scheduler enrichScheduler() {
        return Schedulers.newBoundedElastic(8, Integer.MAX_VALUE, "enrichBoundedElasticPool");
    }

    @Bean
    public ReactiveCommandDispatcher orderCommandDispatcher(
            RouteSagaStepHandler routeSagaStepHandler,
            ReactiveSagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
        return sagaCommandDispatcherFactory.make(
                "routeCommandDispatcher",
                routeSagaStepHandler.commandHandlerDefinitions(enrichScheduler()));
    }

}
