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

    public Url(String url) {
        this.name = url;
    }
}
