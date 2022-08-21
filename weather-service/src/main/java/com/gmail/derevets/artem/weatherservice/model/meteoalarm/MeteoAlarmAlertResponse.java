package com.gmail.derevets.artem.weatherservice.model.meteoalarm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeteoAlarmAlertResponse {

    private String identifier;
    private String sender;
    private String sent;
    private String status;
    private String msgType;
    private String source;
    private String scope;
    private String code;
    private List<MeteoAlarmAlertInformation> info;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeteoAlarmAlertInformation {
        private String language;
        private String category;
        private String event;
        private String responseType;
        private String urgency;
        private String severity;
        private String certainty;
        private ZonedDateTime effective;
        private ZonedDateTime onset;
        private ZonedDateTime expires;
        private String senderName;
        private String headline;
        private String description;
        private String instruction;
        private List<AlertParameter> parameter;
        private List<AlertArea> area;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AlertParameter {
        private String value;
        private String valueName;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AlertArea {
        private String areaDesc;
        private Double altitude;
        private Double ceiling;
        private AlertGeocode geocode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AlertGeocode {
        private String value;
        private String valueName;
    }
}
