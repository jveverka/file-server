package itx.fileserver.services;

import itx.fileserver.config.FileServerConfig;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SecurityServiceImpl implements SecurityService {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityServiceImpl.class);

    private final Map<UserId, UserData> users;
    private final Map<SessionId, UserData> authorizedSessions;
    private final Map<SessionId, UserData> anonymousSessions;
    private final RoleId anonymousRole;
    private final RoleId adminRole;

    @Autowired
    public SecurityServiceImpl(FileServerConfig fileServerConfig) {
        this.users = new ConcurrentHashMap<>();
        this.authorizedSessions = new ConcurrentHashMap<>();
        this.anonymousSessions = new ConcurrentHashMap<>();
        this.anonymousRole = new RoleId(fileServerConfig.getAnonymousRole());
        this.adminRole = new RoleId(fileServerConfig.getAdminRole());

        fileServerConfig.getUsers().forEach(uc->{
            Set<RoleId> roles = new HashSet<>();
            uc.getRoles().forEach(r-> {
                roles.add(new RoleId(r));
            });
            UserData userData = new UserData(new UserId(uc.getUsername()), roles, uc.getPassword());
            LOG.info("User: {}", uc.getUsername());
            users.put(userData.getId(), userData);
        });
    }

    @Override
    public UserData createAnonymousSession(SessionId sessionId) {
        UserData userData = new UserData(new UserId(sessionId.getId()), anonymousRole, "");
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
            return userData.getRoles().contains(adminRole);
        }
        return false;
    }

    @Override
    public Optional<UserData> authorize(SessionId sessionId, String username, String password) {
        UserId userId = new UserId(username);
        UserData userData = users.get(userId);
        if (userData != null && userData.verifyPassword(password)) {
            authorizedSessions.put(sessionId, userData);
            anonymousSessions.remove(sessionId);
            return Optional.of(userData);
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
            if (user.getRoles().contains(adminRole)) {
                admins.add(new SessionInfo(id, user.getId(), user.getRoles()));
            } else {
                users.add(new SessionInfo(id, user.getId(), user.getRoles()));
            }
        });
        return new Sessions(anonymous, users, admins);
    }

}
