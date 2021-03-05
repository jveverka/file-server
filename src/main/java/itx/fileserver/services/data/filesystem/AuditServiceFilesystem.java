package itx.fileserver.services.data.filesystem;

import itx.fileserver.services.data.AuditService;
import itx.fileserver.dto.AuditQuery;
import itx.fileserver.dto.AuditRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

public class AuditServiceFilesystem implements AuditService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditServiceFilesystem.class);

    private final Path path;
    private final PersistenceService persistenceService;

    public AuditServiceFilesystem(Path path, PersistenceService persistenceService) {
        LOG.info("AuditServiceFilesystem: path={}", path);
        this.path = path;
        this.persistenceService = persistenceService;
    }

    @Override
    public synchronized void storeAudit(AuditRecord record) {
        try {
            persistenceService.append(path, record);
        } catch (IOException e) {
            LOG.error("persistence error:", e);
        }
    }

    @Override
    public synchronized Collection<AuditRecord> getAudits(AuditQuery query) {
        try {
            return persistenceService.filterAudits(path, query);
        } catch (IOException e) {
            LOG.error("persistence error:", e);
            return Collections.emptyList();
        }
    }

}
