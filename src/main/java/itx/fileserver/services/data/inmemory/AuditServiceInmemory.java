package itx.fileserver.services.data.inmemory;

import itx.fileserver.services.data.AuditService;
import itx.fileserver.services.data.base.AuditQueryFilter;
import itx.fileserver.dto.AuditQuery;
import itx.fileserver.dto.AuditRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public class AuditServiceInmemory implements AuditService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditServiceInmemory.class);

    private final Deque<AuditRecord> records;
    private final int maxLength;

    public AuditServiceInmemory(int maxLength) {
        LOG.info("AuditServiceInmemory: maxLength={}", maxLength);
        this.records = new ConcurrentLinkedDeque<>();
        this.maxLength = maxLength;
    }

    @Override
    public synchronized void storeAudit(AuditRecord record) {
        LOG.info("storeAudit: {} {} {}", record.getTimestamp(), record.getUserId(), record.getAction());
        records.addFirst(record);
        if (records.size() > maxLength) {
            records.removeLast();
        }
    }

    @Override
    public synchronized Collection<AuditRecord> getAudits(AuditQuery query) {
        AuditQueryFilter auditQueryFilter = new AuditQueryFilter(query);
        return records.stream().filter(auditQueryFilter).collect(Collectors.toList());
    }

}
