package com.gmail.derevets.artem.api.model.rest;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@With
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class RouteApi {

    UUID id;
    UUID userId;
    String status;
    Date createdDate;
    Date enrichedDate;
    String name;
    PointApi startPoint;
    PointApi endPoint;
    List<RouteInformationApi> routesInformation;
}

