package com.gmail.derevets.artem.api.model.rest;

import org.mapstruct.Named;

import java.text.MessageFormat;

public enum PointType {
    LOCATION("location"),
    ORIGINAL_LOCATION("originalLocation"),
    START("start"),
    END("end");

    String value;

    PointType(String value) {
        this.value = value;
    }

    @Named("stringToRouteTypeEnum")
    public static PointType fromString(String type) {
        for (PointType routeType : PointType.values()) {
            if (routeType.value.equalsIgnoreCase(type)) {
                return routeType;
            }
        }
        throw new IllegalArgumentException(MessageFormat.format("No enum found for {0} in {1}", type, values()));
    }
}
