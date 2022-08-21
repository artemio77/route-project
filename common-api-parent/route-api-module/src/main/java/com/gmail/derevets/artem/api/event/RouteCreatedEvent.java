package com.gmail.derevets.artem.api.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.gmail.derevets.artem.api.model.rest.RouteApi;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class RouteCreatedEvent implements RouteEvent<RouteApi> {

    public static final String TYPE = "CreatedRouteEvent";
    @Builder.Default
    String type = TYPE;
    RouteApi data;
}
