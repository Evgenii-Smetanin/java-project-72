package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UrlRepository extends Repository {
    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO url (name, created_at) VALUES (?, ?)";

        url.setCreatedAt(LocalDateTime.now());

        try (var preparedStatement = dataSource.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, url.getName());
            preparedStatement.setString(2, url.getCreatedAt().toString());
            preparedStatement.executeUpdate();
        }
    }

    public static List<Url> getEntities() throws SQLException {
        var sql = "SELECT * FROM url ORDER BY name";

        List<Url> urls = new ArrayList<>();

        try (var statement = dataSource.getConnection().createStatement()) {
            var resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                var id = Integer.parseInt(resultSet.getString("id"));
                var name = resultSet.getString("name");
                var createdAt = LocalDateTime.parse(resultSet.getString("created_at"));

                var url = new Url(id, name, createdAt);
                urls.add(url);
            }
        }

        return urls;
    }
}
