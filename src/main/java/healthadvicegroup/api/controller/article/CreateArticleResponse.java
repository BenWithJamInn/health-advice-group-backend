package healthadvicegroup.api.controller.article;

import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class CreateArticleResponse {
    @NonNull private String status;
    @NonNull private UUID id;
}
