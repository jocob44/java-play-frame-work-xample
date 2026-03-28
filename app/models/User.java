package models;

import java.time.OffsetDateTime;

public class User {
    private final long id;
    private final String email;
    private final String passwordHash;
    private final OffsetDateTime createdAt;

    public User(long id, String email, String passwordHash, OffsetDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
