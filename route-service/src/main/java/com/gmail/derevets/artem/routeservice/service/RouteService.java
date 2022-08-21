package com.gmail.derevets.artem.routeservice.service;

import com.gmail.derevets.artem.routeservice.domain.RouteEntityV1;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RouteService {

    Mono<RouteEntityV1> createRoute(RouteEntityV1 route);

    Mono<RouteEntityV1> enrichRoute(RouteEntityV1 route);

    Mono<RouteEntityV1> findById(UUID routeId);
}
