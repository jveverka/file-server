package itx.fileserver.services.dto;

import java.util.Set;

public class UserData {

    private final UserId id;
    private final Set<RoleId> roles;
    private final String password;

    public UserData(UserId id, Set<RoleId> roles, String password) {
        this.id = id;
        this.roles = roles;
        this.password = password;
    }

    public UserId getId() {
        return id;
    }

    public Set<RoleId> getRoles() {
        return roles;
    }

    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }

}
