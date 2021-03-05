package itx.fileserver.services.data.inmemory;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.data.base.UserManagerServiceImpl;
import itx.fileserver.dto.RoleId;
import itx.fileserver.dto.UserData;
import itx.fileserver.dto.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserManagerServiceInmemory extends UserManagerServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(UserManagerServiceInmemory.class);

    public UserManagerServiceInmemory(FileServerConfig fileServerConfig) {
        this.users = new ConcurrentHashMap<>();
        fileServerConfig.getUsers().forEach(uc->{
            Set<RoleId> roles = new HashSet<>();
            uc.getRoles().forEach(r-> roles.add(new RoleId(r)));
            UserData userData = new UserData(new UserId(uc.getUsername()), roles, uc.getPassword());
            LOG.info("User: {}", uc.getUsername());
            users.put(userData.getId(), userData);
        });
        this.anonymousRole = new RoleId(fileServerConfig.getAnonymousRole());
        this.adminRole = new RoleId(fileServerConfig.getAdminRole());
    }


    @Override
    public void persist() {
        LOG.debug("persist: in-memory");
    }

}
