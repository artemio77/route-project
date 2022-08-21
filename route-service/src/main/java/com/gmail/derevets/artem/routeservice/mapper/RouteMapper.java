package com.gmail.derevets.artem.routeservice.mapper;

import com.gmail.derevets.artem.api.model.rest.RouteApi;
import com.gmail.derevets.artem.routeservice.domain.RouteEntityV1;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring",
        uses = RouteInformationMapper.class,
        injectionStrategy = InjectionStrategy.FIELD,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface RouteMapper {

    RouteApi map(RouteEntityV1 routeEntity);

    RouteEntityV1 mapEntity(RouteApi routeApi);
}
