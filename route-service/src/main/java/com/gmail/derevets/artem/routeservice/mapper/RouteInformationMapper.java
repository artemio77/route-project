package com.gmail.derevets.artem.routeservice.mapper;

import com.gmail.derevets.artem.api.model.rest.PlaceApi;
import com.gmail.derevets.artem.api.model.rest.RouteInformationApi;
import com.gmail.derevets.artem.api.model.rest.RouteType;
import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto.PlaceDto;
import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto.SectionDto;
import com.gmail.derevets.artem.routeservice.domain.PlaceEntityV1;
import com.gmail.derevets.artem.routeservice.domain.RouteInformationEntityV1;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        uses = {RouteType.class, PointMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RouteInformationMapper {


    @Mapping(source = "sectionDto.transport.mode", target = "type")
    @Mapping(source = "sectionDto.departure.place", target = "departurePlace")
    @Mapping(source = "sectionDto.arrival.place", target = "arrivalPlace")
    @Mapping(source = "sectionDto.summary.duration", target = "duration")
    @Mapping(source = "sectionDto.summary.length", target = "length")
    @Mapping(source = "sectionDto.summary.baseDuration", target = "baseDuration")
    @Mapping(source = "id", target = "externalId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true, defaultExpression = ("java(Instant.now())"))
    RouteInformationApi map(SectionDto sectionDto);

    RouteInformationApi map(RouteInformationEntityV1 routeInformationEntity);

    @Mapping(source = "type", target = "type", qualifiedByName = {"stringToRouteTypeEnum"})
    RouteInformationEntityV1 mapEntity(RouteInformationApi routeInformationApi);

    PlaceApi map(PlaceDto placeDto);

    PlaceApi map(PlaceEntityV1 placeUdt);
}
