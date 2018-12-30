package itx.fileserver.test;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.FileAccessService;
import itx.fileserver.services.FileAccessServiceImpl;
import itx.fileserver.services.SecurityService;
import itx.fileserver.services.SecurityServiceImpl;
import itx.fileserver.services.data.FileAccessManagerService;
import itx.fileserver.services.data.UserManagerService;
import itx.fileserver.services.data.inmemory.FileAccessManagerServiceInmemory;
import itx.fileserver.services.data.inmemory.UserManagerServiceInmemory;
import itx.fileserver.services.dto.RoleId;
import itx.fileserver.services.dto.SessionId;
import itx.fileserver.services.dto.UserData;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@RunWith(Parameterized.class)
public class FileAccessServiceTest {

    private static final SessionId authorizedSessionJoe = new SessionId("SessionJoe");
    private static final SessionId authorizedSessionJane = new SessionId("SessionJane");
    private static final SessionId authorizedSessionPublic = new SessionId("SessionPublic");
    private static final String validPassword = "secret";

    private static FileAccessService fileAccessService;
    private static SecurityService securityService;

    @BeforeClass
    public static void init() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForFileAccessService();
        UserManagerService userManagerService = new UserManagerServiceInmemory(fileServerConfig);
        FileAccessManagerService fileAccessManagerService = new FileAccessManagerServiceInmemory(fileServerConfig);
        securityService = new SecurityServiceImpl(userManagerService);
        fileAccessService = new FileAccessServiceImpl(fileAccessManagerService);
        Optional<UserData> authorized = null;

        authorized = securityService.authorize(authorizedSessionJoe, "joe", validPassword);
        Assert.assertTrue(authorized.isPresent());
        authorized = securityService.authorize(authorizedSessionJane, "jane", validPassword);
        Assert.assertTrue(authorized.isPresent());
        authorized = securityService.authorize(authorizedSessionPublic, "public", validPassword);
        Assert.assertTrue(authorized.isPresent());
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { authorizedSessionJoe, "joe/data", true, true },
                { authorizedSessionJoe, "joe/data.txt", true, true },
                { authorizedSessionJoe, "jane/data.txt", false, false },

                { authorizedSessionJoe, "secret.pdf", false, false },
                { authorizedSessionJane, "secret.pdf", false, false },
                { authorizedSessionPublic, "secret.pdf", false, false },

                { authorizedSessionJane, "jane/data", true, true },
                { authorizedSessionJane, "jane/data.txt", true, true },
                { authorizedSessionJane, "jane/nested/data.txt", true, true },

                { authorizedSessionJane, "joe/for-jane/data", true, true },
                { authorizedSessionJane, "joe/for-jane/data.txt", true, true },
                { authorizedSessionJane, "joe/for-public/data.txt", true, false },
                { authorizedSessionJane, "joe/data.txt", false, false },

                { authorizedSessionJoe, "public/data", true, true },
                { authorizedSessionJoe, "public/readonly", true, true },
                { authorizedSessionJoe, "public/readonly/image.jpg", true, false },

                { authorizedSessionJane, "public/data", true, true },
                { authorizedSessionJane, "public/readonly", true, true },
                { authorizedSessionJane, "public/readonly/image.jpg", true, false },

                { authorizedSessionPublic, "public/data", true, true },
                { authorizedSessionPublic, "public/readonly", true, true },
                { authorizedSessionPublic, "public/readonly/image.jpg", true, false },
                { authorizedSessionPublic, "public/readonly/subdir/image.jpg", true, false },
        });
    }

    private SessionId sessionId;
    private String path;
    boolean expectedCanRead;
    private boolean expectedCanReadAndWrite;

    public FileAccessServiceTest(SessionId sessionId, String path, boolean expectedCanRead, boolean expectedCanReadAndWrite) {
        this.sessionId = sessionId;
        this.path = path;
        this.expectedCanRead = expectedCanRead;
        this.expectedCanReadAndWrite = expectedCanReadAndWrite;
    }

    @Test
    public void testReadAccess() {
        Optional<Set<RoleId>> roles = securityService.getRoles(sessionId);
        Path p = Paths.get(path);
        boolean canRead = fileAccessService.canRead(roles.get(), p);
        Assert.assertTrue(canRead == expectedCanRead);
    }

    @Test
    public void testWriteAccess() {
        Optional<Set<RoleId>> roles = securityService.getRoles(sessionId);
        Path p = Paths.get(path);
        boolean canReadAndWrite = fileAccessService.canReadAndWrite(roles.get(), p);
        Assert.assertTrue(canReadAndWrite == expectedCanReadAndWrite);
    }

}
