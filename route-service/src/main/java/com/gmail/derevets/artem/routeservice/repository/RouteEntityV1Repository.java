package com.gmail.derevets.artem.routeservice.repository;

import com.gmail.derevets.artem.routeservice.domain.RouteEntityV1;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface RouteEntityV1Repository extends ReactiveCrudRepository<RouteEntityV1, UUID> {
}