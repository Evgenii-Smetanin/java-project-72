package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;

import java.sql.SQLException;

public class UrlController {
    public static void save(Context ctx) throws SQLException {
        var address = ctx.formParam("url");
        var url = new Url(address);
        UrlRepository.save(url);
    }
}
