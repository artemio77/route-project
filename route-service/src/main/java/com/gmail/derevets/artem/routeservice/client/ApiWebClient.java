package com.gmail.derevets.artem.routeservice.client;

import com.gmail.derevets.artem.routeservice.client.route.RouteApiWebClient;
import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto;
import com.gmail.derevets.artem.routeservice.domain.RouteEntityV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.BiFunction;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiWebClient implements RouteApiWebClient {

    private final WebClient routeV8WebClient;

    @Override
    public Mono<RouteCalculateDto> calculateRouteWay(RouteEntityV1 route) {
        return routeV8WebClient
                .get()
                .uri(uriBuilder -> calculateUriFunction.apply(route, uriBuilder))
                .retrieve()
                .bodyToMono(RouteCalculateDto.class);
    }

    private static final BiFunction<RouteEntityV1, UriBuilder, URI> calculateUriFunction = (route, uriBuilder) -> {
        var startPoint = route.getStartPoint();
        var endPoint = route.getEndPoint();
        var origin = String.join(",", startPoint.getLatitude().toString(),
                startPoint.getLatitude().toString());
        var destination = String.join(",", endPoint.getLatitude().toString(),
                endPoint.getLongitude().toString());
        return uriBuilder
                .queryParam("transportMode", "car")
                .queryParam("return", "summary")
                .queryParam("alternatives", "6")
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .build();
    };
}
