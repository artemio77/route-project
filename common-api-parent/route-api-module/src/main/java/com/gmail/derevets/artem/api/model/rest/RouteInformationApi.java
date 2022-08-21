package com.gmail.derevets.artem.api.model.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@With
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class RouteInformationApi implements Serializable {

    UUID id;
    UUID externalId;
    String type;
    Date createdDate;

    Long duration;
    Long length;
    Long baseDuration;

    PlaceApi departurePlace;

    PlaceApi arrivalPlace;

}