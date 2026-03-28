package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.AuthService;

public class AuthController extends Controller {
    private final AuthService authService;

    @Inject
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public Result register(Http.Request request) {
        try {
            Credentials credentials = parseCredentials(request.body().asJson());
            AuthService.AuthResult authResult = authService.register(credentials.email(), credentials.password());
            return created(toAuthResponse(authResult));
        } catch (IllegalArgumentException ex) {
            return badRequest(error("validation_error", ex.getMessage()));
        }
    }

    public Result login(Http.Request request) {
        try {
            Credentials credentials = parseCredentials(request.body().asJson());
            AuthService.AuthResult authResult = authService.login(credentials.email(), credentials.password());
            return ok(toAuthResponse(authResult));
        } catch (IllegalArgumentException ex) {
            return unauthorized(error("unauthorized", ex.getMessage()));
        }
    }

    private Credentials parseCredentials(JsonNode json) {
        if (json == null) {
            throw new IllegalArgumentException("Body JSON requerido");
        }
        String email = textValue(json, "email");
        String password = textValue(json, "password");
        return new Credentials(email, password);
    }

    private String textValue(JsonNode json, String field) {
        JsonNode node = json.get(field);
        if (node == null || node.asText().isBlank()) {
            throw new IllegalArgumentException("El campo '" + field + "' es requerido");
        }
        return node.asText();
    }

    private ObjectNode toAuthResponse(AuthService.AuthResult authResult) {
        return Json.newObject()
                .put("userId", authResult.userId())
                .put("email", authResult.email())
                .put("token", authResult.token());
    }

    private ObjectNode error(String code, String message) {
        return Json.newObject()
                .put("error", code)
                .put("message", message);
    }

    private record Credentials(String email, String password) {
    }
}
