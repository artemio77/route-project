package com.gmail.derevets.artem.routeservice.mapper;

import com.gmail.derevets.artem.api.model.rest.PlaceApi;
import com.gmail.derevets.artem.api.model.rest.PlaceApi.PlaceApiBuilder;
import com.gmail.derevets.artem.api.model.rest.RouteInformationApi;
import com.gmail.derevets.artem.api.model.rest.RouteInformationApi.RouteInformationApiBuilder;
import com.gmail.derevets.artem.api.model.rest.RouteType;
import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto.ArrivalDto;
import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto.DepartureDto;
import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto.PlaceDto;
import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto.SectionDto;
import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto.SummaryDto;
import com.gmail.derevets.artem.routeservice.client.route.dto.RouteCalculateDto.TransportDto;
import com.gmail.derevets.artem.routeservice.domain.PlaceEntityV1;
import com.gmail.derevets.artem.routeservice.domain.RouteInformationEntityV1;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-11-17T01:23:13+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.12 (Oracle Corporation)"
)
@Component
public class RouteInformationMapperImpl implements RouteInformationMapper {

    @Autowired
    private PointMapper pointMapper;

    @Override
    public RouteInformationApi map(SectionDto sectionDto) {
        if ( sectionDto == null ) {
            return null;
        }

        RouteInformationApiBuilder routeInformationApi = RouteInformationApi.builder();

        String mode = sectionDtoTransportMode( sectionDto );
        if ( mode != null ) {
            routeInformationApi.type( mode );
        }
        PlaceDto place = sectionDtoDeparturePlace( sectionDto );
        if ( place != null ) {
            routeInformationApi.departurePlace( map( place ) );
        }
        PlaceDto place1 = sectionDtoArrivalPlace( sectionDto );
        if ( place1 != null ) {
            routeInformationApi.arrivalPlace( map( place1 ) );
        }
        Long duration = sectionDtoSummaryDuration( sectionDto );
        if ( duration != null ) {
            routeInformationApi.duration( duration );
        }
        Long length = sectionDtoSummaryLength( sectionDto );
        if ( length != null ) {
            routeInformationApi.length( length );
        }
        Long baseDuration = sectionDtoSummaryBaseDuration( sectionDto );
        if ( baseDuration != null ) {
            routeInformationApi.baseDuration( baseDuration );
        }
        if ( sectionDto.getId() != null ) {
            routeInformationApi.externalId( sectionDto.getId() );
        }

        return routeInformationApi.build();
    }

    @Override
    public RouteInformationApi map(RouteInformationEntityV1 routeInformationEntity) {
        if ( routeInformationEntity == null ) {
            return null;
        }

        RouteInformationApiBuilder routeInformationApi = RouteInformationApi.builder();

        if ( routeInformationEntity.getId() != null ) {
            routeInformationApi.id( routeInformationEntity.getId() );
        }
        if ( routeInformationEntity.getType() != null ) {
            routeInformationApi.type( routeInformationEntity.getType().name() );
        }
        if ( routeInformationEntity.getCreatedDate() != null ) {
            routeInformationApi.createdDate( Date.from( routeInformationEntity.getCreatedDate() ) );
        }
        if ( routeInformationEntity.getDuration() != null ) {
            routeInformationApi.duration( routeInformationEntity.getDuration() );
        }
        if ( routeInformationEntity.getLength() != null ) {
            routeInformationApi.length( routeInformationEntity.getLength() );
        }
        if ( routeInformationEntity.getBaseDuration() != null ) {
            routeInformationApi.baseDuration( routeInformationEntity.getBaseDuration() );
        }
        if ( routeInformationEntity.getDeparturePlace() != null ) {
            routeInformationApi.departurePlace( map( routeInformationEntity.getDeparturePlace() ) );
        }
        if ( routeInformationEntity.getArrivalPlace() != null ) {
            routeInformationApi.arrivalPlace( map( routeInformationEntity.getArrivalPlace() ) );
        }

        return routeInformationApi.build();
    }

    @Override
    public RouteInformationEntityV1 mapEntity(RouteInformationApi routeInformationApi) {
        if ( routeInformationApi == null ) {
            return null;
        }

        RouteInformationEntityV1 routeInformationEntityV1 = new RouteInformationEntityV1();

        if ( routeInformationApi.type() != null ) {
            routeInformationEntityV1.setType( RouteType.fromString( routeInformationApi.type() ) );
        }
        if ( routeInformationApi.id() != null ) {
            routeInformationEntityV1.setId( routeInformationApi.id() );
        }
        if ( routeInformationApi.createdDate() != null ) {
            routeInformationEntityV1.setCreatedDate( routeInformationApi.createdDate().toInstant() );
        }
        if ( routeInformationApi.duration() != null ) {
            routeInformationEntityV1.setDuration( routeInformationApi.duration() );
        }
        if ( routeInformationApi.length() != null ) {
            routeInformationEntityV1.setLength( routeInformationApi.length() );
        }
        if ( routeInformationApi.baseDuration() != null ) {
            routeInformationEntityV1.setBaseDuration( routeInformationApi.baseDuration() );
        }
        if ( routeInformationApi.departurePlace() != null ) {
            routeInformationEntityV1.setDeparturePlace( placeApiToPlaceEntityV1( routeInformationApi.departurePlace() ) );
        }
        if ( routeInformationApi.arrivalPlace() != null ) {
            routeInformationEntityV1.setArrivalPlace( placeApiToPlaceEntityV1( routeInformationApi.arrivalPlace() ) );
        }

        return routeInformationEntityV1;
    }

