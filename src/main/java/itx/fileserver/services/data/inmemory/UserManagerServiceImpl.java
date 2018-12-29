package itx.fileserver.services.data.inmemory;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.data.UserManagerService;
import itx.fileserver.services.dto.RoleId;
import itx.fileserver.services.dto.UserData;
import itx.fileserver.services.dto.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserManagerServiceImpl implements UserManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(UserManagerServiceImpl.class);

    private final Map<UserId, UserData> users;
    private final RoleId anonymousRole;
    private final RoleId adminRole;

    public UserManagerServiceImpl(FileServerConfig fileServerConfig) {
        this.users = new ConcurrentHashMap<>();
        fileServerConfig.getUsers().forEach(uc->{
            Set<RoleId> roles = new HashSet<>();
            uc.getRoles().forEach(r-> {
                roles.add(new RoleId(r));
            });
            UserData userData = new UserData(new UserId(uc.getUsername()), roles, uc.getPassword());
            LOG.info("User: {}", uc.getUsername());
            users.put(userData.getId(), userData);
        });
        this.anonymousRole = new RoleId(fileServerConfig.getAnonymousRole());
        this.adminRole = new RoleId(fileServerConfig.getAdminRole());
    }

    @Override
    public Optional<UserData> getUser(UserId id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<UserData> getUsers() {
        return Collections.unmodifiableList(new ArrayList<>(users.values()));
    }

    @Override
    public void addUser(UserData userData) {
        if (users.get(userData.getId()) != null) {
            throw new UnsupportedOperationException();
        }
        users.put(userData.getId(), userData);
    }

    @Override
    public void removeUser(UserId id) {
        users.remove(id);
    }

    @Override
    public RoleId getAnonymousRole() {
        return anonymousRole;
    }

    @Override
    public RoleId getAdminRole() {
        return adminRole;
    }

}
