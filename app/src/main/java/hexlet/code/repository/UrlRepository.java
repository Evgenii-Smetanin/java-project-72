package hexlet.code.repository;

import hexlet.code.exception.UrlExistsException;
import hexlet.code.model.Url;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class UrlRepository extends Repository {
    public static Url findById(String urlId) throws SQLException {
        var sql = "SELECT * FROM url WHERE id = ?";

        Url url = null;

        try (var preparedStatement = dataSource.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, urlId);
            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                var id = Integer.parseInt(resultSet.getString("id"));
                var name = resultSet.getString("name");
                var createdAt = parseDateTime(resultSet.getString("created_at"));

                url = new Url(id, name, createdAt);
            }
        }

        return url;
    }

    public static Url findByName(String urlName) throws SQLException {
        var sql = "SELECT * FROM url WHERE name = ?";

        Url url = null;

        try (var preparedStatement = dataSource.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, urlName);
            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                var id = Integer.parseInt(resultSet.getString("id"));
                var name = resultSet.getString("name");
                var createdAt = parseDateTime(resultSet.getString("created_at"));

                url = new Url(id, name, createdAt);
            }
        }

        return url;
    }

    public static void save(Url url) throws SQLException {
        if (findByName(url.getName()) != null) {
            throw new UrlExistsException("Url с именем " + url.getName() + " уже существует");
        }

        var sql = "INSERT INTO url (name, created_at) VALUES (?, ?)";

        url.setCreatedAt(LocalDateTime.now());

        try (var preparedStatement = dataSource.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, url.getName());
            preparedStatement.setString(2, url.getCreatedAt()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            preparedStatement.executeUpdate();
        }
    }

    public static List<Url> getEntities() throws SQLException {
        var sql = "SELECT * FROM url ORDER BY id DESC";

        List<Url> urls = new ArrayList<>();

        try (var statement = dataSource.getConnection().createStatement()) {
            var resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                var id = Integer.parseInt(resultSet.getString("id"));
                var name = resultSet.getString("name");
                var createdAt = parseDateTime(resultSet.getString("created_at"));

                var url = new Url(id, name, createdAt);
                urls.add(url);
            }
        }

        return urls;
    }

    private static LocalDateTime parseDateTime(String dateString) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
                .optionalEnd()
                .toFormatter();

        return LocalDateTime.parse(dateString, formatter);
    }
}
