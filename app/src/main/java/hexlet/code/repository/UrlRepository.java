package hexlet.code.repository;

import hexlet.code.App;
import hexlet.code.exception.UrlExistsException;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.util.Environment;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UrlRepository extends Repository {

    public static Url findById(int urlId) throws SQLException {
        var sql = "SELECT * FROM url WHERE id = ?";
        Url url;

        try (var conn = dataSource.getConnection()) {
            var preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, urlId);
            url = getUrl(preparedStatement);
        }

        if (url != null) {
            url.setChecks(getChecksByUrlId(url.getId()));
            enrichUrlWithLastCheck(url);
        }

        return url;
    }

    public static Url findByName(String urlName) throws SQLException {
        var sql = "SELECT * FROM url WHERE name = ?";
        Url url;

        try (var conn = dataSource.getConnection()) {
            var preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, urlName);
            url = getUrl(preparedStatement);
        }

        return url;
    }

    public static void save(Url url) throws SQLException {
        if (findByName(url.getName()) != null) {
            throw new UrlExistsException("Страница уже существует");
        }

        var sql = "INSERT INTO url (name, created_at) VALUES (?, ?)";
        url.setCreatedAt(LocalDateTime.now());

        try (var conn = dataSource.getConnection()) {
            var preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, url.getName());

            if (App.getEnvironment().equals(Environment.PROD)) {
                preparedStatement.setTimestamp(2, Timestamp.valueOf(url.getCreatedAt()));
            } else {
                preparedStatement.setString(2, url.getCreatedAt()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            preparedStatement.executeUpdate();
        }
    }

    public static void save(UrlCheck urlCheck) throws SQLException {
        var sql = "INSERT INTO url_check (status_code, title, h1, description, url_id, created_at)"
                + "VALUES (?, ?, ?, ?, ?, ?)";
        urlCheck.setCreatedAt(LocalDateTime.now());

        try (var conn = dataSource.getConnection()) {
            var preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, urlCheck.getStatusCode());
            preparedStatement.setString(2, urlCheck.getTitle());
            preparedStatement.setString(3, urlCheck.getH1());
            preparedStatement.setString(4, urlCheck.getDescription());
            preparedStatement.setInt(5, urlCheck.getUrlId());

            if (App.getEnvironment().equals(Environment.PROD)) {
                preparedStatement.setTimestamp(6, Timestamp.valueOf(urlCheck.getCreatedAt()));
            } else {
                preparedStatement.setString(6, urlCheck.getCreatedAt()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            preparedStatement.executeUpdate();
        }
    }

    public static List<Url> getEntities() throws SQLException {
        log.trace("Getting Urls from DB");
        var sql = "SELECT * FROM url ORDER BY id DESC";
        List<Url> urls = new ArrayList<>();

        try (var conn = dataSource.getConnection()) {
            var statement = conn.createStatement();
            var resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                var id = Integer.parseInt(resultSet.getString("id"));
                var name = resultSet.getString("name");
                var createdAt = parseDateTime(resultSet.getString("created_at"));

                var url = new Url(id, name, createdAt);
                urls.add(url);
            }
        }

        if (!urls.isEmpty()) {
            log.trace("Found {} Urls", urls.size());
            enrichUrlsWithLastCheck(urls);
        } else {
            log.trace("No Urls found");
        }

        log.trace("Completed getting Urls from DB");
        return urls;
    }

    public static void removeAll() throws SQLException {
        var sql1 = "TRUNCATE TABLE url_check";
        var sql2 = "DELETE FROM url";

        try (var conn = dataSource.getConnection()) {
            var preparedStatement = conn.prepareStatement(sql1);
            preparedStatement.executeUpdate();
        }

        try (var conn = dataSource.getConnection()) {
            var preparedStatement = conn.prepareStatement(sql2);
            preparedStatement.executeUpdate();
        }
    }

    private static Url getUrl(PreparedStatement preparedStatement) throws SQLException {
        var resultSet = preparedStatement.executeQuery();
        Url url = null;

        if (resultSet.next()) {
            var id = Integer.parseInt(resultSet.getString("id"));
            var name = resultSet.getString("name");
            var createdAt = parseDateTime(resultSet.getString("created_at"));

            url = new Url(id, name, createdAt);
        }

        return url;
    }

    private static List<UrlCheck> getChecksByUrlId(int urlId) throws SQLException {
        var sql = "SELECT * FROM url_check WHERE url_id = ?";
        List<UrlCheck> urlChecks = new ArrayList<>();

        try (var conn = dataSource.getConnection()) {
            var preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, urlId);
            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                var id = Integer.parseInt(resultSet.getString("id"));
                var status = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = parseDateTime(resultSet.getString("created_at"));

                urlChecks.add(new UrlCheck(id, status, title, h1, description, urlId, createdAt));
            }

        }
        return urlChecks;
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

    private static void enrichUrlsWithLastCheck(List<Url> urls) throws SQLException {
        for (var url : urls) {
            enrichUrlWithLastCheck(url);
        }
    }

    private static void enrichUrlWithLastCheck(Url url) throws SQLException {
        log.trace("Starting Url enrich with last Check process");
        var sql = "SELECT * FROM url_check WHERE url_id = ? ORDER BY id DESC";

        try (var conn = dataSource.getConnection()) {
            var preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, url.getId());
            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                url.setLastCheck(parseDateTime(resultSet.getString("created_at")));
                url.setLastStatusCode(resultSet.getInt("status_code"));
            }
        }

        log.trace("Completed Url enrich with last Check process");
    }
}
