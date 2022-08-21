package com.gmail.derevets.artem.api.model.rest;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import java.io.Serializable;
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
public class PointApi implements Serializable {

    Double latitude;
    Double longitude;
}

