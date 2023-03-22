package healthadvicegroup.api.controller;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import healthadvicegroup.api.Main;
import healthadvicegroup.api.controller.account.AccountInfo;
import healthadvicegroup.api.controller.account.AccountType;
import healthadvicegroup.api.controller.account.AuthRequest;
import healthadvicegroup.api.controller.account.AuthResponse;
import healthadvicegroup.api.database.DatabaseManager;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.security.crypto.bcrypt.BCrypt;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountController {
    private static final MongoCollection<Document> collection = DatabaseManager.getDatabase().getCollection("Accounts");
    private static final Pattern passwordCheck = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})");
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static final Route signUp = (Request request, Response response) -> {
        AuthRequest authRequest = Main.getGson().fromJson(request.body(), healthadvicegroup.api.controller.account.AuthRequest.class);
        // check for request contents
        if (authRequest.getUsername() == null || authRequest.getPassword() == null) {
            response.status(400);
            return Main.getGson().toJson(new AuthResponse("failed", "Invalid Request."));
        }
        // validate email address
        InternetAddress emailAddress = new InternetAddress(authRequest.getUsername());
        try {
            emailAddress.validate();
        } catch (AddressException e) {
            response.status(400);
            return Main.getGson().toJson(new AuthResponse("failed", "Invalid Email."));
        }
        if (authRequest.getUsername().length() > 320) {
            response.status(400);
            return Main.getGson().toJson(new AuthResponse("failed", "Invalid Email."));
        }
        // validate password complexity
        Matcher matcher = passwordCheck.matcher(authRequest.getPassword());
        if (!matcher.find()) {
            response.status(400);
            return Main.getGson().toJson(new AuthResponse("failed", "Invalid Password."));
        }
        // check if email already exists
        FindIterable<Document> userList = collection.find(new Document("username", authRequest.getUsername()));
        Document user = userList.first();
        if (user != null) {
            response.status(400);
            return Main.getGson().toJson(new AuthResponse("failed", "Account with this email already exists."));
        }
        // hash password
        String salt = BCrypt.gensalt();
        String hash = BCrypt.hashpw(authRequest.getPassword(), salt);
        // misc info
        UUID id = UUID.randomUUID();
        Date now = new Date();
        String token = genToken();
        // store in db
        Document document = new Document();
        document.append("_id", id.toString());
        document.append("username", authRequest.getUsername());
        document.append("password", hash);
        document.append("accountCreation", now);
        document.append("lastLogin", now);
        document.append("accountType", AccountType.USER.toString());
        document.append("token", token);
        collection.insertOne(document);
        // response
        AuthResponse signUpResponse = new AuthResponse(
                "success",
                "Signed Up.",
                id,
                token
        );
        return Main.getGson().toJson(signUpResponse);
    };

    public static final Route signIn = (Request request, Response response) -> {
        AuthRequest authRequest = Main.getGson().fromJson(request.body(), AuthRequest.class);
        // check for request contents
        if (authRequest.getUsername() == null || authRequest.getPassword() == null) {
            response.status(400);
            return Main.getGson().toJson(new AuthResponse("failed", "Invalid Request."));
        }
        // fetch details from database
        FindIterable<Document> users = collection.find(new Document("username", authRequest.getUsername()));
        Document user = users.first();
        if (user == null) {
            response.status(400);
            return Main.getGson().toJson(new AuthResponse("failed", "Invalid email or password."));
        }
        // compare passwords
        if (!BCrypt.checkpw(authRequest.getPassword(), user.getString("password"))) {
            response.status(401);
            return Main.getGson().toJson(new AuthResponse("failed", "Invalid email or password."));
        }
        // gen new token and update db
        String token = genToken();
        Bson update = Updates.combine(
                Updates.set("token", token),
                Updates.currentDate("lastLogin")
        );
        collection.updateOne(new Document("_id", user.getString("_id")), update);
        // send response with new token
        AuthResponse authResponse = new AuthResponse(
                "success",
                "Signed in.",
                UUID.fromString(user.getString("_id")),
                token
        );
        return Main.getGson().toJson(authResponse);
    };

    /**
     * Generates a secure token that the client can use to authorise
     *
     * @return The token string
     */
    private static String genToken() {
        byte[] randomByteArray = new byte[32];
        secureRandom.nextBytes(randomByteArray);
        return base64Encoder.encodeToString(randomByteArray);
    }

    /**
     * Retrieve account info from token, null if no account is found
     *
     * @param token The auth token
     *
     * @return The account info or null
     */
    public static AccountInfo getAccountInfo(String token) {
        FindIterable<Document> documents = collection.find(new Document("token", token));
        Document document = documents.first();
        // if no documents then there is no account
        if (document == null) {
            return null;
        }
        return AccountInfo.fromDocument(document);
    }
}
