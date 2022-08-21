package com.gmail.derevets.artem.weatherservice.cache;


import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapCache;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class RedisMessageCache implements Cache<String, String> {

    private final RMapCache<String, String> cacheMap;

    @Override
    public void store(String key, String value, long ttl, ChronoUnit ttlUnit) {
        cacheMap.fastPut(key, value, ttl, TimeUnit.of(ttlUnit));
    }

    @Override
    public boolean putIfAbsent(String key, String value, long ttl, ChronoUnit ttlUnit) {
        return cacheMap.fastPutIfAbsent(key, value, ttl, TimeUnit.of(ttlUnit));
    }

    @Override
    public boolean isPresent(String key) {
        return cacheMap.containsKey(key);
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(cacheMap.get(key));
    }
}
