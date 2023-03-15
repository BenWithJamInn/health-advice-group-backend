package healthadvicegroup.api.controller.account;

import com.mongodb.lang.Nullable;
import lombok.Data;

import java.util.UUID;

@Data
public class AuthResponse {
    private String status;
    private String message;
    @Nullable private UUID id;
    @Nullable private String token;

    public AuthResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public AuthResponse(String status, String message, @Nullable UUID id, @Nullable String token) {
        this.status = status;
        this.message = message;
        this.id = id;
        this.token = token;
    }
}
