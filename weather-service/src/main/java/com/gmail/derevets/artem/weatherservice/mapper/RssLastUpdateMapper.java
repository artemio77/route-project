package com.gmail.derevets.artem.weatherservice.mapper;

import com.gmail.derevets.artem.weatherservice.model.RssFeedProcessResult;
import com.gmail.derevets.artem.weatherservice.poller.lastupdate.RssFeedLastUpdateV1;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RssLastUpdateMapper {

    @Mapping(target = "id", source = "countryId")
    RssFeedLastUpdateV1 map(RssFeedProcessResult rssFeedProcessResult);
}
