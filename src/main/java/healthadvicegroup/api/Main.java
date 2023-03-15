package healthadvicegroup.api;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import healthadvicegroup.api.controller.ArticleController;
import healthadvicegroup.api.controller.WeatherAPI;
import healthadvicegroup.api.database.DatabaseManager;
import lombok.Getter;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class Main {
    @Getter private static final OkHttpClient client = new OkHttpClient();
    @Getter private static final Gson gson = new Gson();

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
            response.header("Access-Control-Allow-Origin", "*");
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
            exception(Exception.class, (Exception exc, Request request, Response response) -> {
                exc.printStackTrace();
                response.status(500);
            });
        });
    }
}