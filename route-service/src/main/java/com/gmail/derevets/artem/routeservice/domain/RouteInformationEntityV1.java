package com.gmail.derevets.artem.routeservice.domain;

import com.gmail.derevets.artem.api.model.rest.RouteType;
import lombok.Data;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Data
@Document
public class RouteInformationEntityV1 {

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private UUID id;

    @Field
    private RouteType type;

    @Field
    private Instant createdDate;

    @Field
    private Long duration;

    @Field
    private Long length;

    @Field
    private Long baseDuration;

    @Field
    private PlaceEntityV1 departurePlace;
    @Field
    private PlaceEntityV1 arrivalPlace;
    @Field
    private Long routeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RouteInformationEntityV1 that = (RouteInformationEntityV1) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "type = " + type + ", " +
                "createdDate = " + createdDate + ", " +
                "duration = " + duration + ", " +
                "length = " + length + ", " +
                "baseDuration = " + baseDuration + ")";
    }
}