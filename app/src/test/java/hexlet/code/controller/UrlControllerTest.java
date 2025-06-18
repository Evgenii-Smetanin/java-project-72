package hexlet.code.controller;

import hexlet.code.App;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.Environment;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlControllerTest {

    private Javalin app;

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        App.setEnvironment(Environment.DEV);
        App.setSqlLocation("h2/Schema.sql");
        App.setDatabaseUrl("jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        app = App.getApp();
        UrlRepository.removeAll();
    }

    @Test
    public void whenIndexThenReturnOk() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void givenArgumentProvidedWhenSaveThenReturnOkAndData() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://ru.hexlet.io";
            var response = client.post("/urls", requestBody);
            assertThat(UrlRepository.findById(1).getName()).isEqualTo("https://ru.hexlet.io");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://ru.hexlet.io");
        });
    }

    @Test
    public void givenIdNotExistsWhenShowThenReturnNotFound() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls/1");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void givenArgumentProvidedWhenShowThenReturnOk() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://ru.hexlet.io";
            client.post("/urls", requestBody);
            var response = client.get("/urls/1");
            assertThat(response.code()).isEqualTo(200);
        });
    }
}
