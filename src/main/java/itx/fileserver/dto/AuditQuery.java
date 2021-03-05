package itx.fileserver.dto;

import itx.fileserver.services.FileUtils;

public class AuditQuery {

    public static final AuditQuery MATCH_ALL = new AuditQuery();

    private String userId;
    private Long timeBegin;
    private Long timeEnd;
    private String category;
    private String action;
    private String resourcePattern;
    private String messagePattern;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(Long timeBegin) {
        this.timeBegin = timeBegin;
    }

    public Long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResourcePattern() {
        return resourcePattern;
    }

    public void setResourcePattern(String resourcePattern) {
        this.resourcePattern = resourcePattern;
    }

    public String getMessagePattern() {
        return messagePattern;
    }

    public void setMessagePattern(String messagePattern) {
        this.messagePattern = messagePattern;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static class Builder {
        private final AuditQuery auditQuery;
        private Builder() {
            this.auditQuery = new AuditQuery();
        }

        /**
         * Set userId to match exactly.
         * If not set any userId will match.
         * @param userId
         * @return
         */
        public Builder withUserId(String userId) {
            auditQuery.setUserId(userId);
            return this;
        }

        /**
         * Set begin time.
         * If not set, time interval is not checked.
         * @param timeStamp
         * @return
         */
        public Builder from(Long timeStamp) {
            auditQuery.setTimeBegin(timeStamp);
            return this;
        }

        /**
         * Set end time.
         * If not set, time interval is not checked.
         * @param timeStamp
         * @return
         */
        public Builder to(Long timeStamp) {
            auditQuery.setTimeEnd(timeStamp);
            return this;
        }

        /**
         * Set category to match exactly.
         * If not set, all categories will match.
         * @param category
         * @return
         */
        public Builder withCategory(String category) {
            auditQuery.setCategory(category);
            return this;
        }

        /**
         * Set action to match exactly.
         * If not set, all actions will match.
         * @param action
         * @return
         */
        public Builder withAction(String action) {
            auditQuery.setAction(action);
            return this;
        }

        /**
         * Set wildcard matcher patter {@link FileUtils#wildcardMatch(String, String)}.
         * If not set, any resource will match.
         * @param resourcePattern
         * @return
         */
        public Builder withResourcePattern(String resourcePattern) {
            auditQuery.setResourcePattern(resourcePattern);
            return this;
        }

        /**
         * Set regexp pattern.
         * If not set, any message will match.
         * @param messagePattern
         * @return
         */
        public Builder withMessagePattern(String messagePattern) {
            auditQuery.setMessagePattern(messagePattern);
            return this;
        }

        public AuditQuery build() {
            return auditQuery;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

}
