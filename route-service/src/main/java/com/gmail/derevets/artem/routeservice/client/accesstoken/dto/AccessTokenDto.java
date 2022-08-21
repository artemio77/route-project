package com.gmail.derevets.artem.routeservice.client.accesstoken.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class AccessTokenDto implements Serializable {

    String accessToken;
    String tokenType;
    Long expiresIn;
}
