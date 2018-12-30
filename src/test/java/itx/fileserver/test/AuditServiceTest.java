package itx.fileserver.test;

import itx.fileserver.services.data.AuditService;
import itx.fileserver.services.data.filesystem.AuditServiceFilesystem;
import itx.fileserver.services.data.filesystem.PersistenceService;
import itx.fileserver.services.data.inmemory.AuditServiceInmemory;
import itx.fileserver.services.dto.AuditQuery;
import itx.fileserver.services.dto.AuditRecord;
import itx.fileserver.test.mocks.PersistenceServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import static itx.fileserver.services.dto.AuditConstants.FILE_ACCESS;
import static itx.fileserver.services.dto.AuditConstants.USER_ACCESS;

@RunWith(Parameterized.class)
public class AuditServiceTest {

    private final AuditService auditService;

    public AuditServiceTest(AuditService auditService) {
        this.auditService = auditService;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                { createInmemoryAuditService() },
                { createFilesystemAuditService() }
        });
    }

    @Test
    public void testQueryAuditServiceMatchAll() {
        Collection<AuditRecord> audits = auditService.getAudits(AuditQuery.MATCH_ALL);
        Assert.assertTrue(audits.size() == 10);
    }

    @Test
    public void testQueryAuditServiceMatchCategory() {
        AuditQuery auditQuery = AuditQuery.newBuilder().withCategory(USER_ACCESS.NAME).build();
        Collection<AuditRecord> audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 2);

        auditQuery = AuditQuery.newBuilder().withCategory(FILE_ACCESS.NAME).build();
        audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 8);
    }

    @Test
    public void testQueryAuditServiceMatchAction() {
        AuditQuery auditQuery = AuditQuery.newBuilder().withAction(USER_ACCESS.LOGIN).build();
        Collection<AuditRecord> audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 1);

        auditQuery = AuditQuery.newBuilder().withAction(FILE_ACCESS.DOWNLOAD).build();
        audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 2);

        auditQuery = AuditQuery.newBuilder().withAction(FILE_ACCESS.LIST_DIR).build();
        audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 3);
    }

    @Test
    public void testQueryAuditServiceMatchUser() {
        AuditQuery auditQuery = AuditQuery.newBuilder().withUserId("user1").build();
        Collection<AuditRecord> audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 8);

        auditQuery = AuditQuery.newBuilder().withUserId("user2").build();
        audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 2);
    }

    @Test
    public void testQueryAuditServiceMatchTimeIntervals() {
        AuditQuery auditQuery = AuditQuery.newBuilder().from(1546182200L).to(1546182700L).build();
        Collection<AuditRecord> audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 6);

        auditQuery = AuditQuery.newBuilder().to(1546182700L).build();
        audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 8);

        auditQuery = AuditQuery.newBuilder().from(1546182200L).build();
        audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 8);
    }

    @Test
    public void testQueryAuditServiceMatchResourcePatterns() {
        AuditQuery auditQuery = AuditQuery.newBuilder().withResourcePattern("user1/files/*").build();
        Collection<AuditRecord> audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 6);

        auditQuery = AuditQuery.newBuilder().withResourcePattern("*.txt").build();
        audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 5);
    }

    @Test
    public void testQueryAuditServiceMatchMessagePatterns() {
        AuditQuery auditQuery = AuditQuery.newBuilder().withMessagePattern("ok").build();
        Collection<AuditRecord> audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 6);

        auditQuery = AuditQuery.newBuilder().withMessagePattern("error.*").build();
        audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 2);
    }

    @Test
    public void testQueryAuditServiceMatchMixed() {
        AuditQuery auditQuery = AuditQuery.newBuilder()
                .withUserId("user1")
                .withResourcePattern("user1/files/*")
                .withMessagePattern("ok")
                .build();
        Collection<AuditRecord> audits = auditService.getAudits(auditQuery);
        Assert.assertTrue(audits.size() == 4);
    }

    private static void populateAudits(AuditService auditService) {
        auditService.storeAudit(new AuditRecord(1546182000L, USER_ACCESS.NAME, USER_ACCESS.LOGIN, "user1", "", "login ok", null));
        auditService.storeAudit(new AuditRecord(1546182100L, FILE_ACCESS.NAME, FILE_ACCESS.DOWNLOAD, "user1", "user1/files/data.txt", "ok", ""));
        auditService.storeAudit(new AuditRecord(1546182200L, FILE_ACCESS.NAME, FILE_ACCESS.UPLOAD, "user1", "user1/files/upload.txt", "ok", ""));
        auditService.storeAudit(new AuditRecord(1546182300L, FILE_ACCESS.NAME, FILE_ACCESS.DELETE, "user1", "user1/files/upload.txt", "ok", ""));
        auditService.storeAudit(new AuditRecord(1546182400L, FILE_ACCESS.NAME, FILE_ACCESS.UPLOAD, "user2", "user1/files/upload.txt", "ok", ""));
        auditService.storeAudit(new AuditRecord(1546182500L, FILE_ACCESS.NAME, FILE_ACCESS.LIST_DIR, "user1", "user1/files/", "ok", ""));
        auditService.storeAudit(new AuditRecord(1546182600L, FILE_ACCESS.NAME, FILE_ACCESS.LIST_DIR, "user1", "user1/", "ok", ""));
        auditService.storeAudit(new AuditRecord(1546182700L, FILE_ACCESS.NAME, FILE_ACCESS.LIST_DIR, "user1", "user1/xxx/", "error: file does not exits", ""));
        auditService.storeAudit(new AuditRecord(1546182800L, FILE_ACCESS.NAME, FILE_ACCESS.DOWNLOAD, "user2", "user1/files/zzzz.txt", "error: file does not exits", ""));
        auditService.storeAudit(new AuditRecord(1546182900L, USER_ACCESS.NAME, USER_ACCESS.LOGOUT, "user1", "", "logout ok", null));
    }

    public static AuditService createInmemoryAuditService() {
        AuditService auditService = new AuditServiceInmemory(1024);
        populateAudits(auditService);
        return auditService;
    }

    public static AuditService createFilesystemAuditService() {
        PersistenceService persistenceService = new PersistenceServiceImpl();
        AuditService auditService = new AuditServiceFilesystem(Paths.get("some", "path"), persistenceService);
        populateAudits(auditService);
        return auditService;
    }

}
