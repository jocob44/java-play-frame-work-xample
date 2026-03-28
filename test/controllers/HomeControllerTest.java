package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.NO_CONTENT;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.DELETE;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.PUT;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

public class HomeControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        Map<String, Object> config = new HashMap<>();
        config.put("db.default.driver", "org.h2.Driver");
        config.put("db.default.url", "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        config.put("db.default.username", "sa");
        config.put("db.default.password", "");
        config.put("jwt.secret", "test-secret");
        config.put("play.http.secret.key", "test-app-secret-with-enough-entropy-abcdefghijklmnopqrstuvwxyz");
        return new GuiceApplicationBuilder()
                .configure(config)
                .build();
    }

    @Test
    public void authAndProjectCrudFlow() {
        ObjectNode registerBody = play.libs.Json.newObject()
                .put("email", "demo@example.com")
                .put("password", "pass1234");
        Http.RequestBuilder registerRequest = new Http.RequestBuilder()
                .method(POST)
                .uri("/api/auth/register")
                .bodyJson(registerBody);

        Result registerResult = route(app, registerRequest);
        assertEquals(CREATED, registerResult.status());
        JsonNode registerJson = play.libs.Json.parse(contentAsString(registerResult));
        String token = registerJson.get("token").asText();
        assertNotNull(token);

        ObjectNode createBody = play.libs.Json.newObject()
                .put("name", "Integracion React")
                .put("description", "Practica backend Play")
                .put("status", "TODO");
        Http.RequestBuilder createRequest = new Http.RequestBuilder()
                .method(POST)
                .uri("/api/projects")
                .header("Authorization", "Bearer " + token)
                .bodyJson(createBody);

        Result createResult = route(app, createRequest);
        assertEquals(CREATED, createResult.status());
        JsonNode created = play.libs.Json.parse(contentAsString(createResult));
        long projectId = created.get("id").asLong();

        Http.RequestBuilder listRequest = new Http.RequestBuilder()
                .method(GET)
                .uri("/api/projects")
                .header("Authorization", "Bearer " + token);
        Result listResult = route(app, listRequest);
        assertEquals(OK, listResult.status());
        JsonNode listJson = play.libs.Json.parse(contentAsString(listResult));
        assertEquals(1, listJson.size());

        ObjectNode updateBody = play.libs.Json.newObject()
                .put("name", "Integracion React UI")
                .put("description", "Con JWT")
                .put("status", "IN_PROGRESS");
        Http.RequestBuilder updateRequest = new Http.RequestBuilder()
                .method(PUT)
                .uri("/api/projects/" + projectId)
                .header("Authorization", "Bearer " + token)
                .bodyJson(updateBody);
        Result updateResult = route(app, updateRequest);
        assertEquals(OK, updateResult.status());

        Http.RequestBuilder deleteRequest = new Http.RequestBuilder()
                .method(DELETE)
                .uri("/api/projects/" + projectId)
                .header("Authorization", "Bearer " + token);
        Result deleteResult = route(app, deleteRequest);
        assertEquals(NO_CONTENT, deleteResult.status());

        Result listAfterDelete = route(app, listRequest);
        JsonNode emptyList = play.libs.Json.parse(contentAsString(listAfterDelete));
        assertEquals(0, emptyList.size());
    }

    @Test
    public void projectsEndpointRequiresToken() {
        Http.RequestBuilder listWithoutToken = new Http.RequestBuilder()
                .method(GET)
                .uri("/api/projects");
        Result result = route(app, listWithoutToken);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void updatingNonExistingProjectReturnsNotFound() {
        ObjectNode registerBody = play.libs.Json.newObject()
                .put("email", "another@example.com")
                .put("password", "pass1234");
        Result registerResult = route(app, new Http.RequestBuilder()
                .method(POST)
                .uri("/api/auth/register")
                .bodyJson(registerBody));
        String token = play.libs.Json.parse(contentAsString(registerResult)).get("token").asText();

        ObjectNode updateBody = play.libs.Json.newObject()
                .put("name", "No existe")
                .put("description", "No existe")
                .put("status", "DONE");
        Result updateResult = route(app, new Http.RequestBuilder()
                .method(PUT)
                .uri("/api/projects/9999")
                .header("Authorization", "Bearer " + token)
                .bodyJson(updateBody));
        assertEquals(NOT_FOUND, updateResult.status());
    }

}
