package security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

public class AuthAction extends Action<Authenticated> {
    private final JwtService jwtService;

    @Inject
    public AuthAction(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        String authHeader = request.getHeaders().get("Authorization").orElse("");
        if (!authHeader.startsWith("Bearer ")) {
            return completedUnauthorized("Falta el token Bearer");
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            return completedUnauthorized("Token vacío");
        }

        try {
            long userId = jwtService.parseUserId(token);
            Http.Request authenticatedRequest = request.addAttr(RequestAttrs.USER_ID, userId);
            return delegate.call(authenticatedRequest);
        } catch (IllegalArgumentException ex) {
            return completedUnauthorized(ex.getMessage());
        }
    }

    private CompletionStage<Result> completedUnauthorized(String message) {
        ObjectNode body = Json.newObject()
                .put("error", "unauthorized")
                .put("message", message);
        return java.util.concurrent.CompletableFuture.completedFuture(unauthorized(body));
    }
}
