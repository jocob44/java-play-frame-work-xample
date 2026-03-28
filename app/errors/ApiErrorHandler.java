package errors;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Singleton;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.http.HttpErrorHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class ApiErrorHandler implements HttpErrorHandler {
    @Override
    public CompletionStage<Result> onClientError(Http.RequestHeader request, int statusCode, String message) {
        String code;
        if (statusCode == 400) {
            code = "bad_request";
        } else if (statusCode == 401) {
            code = "unauthorized";
        } else if (statusCode == 403) {
            code = "forbidden";
        } else if (statusCode == 404) {
            code = "not_found";
        } else {
            code = "client_error";
        }
        return apiAwareResponse(request, statusCode, code, message);
    }

    @Override
    public CompletionStage<Result> onServerError(Http.RequestHeader request, Throwable exception) {
        return apiAwareResponse(request, 500, "server_error", "Error interno del servidor");
    }

    private CompletionStage<Result> apiAwareResponse(Http.RequestHeader request, int status, String code, String message) {
        if (!request.path().startsWith("/api")) {
            return CompletableFuture.completedFuture(Results.status(status, message));
        }
        ObjectNode payload = Json.newObject()
                .put("error", code)
                .put("message", message == null || message.isBlank() ? "Error" : message);
        return CompletableFuture.completedFuture(Results.status(status, payload));
    }
}
