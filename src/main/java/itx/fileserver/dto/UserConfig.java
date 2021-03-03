package itx.fileserver.dto;

import java.util.Arrays;
import java.util.List;

public class UserConfig {

    private String username;
    private String password;
    private List<String> roles;

    public UserConfig() {
    }

    public UserConfig(String username, String password, String ... roles) {
        this.username = username;
        this.password = password;
        this.roles = Arrays.asList(roles);
    }

    public UserConfig(String username, String password, List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
