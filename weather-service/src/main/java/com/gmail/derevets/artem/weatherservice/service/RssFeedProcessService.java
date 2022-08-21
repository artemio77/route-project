package com.gmail.derevets.artem.weatherservice.service;

import com.gmail.derevets.artem.weatherservice.poller.lastupdate.RssFeedLastUpdateV1;
import com.gmail.derevets.artem.weatherservice.scheduler.RssAlertFeedProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class RssFeedProcessService {

    private final RssFeedUpdateService rssFeedUpdateService;

    private final static Scheduler common = Schedulers.boundedElastic();

    public Mono<RssFeedLastUpdateV1> process(RssAlertFeedProcessService rssAlertFeedProcessService) {
        return Mono.just(rssAlertFeedProcessService)
                .flatMap(alertScheduler -> rssFeedUpdateService.createIfNotExist(alertScheduler.getCountry()).publishOn(common))
                .thenReturn(rssAlertFeedProcessService)
                .doOnNext(service -> log.info("Start monitoring {} alerts", service.getCountry()))
                .flatMap(rssAlertFeedProcessService1 -> rssAlertFeedProcessService1.processRssFeed().publishOn(common))
                .map(rssFeedUpdateService::map)
                .flatMap(rssFeedLastUpdate -> rssFeedUpdateService.update(rssFeedLastUpdate).publishOn(common))
                .doFinally(rssFeedLastUpdate -> log.info("Finish monitoring {} alerts", rssAlertFeedProcessService.getCountry()));
    }

}
