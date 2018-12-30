package itx.fileserver.services.data;

import itx.fileserver.services.dto.AuditQuery;
import itx.fileserver.services.dto.AuditRecord;

import java.util.Collection;

public interface AuditService {

    void storeAudit(AuditRecord record);

    Collection<AuditRecord> getAudits(AuditQuery query);

}
