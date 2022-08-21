package com.gmail.derevets.artem.routeservice.domain;

import com.gmail.derevets.artem.api.model.rest.RouteStatus;
import lombok.Data;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@Document
public class RouteEntityV1 {

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private UUID id;

    @Field(name = "user_id")
    private UUID userId;

    @Field(name = "status")
    private RouteStatus status;

    @Field
    private Instant createdDate;

    @Field
    private Instant enrichedDate;

    @Field(name = "name")
    private String name;

    @Field
    private PointEntityV1 startPoint;

    @Field
    private PointEntityV1 endPoint;

    @Field
    private List<RouteInformationEntityV1> routeInformationEntityV1List;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RouteEntityV1 that = (RouteEntityV1) o;
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
                "userId = " + userId + ", " +
                "status = " + status + ", " +
                "createdDate = " + createdDate + ", " +
                "enrichedDate = " + enrichedDate + ", " +
                "name = " + name + ")";
    }
}