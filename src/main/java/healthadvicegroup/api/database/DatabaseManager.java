package healthadvicegroup.api.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

public class DatabaseManager {
    private static final String CONNECTION_URI = "mongodb://localhost:27017";
    @Getter private static MongoDatabase database;
    @Getter private static MongoClient client;

    public static void init() {
        MongoClient mongoClient = MongoClients.create(CONNECTION_URI);
        client = mongoClient;
        database = mongoClient.getDatabase("HealthAdviceGroup");
    }
}
