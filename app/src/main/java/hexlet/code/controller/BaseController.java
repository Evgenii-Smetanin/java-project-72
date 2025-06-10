package hexlet.code.controller;

import hexlet.code.model.dto.BasePage;
import io.javalin.http.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static io.javalin.rendering.template.TemplateUtil.model;

public class BaseController {
    public static void index(Context ctx) throws UnsupportedEncodingException {
        BasePage page = new BasePage();
        String flash = ctx.consumeSessionAttribute("flash");

//        if (flash != null) {
//            byte[] windows1251Bytes = flash.getBytes("Windows-1251");
//            flash = new String(windows1251Bytes, StandardCharsets.UTF_8);
//        }

        page.setFlash(flash);
        ctx.render("index.jte", model("page", page));
    }
}