    @Override
    public PlaceApi map(PlaceDto placeDto) {
        if ( placeDto == null ) {
            return null;
        }

        PlaceApiBuilder placeApi = PlaceApi.builder();

        if ( placeDto.getType() != null ) {
            placeApi.type( placeDto.getType() );
        }
        if ( placeDto.getLocation() != null ) {
            placeApi.location( pointMapper.map( placeDto.getLocation() ) );
        }
        if ( placeDto.getOriginalLocation() != null ) {
            placeApi.originalLocation( pointMapper.map( placeDto.getOriginalLocation() ) );
        }

        return placeApi.build();
    }

    @Override
    public PlaceApi map(PlaceEntityV1 placeUdt) {
        if ( placeUdt == null ) {
            return null;
        }

        PlaceApiBuilder placeApi = PlaceApi.builder();

        if ( placeUdt.getType() != null ) {
            placeApi.type( placeUdt.getType() );
        }
        if ( placeUdt.getLocation() != null ) {
            placeApi.location( pointMapper.map( placeUdt.getLocation() ) );
        }
        if ( placeUdt.getOriginalLocation() != null ) {
            placeApi.originalLocation( pointMapper.map( placeUdt.getOriginalLocation() ) );
        }

        return placeApi.build();
    }

    private String sectionDtoTransportMode(SectionDto sectionDto) {
        if ( sectionDto == null ) {
            return null;
        }
        TransportDto transport = sectionDto.getTransport();
        if ( transport == null ) {
            return null;
        }
        String mode = transport.getMode();
        if ( mode == null ) {
            return null;
        }
        return mode;
    }

    private PlaceDto sectionDtoDeparturePlace(SectionDto sectionDto) {
        if ( sectionDto == null ) {
            return null;
        }
        DepartureDto departure = sectionDto.getDeparture();
        if ( departure == null ) {
            return null;
        }
        PlaceDto place = departure.getPlace();
        if ( place == null ) {
            return null;
        }
        return place;
    }

    private PlaceDto sectionDtoArrivalPlace(SectionDto sectionDto) {
        if ( sectionDto == null ) {
            return null;
        }
        ArrivalDto arrival = sectionDto.getArrival();
        if ( arrival == null ) {
            return null;
        }
        PlaceDto place = arrival.getPlace();
        if ( place == null ) {
            return null;
        }
        return place;
    }

    private Long sectionDtoSummaryDuration(SectionDto sectionDto) {
        if ( sectionDto == null ) {
            return null;
        }
        SummaryDto summary = sectionDto.getSummary();
        if ( summary == null ) {
            return null;
        }
        Long duration = summary.getDuration();
        if ( duration == null ) {
            return null;
        }
        return duration;
    }

    private Long sectionDtoSummaryLength(SectionDto sectionDto) {
        if ( sectionDto == null ) {
            return null;
        }
        SummaryDto summary = sectionDto.getSummary();
        if ( summary == null ) {
            return null;
        }
        Long length = summary.getLength();
        if ( length == null ) {
            return null;
        }
        return length;
    }

    private Long sectionDtoSummaryBaseDuration(SectionDto sectionDto) {
        if ( sectionDto == null ) {
            return null;
        }
        SummaryDto summary = sectionDto.getSummary();
        if ( summary == null ) {
            return null;
        }
        Long baseDuration = summary.getBaseDuration();
        if ( baseDuration == null ) {
            return null;
        }
        return baseDuration;
    }

    protected PlaceEntityV1 placeApiToPlaceEntityV1(PlaceApi placeApi) {
        if ( placeApi == null ) {
            return null;
        }

        PlaceEntityV1 placeEntityV1 = new PlaceEntityV1();

        if ( placeApi.type() != null ) {
            placeEntityV1.setType( placeApi.type() );
        }
        if ( placeApi.location() != null ) {
            placeEntityV1.setLocation( pointMapper.map( placeApi.location() ) );
        }
        if ( placeApi.originalLocation() != null ) {
            placeEntityV1.setOriginalLocation( pointMapper.map( placeApi.originalLocation() ) );
        }

        return placeEntityV1;
    }
}
