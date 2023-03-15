package healthadvicegroup.api.controller.article;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class GetArticleFromFilterRequest {
    @NonNull private List<String> categories;
}
