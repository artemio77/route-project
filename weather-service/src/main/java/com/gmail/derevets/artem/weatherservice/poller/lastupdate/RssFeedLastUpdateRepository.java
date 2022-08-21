package com.gmail.derevets.artem.weatherservice.poller.lastupdate;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;



public interface RssFeedLastUpdateRepository extends R2dbcRepository<RssFeedLastUpdateV1, String> {

    @Query("select case when count(id)> 0 then true else false end FROM rss_feed_last_update rflu where rflu.id=:id and rflu.last_updated != :lastUpdated or rflu.last_updated is null")
    Mono<Boolean> isOutDated(String id, Instant lastUpdated);
}
