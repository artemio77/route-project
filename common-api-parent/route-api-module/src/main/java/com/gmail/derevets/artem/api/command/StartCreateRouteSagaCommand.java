package com.gmail.derevets.artem.api.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import io.eventuate.tram.commands.common.Command;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class StartCreateRouteSagaCommand implements Command {

    UUID routeId;

    @Value
    @Builder
    @Jacksonized
    @Accessors(fluent = true)
    @JsonAutoDetect(fieldVisibility = Visibility.ANY)
    public static class SagaEnrichRouteCommand implements Command {

        UUID routeId;
    }
}




