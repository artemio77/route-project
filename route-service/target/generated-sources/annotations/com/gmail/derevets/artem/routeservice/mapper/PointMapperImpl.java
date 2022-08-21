package com.gmail.derevets.artem.routeservice.mapper;

import com.gmail.derevets.artem.api.model.rest.PointApi;
import com.gmail.derevets.artem.api.model.rest.PointApi.PointApiBuilder;
import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto.LocationDto;
import com.gmail.derevets.artem.routeservice.domain.PointEntityV1;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-11-17T01:23:13+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.12 (Oracle Corporation)"
)
@Component
public class PointMapperImpl implements PointMapper {

    @Override
    public PointApi map(LocationDto locationDto) {
        if ( locationDto == null ) {
            return null;
        }

        PointApiBuilder pointApi = PointApi.builder();

        pointApi.latitude( locationDto.getLat() );
        pointApi.longitude( locationDto.getLng() );

        return pointApi.build();
    }

    @Override
    public PointEntityV1 map(PointApi pointApi) {
        if ( pointApi == null ) {
            return null;
        }

        PointEntityV1 pointEntityV1 = new PointEntityV1();

        pointEntityV1.setLatitude( pointApi.latitude() );
        pointEntityV1.setLongitude( pointApi.longitude() );

        return pointEntityV1;
    }

    @Override
    public PointApi map(PointEntityV1 pointEntityV1) {
        if ( pointEntityV1 == null ) {
            return null;
        }

        PointApiBuilder pointApi = PointApi.builder();

        pointApi.latitude( pointEntityV1.getLatitude() );
        pointApi.longitude( pointEntityV1.getLongitude() );

        return pointApi.build();
    }
}
