package itx.fileserver.test;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.data.UserManagerService;
import itx.fileserver.services.data.inmemory.UserManagerServiceImpl;
import itx.fileserver.services.dto.RoleId;
import itx.fileserver.services.dto.UserData;
import itx.fileserver.services.dto.UserId;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class UserManagerServiceTest {

    @Test
    public void inMemoryUserManagerServiceTest() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForFileAccessService();
        UserManagerService userManagerService = new UserManagerServiceImpl(fileServerConfig);
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

}
