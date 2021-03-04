package itx.fileserver.test;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.dto.FilterConfig;
import itx.fileserver.dto.UserConfig;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TestUtils {

    private TestUtils() {
        throw new UnsupportedOperationException("do not instantiate utility class");
    }

    public static FileServerConfig createFileServerConfigForSecurityService() {
        List<UserConfig> users = new ArrayList<>();
        users.add(new UserConfig("master", "secret", "master", "public"));
        users.add(new UserConfig("joe", "secret", "joe", "public"));
        users.add(new UserConfig("jane", "secret", "jane", "public"));
        users.add(new UserConfig("public", "secret", "public"));
        FileServerConfig fileServerConfig = new FileServerConfig();
        fileServerConfig.setUsers(users);
        fileServerConfig.setAdminRole("master");
        fileServerConfig.setAnonymousRole("anonymous");
        return fileServerConfig;
    }

    public static FileServerConfig createFileServerConfigForFileAccessService() {
        List<FilterConfig> filters = new ArrayList<>();
        filters.add(new FilterConfig("*", "READ_WRITE", "master"));
        filters.add(new FilterConfig("joe/*", "READ_WRITE", "joe"));
        filters.add(new FilterConfig("joe/for-jane/*", "READ_WRITE", "joe", "jane"));
        filters.add(new FilterConfig("joe/for-public/*", "READ", "public"));
        filters.add(new FilterConfig("joe/for-public/*", "READ_WRITE", "joe"));
        filters.add(new FilterConfig("jane/*", "READ_WRITE", "jane"));
        filters.add(new FilterConfig("public/*", "READ_WRITE", "public"));
        filters.add(new FilterConfig("public/readonly/*", "READ", "public"));
        FileServerConfig fileServerConfig = createFileServerConfigForSecurityService();
        fileServerConfig.setFilters(filters);
        return fileServerConfig;
    }

    public static Optional<String> getJSessionId(String cookies) {
        String[] split = cookies.split(";");
        for (int i=0; i<split.length; i++) {
            if (split[i].startsWith("JSESSIONID=")) {
                String[] jSplit = split[i].split("=");
                if (jSplit.length > 1) {
                    return Optional.of(jSplit[1]);
                }
            }
        }
        return Optional.empty();
    }

    public static HttpHeaders createHeaders(String jSessionId) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", "JSESSIONID=" + jSessionId);
        return requestHeaders;
    }

}
