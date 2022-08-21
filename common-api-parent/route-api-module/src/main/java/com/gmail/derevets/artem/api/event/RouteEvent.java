package com.gmail.derevets.artem.api.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.eventuate.Event;
import io.eventuate.EventEntity;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @Type(value = RouteCreatedEvent.class, name = "CreatedRouteEvent"),
        @Type(value = RouteEnrichedEvent.class, name = "EnrichedRouteEvent"),
})
@EventEntity(entity = "com.gmail.derevets.artem.routeservice.domain.RouteEntity")
public interface RouteEvent<T> extends Event {

    T getData();

    String getType();
}
