package healthadvicegroup.api.controller.article;

import lombok.Getter;

import java.util.List;

@Getter
public class GetArticleFromFilterRequest {
    private List<String> categories;
}
