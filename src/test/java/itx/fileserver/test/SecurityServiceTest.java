package itx.fileserver.test;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.SecurityService;
import itx.fileserver.services.SecurityServiceImpl;
import itx.fileserver.services.data.AuditService;
import itx.fileserver.services.data.UserManagerService;
import itx.fileserver.services.data.inmemory.AuditServiceInmemory;
import itx.fileserver.services.data.inmemory.UserManagerServiceInmemory;
import itx.fileserver.services.dto.RoleId;
import itx.fileserver.services.dto.SessionId;
import itx.fileserver.services.dto.Sessions;
import itx.fileserver.services.dto.UserData;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityServiceTest {

    private static final SessionId authorizedSessionJoe = new SessionId("SessionJoe");
    private static final SessionId authorizedSessionJane = new SessionId("SessionJane");
    private static final SessionId authorizedSessionAdmin = new SessionId("SessionAdmin");
    private static final SessionId anonymousSession = new SessionId("SessionAnonymous");
    private static final SessionId notExistingSession  = new SessionId("notexisting");
    private static final String validPassword = "secret";
    private static final String invalidPassword = "xxxx";

    @Test
    public void testValidLoginAndSession() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForSecurityService();
        UserManagerService userManagerService = new UserManagerServiceInmemory(fileServerConfig);
        AuditService auditService = new AuditServiceInmemory(1024);
        SecurityService securityService = new SecurityServiceImpl(userManagerService, auditService);
        Optional<UserData> authorized = null;
        Optional<Set<RoleId>> roles = null;
        Sessions activeSessions = null;

        activeSessions = securityService.getActiveSessions();
        assertTrue(activeSessions.getAnonymousSessions().size() == 0);
        assertTrue(activeSessions.getUserSessions().size() == 0);
        assertTrue(activeSessions.getAdminSessions().size() == 0);

        //login session 1
        authorized = securityService.authorize(authorizedSessionJoe, "joe", validPassword);
        assertTrue(authorized.isPresent());
        assertTrue(authorized.get().verifyPassword(validPassword));
        roles = securityService.getRoles(authorizedSessionJoe);
        assertTrue(roles.isPresent());
        authorized = securityService.isAuthorized(authorizedSessionJoe);
        assertTrue(authorized.isPresent());

        //login session 2
        authorized = securityService.authorize(authorizedSessionJane, "jane", validPassword);
        assertTrue(authorized.isPresent());
        assertTrue(authorized.get().verifyPassword(validPassword));
        roles = securityService.getRoles(authorizedSessionJane);
        assertTrue(roles.isPresent());
        authorized = securityService.isAuthorized(authorizedSessionJane);
        assertTrue(authorized.isPresent());

        activeSessions = securityService.getActiveSessions();
        assertTrue(activeSessions.getAnonymousSessions().size() == 0);
        assertTrue(activeSessions.getUserSessions().size() == 2);
        assertTrue(activeSessions.getAdminSessions().size() == 0);

        //logout both sessions
        securityService.terminateSession(authorizedSessionJoe);
        securityService.terminateSession(authorizedSessionJane);

        //check session 1 status
        authorized = securityService.isAuthorized(authorizedSessionJoe);
        assertFalse(authorized.isPresent());

        //check session 2 status
        authorized = securityService.isAuthorized(authorizedSessionJane);
        assertFalse(authorized.isPresent());

        activeSessions = securityService.getActiveSessions();
        assertTrue(activeSessions.getAnonymousSessions().size() == 0);
        assertTrue(activeSessions.getUserSessions().size() == 0);
        assertTrue(activeSessions.getAdminSessions().size() == 0);

    }

    @Test
    public void testInvalidSession() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForSecurityService();
        UserManagerService userManagerService = new UserManagerServiceInmemory(fileServerConfig);
        AuditService auditService = new AuditServiceInmemory(1024);
        SecurityService securityService = new SecurityServiceImpl(userManagerService, auditService);
        Optional<UserData> authorized = null;
        Optional<Set<RoleId>> roles = null;
        Sessions activeSessions = null;

        roles = securityService.getRoles(notExistingSession);
        assertFalse(roles.isPresent());
        authorized = securityService.isAuthorized(notExistingSession);
        assertFalse(authorized.isPresent());

        activeSessions = securityService.getActiveSessions();
        assertTrue(activeSessions.getAnonymousSessions().size() == 0);
        assertTrue(activeSessions.getUserSessions().size() == 0);
        assertTrue(activeSessions.getAdminSessions().size() == 0);
    }

    @Test
    public void testInvalidLogin() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForSecurityService();
        UserManagerService userManagerService = new UserManagerServiceInmemory(fileServerConfig);
        AuditService auditService = new AuditServiceInmemory(1024);
        SecurityService securityService = new SecurityServiceImpl(userManagerService, auditService);
        Optional<UserData> authorized = null;
        Optional<Set<RoleId>> roles = null;

        authorized = securityService.authorize(authorizedSessionJoe, "joe", invalidPassword);
        assertFalse(authorized.isPresent());
        authorized = securityService.isAuthorized(authorizedSessionJoe);
        assertFalse(authorized.isPresent());
        roles = securityService.getRoles(authorizedSessionJoe);
        assertFalse(roles.isPresent());

        authorized = securityService.authorize(authorizedSessionJane, "jane", invalidPassword);
        assertFalse(authorized.isPresent());
        authorized = securityService.isAuthorized(authorizedSessionJane);
        assertFalse(authorized.isPresent());
        roles = securityService.getRoles(authorizedSessionJane);
        assertFalse(roles.isPresent());
    }

    @Test
    public void testAnonymousSession() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForSecurityService();
        UserManagerService userManagerService = new UserManagerServiceInmemory(fileServerConfig);
        AuditService auditService = new AuditServiceInmemory(1024);
        SecurityService securityService = new SecurityServiceImpl(userManagerService, auditService);
        Optional<UserData> authorized = null;
        Optional<Set<RoleId>> roles = null;
        UserData anonymousUser = null;
        Sessions activeSessions = null;

        //create anonymous session for valid user and anonymous one
        anonymousUser = securityService.createAnonymousSession(authorizedSessionJane);
        assertNotNull(anonymousUser);
        anonymousUser = securityService.createAnonymousSession(anonymousSession);
        assertNotNull(anonymousUser);
        assertTrue(securityService.isAnonymous(authorizedSessionJane).isPresent());
        assertTrue(securityService.isAnonymous(anonymousSession).isPresent());

        //login one valid user (not admin)
        authorized = securityService.authorize(authorizedSessionJane, "jane", validPassword);
        assertTrue(authorized.isPresent());
        assertTrue(authorized.get().verifyPassword(validPassword));

        //verify if sessions have correct access privileges
        assertTrue(securityService.isAuthorized(authorizedSessionJane).isPresent());
        assertFalse(securityService.isAuthorized(anonymousSession).isPresent());

        assertFalse(securityService.isAnonymous(authorizedSessionJane).isPresent());
        assertTrue(securityService.isAnonymous(anonymousSession).isPresent());

        assertFalse(securityService.isAuthorizedAdmin(authorizedSessionJane));
        assertFalse(securityService.isAuthorizedAdmin(anonymousSession));

        activeSessions = securityService.getActiveSessions();
        assertTrue(activeSessions.getAnonymousSessions().size() == 1);
        assertTrue(activeSessions.getUserSessions().size() == 1);
        assertTrue(activeSessions.getAdminSessions().size() == 0);

        //terminate both sessions
        securityService.terminateSession(authorizedSessionJane);
        securityService.terminateSession(anonymousSession);

        //verify if sessions have correct access privileges after session termination
        assertFalse(securityService.isAuthorized(authorizedSessionJane).isPresent());
        assertFalse(securityService.isAuthorized(anonymousSession).isPresent());

        assertFalse(securityService.isAnonymous(authorizedSessionJane).isPresent());
        assertFalse(securityService.isAnonymous(anonymousSession).isPresent());

        assertFalse(securityService.isAuthorizedAdmin(authorizedSessionJane));
        assertFalse(securityService.isAuthorizedAdmin(anonymousSession));
    }

    @Test
    public void testAdminSession() {
        FileServerConfig fileServerConfig = TestUtils.createFileServerConfigForSecurityService();
        UserManagerService userManagerService = new UserManagerServiceInmemory(fileServerConfig);
        AuditService auditService = new AuditServiceInmemory(1024);
        SecurityService securityService = new SecurityServiceImpl(userManagerService, auditService);
        Optional<UserData> authorized = null;
        Optional<Set<RoleId>> roles = null;
        UserData anonymousUser = null;
        Sessions activeSessions = null;

        //create anonymous session for valid users
        anonymousUser = securityService.createAnonymousSession(authorizedSessionJane);
        assertNotNull(anonymousUser);
        anonymousUser = securityService.createAnonymousSession(authorizedSessionAdmin);
        assertNotNull(anonymousUser);
        assertTrue(securityService.isAnonymous(authorizedSessionJane).isPresent());
        assertTrue(securityService.isAnonymous(authorizedSessionAdmin).isPresent());

        //login one valid users (one admin, one ordinary user)
        authorized = securityService.authorize(authorizedSessionJane, "jane", validPassword);
        assertTrue(authorized.isPresent());
        assertTrue(authorized.get().verifyPassword(validPassword));
        authorized = securityService.authorize(authorizedSessionAdmin, "master", validPassword);
        assertTrue(authorized.isPresent());
        assertTrue(authorized.get().verifyPassword(validPassword));

        //verify if sessions have correct access privileges
        assertTrue(securityService.isAuthorized(authorizedSessionJane).isPresent());
        assertTrue(securityService.isAuthorized(authorizedSessionAdmin).isPresent());

        assertFalse(securityService.isAnonymous(authorizedSessionJane).isPresent());
        assertFalse(securityService.isAnonymous(authorizedSessionAdmin).isPresent());

        assertFalse(securityService.isAuthorizedAdmin(authorizedSessionJane));
        assertTrue(securityService.isAuthorizedAdmin(authorizedSessionAdmin));

        activeSessions = securityService.getActiveSessions();
        assertTrue(activeSessions.getAnonymousSessions().size() == 0);
        assertTrue(activeSessions.getUserSessions().size() == 1);
        assertTrue(activeSessions.getAdminSessions().size() == 1);

        //terminate both sessions
        securityService.terminateSession(authorizedSessionJane);
        securityService.terminateSession(authorizedSessionAdmin);

        //verify if sessions have correct access privileges after session termination
        assertFalse(securityService.isAuthorized(authorizedSessionJane).isPresent());
        assertFalse(securityService.isAuthorized(authorizedSessionAdmin).isPresent());

        assertFalse(securityService.isAnonymous(authorizedSessionJane).isPresent());
        assertFalse(securityService.isAnonymous(authorizedSessionAdmin).isPresent());

        assertFalse(securityService.isAuthorizedAdmin(authorizedSessionJane));
        assertFalse(securityService.isAuthorizedAdmin(authorizedSessionAdmin));
    }

}
