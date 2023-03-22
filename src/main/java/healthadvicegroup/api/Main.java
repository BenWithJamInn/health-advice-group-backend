package healthadvicegroup.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import healthadvicegroup.api.controller.AccountController;
import healthadvicegroup.api.controller.ArticleController;
import healthadvicegroup.api.controller.HealthLogController;
import healthadvicegroup.api.controller.WeatherAPI;
import healthadvicegroup.api.database.DatabaseManager;
import lombok.Getter;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class Main {
    @Getter private static final OkHttpClient client = new OkHttpClient();
    @Getter private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
            .create();

    public static void main(String[] args) {
        // connect to mongoDB
        DatabaseManager.init();

        // options and before are used to enable CORS control (Allows for use with test server)
        options("/*", (request, response) -> {
                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "OK";
                });

        before(((request, response) -> {
            response.header("Access-Control-Allow-Origin", "http://localhost:3000");
        }));

        path("/api/v1", () -> {
            path("/forecast", () -> {
                get("/hourly", WeatherAPI.fetchForecast);
            });
            path("/article", () -> {
               get("/data/:id", ArticleController.fetchArticleData);
               post("/filter", ArticleController.getArticleFromFilter);
               post("/", ArticleController.createArticle);
            });
            path("/account", () -> {
                post("/signup", AccountController.signUp);
                post("/signin", AccountController.signIn);
            });
            path("/healthlog", () -> {
                get("/", HealthLogController.fetchHealthLogs);
                post("/new", HealthLogController.newHealthLog);
            });
            exception(Exception.class, (Exception exc, Request request, Response response) -> {
                response.status(500);
                exc.printStackTrace();
            });
        });
    }
}