package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.model.dto.UrlsPage;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {

    public static void save(Context ctx) throws SQLException {
        var address = ctx.formParam("url");
        URI uri = URI.create(address);
        URL url;

        try {
            url = uri.toURL();
        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect("/");
            return;
        }

        var validatedUrl = new Url(url.getProtocol() + "://" + url.getAuthority());
        UrlRepository.save(validatedUrl);
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.redirect("/urls");
    }

    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        UrlsPage page = new UrlsPage(urls);
        ctx.render("urls/index.jte", model("page", page));
    }
}
