package itx.fileserver.dto;

import java.util.Set;

public class SessionInfo {

    private final SessionId id;
    private final UserId userId;
    private final Set<RoleId> roles;

    public SessionInfo(SessionId id, UserId userId, Set<RoleId> roles) {
        this.id = id;
        this.userId = userId;
        this.roles = roles;
    }

    public SessionId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public Set<RoleId> getRoles() {
        return roles;
    }

}
