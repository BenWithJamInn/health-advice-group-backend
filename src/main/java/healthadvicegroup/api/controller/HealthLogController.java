package healthadvicegroup.api.controller;

import com.google.gson.JsonSyntaxException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import healthadvicegroup.api.Main;
import healthadvicegroup.api.controller.account.AccountInfo;
import healthadvicegroup.api.controller.healthlog.HealthLogData;
import healthadvicegroup.api.controller.healthlog.NewHealthLogRequest;
import healthadvicegroup.api.database.DatabaseManager;
import org.bson.Document;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HealthLogController {
    private static final MongoCollection<Document> collection = DatabaseManager.getDatabase().getCollection("HealthLogs");

    /**
     * Creates a new health log for the authorised user
     */
    public static final Route newHealthLog = (Request request, Response response) -> {
        String token = request.headers("Authorization");
        // if there is no token then return 401
        if (token == null) {
            response.status(401);
            return "Unauthorised";
        }
        AccountInfo account = AccountController.getAccountInfo(token);
        // if no account with that token exists return 401
        if (account == null) {
            response.status(401);
            return "Unauthorised";
        }
        NewHealthLogRequest newHealthLogRequest;
        // if json syntax is incorrect return 400
        try {
            newHealthLogRequest = Main.getGson().fromJson(request.body(), NewHealthLogRequest.class);
        } catch (JsonSyntaxException e) {
            response.status(400);
            return "Bad Request";
        }
        // if no data is present return 400
        if (newHealthLogRequest == null) {
            response.status(400);
            return "Bad Request";
        }
        // if health score range is out of bounds return 400
        if (newHealthLogRequest.getHealthScore() > 10 || newHealthLogRequest.getHealthScore() < 1) {
            response.status(400);
            return "Bad Request";
        }
        // send new health log to the database
        Document document = new Document();
        document.append("_id", UUID.randomUUID().toString());
        document.append("userID", account.getId().toString());
        document.append("date", new Date());
        document.append("healthScore", newHealthLogRequest.getHealthScore());
        document.append("notes", newHealthLogRequest.getNotes());
        collection.insertOne(document);
        return "Ok";
    };

    /**
     * Lists all health logs of the authorised user
     */
    public static final Route fetchHealthLogs = (Request request, Response response) -> {
        String token = request.headers("Authorization");
        // if there is no token then return 401
        if (token == null) {
            response.status(401);
            return "Unauthorised";
        }
        AccountInfo account = AccountController.getAccountInfo(token);
        // if no account with that token exists return 401
        if (account == null) {
            response.status(401);
            return "Unauthorised";
        }
        FindIterable<Document> documents = collection.find(new Document("userID", account.getId().toString()));
        List<HealthLogData> logs = new ArrayList<>();
        for (Document document : documents) {
            HealthLogData data = HealthLogData.fromDocument(document);
            if (data != null) {
                logs.add(data);
            }
        }
        return Main.getGson().toJson(logs);
    };
}
