package itx.fileserver.test;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.SecurityService;
import itx.fileserver.services.SecurityServiceImpl;
import itx.fileserver.services.dto.RoleId;
import itx.fileserver.services.dto.UserData;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

public class SecurityServiceTest {

    private static final String authorizedSession01 = "001";
    private static final String authorizedSession02 = "002";
    private static final String notExistingSession  = "notexisting";
    private static final String validPassword = "secret";
    private static final String invalidPassword = "xxxx";

    @Test
    public void testValidLoginAndSession() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForSecurityService();
        SecurityService securityService = new SecurityServiceImpl(fileServerConfig);
        Optional<UserData> authorized = null;
        Optional<Set<RoleId>> roles = null;

        //login session 1
        authorized = securityService.authorize(authorizedSession01, "joe", validPassword);
        Assert.assertTrue(authorized.isPresent());
        Assert.assertTrue(authorized.get().verifyPassword(validPassword));
        roles = securityService.getRoles(authorizedSession01);
        Assert.assertTrue(roles.isPresent());
        authorized = securityService.isAuthorized(authorizedSession01);
        Assert.assertTrue(authorized.isPresent());

        //login session 2
        authorized = securityService.authorize(authorizedSession02, "jane", validPassword);
        Assert.assertTrue(authorized.isPresent());
        Assert.assertTrue(authorized.get().verifyPassword(validPassword));
        roles = securityService.getRoles(authorizedSession02);
        Assert.assertTrue(roles.isPresent());
        authorized = securityService.isAuthorized(authorizedSession02);
        Assert.assertTrue(authorized.isPresent());

        //logout both sessions
        securityService.terminateSession(authorizedSession01);
        securityService.terminateSession(authorizedSession02);

        //check session 1 status
        authorized = securityService.isAuthorized(authorizedSession01);
        Assert.assertFalse(authorized.isPresent());

        //check session 2 status
        authorized = securityService.isAuthorized(authorizedSession02);
        Assert.assertFalse(authorized.isPresent());
    }

    @Test
    public void testInvalidSession() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForSecurityService();
        SecurityService securityService = new SecurityServiceImpl(fileServerConfig);
        Optional<UserData> authorized = null;
        Optional<Set<RoleId>> roles = null;

        roles = securityService.getRoles(notExistingSession);
        Assert.assertFalse(roles.isPresent());
        authorized = securityService.isAuthorized(notExistingSession);
        Assert.assertFalse(authorized.isPresent());
    }

    @Test
    public void testInvalidLogin() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForSecurityService();
        SecurityService securityService = new SecurityServiceImpl(fileServerConfig);
        Optional<UserData> authorized = null;
        Optional<Set<RoleId>> roles = null;

        authorized = securityService.authorize(authorizedSession01, "joe", invalidPassword);
        Assert.assertFalse(authorized.isPresent());
        authorized = securityService.isAuthorized(authorizedSession01);
        Assert.assertFalse(authorized.isPresent());
        roles = securityService.getRoles(authorizedSession01);
        Assert.assertFalse(roles.isPresent());

        authorized = securityService.authorize(authorizedSession02, "jane", invalidPassword);
        Assert.assertFalse(authorized.isPresent());
        authorized = securityService.isAuthorized(authorizedSession02);
        Assert.assertFalse(authorized.isPresent());
        roles = securityService.getRoles(authorizedSession02);
        Assert.assertFalse(roles.isPresent());
    }

}
