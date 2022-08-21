package com.gmail.derevets.artem.routeservice.client.route.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class RouteCalculateDto {

    List<RouteDto> routes;


    @Value
    @Builder
    @Jacksonized
    public static class RouteDto {

        UUID id;
        List<SectionDto> sections;

    }


    @Value
    @Builder
    @Jacksonized
    public static class SectionDto {

        UUID id;
        String type;
        DepartureDto departure;
        ArrivalDto arrival;
        SummaryDto summary;
        TransportDto transport;
    }


    @Value
    @Builder
    @Jacksonized
    public static class DepartureDto {

        PlaceDto place;
    }

    @Value
    @Builder
    @Jacksonized
    public static class PlaceDto {

        String type;
        LocationDto location;
        LocationDto originalLocation;
    }


    @Value
    @Builder
    @Jacksonized
    public static class LocationDto {

        Double lat;
        Double lng;
    }


    @Value
    @Builder
    @Jacksonized
    public static class ArrivalDto {

        PlaceDto place;
    }


    @Value
    @Builder
    @Jacksonized
    public static class SummaryDto {

        Long duration;
        Long length;
        Long baseDuration;
    }


    @Value
    @Builder
    @Jacksonized
    public static class TransportDto {

        String mode;
    }
}
