package com.gmail.derevets.artem.weatherservice.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Value
@With
@Builder
@Jacksonized
public class RssFeedProcessResult {
    String countryId;
    Instant lastUpdated;
}
