package healthadvicegroup.api.controller.article;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ArticleData {
    @NonNull @Setter private UUID id;
    @NonNull private String title;
    @NonNull private String description;
    @Nullable private String body;
    @NonNull private List<String> categories;

    /**
     * Turns this data into a {@link Document}
     *
     * @return The document
     */
    public Document toDocument() {
        Document doc = new Document();
        doc.append("_id", this.id.toString());
        doc.append("title", this.title);
        doc.append("description", this.description);
        doc.append("body", this.body);
        doc.append("categories", this.categories);
        return doc;
    }

    public static ArticleData fromDocument(Document document) {
        return new ArticleData(
                UUID.fromString(document.getString("_id")),
                document.getString("title"),
                document.getString("description"),
                document.getString("body"),
                document.getList("categories", String.class)
        );
    }
}
