package healthadvicegroup.api.controller;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import healthadvicegroup.api.Main;
import healthadvicegroup.api.controller.article.ArticleData;
import healthadvicegroup.api.controller.article.CreateArticleResponse;
import healthadvicegroup.api.controller.article.GetArticleFromFilterRequest;
import healthadvicegroup.api.database.DatabaseManager;
import org.bson.Document;
import org.bson.conversions.Bson;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArticleController {
    private static final MongoCollection<Document> collection = DatabaseManager.getDatabase().getCollection("Articles");

    /**
     * Fetches all article data from an ID
     */
    public static final Route fetchArticleData = (Request request, Response response) -> {
        String id = request.params(":id");
        FindIterable<Document> foundDocuments = collection.find(new Document("_id", id));
        Document articleDoc = foundDocuments.first();
        if (articleDoc == null) {
            response.status(404);
            return "No articles with that ID exist.";
        }
        return articleDoc.toJson();
    };

    /**
     * Creates a new article with given information
     */
    public static final Route createArticle = (Request request, Response response) -> {
        ArticleData fromBody = Main.getGson().fromJson(request.body(), ArticleData.class);
        fromBody.setId(UUID.randomUUID());
        collection.insertOne(fromBody.toDocument());
        CreateArticleResponse createArticleResponse = new CreateArticleResponse("Article Created!", fromBody.getId());
        return Main.getGson().toJson(createArticleResponse);
    };

    /**
     * Filters article by category and returns a list of available articles (no body)
     */
    public static final Route getArticleFromFilter = (Request request, Response response) -> {
        GetArticleFromFilterRequest requestData = Main.getGson().fromJson(request.body(), GetArticleFromFilterRequest.class);
        if (requestData.getCategories() == null) {
            response.status(400);
            return "Invalid request.";
        }
        Bson filter;
        if (requestData.getCategories().isEmpty()) {
            filter = Filters.empty();
        } else {
            filter = Filters.all("categories", requestData.getCategories());
        }
        Bson projector = Projections.exclude("body");
        List<ArticleData> articleDataList = new ArrayList<>();
        FindIterable<Document> documents = collection.find(filter).projection(projector);
        for (Document document : documents) {
            articleDataList.add(ArticleData.fromDocument(document));
        }
        return Main.getGson().toJson(articleDataList);
    };
}
