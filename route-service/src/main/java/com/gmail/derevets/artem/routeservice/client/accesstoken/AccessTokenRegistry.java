package com.gmail.derevets.artem.routeservice.client.accesstoken;

import com.gmail.derevets.artem.routeservice.client.accesstoken.dto.AccessTokenDto;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccessTokenRegistry {

    private final Cache<String, AccessTokenDto> accessTokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public Mono<AccessTokenDto> getAccessToken(ExchangeFunction exchangeFunction) {
        ClientRequest accessTokenRequest = ClientRequest
                .create(HttpMethod.POST, URI.create("https://account.api.here.com/oauth2/token"))
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, buildAuthorizationHeader()))
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .build();
        return Mono.justOrEmpty(accessTokenCache.getIfPresent("route-service"))
                .switchIfEmpty(exchangeFunction.exchange(accessTokenRequest)
                        .flatMap(clientResponse -> clientResponse.bodyToMono(AccessTokenDto.class))
                        .doOnNext(accessTokenDto -> accessTokenCache.put("route-service", accessTokenDto)));
    }

    private String buildAuthorizationHeader() {
        var nonce = UUID.randomUUID().toString();
        var timestamp = Instant.now().getEpochSecond();
        var body = new HashMap<String, List<String>>();
        body.put("grant_type", Collections.singletonList("client_credentials"));
        final String consumerKey = "CGcTQU-3wSz41m0gC2uInw";
        final String consumerSecret = "Z5UTX7t_dXjsXBw6XHFRGaw2lMdKGMKRjk1qy6wjAnzt6wbYYF6_rUTz4hrZJ4yQOnTTqDDcg9QTkL167BwdZg";
        String signature = new SignatureCalculator(consumerKey, consumerSecret)
                .calculateSignature("POST", "https://account.api.here.com/oauth2/token",
                        timestamp,
                        nonce,
                        SignatureMethod.HMACSHA256,
                        "1.0",
                        body,
                        null);
        return new SignatureCalculator(consumerKey, consumerSecret)
                .constructAuthHeader(signature, nonce, timestamp, SignatureMethod.HMACSHA256);
    }

}
