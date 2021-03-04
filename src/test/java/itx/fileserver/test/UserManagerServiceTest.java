package itx.fileserver.test;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.data.UserManagerService;
import itx.fileserver.services.data.filesystem.UserManagerServiceFilesystem;
import itx.fileserver.dto.UserManagerData;
import itx.fileserver.services.data.inmemory.UserManagerServiceInmemory;
import itx.fileserver.dto.RoleId;
import itx.fileserver.dto.UserData;
import itx.fileserver.dto.UserId;
import itx.fileserver.test.mocks.PersistenceServiceImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class UserManagerServiceTest {

    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of( createInmemoryUserManagerService() ),
                Arguments.of( createFilesystemUserManagerService() )
        );
    }


    @ParameterizedTest
    @MethodSource("data")
    void userManagerServiceTest(UserManagerService userManagerService) {
        UserId masterUserId = new UserId("master");
        UserId newUserId = new UserId("newUser");

        assertEquals(4, userManagerService.getUsers().size());
        Optional<UserData> userData = userManagerService.getUser(masterUserId);
        assertTrue(userData.isPresent());
        userManagerService.removeUser(masterUserId);
        userData = userManagerService.getUser(masterUserId);
        assertFalse(userData.isPresent());
        assertEquals(3, userManagerService.getUsers().size());
        UserData newUser = new UserData(newUserId, new RoleId("newUser"), "secret");
        userManagerService.addUser(newUser);
        assertEquals(4, userManagerService.getUsers().size());
        userData = userManagerService.getUser(newUserId);
        assertTrue(userData.isPresent());
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
            fail();
            return null;
        }
    }

}