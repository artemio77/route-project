package com.gmail.derevets.artem.sagaorchestrator.saga.handler;

import com.gmail.derevets.artem.api.command.StartCreateRouteSagaCommand;
import com.gmail.derevets.artem.sagaorchestrator.data.SagaData;
import com.gmail.derevets.artem.sagaorchestrator.saga.CreateRouteSaga;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.reactive.commands.consumer.ReactiveCommandHandlers;
import io.eventuate.tram.reactive.commands.consumer.ReactiveCommandHandlersBuilder;
import io.eventuate.tram.sagas.reactive.orchestration.ReactiveSagaInstanceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateRouteSagaCommandHandler {
    private final CreateRouteSaga createRouteSaga;

    private final ReactiveSagaInstanceFactory sagaInstanceFactory;

    private final TransactionalOperator transactionalOperator;

    private final static Scheduler scheduler = Schedulers.boundedElastic();

    public ReactiveCommandHandlers getCommandHandlers() {
        return ReactiveCommandHandlersBuilder
                .fromChannel("rt.route.saga.commands")
                .onMessage(StartCreateRouteSagaCommand.class, this::handleStartCreateRouteSagaCommand)
                .build();
    }

    @NewSpan
    @Transactional
    public Mono<Message> handleStartCreateRouteSagaCommand(CommandMessage<StartCreateRouteSagaCommand> command) {
        final Map<String, Object> routeIdDetailsMapKey = Map.of(SagaData.ROUTE_ID_DETAILS_MAP_KEY, command.getCommand().routeId());
        final SagaData build = SagaData.builder()
                .details(routeIdDetailsMapKey)
                .build();
        return sagaInstanceFactory.create(createRouteSaga, build)
                .publishOn(scheduler)
                .then(Mono.just(withSuccess()))
                .onErrorResume(throwable -> Mono.just(withFailure(throwable.getMessage())));
    }

}
