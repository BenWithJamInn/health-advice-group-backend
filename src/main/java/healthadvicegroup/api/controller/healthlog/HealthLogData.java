package healthadvicegroup.api.controller.healthlog;

import lombok.Data;
import lombok.NonNull;
import org.bson.Document;

import java.util.Date;
import java.util.UUID;

@Data
public class HealthLogData {
    @NonNull private UUID id;
    @NonNull private UUID userID;
    @NonNull private Date date;
    @NonNull private int healthScore;
    @NonNull private String notes;

    /**
     * Returns {@link HealthLogData} from a {@link Document}
     *
     * @param document The document to deserialize
     *
     * @return The new instance
     */
    public static HealthLogData fromDocument(Document document) {
        try {
            return new HealthLogData(
                    UUID.fromString(document.getString("_id")),
                    UUID.fromString(document.getString("userID")),
                    document.getDate("date"),
                    document.getInteger("healthScore"),
                    document.getString("notes")
            );
        } catch (NullPointerException ignore) {
            return null;
        }
    }
}
