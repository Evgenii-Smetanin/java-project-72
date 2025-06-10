package hexlet.code.controller;

import hexlet.code.exception.UrlExistsException;
import hexlet.code.model.Url;
import hexlet.code.model.dto.UrlPage;
import hexlet.code.model.dto.UrlsPage;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
//import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {

    public static void save(Context ctx) throws SQLException {
        var address = ctx.formParam("url");
        URI uri = URI.create(address);
        URL url;

        try {
            url = uri.toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect("/");
            return;
        }

        var validatedUrl = new Url(url.getProtocol() + "://" + url.getAuthority());
        try {
            UrlRepository.save(validatedUrl);
        } catch (UrlExistsException e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.redirect("/");
            return;
        }
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.redirect("/urls");
    }

    public static void index(Context ctx) throws SQLException, UnsupportedEncodingException {
        var urls = UrlRepository.getEntities();
        UrlsPage page = new UrlsPage(urls);
        String flash = ctx.consumeSessionAttribute("flash");

//        if (flash != null) {
//            byte[] windows1251Bytes = flash.getBytes("Windows-1251");
//            flash = new String(windows1251Bytes, StandardCharsets.UTF_8);
//        }

        page.setFlash(flash);

        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException, UnsupportedEncodingException {
        var id = ctx.pathParamAsClass("id", Integer.class).get();
        var url = UrlRepository.findById(id);

        if (url == null) {
            ctx.sessionAttribute("flash", "Страница не найдена");
            ctx.redirect("/urls");
            return;
        }

        UrlPage page = new UrlPage(url);

        ctx.render("urls/show.jte", model("page", page));
    }
}
