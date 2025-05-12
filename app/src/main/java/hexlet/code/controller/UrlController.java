package hexlet.code.controller;

import io.javalin.http.Handler;

import java.net.URL;

import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.query.QUrlCheck;
import io.ebean.PagedList;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import io.javalin.http.NotFoundResponse;


public class UrlController {
    private static final int ROWS_PER_PAGE = 10;

    public static Handler addUrl = ctx -> {
        String formUrl = ctx.formParam("url");
        URL url;

        try {
            url = new URL(formUrl);
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }

        String parsedUrl = url.getProtocol() + "://" + url.getAuthority();
        Url savedUrl = new QUrl().name.equalTo(parsedUrl).findOne();

        if (savedUrl != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "info");
            ctx.redirect("/urls");
            return;
        }

        Url newUrl = new Url(parsedUrl);
        newUrl.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");
    };

    public static Handler listUrls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;
        PagedList<Url> pagedUrls = new QUrl().setFirstRow(page * ROWS_PER_PAGE)
                .setMaxRows(ROWS_PER_PAGE)
                .orderBy().id.asc()
                .findPagedList();

        List<Url> urls = pagedUrls.getList();

        int lastPage = pagedUrls.getTotalPageCount() + 1;
        int currentPage = pagedUrls.getPageIndex() + 1;
        List<Integer> pages = IntStream.range(1, lastPage).boxed().collect(Collectors.toList());

        ctx.attribute("urls", urls);
        ctx.attribute("currentPage", currentPage);
        ctx.attribute("pages", pages);
        ctx.render("urls/list.html");
    };

    public static Handler showUrl = ctx -> {
        int id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
        Url url = new QUrl().id.equalTo(id).findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }

        List<UrlCheck> checks = url.getUrlChecks().stream()
                .sorted(Comparator.comparing(UrlCheck::getId).reversed())
                .toList();

        ctx.attribute("url", url);
        ctx.attribute("checks", checks);
        ctx.render("urls/show.html");
    };

    public static Handler addCheck = ctx -> {
        long id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
        Url savedUrl = new QUrl().id.equalTo(id).findOne();

        if (savedUrl.getName() == null) {
            throw new NotFoundResponse();
        }

        String savedUrlName = savedUrl.getName();

        try {
            HttpResponse<String> response = Unirest.get(savedUrlName).asString();
            int statusCode = response.getStatus();
            Document doc = Jsoup.parse(response.getBody());

            String title = doc.title();
            String description = doc.selectFirst("meta[name=description]") != null ?
                    Objects.requireNonNull(doc.selectFirst("meta[name=description]")).attr("content")
                    : null;
            String h1 = doc.selectFirst("h1") != null ?
                    Objects.requireNonNull(doc.selectFirst("h1")).text()
                    : null;

            UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, savedUrl);
            urlCheck.save();
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
        } catch (UnirestException exception) {
            ctx.sessionAttribute("flash", "Не удалось проверить страницу");
            ctx.sessionAttribute("flash-type", "danger");
        }

        ctx.redirect("/urls/" + id);
    };
}