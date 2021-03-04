package itx.fileserver.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

public class UserData {

    private final UserId id;
    private final Set<RoleId> roles;
    private final String password;

    @JsonCreator
    public UserData(@JsonProperty("id") UserId id,
                    @JsonProperty("roles") Set<RoleId> roles,
                    @JsonProperty("password") String password) {
        this.id = id;
        this.roles = roles;
        this.password = password;
    }

    public UserData(UserId id, RoleId role, String password) {
        this.id = id;
        this.roles = new HashSet<>();
        this.roles.add(role);
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

    public String password() {
        return password;
    }

}
