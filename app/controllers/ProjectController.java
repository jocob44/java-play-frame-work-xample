package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import models.ProjectItem;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repositories.ProjectRepository;
import security.Authenticated;
import security.RequestAttrs;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Authenticated
public class ProjectController extends Controller {
    private static final Set<String> ALLOWED_STATUS = Set.of("TODO", "IN_PROGRESS", "DONE");
    private final ProjectRepository projectRepository;

    @Inject
    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Result list(Http.Request request) {
        long userId = requireUserId(request);
        List<ProjectItem> items = projectRepository.listByOwner(userId);
        ArrayNode response = Json.newArray();
        items.forEach(item -> response.add(toJson(item)));
        return ok(response);
    }

    public Result create(Http.Request request) {
        try {
            long userId = requireUserId(request);
            ProjectPayload payload = parsePayload(request.body().asJson());
            ProjectItem created = projectRepository.create(payload.name(), payload.description(), payload.status(), userId);
            return created(toJson(created));
        } catch (IllegalArgumentException ex) {
            return badRequest(error("validation_error", ex.getMessage()));
        }
    }

    public Result update(Http.Request request, Long id) {
        try {
            long userId = requireUserId(request);
            ProjectPayload payload = parsePayload(request.body().asJson());
            return projectRepository.update(id, payload.name(), payload.description(), payload.status(), userId)
                    .map(project -> ok(toJson(project)))
                    .orElseGet(() -> notFound(error("not_found", "Proyecto no encontrado")));
        } catch (IllegalArgumentException ex) {
            return badRequest(error("validation_error", ex.getMessage()));
        }
    }

    public Result delete(Http.Request request, Long id) {
        long userId = requireUserId(request);
        boolean deleted = projectRepository.delete(id, userId);
        if (!deleted) {
            return notFound(error("not_found", "Proyecto no encontrado"));
        }
        return noContent();
    }

    private long requireUserId(Http.Request request) {
        return request.attrs().getOptional(RequestAttrs.USER_ID)
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado en request"));
    }

    private ProjectPayload parsePayload(JsonNode json) {
        if (json == null) {
            throw new IllegalArgumentException("Body JSON requerido");
        }
        String name = textValue(json, "name");
        String description = textValue(json, "description");
        String status = normalizeStatus(textValue(json, "status"));
        return new ProjectPayload(name, description, status);
    }

    private String normalizeStatus(String value) {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_STATUS.contains(normalized)) {
            throw new IllegalArgumentException("status debe ser TODO, IN_PROGRESS o DONE");
        }
        return normalized;
    }

    private String textValue(JsonNode json, String field) {
        JsonNode node = json.get(field);
        if (node == null || node.asText().isBlank()) {
            throw new IllegalArgumentException("El campo '" + field + "' es requerido");
        }
        return node.asText().trim();
    }

    private ObjectNode toJson(ProjectItem item) {
        return Json.newObject()
                .put("id", item.getId())
                .put("name", item.getName())
                .put("description", item.getDescription())
                .put("status", item.getStatus())
                .put("ownerId", item.getOwnerId())
                .put("createdAt", item.getCreatedAt().toString());
    }

    private ObjectNode error(String code, String message) {
        return Json.newObject()
                .put("error", code)
                .put("message", message);
    }

    private record ProjectPayload(String name, String description, String status) {
    }
}
