package itx.fileserver.services;

import itx.fileserver.services.data.UserManagerService;
import itx.fileserver.services.dto.RoleId;
import itx.fileserver.services.dto.SessionId;
import itx.fileserver.services.dto.SessionInfo;
import itx.fileserver.services.dto.Sessions;
import itx.fileserver.services.dto.UserData;
import itx.fileserver.services.dto.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SecurityServiceImpl implements SecurityService {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityServiceImpl.class);

    private final UserManagerService userService;
    private final Map<SessionId, UserData> authorizedSessions;
    private final Map<SessionId, UserData> anonymousSessions;

    @Autowired
    public SecurityServiceImpl(UserManagerService userService) {
        this.userService = userService;
        this.authorizedSessions = new ConcurrentHashMap<>();
        this.anonymousSessions = new ConcurrentHashMap<>();
    }

    @Override
    public UserData createAnonymousSession(SessionId sessionId) {
        UserData userData = new UserData(new UserId(sessionId.getId()), userService.getAnonymousRole(), "");
        anonymousSessions.put(sessionId, userData);
        return userData;
    }

    @Override
    public Optional<UserData> isAuthorized(SessionId sessionId) {
        return Optional.ofNullable(authorizedSessions.get(sessionId));
    }

    @Override
    public Optional<UserData> isAnonymous(SessionId sessionId) {
        return Optional.ofNullable(anonymousSessions.get(sessionId));
    }

    @Override
    public boolean isAuthorizedAdmin(SessionId sessionId) {
        UserData userData = authorizedSessions.get(sessionId);
        if (userData != null) {
            return userData.getRoles().contains(userService.getAdminRole());
        }
        return false;
    }

    @Override
    public Optional<UserData> authorize(SessionId sessionId, String username, String password) {
        UserId userId = new UserId(username);
        Optional<UserData> userData = userService.getUser(userId);
        if (userData.isPresent() && userData.get().verifyPassword(password)) {
            authorizedSessions.put(sessionId, userData.get());
            anonymousSessions.remove(sessionId);
            return userData;
        }
        return Optional.empty();
    }

    @Override
    public void terminateSession(SessionId sessionId) {
        authorizedSessions.remove(sessionId);
        anonymousSessions.remove(sessionId);
    }

    @Override
    public Optional<Set<RoleId>> getRoles(SessionId sessionId) {
        UserData userData = authorizedSessions.get(sessionId);
        if (userData != null) {
            return Optional.of(userData.getRoles());
        } else {
            userData = anonymousSessions.get(sessionId);
            if (userData != null) {
                return Optional.of(userData.getRoles());
            }
        }
        return Optional.empty();
    }

    @Override
    public Sessions getActiveSessions() {
        List<SessionInfo> anonymous = new ArrayList<>();
        List<SessionInfo> users = new ArrayList<>();
        List<SessionInfo> admins = new ArrayList<>();
        anonymousSessions.forEach((id,user)->{
            anonymous.add(new SessionInfo(id, user.getId(), user.getRoles()));
        });
        authorizedSessions.forEach((id,user)->{
            if (user.getRoles().contains(userService.getAdminRole())) {
                admins.add(new SessionInfo(id, user.getId(), user.getRoles()));
            } else {
                users.add(new SessionInfo(id, user.getId(), user.getRoles()));
            }
        });
        return new Sessions(anonymous, users, admins);
    }

}
