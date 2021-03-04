package itx.fileserver.dto;

import java.util.Collection;

public class Sessions {

    private final Collection<SessionInfo> anonymousSessions;
    private final Collection<SessionInfo> userSessions;
    private final Collection<SessionInfo> adminSessions;

    public Sessions(Collection<SessionInfo> anonymousSessions, Collection<SessionInfo> userSessions, Collection<SessionInfo> adminSessions) {
        this.anonymousSessions = anonymousSessions;
        this.userSessions = userSessions;
        this.adminSessions = adminSessions;
    }

    public Collection<SessionInfo> getAnonymousSessions() {
        return anonymousSessions;
    }

    public Collection<SessionInfo> getUserSessions() {
        return userSessions;
    }

    public Collection<SessionInfo> getAdminSessions() {
        return adminSessions;
    }

}
