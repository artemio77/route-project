package com.gmail.derevets.artem.routeservice.workflow;

import com.gmail.derevets.artem.api.command.StartCreateRouteSagaCommand.SagaEnrichRouteCommand;
import com.gmail.derevets.artem.routeservice.service.RouteApiService;
import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.reactive.commands.consumer.ReactiveCommandHandlers;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import io.eventuate.tram.sagas.reactive.participant.ReactiveSagaCommandHandlersBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteSagaStepHandler {

    private final RouteApiService routeApiService;

    public ReactiveCommandHandlers commandHandlerDefinitions(Scheduler enrichScheduler) {
        return ReactiveSagaCommandHandlersBuilder
                .fromChannel("rt.create.route.saga.commands")
                .onMessage(SagaEnrichRouteCommand.class, enrichRouteCommandCommandMessage -> enrichRoute(enrichRouteCommandCommandMessage, enrichScheduler))
                .build();
    }

    @SneakyThrows
    private Mono<Message> enrichRoute(CommandMessage<SagaEnrichRouteCommand> message, Scheduler enrichScheduler) {
        return Mono.justOrEmpty(message)
                .publishOn(enrichScheduler)
                .map(CommandMessage::getCommand)
                .map(SagaEnrichRouteCommand::routeId)
                .doOnNext(routeId -> log.info("Start Enrich route, {}", routeId))
                .flatMap(uuid -> routeApiService.enrichRoute(uuid).publishOn(enrichScheduler))
                .doOnNext(routeApiSignal -> log.info("Enriched route, {}", routeApiSignal))
                .map(CommandHandlerReplyBuilder::withSuccess)
                .onErrorResume(throwable -> {
                    log.error("Enrich route was failed ", throwable);
                    return Mono.just(withFailure(message));
                });
    }

}
