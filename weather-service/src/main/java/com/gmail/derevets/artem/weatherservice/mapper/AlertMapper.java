package com.gmail.derevets.artem.weatherservice.mapper;

import com.gmail.derevets.artem.weatherservice.model.Alert;
import com.gmail.derevets.artem.weatherservice.model.meteoalarm.MeteoAlarmAlertResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AlertMapper {

    Alert map(MeteoAlarmAlertResponse meteoAlarmEntry);
}
