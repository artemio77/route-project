package com.gmail.derevets.artem.routeservice.mapper;

import com.gmail.derevets.artem.api.model.rest.PointApi;
import com.gmail.derevets.artem.api.model.rest.PointApi.PointApiBuilder;
import com.gmail.derevets.artem.api.model.rest.RouteApi;
import com.gmail.derevets.artem.api.model.rest.RouteApi.RouteApiBuilder;
import com.gmail.derevets.artem.api.model.rest.RouteStatus;
import com.gmail.derevets.artem.routeservice.domain.PointEntityV1;
import com.gmail.derevets.artem.routeservice.domain.RouteEntityV1;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-11-17T01:23:13+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.12 (Oracle Corporation)"
)
@Component
public class RouteMapperImpl implements RouteMapper {

    @Override
    public RouteApi map(RouteEntityV1 routeEntity) {
        if ( routeEntity == null ) {
            return null;
        }

        RouteApiBuilder routeApi = RouteApi.builder();

        if ( routeEntity.getId() != null ) {
            routeApi.id( routeEntity.getId() );
        }
        if ( routeEntity.getUserId() != null ) {
            routeApi.userId( routeEntity.getUserId() );
        }
        if ( routeEntity.getStatus() != null ) {
            routeApi.status( routeEntity.getStatus().name() );
        }
        if ( routeEntity.getCreatedDate() != null ) {
            routeApi.createdDate( Date.from( routeEntity.getCreatedDate() ) );
        }
        if ( routeEntity.getEnrichedDate() != null ) {
            routeApi.enrichedDate( Date.from( routeEntity.getEnrichedDate() ) );
        }
        if ( routeEntity.getName() != null ) {
            routeApi.name( routeEntity.getName() );
        }
        if ( routeEntity.getStartPoint() != null ) {
            routeApi.startPoint( pointEntityV1ToPointApi( routeEntity.getStartPoint() ) );
        }
        if ( routeEntity.getEndPoint() != null ) {
            routeApi.endPoint( pointEntityV1ToPointApi( routeEntity.getEndPoint() ) );
        }

        return routeApi.build();
    }

    @Override
    public RouteEntityV1 mapEntity(RouteApi routeApi) {
        if ( routeApi == null ) {
            return null;
        }

        RouteEntityV1 routeEntityV1 = new RouteEntityV1();

        if ( routeApi.id() != null ) {
            routeEntityV1.setId( routeApi.id() );
        }
        if ( routeApi.userId() != null ) {
            routeEntityV1.setUserId( routeApi.userId() );
        }
        if ( routeApi.status() != null ) {
            routeEntityV1.setStatus( Enum.valueOf( RouteStatus.class, routeApi.status() ) );
        }
        if ( routeApi.createdDate() != null ) {
            routeEntityV1.setCreatedDate( routeApi.createdDate().toInstant() );
        }
        if ( routeApi.enrichedDate() != null ) {
            routeEntityV1.setEnrichedDate( routeApi.enrichedDate().toInstant() );
        }
        if ( routeApi.name() != null ) {
            routeEntityV1.setName( routeApi.name() );
        }
        if ( routeApi.startPoint() != null ) {
            routeEntityV1.setStartPoint( pointApiToPointEntityV1( routeApi.startPoint() ) );
        }
        if ( routeApi.endPoint() != null ) {
            routeEntityV1.setEndPoint( pointApiToPointEntityV1( routeApi.endPoint() ) );
        }

        return routeEntityV1;
    }

    protected PointApi pointEntityV1ToPointApi(PointEntityV1 pointEntityV1) {
        if ( pointEntityV1 == null ) {
            return null;
        }

        PointApiBuilder pointApi = PointApi.builder();

        if ( pointEntityV1.getLatitude() != null ) {
            pointApi.latitude( pointEntityV1.getLatitude() );
        }
        if ( pointEntityV1.getLongitude() != null ) {
            pointApi.longitude( pointEntityV1.getLongitude() );
        }

        return pointApi.build();
    }

    protected PointEntityV1 pointApiToPointEntityV1(PointApi pointApi) {
        if ( pointApi == null ) {
            return null;
        }

        PointEntityV1 pointEntityV1 = new PointEntityV1();

        if ( pointApi.latitude() != null ) {
            pointEntityV1.setLatitude( pointApi.latitude() );
        }
        if ( pointApi.longitude() != null ) {
            pointEntityV1.setLongitude( pointApi.longitude() );
        }

        return pointEntityV1;
    }
}
