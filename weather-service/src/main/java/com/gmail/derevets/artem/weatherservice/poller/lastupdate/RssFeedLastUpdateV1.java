package com.gmail.derevets.artem.weatherservice.poller.lastupdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Objects;


@Table(name = "rss_feed_last_update_v1")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class RssFeedLastUpdateV1 {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RssFeedLastUpdateV1 that = (RssFeedLastUpdateV1) o;
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
                "lastUpdated = " + lastUpdated + ")";
    }
}
