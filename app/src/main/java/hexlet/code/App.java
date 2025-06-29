package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.BaseController;
import hexlet.code.controller.UrlController;
import hexlet.code.repository.Repository;
import hexlet.code.util.Environment;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Slf4j
public class App {
    @Getter
    @Setter
    private static Environment environment;
    @Setter
    private static String sqlLocation;
    @Setter
    private static String databaseUrl;

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        log.info("PORT: {}", port);
        return Integer.parseInt(port);
    }

    public static void main(String[] args) throws IOException, SQLException {
        var app = getApp();
        app.start(getPort());
    }

    public static Javalin getApp() throws IOException, SQLException {
        initEnv();
        initDatabaseConnection();

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.get("/", BaseController::index);
        app.post("/urls", UrlController::save);
        app.get("/urls", UrlController::index);
        app.get("/urls/{id}", UrlController::show);
        app.post("/urls/{id}/checks", UrlController::check);

        return app;
    }

    private static void initDatabaseConnection() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setLeakDetectionThreshold(2000);
        var dataSource = new HikariDataSource(hikariConfig);

        log.info("Schema location: {}", getSqlLocation());
        log.info("JDBC URL: {}", getDatabaseUrl());
        var sql = readResourceFile(getSqlLocation());
        log.info(sql);

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        Repository.dataSource = dataSource;
    }


    private static String getSqlLocation() {
        if (sqlLocation == null) {
            sqlLocation = System.getenv().getOrDefault("SCHEMA_LOCATION", "h2/Schema.sql");
        }

        return sqlLocation;
    }

    private static void initEnv() {
        if (environment == null) {
            environment = System.getenv().getOrDefault("ENV", "DEV").equals("PROD")
                    ? Environment.PROD : Environment.DEV;
        }
    }

    private static String getDatabaseUrl() {
        if (databaseUrl == null) {
            databaseUrl =  System.getenv().getOrDefault("JDBC_DATABASE_URL",
                    "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        }

        return databaseUrl;
    }

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }
}
