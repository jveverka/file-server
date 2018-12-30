package itx.fileserver.test;

import itx.fileserver.services.data.AuditService;
import itx.fileserver.services.data.inmemory.AuditServiceInmemory;
import itx.fileserver.services.dto.AuditQuery;
import itx.fileserver.services.dto.AuditRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static itx.fileserver.services.dto.AuditConstants.USER_ACCESS;

public class AuditServiceInMemoryTest {

    @Test
    public void testInMemotyRolingBuffer() {
        AuditService auditService = new AuditServiceInmemory(3);
        Collection<AuditRecord> audits = auditService.getAudits(AuditQuery.MATCH_ALL);
        Assert.assertTrue(audits.size() == 0);

        auditService.storeAudit(new AuditRecord(1546182100L, USER_ACCESS.NAME, USER_ACCESS.LOGIN, "user1", "", "login ok", null));
        auditService.storeAudit(new AuditRecord(1546182200L, USER_ACCESS.NAME, USER_ACCESS.LOGIN, "user1", "", "login ok", null));
        auditService.storeAudit(new AuditRecord(1546182300L, USER_ACCESS.NAME, USER_ACCESS.LOGIN, "user1", "", "login ok", null));

        audits = auditService.getAudits(AuditQuery.MATCH_ALL);
        Assert.assertTrue(audits.size() == 3);
        Iterator<AuditRecord> iterator = audits.iterator();
        AuditRecord auditRecord = iterator.next();
        Assert.assertTrue(auditRecord.getTimestamp() == 1546182300L);
        auditRecord = iterator.next();
        Assert.assertTrue(auditRecord.getTimestamp() == 1546182200L);
        auditRecord = iterator.next();
        Assert.assertTrue(auditRecord.getTimestamp() == 1546182100L);

        auditService.storeAudit(new AuditRecord(1546182400L, USER_ACCESS.NAME, USER_ACCESS.LOGIN, "user1", "", "login ok", null));

        audits = auditService.getAudits(AuditQuery.MATCH_ALL);
        Assert.assertTrue(audits.size() == 3);
        iterator = audits.iterator();
        auditRecord = iterator.next();
        Assert.assertTrue(auditRecord.getTimestamp() == 1546182400L);
        auditRecord = iterator.next();
        Assert.assertTrue(auditRecord.getTimestamp() == 1546182300L);
        auditRecord = iterator.next();
        Assert.assertTrue(auditRecord.getTimestamp() == 1546182200L);
    }

}
