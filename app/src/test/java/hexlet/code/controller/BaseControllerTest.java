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

public class BaseControllerTest {

    private Javalin app;

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        App.setEnvironment(Environment.DEV);
        App.setSqlLocation("h2/Schema.sql");
        app = App.getApp();
        UrlRepository.removeAll();
    }

    @Test
    public void whenIndexThenReturnOk() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
        });
    }
}
