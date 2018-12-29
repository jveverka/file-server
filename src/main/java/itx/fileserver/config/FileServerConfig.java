package itx.fileserver.config;

import itx.fileserver.config.dto.FilterConfig;
import itx.fileserver.config.dto.UserConfig;
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

}
