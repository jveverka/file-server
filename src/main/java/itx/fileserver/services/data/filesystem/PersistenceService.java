package itx.fileserver.services.data.filesystem;

import itx.fileserver.dto.AuditQuery;
import itx.fileserver.dto.AuditRecord;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public interface PersistenceService {

    void persist(Path path, Object data) throws IOException;

    <T> T restore(Path path, Class<T> type) throws IOException;

    void append(Path path, AuditRecord data) throws IOException;

    Collection<AuditRecord> filterAudits(Path path, AuditQuery auditQuery) throws IOException;

}
