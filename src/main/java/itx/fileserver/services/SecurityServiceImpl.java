package itx.fileserver.services;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.dto.RoleId;
import itx.fileserver.services.dto.UserData;
import itx.fileserver.services.dto.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SecurityServiceImpl implements SecurityService {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityServiceImpl.class);

    private final Map<UserId, UserData> users;
    private final Map<String, UserData> authorizedSessions;
    private final Map<String, UserData> anonymousSessions;
    private final RoleId anonymousRole;

    @Autowired
    public SecurityServiceImpl(FileServerConfig fileServerConfig) {
        this.users = new ConcurrentHashMap<>();
        this.authorizedSessions = new ConcurrentHashMap<>();
        this.anonymousSessions = new ConcurrentHashMap<>();
        this.anonymousRole = new RoleId(fileServerConfig.getAnonymousRole());

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
    public UserData createAnonymousSession(String sessionId) {
        UserData userData = new UserData(new UserId(sessionId), anonymousRole, "");
        anonymousSessions.put(sessionId, userData);
        return userData;
    }

    @Override
    public Optional<UserData> isAuthorized(String sessionId) {
        return Optional.ofNullable(authorizedSessions.get(sessionId));
    }

    @Override
    public Optional<UserData> authorize(String sessionId, String username, String password) {
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
    public void terminateSession(String sessionId) {
        authorizedSessions.remove(sessionId);
        anonymousSessions.remove(sessionId);
    }

    @Override
    public Optional<Set<RoleId>> getRoles(String sessionId) {
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

}
