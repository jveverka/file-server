package itx.fileserver.test;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.config.dto.FilterConfig;
import itx.fileserver.config.dto.UserConfig;

import java.util.ArrayList;
import java.util.List;

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

}
