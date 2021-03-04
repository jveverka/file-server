package itx.fileserver.config;

import itx.fileserver.dto.FilterConfig;
import itx.fileserver.dto.UserConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "fileserver")
public class FileServerConfig {

    @Value("${fileserver.home}")
    private String home;

    @Value("${server.session.timeout}")
    private int sessionTimeout;

    @Value("${fileserver.anonymous.role:#{null}}")
    private String anonymousRole;

    @Value("${fileserver.admin.role:#{null}}")
    private String adminRole;

    @Value("${fileserver.data.storage:#{null}}")
    private String dataStorage;

    @Value("${fileserver.data.basedir:#{null}}")
    private String dataBasedir;

    private List<UserConfig> users;

    private List<FilterConfig> filters;

    public String getHome() {
        return home;
    }

    public List<UserConfig> getUsers() {
        return users;
    }

    public void setUsers(List<UserConfig> users) {
        this.users = users;
    }

    public List<FilterConfig> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterConfig> filters) {
        this.filters = filters;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public String getAnonymousRole() {
        return anonymousRole;
    }

    public String getAdminRole() {
        return adminRole;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public void setAnonymousRole(String anonymousRole) {
        this.anonymousRole = anonymousRole;
    }

    public void setAdminRole(String adminRole) {
        this.adminRole = adminRole;
    }

    public String getDataStorage() {
        return dataStorage;
    }

    public void setDataStorage(String dataStorage) {
        this.dataStorage = dataStorage;
    }

    public String getDataBasedir() {
        return dataBasedir;
    }

    public void setDataBasedir(String dataBasedir) {
        this.dataBasedir = dataBasedir;
    }
}
