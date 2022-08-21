package com.gmail.derevets.artem.routeservice.controller;

import com.gmail.derevets.artem.api.model.rest.RouteApi;
import com.gmail.derevets.artem.routeservice.service.RouteApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/route")
public class RouteController {

    private final RouteApiService routeApiService;

    @PostMapping
    public Mono<RouteApi> createRoute(@RequestBody RouteApi routeApi) {
        log.info("POST Request body{}", routeApi);
        return routeApiService.createRoute(routeApi);
    }

    @PostMapping("/enrich/{routeId}")
    public Mono<RouteApi> enrichRoute(@PathVariable UUID routeId) {
        return routeApiService.enrichRoute(routeId);
    }

    @GetMapping("/{routeId}")
    public Mono<RouteApi> getRoute(@PathVariable UUID routeId) {
        return routeApiService.getRoute(routeId);
    }

}
