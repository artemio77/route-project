package com.gmail.derevets.artem.api.model.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Value
@With
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class PlaceApi {

    String type;
    PointApi location;
    PointApi originalLocation;
}
