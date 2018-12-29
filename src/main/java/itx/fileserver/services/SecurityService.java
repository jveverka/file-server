package itx.fileserver.services;

import itx.fileserver.services.dto.RoleId;
import itx.fileserver.services.dto.UserData;

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
    UserData createAnonymousSession(String sessionId);

    /**
     * Verify is session is authorized.
     * @param sessionId unique session id.
     * @return {@link UserData} if session is authorized, empty if not.
     */
    Optional<UserData> isAuthorized(String sessionId);

    /**
     * Authorize user session.
     * @param sessionId unique session id.
     * @param username username for the user's identity.
     * @param password password for the user's identity.
     * @return {@link UserData} if session is authorized, empty if not.
     */
    Optional<UserData> authorize(String sessionId, String username, String password);

    /**
     * Terminate existing session.
     * @param sessionId unique session id.
     */
    void terminateSession(String sessionId);

    /**
     * Get roles for existing session.
     * @param sessionId unique session id.
     * @return set of user's roles for the session or empty if session does not exist.
     */
    Optional<Set<RoleId>> getRoles(String sessionId);

}
