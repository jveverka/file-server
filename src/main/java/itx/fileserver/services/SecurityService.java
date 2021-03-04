package itx.fileserver.services;

import itx.fileserver.dto.RoleId;
import itx.fileserver.dto.SessionId;
import itx.fileserver.dto.Sessions;
import itx.fileserver.dto.UserData;

import java.util.Optional;
import java.util.Set;

/**
 * Service for managing and authorizing user sessions.
 */
public interface SecurityService {

    /**
     * Create anonymous session
     * @param sessionId unique session id.
     * @return {@link UserData} for anonymous session.
     */
    UserData createAnonymousSession(SessionId sessionId);

    /**
     * Verify is session is authorized.
     * @param sessionId unique session id.
     * @return {@link UserData} if session is authorized, empty if not.
     */
    Optional<UserData> isAuthorized(SessionId sessionId);

    /**
     * Verify is session is anonymous.
     * @param sessionId unique session id.
     * @return {@link UserData} if session is anonymous, empty if not.
     */
    Optional<UserData> isAnonymous(SessionId sessionId);

    /**
     * Verify is session is authorized and has admin role.
     * @param sessionId unique session id.
     * @return true if session is authorized admin user, false if not.
     */
    boolean isAuthorizedAdmin(SessionId sessionId);

    /**
     * Authorize user session.
     * @param sessionId unique session id.
     * @param username username for the user's identity.
     * @param password password for the user's identity.
     * @return {@link UserData} if session is authorized, empty if not.
     */
    Optional<UserData> authorize(SessionId sessionId, String username, String password);

    /**
     * Terminate existing session.
     * @param sessionId unique session id.
     */
    void terminateSession(SessionId sessionId);

    /**
     * Get userData for existing session.
     * @param sessionId unique session id.
     * @return set of user's roles for the session or empty if session does not exist.
     */
    Optional<Set<RoleId>> getRoles(SessionId sessionId);

    /**
     * Get active sessions currently managed by {@link SecurityService}
     * @return all active sessions.
     */
    Sessions getActiveSessions();

}
