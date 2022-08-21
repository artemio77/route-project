package com.gmail.derevets.artem.weatherservice.scheduler;

import com.gmail.derevets.artem.weatherservice.model.RssFeedProcessResult;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

public interface RssAlertFeedProcessService {

    Mono<RssFeedProcessResult> processRssFeed();

    Scheduler getScheduler();

    String getCountry();
}
