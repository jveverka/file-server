package itx.fileserver.test;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.data.FileAccessManagerService;
import itx.fileserver.services.data.filesystem.FileAccessManagerServiceFilesystem;
import itx.fileserver.services.data.filesystem.dto.FilterAccessManagerData;
import itx.fileserver.services.data.inmemory.FileAccessManagerServiceInmemory;
import itx.fileserver.services.dto.FilterConfig;
import itx.fileserver.services.dto.RoleId;
import itx.fileserver.test.mocks.PersistenceServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class FileAccessManagerServiceTest {

    private final FileAccessManagerService fileAccessManagerService;

    public FileAccessManagerServiceTest(FileAccessManagerService fileAccessManagerService) {
        this.fileAccessManagerService = fileAccessManagerService;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                { createInmemoryFileAccessManagerService() },
                { createFilesystemFileAccessManagerService() }
        });
    }

    @Test
    public void fileAccessManagerServiceTest() {
        FilterConfig filterToRemove01 = new FilterConfig("public/readonly/*", "READ", "public");
        FilterConfig filterToRemove02 = new FilterConfig("joe/for-public/*", "READ", "public");
        FilterConfig filterToRemove03 = new FilterConfig("public/*", "READ_WRITE", "public");
        FilterConfig filterToAdd = new FilterConfig("public-dir/*", "READ_WRITE", "public");
        RoleId publicRoleId = new RoleId("public");

        Assert.assertTrue(fileAccessManagerService.getFilters().size() == 8);
        Assert.assertTrue(fileAccessManagerService.getFilters(publicRoleId).size() == 3);

        fileAccessManagerService.removeFilter(filterToRemove01);
        Assert.assertTrue(fileAccessManagerService.getFilters().size() == 7);
        Assert.assertTrue(fileAccessManagerService.getFilters(publicRoleId).size() == 2);

        fileAccessManagerService.removeFilter(filterToRemove02);
        Assert.assertTrue(fileAccessManagerService.getFilters().size() == 6);
        Assert.assertTrue(fileAccessManagerService.getFilters(publicRoleId).size() == 1);

        fileAccessManagerService.removeFilter(filterToRemove03);
        Assert.assertTrue(fileAccessManagerService.getFilters().size() == 5);
        Assert.assertTrue(fileAccessManagerService.getFilters(publicRoleId).size() == 0);

        fileAccessManagerService.addFilter(filterToAdd);
        Assert.assertTrue(fileAccessManagerService.getFilters().size() == 6);
        Assert.assertTrue(fileAccessManagerService.getFilters(publicRoleId).size() == 1);
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
            Assert.fail();
            return null;
        }
    }

}
