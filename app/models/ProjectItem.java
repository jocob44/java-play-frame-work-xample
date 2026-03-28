package models;

import java.time.OffsetDateTime;

public class ProjectItem {
    private final long id;
    private final String name;
    private final String description;
    private final String status;
    private final long ownerId;
    private final OffsetDateTime createdAt;

    public ProjectItem(long id, String name, String description, String status, long ownerId, OffsetDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
