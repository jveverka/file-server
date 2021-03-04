package itx.fileserver.services.data.base;

import itx.fileserver.services.FileUtils;
import itx.fileserver.dto.AuditQuery;
import itx.fileserver.dto.AuditRecord;

import java.util.function.Predicate;

public class AuditQueryFilter implements Predicate<AuditRecord> {

    private final AuditQuery query;
    private final long timeBegin;
    private final long timeEnd;

    public AuditQueryFilter(AuditQuery query) {
        this.query = query;
        this.timeBegin = (query.getTimeBegin() != null) ? query.getTimeBegin() : 0;
        this.timeEnd = (query.getTimeEnd() != null) ? query.getTimeEnd() : Integer.MAX_VALUE;
    }

    @Override
    public boolean test(AuditRecord auditRecord) {
        if (!(auditRecord.getTimestamp() >= timeBegin && auditRecord.getTimestamp()<= timeEnd)) {
            return false;
        }
        if (query.getCategory() != null && !query.getCategory().equals(auditRecord.getCategory())) {
            return false;
        }
        if (query.getUserId() != null && !query.getUserId().equals(auditRecord.getUserId())) {
            return false;
        }
        if (query.getAction() != null && !query.getAction().equals(auditRecord.getAction())) {
            return false;
        }
        if (query.getResourcePattern() != null && !FileUtils.wildcardMatch(auditRecord.getResource(), query.getResourcePattern())) {
            return false;
        }
        if (query.getMessagePattern() != null && !auditRecord.getMessage().matches(query.getMessagePattern())) {
            return false;
        }
        return true;
    }

}
