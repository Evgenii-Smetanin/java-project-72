package hexlet.code.model;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
public final class UrlCheck extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private final int statusCode;

    private final String title;

    private final String h1;

    @Lob
    private final String description;

    @WhenCreated
    private Instant createdAt;

    @ManyToOne
    private final Url url;

    public UrlCheck(int statusCode, String title, String h1, String description, Url url) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.url = url;
    }

    public LocalDateTime getCreatedAt() {
        ZoneId zoneId = ZoneId.of("Europe/Moscow");
        return LocalDateTime.ofInstant(this.createdAt, zoneId);
    }
}