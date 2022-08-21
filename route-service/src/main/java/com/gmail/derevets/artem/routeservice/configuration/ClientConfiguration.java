package com.gmail.derevets.artem.routeservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gmail.derevets.artem.routeservice.client.accesstoken.AccessTokenRegistry;
import com.gmail.derevets.artem.routeservice.client.accesstoken.dto.AccessTokenDto;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ClientConfiguration {

    private final AccessTokenRegistry accessTokenRegistry;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules();
    }

    @Bean
    public WebClient routeV8WebClient() {
        return WebClient.builder()
                .baseUrl("https://router.hereapi.com/v8/routes")
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(oauth2AuthorizationHeaderFilterFunction());
                    exchangeFilterFunctions.add(WebClientFilter.logRequest());
                    exchangeFilterFunctions.add(WebClientFilter.logResponse());
                })
                .build();
    }


    @Bean
    public ExchangeFilterFunction oauth2AuthorizationHeaderFilterFunction() {
        return (clientRequest, nextFilter) -> accessTokenRegistry.getAccessToken(nextFilter)
                .map(AccessTokenDto::accessToken)
                .map(token -> setBearer(clientRequest, token))
                .flatMap(nextFilter::exchange);

    }

    private ClientRequest setBearer(ClientRequest request, String token) {
        return ClientRequest.from(request)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
    }

}
