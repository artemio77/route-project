package com.gmail.derevets.artem.routeservice;

import io.eventuate.common.id.ApplicationIdGenerator;
import io.eventuate.common.id.Int128;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Primary
public class RouteMessagesApplicationIdGenerator extends ApplicationIdGenerator {

    @Override
    public Int128 genIdInternal() {
        Int128 int128 = super.genIdInternal();
        UUID randomPart = UUID.randomUUID();
        return new Int128(int128.getHi(), randomPart.getLeastSignificantBits());
    }
}