package com.gmail.derevets.artem.weatherservice.service;

import com.gmail.derevets.artem.weatherservice.mapper.RssLastUpdateMapper;
import com.gmail.derevets.artem.weatherservice.model.RssFeedProcessResult;
import com.gmail.derevets.artem.weatherservice.model.meteoalarm.MeteoAlarmRssFeedResponse;
import com.gmail.derevets.artem.weatherservice.poller.lastupdate.RssFeedLastUpdateV1;
import com.gmail.derevets.artem.weatherservice.poller.lastupdate.RssFeedLastUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RssFeedUpdateService {

    private final RssLastUpdateMapper rssLastUpdateMapper;
    private final RssFeedLastUpdateRepository rssFeedLastUpdateRepository;

    public RssFeedLastUpdateV1 map(RssFeedProcessResult rssFeedProcessResult) {
        return rssLastUpdateMapper.map(rssFeedProcessResult);
    }

    @Transactional
    public Mono<RssFeedLastUpdateV1> createIfNotExist(String country) {
        return findById(country)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Rss feed record not present for {}", country);
                    return save(RssFeedLastUpdateV1.builder()
                            .id(country)
                            .build());
                }));
    }

    @Transactional
    public Mono<RssFeedLastUpdateV1> findById(String countryId) {
        return rssFeedLastUpdateRepository.findById(countryId);
    }

    @Transactional
    public Mono<Boolean> isFeedHaveUpdates(String country, MeteoAlarmRssFeedResponse feedResponse) {
        return rssFeedLastUpdateRepository.isOutDated(country, feedResponse.getUpdated());
    }

    @Transactional
    public Mono<RssFeedLastUpdateV1> update(RssFeedLastUpdateV1 rssFeedLastUpdate) {
        return rssFeedLastUpdateRepository.save(rssFeedLastUpdate)
                .doOnSuccess(rssFeedLastUpdate1 -> log.debug("Update RssFeedLastUpdate : {}", rssFeedLastUpdate1));
    }


    @Transactional
    public Mono<RssFeedLastUpdateV1> save(RssFeedLastUpdateV1 rssFeedLastUpdate) {
        return Mono.from(rssFeedLastUpdateRepository.save(rssFeedLastUpdate)
                .doOnSuccess(rssFeedLastUpdate1 -> log.debug("Register new alert scheduler : {}", rssFeedLastUpdate1)));
    }

}
