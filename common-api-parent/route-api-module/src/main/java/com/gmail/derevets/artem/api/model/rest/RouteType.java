package com.gmail.derevets.artem.api.model.rest;

import java.text.MessageFormat;
import org.mapstruct.Named;

public enum RouteType {
    CAR("car"),
    FERRY("ferry"),
    TRUCK("truck"),
    BICYCLE("bicycle"),
    SCOOTER("scooter"),
    CAR_SHUTTLE_TRAIN("carShuttleTrain");

    String value;

    RouteType(String value) {
        this.value = value;
    }

    @Named("stringToRouteTypeEnum")
    public static RouteType fromString(String type) {
        for (RouteType routeType : RouteType.values()) {
            if (routeType.value.equalsIgnoreCase(type)) {
                return routeType;
            }
        }
        throw new IllegalArgumentException(MessageFormat.format("No enum found for {0} in {1}", type, values()));
    }
}
