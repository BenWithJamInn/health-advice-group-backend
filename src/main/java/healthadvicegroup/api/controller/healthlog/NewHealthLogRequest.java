package healthadvicegroup.api.controller.healthlog;

import lombok.Data;

@Data
public class NewHealthLogRequest {
    private int healthScore;
    private String notes;
}
