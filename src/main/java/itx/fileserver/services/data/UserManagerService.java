package itx.fileserver.services.data;

import itx.fileserver.services.dto.RoleId;
import itx.fileserver.services.dto.UserData;
import itx.fileserver.services.dto.UserId;

import java.util.Collection;
import java.util.Optional;

public interface UserManagerService {

    Optional<UserData> getUser(UserId id);

    Collection<UserData> getUsers();

    void addUser(UserData userData);

    void removeUser(UserId id);

    RoleId getAnonymousRole();

    RoleId getAdminRole();

}
