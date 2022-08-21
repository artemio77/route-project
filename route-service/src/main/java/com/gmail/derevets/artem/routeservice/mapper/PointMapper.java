package com.gmail.derevets.artem.routeservice.mapper;

import com.gmail.derevets.artem.api.model.rest.PointApi;
import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto.LocationDto;
import com.gmail.derevets.artem.routeservice.domain.PointEntityV1;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface PointMapper {

    @Mapping(target = "latitude", source = "lat")
    @Mapping(target = "longitude", source = "lng")
    PointApi map(LocationDto locationDto);

    PointEntityV1 map(PointApi pointApi);

    PointApi map(PointEntityV1 pointEntityV1);
}
