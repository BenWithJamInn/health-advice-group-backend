package healthadvicegroup.api.controller.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AccountInfo {
    private UUID id;
    private String userName;
    private AccountType accountType;

    /**
     * Returns {@link AccountInfo} from a {@link Document}
     *
     * @param document The document to deserialize
     *
     * @return The new instance
     */
    public static AccountInfo fromDocument(Document document) {
        return new AccountInfo(
                UUID.fromString(document.getString("_id")),
                document.getString("username"),
                AccountType.valueOf(document.getString("accountType"))
        );
    }
}
