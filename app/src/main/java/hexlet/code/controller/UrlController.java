package hexlet.code.controller;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import hexlet.code.exception.UrlExistsException;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.dto.UrlPage;
import hexlet.code.model.dto.UrlsPage;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
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

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Integer.class).get();
        var url = UrlRepository.findById(id);

        if (url == null) {
            ctx.sessionAttribute("flash", "Страница не найдена");
            ctx.status(404);
            ctx.redirect("/urls");
            return;
        }

        var page = new UrlPage(url);

        ctx.render("urls/show.jte", model("page", page));
    }

    public static void check(Context ctx) throws SQLException, UnirestException {
        var id = ctx.pathParamAsClass("id", Integer.class).get();
        var url = UrlRepository.findById(id);

        var response = Unirest.get(url.getName()).asString();
        var statusCode = response.getStatus();

        var doc = Jsoup.parse(response.getBody());

        var title = doc.title();
        String description = null;
        String h1 = null;

        if (doc.selectFirst("meta[name=description]") != null) {
            description = doc.selectFirst("meta[name=description]").attr("content");
        }

        if (doc.selectFirst("h1") != null) {
            h1 = doc.selectFirst("h1").text();
        }

        UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, url.getId());
        UrlRepository.save(urlCheck);

        ctx.redirect("/urls/" + id);
    }
}
