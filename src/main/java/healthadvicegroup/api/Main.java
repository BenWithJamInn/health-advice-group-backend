package healthadvicegroup.api;

import com.squareup.okhttp.OkHttpClient;
import healthadvicegroup.api.controller.weather.WeatherAPI;
import lombok.Getter;

import static spark.Spark.*;

public class Main {
    @Getter private static final OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) {
        after(((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        }));
        path("/api/v1", () -> {
            get("/forecast", WeatherAPI.fetchForecast);
        });
    }
}