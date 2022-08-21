package com.gmail.derevets.artem.weatherservice.model.meteoalarm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeteoAlarmRssFeedResponse {

    private String id;
    private Instant updated;
    private String title;
    private String generator;
    private List<MeteoAlarmEntry> entry = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeteoAlarmEntry {
        private String id;
        private ZonedDateTime updated;
        private ZonedDateTime published;
        private String title;
        private String identifier;
        private String status;
        private MeteoAlarmAuthor author;
        private List<MeteoAlarmEntryLink> link = new ArrayList<>();


        public Optional<String> getAlertId(String country) {
            return link.stream()
                    .filter(meteoAlarmEntryLink -> "application/cap+xml".equals(meteoAlarmEntryLink.getType()))
                    .map(MeteoAlarmRssFeedResponse.MeteoAlarmEntryLink::getHref)
                    .map(href -> StringUtils.remove(href, "https://hub.meteoalarm.org/warnings/feeds-" + country + "/"))
                    .findAny();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeteoAlarmEntryLink {
        private String href;
        private String type;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeteoAlarmAuthor {
        private String name;
        private String uri;
    }
}
