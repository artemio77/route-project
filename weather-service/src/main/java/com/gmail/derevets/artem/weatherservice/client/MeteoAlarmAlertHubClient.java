package com.gmail.derevets.artem.weatherservice.client;

import com.gmail.derevets.artem.weatherservice.model.meteoalarm.MeteoAlarmAlertResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class MeteoAlarmAlertHubClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://hub.meteoalarm.org")
            .defaultHeader(HttpHeaders.HOST, "hub.meteoalarm.org")
            .defaultHeader(HttpHeaders.ACCEPT, "*/*")
            .build();

    public Mono<MeteoAlarmAlertResponse> getAlert(String country, String alertId) {
        return webClient
                .get()
                .uri("/warnings/feeds-{country}/{alertId}", country, alertId)
                .retrieve()
                .bodyToMono(MeteoAlarmAlertResponse.class);
    }
}
