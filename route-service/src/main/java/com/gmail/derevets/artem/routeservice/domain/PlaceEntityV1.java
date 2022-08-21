package com.gmail.derevets.artem.routeservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PlaceEntityV1 {

    private String type;

    private PointEntityV1 location;

    private PointEntityV1 originalLocation;

}