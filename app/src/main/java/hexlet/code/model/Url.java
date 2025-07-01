package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Url {
    private int id;
    private String name;
    private LocalDateTime createdAt;
    private List<UrlCheck> checks;

    private LocalDateTime lastCheck;
    private int lastStatusCode;

    public Url(String url) {
        this.name = url;
    }

    public Url(int id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }
}
