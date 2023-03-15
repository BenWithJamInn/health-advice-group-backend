package healthadvicegroup.api.controller.article;

import healthadvicegroup.api.controller.ArticleController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import javax.print.Doc;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ArticleData {
    @Setter private UUID id;
    private String title;
    private String description;
    private String body;
    private List<String> categories;

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
