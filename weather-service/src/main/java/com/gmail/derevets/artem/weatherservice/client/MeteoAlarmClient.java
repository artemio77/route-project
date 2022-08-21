package com.gmail.derevets.artem.weatherservice.client;

import com.gmail.derevets.artem.weatherservice.model.meteoalarm.MeteoAlarmRssFeedResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.ACCEPT_ENCODING;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.USER_AGENT;


@Component
public class MeteoAlarmClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://feeds.meteoalarm.org")
            .defaultHeader(USER_AGENT, "Micronaut HTTP Client")
            .defaultHeader(ACCEPT, "*/*")
            .defaultHeader(CONTENT_TYPE, "*/*")
            .defaultHeader(ACCEPT_ENCODING, "gzip, deflate, br")
            .build();

    public Mono<MeteoAlarmRssFeedResponse> getRssFeed(String country) {
        return webClient.get()
                .uri("/feeds/meteoalarm-legacy-atom-{country}", country)
                .retrieve()
                .bodyToMono(MeteoAlarmRssFeedResponse.class);
    }
}
