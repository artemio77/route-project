package com.gmail.derevets.artem.weatherservice.service;

import com.gmail.derevets.artem.weatherservice.client.MeteoAlarmAlertHubClient;
import com.gmail.derevets.artem.weatherservice.client.MeteoAlarmClient;
import com.gmail.derevets.artem.weatherservice.mapper.AlertMapper;
import com.gmail.derevets.artem.weatherservice.model.meteoalarm.MeteoAlarmRssFeedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.text.MessageFormat;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeteoAlarmService {

    private final MeteoAlarmClient meteoAlarmClient;
    private final MeteoAlarmAlertHubClient meteoAlarmAlertHubClient;
    private final RssFeedUpdateService rssFeedUpdateService;
    private final DeduplicationService deduplicationService;
    private final AlertMapper alertMapper;

    public Mono<MeteoAlarmRssFeedResponse> pollAlertForCountry(String country, Scheduler scheduler) {
        return getCountryRssAlarms(country)
                .filterWhen(meteoAlarmRssFeedResponse -> rssFeedUpdateService.isFeedHaveUpdates(country, meteoAlarmRssFeedResponse)
                        .publishOn(scheduler))
                .doOnNext(meteoAlarmRssFeedResponse -> log.info("Update date was changed for {}. Start detect new alerts", country))
                .flatMap(meteoAlarmRssFeedResponse -> processMeteoAlertInParallel(meteoAlarmRssFeedResponse, country)
                        .publishOn(scheduler))
                .onErrorContinue((throwable, o) -> log.error("Exception while process {} rss feed", country, throwable));
    }

    public Mono<MeteoAlarmRssFeedResponse> processMeteoAlertInParallel(MeteoAlarmRssFeedResponse meteoAlarmRssFeedResponse,
                                                                       String country) {
        return Flux.fromIterable(meteoAlarmRssFeedResponse.getEntry())
                .distinct(MeteoAlarmRssFeedResponse.MeteoAlarmEntry::getIdentifier)
                .parallel()
                .flatMap(meteoAlarmEntry -> meteoAlarmEntry.getAlertId(country).map(Mono::just).orElseGet(Mono::empty))
                .flatMap(alertId -> meteoAlarmAlertHubClient.getAlert(country, alertId)
                        .doOnError((throwable) -> log.error("Exception while make get alert call {} for {}", alertId, country, throwable)))
                .map(meteoAlarmAlertResponse -> alertMapper.map(meteoAlarmAlertResponse))
                .map(alert -> deduplicationService.deduplicate(alert))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .doOnNext(alert -> log.info("Detect new alert {}", alert))
                .sequential()
                .then()
                .thenReturn(meteoAlarmRssFeedResponse);
    }


    public Mono<MeteoAlarmRssFeedResponse> getCountryRssAlarms(String country) {
        return meteoAlarmClient.getRssFeed(country)
                .checkpoint(MessageFormat.format("Get {0} alert rss feed", country));
    }

}
