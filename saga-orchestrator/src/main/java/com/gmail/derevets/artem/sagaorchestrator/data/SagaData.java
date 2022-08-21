package com.gmail.derevets.artem.sagaorchestrator.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.Access;
import java.util.Map;

@Data
@Builder
@Jacksonized
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Accessors(fluent = true)
public class SagaData {

    public static final String ERROR_DETAILS_MAP_KEY = "errorDetails";
    public static final String ROUTE_ID_DETAILS_MAP_KEY = "routeId";

    @Builder.Default
    private SagaStatus status = SagaStatus.CREATED;
    @Builder.Default
    private Map<String, Object> details = Maps.newHashMap();
}

