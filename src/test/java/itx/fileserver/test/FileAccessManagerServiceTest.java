package itx.fileserver.test;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.data.FileAccessManagerService;
import itx.fileserver.services.data.filesystem.FileAccessManagerServiceFilesystem;
import itx.fileserver.services.data.filesystem.dto.FilterAccessManagerData;
import itx.fileserver.services.data.inmemory.FileAccessManagerServiceInmemory;
import itx.fileserver.services.dto.FilterConfig;
import itx.fileserver.services.dto.RoleId;
import itx.fileserver.test.mocks.PersistenceServiceImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class FileAccessManagerServiceTest {

    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of( createInmemoryFileAccessManagerService() ),
                Arguments.of( createFilesystemFileAccessManagerService() )
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void fileAccessManagerServiceTest(FileAccessManagerService fileAccessManagerService) {
        FilterConfig filterToRemove01 = new FilterConfig("public/readonly/*", "READ", "public");
        FilterConfig filterToRemove02 = new FilterConfig("joe/for-public/*", "READ", "public");
        FilterConfig filterToRemove03 = new FilterConfig("public/*", "READ_WRITE", "public");
        FilterConfig filterToAdd = new FilterConfig("public-dir/*", "READ_WRITE", "public");
        RoleId publicRoleId = new RoleId("public");

        assertTrue(fileAccessManagerService.getFilters().size() == 8);
        assertTrue(fileAccessManagerService.getFilters(publicRoleId).size() == 3);

        fileAccessManagerService.removeFilter(filterToRemove01);
        assertTrue(fileAccessManagerService.getFilters().size() == 7);
        assertTrue(fileAccessManagerService.getFilters(publicRoleId).size() == 2);

        fileAccessManagerService.removeFilter(filterToRemove02);
        assertTrue(fileAccessManagerService.getFilters().size() == 6);
        assertTrue(fileAccessManagerService.getFilters(publicRoleId).size() == 1);

        fileAccessManagerService.removeFilter(filterToRemove03);
        assertTrue(fileAccessManagerService.getFilters().size() == 5);
        assertTrue(fileAccessManagerService.getFilters(publicRoleId).size() == 0);

        fileAccessManagerService.addFilter(filterToAdd);
        assertTrue(fileAccessManagerService.getFilters().size() == 6);
        assertTrue(fileAccessManagerService.getFilters(publicRoleId).size() == 1);
    }

    private static FileAccessManagerService createInmemoryFileAccessManagerService() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForFileAccessService();
        return new FileAccessManagerServiceInmemory(fileServerConfig);
    }

    private static FileAccessManagerService createFilesystemFileAccessManagerService() {
        try {
            FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForFileAccessService();
            FilterAccessManagerData filterAccessManagerData = new FilterAccessManagerData(fileServerConfig.getFilters());
            PersistenceServiceImpl persistenceService = new PersistenceServiceImpl();
            Path path = Paths.get("test", "path");
            persistenceService.persist(path, filterAccessManagerData);
            return new FileAccessManagerServiceFilesystem(path, persistenceService);
        } catch (IOException e) {
            fail();
            return null;
        }
    }

}
