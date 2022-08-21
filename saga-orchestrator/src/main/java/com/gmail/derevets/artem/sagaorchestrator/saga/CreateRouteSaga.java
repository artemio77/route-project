package com.gmail.derevets.artem.sagaorchestrator.saga;

import com.gmail.derevets.artem.api.command.StartCreateRouteSagaCommand.SagaEnrichRouteCommand;
import com.gmail.derevets.artem.sagaorchestrator.data.SagaData;
import com.gmail.derevets.artem.sagaorchestrator.data.SagaStatus;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder;
import io.eventuate.tram.sagas.reactive.orchestration.ReactiveSagaDefinition;
import io.eventuate.tram.sagas.reactive.simpledsl.SimpleReactiveSaga;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateRouteSaga implements SimpleReactiveSaga<SagaData> {

    private ReactiveSagaDefinition<SagaData> saga = step()
            .invokeLocal(sagaData -> changeStatus(sagaData, SagaStatus.IN_PROGRESS))
            .step()
            .invokeParticipant(this::enrichRoute)
            .step()
            .invokeLocal(sagaData -> changeStatus(sagaData, SagaStatus.COMPLETED))
            .build();

    private Mono<CommandWithDestination> enrichRoute(SagaData sagaData) {
        return Mono.just(sagaData)
                .map(SagaData::details)
                .map(stringObjectMap -> CommandWithDestinationBuilder
                        .send(SagaEnrichRouteCommand.builder()
                                .routeId((UUID) stringObjectMap.get(SagaData.ROUTE_ID_DETAILS_MAP_KEY))
                                .build())
                        .to("rt.create.route.saga.commands")
                        .build());
    }

    @Override
    public String getSagaType() {
        return "rt.create.route.saga.commands";
    }

    @Override
    public Mono<Void> onStarting(String sagaId, SagaData sagaData) {
        return Mono.just(sagaData)
                .doOnNext(data -> {
                    var routeId = (String) sagaData.details().get(SagaData.ROUTE_ID_DETAILS_MAP_KEY);
                    log.info("Start Create Route Saga {} route {}", sagaId, routeId);
                })
                .then(Mono.empty());
    }

    @Override
    public ReactiveSagaDefinition<SagaData> getSagaDefinition() {
        return saga;
    }

    @Override
    public Mono<Void> onSagaCompletedSuccessfully(String sagaId, SagaData sagaData) {
        return Mono.just(sagaData)
                .doOnNext(data -> {
                    var routeId = (String) sagaData.details().get(SagaData.ROUTE_ID_DETAILS_MAP_KEY);
                    log.info("Completed Create Route Saga {} route {}", sagaId, routeId);
                })
                .then(Mono.empty());
    }

    @Override
    public Mono<Void> onSagaRolledBack(String sagaId, SagaData sagaData) {
        return Mono.just(sagaData)
                .doOnNext(data -> {
                    var routeId = (String) sagaData.details().get(SagaData.ROUTE_ID_DETAILS_MAP_KEY);
                    log.info("Rollback Create Route Saga {} route {}", sagaId, routeId);
                })
                .then(Mono.empty());
    }


    private Mono<?> changeStatus(SagaData sagaData, SagaStatus newStatus) {
        return Mono.fromCallable(() -> sagaData.status(newStatus));
    }
}
