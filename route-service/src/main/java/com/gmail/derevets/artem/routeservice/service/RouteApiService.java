package com.gmail.derevets.artem.routeservice.service;

import com.gmail.derevets.artem.api.model.rest.RouteApi;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RouteApiService {

    Mono<RouteApi> createRoute(RouteApi route);

    Mono<RouteApi> enrichRoute(UUID routeId);

    Mono<RouteApi> getRoute(UUID routeId);
}
