package hexlet.code;

import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlController;
import io.javalin.Javalin;
import io.javalin.core.validation.ValidationException;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class App {
    public static final String DEFAULT_PORT = "5000";

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            if (!isProduction()) {
                config.enableDevLogging();
            }
            config.enableWebjars();
            JavalinThymeleaf.configure(getTemplateEngine());
        });

        app.exception(ValidationException.class, (e, ctx) -> {
            ctx.json(e.getErrors()).status(422);
        });

        addRoutes(app);
        app.before(ctx -> ctx.attribute("ctx", ctx));

        return app;
    }

    private static int getPort() {
        return Integer.parseInt(System.getenv().getOrDefault("PORT", DEFAULT_PORT));
    }

    private static TemplateEngine getTemplateEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        TemplateEngine engine = new TemplateEngine();

        resolver.setPrefix("/templates/");
        resolver.setCharacterEncoding("UTF-8");

        engine.addDialect(new LayoutDialect());
        engine.addDialect(new Java8TimeDialect());
        engine.addTemplateResolver(resolver);

        return engine;
    }

    private static void addRoutes(Javalin app) {
        app.get("/", RootController.welcome);
        app.routes(() -> {
            path("urls", () -> {
                post(UrlController.addUrl);
                get(UrlController.listUrls);
                get("{id}", UrlController.showUrl);
                post("{id}/checks", UrlController.addCheck);
            });
        });
    }

    private static boolean isProduction() {
        return System.getenv().getOrDefault("APP_ENV", "dev").equals("prod");
    }
}
