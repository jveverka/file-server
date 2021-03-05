package itx.fileserver.services.data.filesystem;

import itx.fileserver.dto.UserConfig;
import itx.fileserver.services.data.base.UserManagerServiceImpl;
import itx.fileserver.dto.UserManagerData;
import itx.fileserver.dto.RoleId;
import itx.fileserver.dto.UserData;
import itx.fileserver.dto.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserManagerServiceFilesystem extends UserManagerServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(UserManagerServiceFilesystem.class);

    private final Path dataPath;
    private final PersistenceService persistenceService;

    public UserManagerServiceFilesystem(Path dataPath, PersistenceService persistenceService) throws IOException {
        this.dataPath = dataPath;
        this.persistenceService = persistenceService;
        LOG.info("dataPath={}", dataPath);

        UserManagerData userManagerData = persistenceService.restore(dataPath, UserManagerData.class);
        this.users = new ConcurrentHashMap<>();
        userManagerData.getUsers().forEach(uc->{
            Set<RoleId> roles = new HashSet<>();
            uc.getRoles().forEach(r-> roles.add(new RoleId(r)));
            UserData userData = new UserData(new UserId(uc.getUsername()), roles, uc.getPassword());
            LOG.info("User: {}", uc.getUsername());
            users.put(userData.getId(), userData);
        });
        this.anonymousRole = new RoleId(userManagerData.getAnonymousRole());
        this.adminRole = new RoleId(userManagerData.getAdminRole());
    }


    @Override
    public void persist() {
        LOG.debug("persist: filesystem");
        try {
            List<UserConfig> userConfigList = new ArrayList<>();
            users.values().forEach(u->{
                List<String> roles = new ArrayList<>();
                u.getRoles().forEach(r -> roles.add(r.getId()));
                userConfigList.add(new UserConfig(u.getId().getId(), u.password(), roles));
            });
            UserManagerData userManagerData = new UserManagerData(anonymousRole.getId(), adminRole.getId(), userConfigList);
            persistenceService.persist(dataPath, userManagerData);
        } catch (IOException e) {
            LOG.error("Persist ERROR:", e);
        }
    }

}
