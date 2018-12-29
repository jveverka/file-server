package itx.fileserver.test;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.data.FileAccessManagerService;
import itx.fileserver.services.data.inmemory.FileAccessManagerServiceImpl;
import itx.fileserver.services.dto.FilterConfig;
import itx.fileserver.services.dto.RoleId;
import org.junit.Assert;
import org.junit.Test;

public class FileAccessManagerServiceTest {

    @Test
    public void inMemoryFileAccessManagerServiceTest() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForFileAccessService();
        FileAccessManagerService fileAccessManagerService = new FileAccessManagerServiceImpl(fileServerConfig);
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

}
