package itx.fileserver.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class UserManagerData {

    private final String anonymousRole;
    private final String adminRole;
    private final Collection<UserConfig> users;

    @JsonCreator
    public UserManagerData(@JsonProperty("anonymousRole") String anonymousRole,
                           @JsonProperty("adminRole") String adminRole,
                           @JsonProperty("users") Collection<UserConfig> users) {
        this.anonymousRole = anonymousRole;
        this.adminRole = adminRole;
        this.users = users;
    }

    public String getAnonymousRole() {
        return anonymousRole;
    }

    public String getAdminRole() {
        return adminRole;
    }

    public Collection<UserConfig> getUsers() {
        return users;
    }

}
