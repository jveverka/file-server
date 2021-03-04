package itx.fileserver.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuditRecord {

    private final Long timestamp;
    private final String category;
    private final String action;
    private final String userId;
    private final String resource;
    private final String message;
    private final String data;

    @JsonCreator
    public AuditRecord(@JsonProperty("timestamp") Long timestamp,
                       @JsonProperty("category") String category,
                       @JsonProperty("action") String action,
                       @JsonProperty("userId") String userId,
                       @JsonProperty("resource") String resource,
                       @JsonProperty("message") String message,
                       @JsonProperty("data") String data) {
        this.timestamp = timestamp;
        this.category = category;
        this.action = action;
        this.userId = userId;
        this.resource = resource;
        this.message = message;
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getCategory() {
        return category;
    }

    public String getAction() {
        return action;
    }

    public String getUserId() {
        return userId;
    }

    public String getResource() {
        return resource;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
}
