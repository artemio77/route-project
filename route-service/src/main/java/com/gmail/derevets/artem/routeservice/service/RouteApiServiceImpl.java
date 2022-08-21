package com.gmail.derevets.artem.routeservice.service;

import com.gmail.derevets.artem.api.model.rest.RouteApi;
import com.gmail.derevets.artem.routeservice.repository.RouteEntityV1Repository;
import com.gmail.derevets.artem.routeservice.mapper.RouteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteApiServiceImpl implements RouteApiService {

    private final RouteMapper routeMapper;
    private final RouteService routeService;
    private final RouteEntityV1Repository routeRepository;

    @Override
    public Mono<RouteApi> createRoute(RouteApi currentRoute) {
        log.debug("Create Route {}", currentRoute);
        return Mono.justOrEmpty(currentRoute)
                .map(routeMapper::mapEntity)
                .flatMap(routeService::createRoute)
                .map(routeMapper::map);
    }

    @Override
    public Mono<RouteApi> enrichRoute(UUID routeId) {
        return Mono.justOrEmpty(routeId)
                .flatMap(routeService::findById)
                .flatMap(routeService::enrichRoute)
                .map(routeMapper::map);
    }

    @Override
    public Mono<RouteApi> getRoute(UUID routeId) {
        return Mono.from(routeRepository.findById(routeId))
                .map(routeMapper::map);
    }
}

