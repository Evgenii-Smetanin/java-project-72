package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Url {
    private int id;
    private String name;
    private LocalDateTime createdAt;

    public Url(String url) {
        this.name = url;
    }
}
