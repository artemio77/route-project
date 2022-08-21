package com.gmail.derevets.artem.routeservice.repository;

import com.gmail.derevets.artem.routeservice.domain.RouteInformationEntityV1;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface RouteInformationEntityV1Repository extends ReactiveCrudRepository<RouteInformationEntityV1, UUID> {
}