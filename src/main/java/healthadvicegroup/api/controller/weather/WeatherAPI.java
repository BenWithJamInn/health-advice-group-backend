package healthadvicegroup.api.controller.weather;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.ResponseBody;
import healthadvicegroup.api.Main;
import org.apache.http.client.utils.URIBuilder;
import spark.Request;
import spark.Response;
import spark.Route;

import java.net.URI;

public class WeatherAPI {
    private static final String baseURL = "http://api.weatherapi.com/v1";
    private static final String apiKey = "9f3b43d59c5746b9b77154059230902";

    public static Route fetchForecast = (Request request, Response response) -> {
        String locastionString = request.queryParams("location");
        URI uri = new URIBuilder(baseURL + "/forecast.json")
                .addParameter("key", apiKey)
                .addParameter("q", locastionString)
                .addParameter("days", "7")
                .addParameter("aqi", "yes")
                .build();

        com.squareup.okhttp.Request forecastRequest = new com.squareup.okhttp.Request.Builder()
                .url(uri.toURL())
                .build();
        Call call = Main.getClient().newCall(forecastRequest);
        com.squareup.okhttp.Response forecastResponse = call.execute();
        try (ResponseBody body = forecastResponse.body()) {
            return body.string();
        }
    };
}
