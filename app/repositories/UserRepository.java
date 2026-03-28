package repositories;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import models.User;
import play.db.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.Optional;

@Singleton
public class UserRepository {
    private final Database database;

    @Inject
    public UserRepository(Database database) {
        this.database = database;
    }

    public Optional<User> findByEmail(String email) {
        return database.withConnection(connection -> {
            String sql = "SELECT id, email, password_hash, created_at FROM app_users WHERE email = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(mapUser(resultSet));
                }
            }
        });
    }

    public Optional<User> findById(long id) {
        return database.withConnection(connection -> {
            String sql = "SELECT id, email, password_hash, created_at FROM app_users WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(mapUser(resultSet));
                }
            }
        });
    }

    public User create(String email, String passwordHash) {
        return database.withConnection(connection -> {
            String sql = "INSERT INTO app_users (email, password_hash) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, email);
                statement.setString(2, passwordHash);
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new IllegalStateException("No se pudo obtener el ID del usuario creado");
                    }
                    long id = keys.getLong(1);
                    return findById(id).orElseThrow(() -> new IllegalStateException("Usuario creado no encontrado"));
                }
            } catch (SQLException ex) {
                if ("23505".equals(ex.getSQLState())) {
                    throw new IllegalArgumentException("El email ya existe");
                }
                throw ex;
            }
        });
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("password_hash"),
                resultSet.getObject("created_at", OffsetDateTime.class)
        );
    }
}
