package healthadvicegroup.api.controller.account;

import lombok.Data;

@Data
public class AuthRequest {
    private String username; // (email)
    private String password;
}
