package com.gmail.derevets.artem.routeservice.service;

import com.gmail.derevets.artem.api.command.StartCreateRouteSagaCommand;
import com.gmail.derevets.artem.api.model.rest.RouteStatus;
import com.gmail.derevets.artem.routeservice.domain.RouteEntityV1;
import com.gmail.derevets.artem.routeservice.repository.RouteEntityV1Repository;
import com.gmail.derevets.artem.routeservice.repository.RouteInformationEntityV1Repository;
import io.eventuate.tram.reactive.commands.producer.ReactiveCommandProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final ReactiveCommandProducer commandProducer;
    private final RouteInformationService routeInformationService;
    private final RouteEntityV1Repository routeRepository;
    private final RouteInformationEntityV1Repository routeInformationEntityV1Repository;

    private final static String commandChannel = "rt.route.saga.commands";
    private final static String replyChannel = "rt.route.saga.commands.reply";

    @Override
    public Mono<RouteEntityV1> createRoute(RouteEntityV1 route) {
        return routeRepository.save(route)
                .doOnNext(entity -> commandProducer.send(commandChannel,
                        StartCreateRouteSagaCommand.builder()
                                .routeId(entity.getId())
                                .build(),
                        replyChannel,
                        Collections.emptyMap()).subscribe());
    }

    @Override
    public Mono<RouteEntityV1> enrichRoute(RouteEntityV1 route) {
        return Mono.just(route)
                .flatMap(routeInformationService::createRouteInformation)
                .flatMapMany(routeInformationEntityV1Repository::saveAll)
                .then(Mono.just(route))
                .map(routeEntity -> {
                    routeEntity.setStatus(RouteStatus.ENRICHED);
                    return routeEntity;
                })
                .flatMap(routeRepository::save);
    }

    @Override
    public Mono<RouteEntityV1> findById(UUID routeId) {
        return routeRepository.findById(routeId);
    }
}
