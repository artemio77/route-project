package com.gmail.derevets.artem.routeservice.service;

import com.gmail.derevets.artem.routeservice.client.route.RouteApiWebClient;
import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto;
import com.gmail.derevets.artem.routeservice.domain.RouteEntityV1;
import com.gmail.derevets.artem.routeservice.domain.RouteInformationEntityV1;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.gmail.derevets.artem.routeservice.mapper.RouteInformationMapper;
import com.gmail.derevets.artem.routeservice.mapper.RouteMapper;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RouteInformationService {

    private final RouteApiWebClient routeApiWebClient;
    private final RouteInformationMapper routeMapper;

    private final static RateLimiter rateLimiter = RateLimiter.of("calculateRouteWayRateLimit",
            RateLimiterConfig.custom()
                    .limitForPeriod(10)
                    .limitRefreshPeriod(Duration.ofSeconds(1))
                    .timeoutDuration(Duration.ofSeconds(3))
                    .build());

    public Mono<List<RouteInformationEntityV1>> createRouteInformation(RouteEntityV1 route) {
        return routeApiWebClient.calculateRouteWay(route)
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .map(this::convertRouteInformation);
    }

    private List<RouteInformationEntityV1> convertRouteInformation(RouteCalculateDto routeCalculateDto) {
        return routeCalculateDto.getRoutes()
                .stream()
                .map(RouteCalculateDto.RouteDto::getSections)
                .flatMap(Collection::stream)
                .map(routeMapper::map)
                .map(routeInformationApi -> routeInformationApi.withCreatedDate(Date.from(Instant.now())))
                .map(routeMapper::mapEntity)
                .collect(Collectors.toList());
    }
}
