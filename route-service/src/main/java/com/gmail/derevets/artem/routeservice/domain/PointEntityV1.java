package com.gmail.derevets.artem.routeservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class PointEntityV1 {

    private Double latitude;

    private Double longitude;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "latitude = " + latitude + ", " +
                "longitude = " + longitude + ")";
    }
}