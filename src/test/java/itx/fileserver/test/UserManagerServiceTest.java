package itx.fileserver.test;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.data.UserManagerService;
import itx.fileserver.services.data.filesystem.UserManagerServiceFilesystem;
import itx.fileserver.services.data.filesystem.dto.UserManagerData;
import itx.fileserver.services.data.inmemory.UserManagerServiceInmemory;
import itx.fileserver.services.dto.RoleId;
import itx.fileserver.services.dto.UserData;
import itx.fileserver.services.dto.UserId;
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
import java.util.Optional;

@RunWith(Parameterized.class)
public class UserManagerServiceTest {

    private final UserManagerService userManagerService;

    public UserManagerServiceTest(UserManagerService userManagerService) {
        this.userManagerService = userManagerService;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {createInmemoryUserManagerService()},
                {createFilesystemUserManagerService()}
        });
    }

    @Test
    public void userManagerServiceTest() {
        UserId masterUserId = new UserId("master");
        UserId newUserId = new UserId("newUser");

        Assert.assertTrue(userManagerService.getUsers().size() == 4);
        Optional<UserData> userData = userManagerService.getUser(masterUserId);
        Assert.assertTrue(userData.isPresent());
        userManagerService.removeUser(masterUserId);
        userData = userManagerService.getUser(masterUserId);
        Assert.assertFalse(userData.isPresent());
        Assert.assertTrue(userManagerService.getUsers().size() == 3);
        UserData newUser = new UserData(newUserId, new RoleId("newUser"), "secret");
        userManagerService.addUser(newUser);
        Assert.assertTrue(userManagerService.getUsers().size() == 4);
        userData = userManagerService.getUser(newUserId);
        Assert.assertTrue(userData.isPresent());
    }

    private static UserManagerService createInmemoryUserManagerService() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForFileAccessService();
        return new UserManagerServiceInmemory(fileServerConfig);
    }

    private static UserManagerService createFilesystemUserManagerService() {
        try {
            FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForFileAccessService();
            UserManagerData userManagerData =
                    new UserManagerData(fileServerConfig.getAnonymousRole(), fileServerConfig.getAdminRole(), fileServerConfig.getUsers());
            PersistenceServiceImpl persistenceService = new PersistenceServiceImpl();
            Path path = Paths.get("test", "path");
            persistenceService.persist(path, userManagerData);
            return new UserManagerServiceFilesystem(path, persistenceService);
        } catch (IOException e) {
            Assert.fail();
            return null;
        }
    }

}