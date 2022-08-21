package com.gmail.derevets.artem.routeservice.client.route;

import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto;
import com.gmail.derevets.artem.routeservice.domain.RouteEntityV1;
import reactor.core.publisher.Mono;

public interface RouteApiWebClient {

    Mono<RouteCalculateDto> calculateRouteWay(RouteEntityV1 routeEntityV1);

}
