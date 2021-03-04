package itx.fileserver.test;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.data.FileAccessManagerService;
import itx.fileserver.services.data.filesystem.FileAccessManagerServiceFilesystem;
import itx.fileserver.dto.FilterAccessManagerData;
import itx.fileserver.services.data.inmemory.FileAccessManagerServiceInmemory;
import itx.fileserver.dto.FilterConfig;
import itx.fileserver.dto.RoleId;
import itx.fileserver.test.mocks.PersistenceServiceImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FileAccessManagerServiceTest {

    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of( createInmemoryFileAccessManagerService() ),
                Arguments.of( createFilesystemFileAccessManagerService() )
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void fileAccessManagerServiceTest(FileAccessManagerService fileAccessManagerService) {
        FilterConfig filterToRemove01 = new FilterConfig("public/readonly/*", "READ", "public");
        FilterConfig filterToRemove02 = new FilterConfig("joe/for-public/*", "READ", "public");
        FilterConfig filterToRemove03 = new FilterConfig("public/*", "READ_WRITE", "public");
        FilterConfig filterToAdd = new FilterConfig("public-dir/*", "READ_WRITE", "public");
        RoleId publicRoleId = new RoleId("public");

        assertEquals(8, fileAccessManagerService.getFilters().size());
        assertEquals(3, fileAccessManagerService.getFilters(publicRoleId).size());

        fileAccessManagerService.removeFilter(filterToRemove01);
        assertEquals(7, fileAccessManagerService.getFilters().size());
        assertEquals(2, fileAccessManagerService.getFilters(publicRoleId).size());

        fileAccessManagerService.removeFilter(filterToRemove02);
        assertEquals(6, fileAccessManagerService.getFilters().size());
        assertEquals(1, fileAccessManagerService.getFilters(publicRoleId).size());

        fileAccessManagerService.removeFilter(filterToRemove03);
        assertEquals(5, fileAccessManagerService.getFilters().size());
        assertEquals(0, fileAccessManagerService.getFilters(publicRoleId).size());

        fileAccessManagerService.addFilter(filterToAdd);
        assertEquals(6, fileAccessManagerService.getFilters().size());
        assertEquals(1, fileAccessManagerService.getFilters(publicRoleId).size());
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
