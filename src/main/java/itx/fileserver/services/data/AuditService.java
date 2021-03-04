package itx.fileserver.services.data;

import itx.fileserver.dto.AuditQuery;
import itx.fileserver.dto.AuditRecord;

import java.util.Collection;

public interface AuditService {

    void storeAudit(AuditRecord record);

    Collection<AuditRecord> getAudits(AuditQuery query);

}
