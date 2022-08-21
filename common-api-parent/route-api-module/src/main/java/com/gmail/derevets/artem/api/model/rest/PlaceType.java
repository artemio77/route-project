package com.gmail.derevets.artem.api.model.rest;

import org.mapstruct.Named;

import java.text.MessageFormat;

public enum PlaceType {
    DEPARTURE_PLACE("departurePlace"),
    ARRIVAL_PLACE("arrivalPlace");

    String value;

    PlaceType(String value) {
        this.value = value;
    }

    @Named("stringToRouteTypeEnum")
    public static PlaceType fromString(String type) {
        for (PlaceType routeType : PlaceType.values()) {
            if (routeType.value.equalsIgnoreCase(type)) {
                return routeType;
            }
        }
        throw new IllegalArgumentException(MessageFormat.format("No enum found for {0} in {1}", type, values()));
    }
}
