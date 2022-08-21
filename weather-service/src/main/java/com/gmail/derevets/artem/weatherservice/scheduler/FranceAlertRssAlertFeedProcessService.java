package com.gmail.derevets.artem.weatherservice.scheduler;

import com.gmail.derevets.artem.weatherservice.model.RssFeedProcessResult;
import com.gmail.derevets.artem.weatherservice.model.meteoalarm.MeteoAlarmRssFeedResponse;
import com.gmail.derevets.artem.weatherservice.service.MeteoAlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class FranceAlertRssAlertFeedProcessService implements RssAlertFeedProcessService {

    private final MeteoAlarmService meteoAlarmService;

    private static final Scheduler scheduler = Schedulers.boundedElastic();

    @Override
    public Mono<RssFeedProcessResult> processRssFeed() {
        return meteoAlarmService.pollAlertForCountry(getCountry(), scheduler)
                .map(MeteoAlarmRssFeedResponse::getUpdated)
                .map(lastUpdated -> RssFeedProcessResult.builder()
                        .countryId(getCountry())
                        .lastUpdated(lastUpdated)
                        .build());
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public String getCountry() {
        return "france";
    }
}
