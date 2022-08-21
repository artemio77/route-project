package com.gmail.derevets.artem.weatherservice.service;

import com.gmail.derevets.artem.weatherservice.cache.Cache;
import com.gmail.derevets.artem.weatherservice.model.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeduplicationService {

    private final Cache<String, String> alertCache;

    public Optional<Alert> deduplicate(Alert alert) {
        String constructKey = alert.getIdentifier();
        try {
            // Value is not needed because we rely only on the key
            return alertCache.putIfAbsent(constructKey, "empty", Duration.ofDays(7).getSeconds(), ChronoUnit.SECONDS)
                    ? Optional.of(alert)
                    : Optional.empty();
        } catch (Exception e) {
            log.error("Error while alert {} for channelId {}", alert, e);
            return Optional.of(alert);
        }
    }


}
