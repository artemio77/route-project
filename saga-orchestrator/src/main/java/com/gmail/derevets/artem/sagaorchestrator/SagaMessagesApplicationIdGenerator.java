package com.gmail.derevets.artem.sagaorchestrator;

import io.eventuate.common.id.ApplicationIdGenerator;
import io.eventuate.common.id.Int128;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Primary
public class SagaMessagesApplicationIdGenerator extends ApplicationIdGenerator {

    @Override
    public Int128 genIdInternal() {
        Int128 int128 = super.genIdInternal();
        UUID randomPart = UUID.nameUUIDFromBytes(int128.toString().getBytes(StandardCharsets.UTF_8));
        return new Int128(int128.getHi(), randomPart.getLeastSignificantBits());
    }
}