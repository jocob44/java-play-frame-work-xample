package repositories;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import models.ProjectItem;
import play.db.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class ProjectRepository {
    private final Database database;

    @Inject
    public ProjectRepository(Database database) {
        this.database = database;
    }

    public List<ProjectItem> listByOwner(long ownerId) {
        return database.withConnection(connection -> {
            String sql = "SELECT id, name, description, status, owner_id, created_at FROM projects WHERE owner_id = ? ORDER BY id ASC";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, ownerId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<ProjectItem> items = new ArrayList<>();
                    while (resultSet.next()) {
                        items.add(mapProject(resultSet));
                    }
                    return items;
                }
            }
        });
    }

    public Optional<ProjectItem> findByIdAndOwner(long id, long ownerId) {
        return database.withConnection(connection -> {
            String sql = "SELECT id, name, description, status, owner_id, created_at FROM projects WHERE id = ? AND owner_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                statement.setLong(2, ownerId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(mapProject(resultSet));
                }
            }
        });
    }

    public ProjectItem create(String name, String description, String status, long ownerId) {
        return database.withConnection(connection -> {
            String sql = "INSERT INTO projects (name, description, status, owner_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, name);
                statement.setString(2, description);
                statement.setString(3, status);
                statement.setLong(4, ownerId);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new IllegalStateException("No se pudo obtener el ID del proyecto creado");
                    }
                    long id = keys.getLong(1);
                    return findByIdAndOwner(id, ownerId).orElseThrow(() -> new IllegalStateException("Proyecto creado no encontrado"));
                }
            }
        });
    }

    public Optional<ProjectItem> update(long id, String name, String description, String status, long ownerId) {
        return database.withConnection(connection -> {
            String sql = "UPDATE projects SET name = ?, description = ?, status = ? WHERE id = ? AND owner_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setString(2, description);
                statement.setString(3, status);
                statement.setLong(4, id);
                statement.setLong(5, ownerId);
                int rows = statement.executeUpdate();
                if (rows == 0) {
                    return Optional.empty();
                }
                return findByIdAndOwner(id, ownerId);
            }
        });
    }

    public boolean delete(long id, long ownerId) {
        return database.withConnection(connection -> {
            String sql = "DELETE FROM projects WHERE id = ? AND owner_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                statement.setLong(2, ownerId);
                return statement.executeUpdate() > 0;
            }
        });
    }

    private ProjectItem mapProject(ResultSet resultSet) throws java.sql.SQLException {
        return new ProjectItem(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("status"),
                resultSet.getLong("owner_id"),
                resultSet.getObject("created_at", OffsetDateTime.class)
        );
    }
}
