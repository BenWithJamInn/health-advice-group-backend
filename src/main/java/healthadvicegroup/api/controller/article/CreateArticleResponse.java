package healthadvicegroup.api.controller.article;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class CreateArticleResponse {
    private String status;
    private UUID id;
}
