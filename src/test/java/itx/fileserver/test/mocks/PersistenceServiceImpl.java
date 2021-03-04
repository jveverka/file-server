package itx.fileserver.test.mocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.fileserver.services.data.base.AuditQueryFilter;
import itx.fileserver.services.data.filesystem.PersistenceService;
import itx.fileserver.dto.AuditQuery;
import itx.fileserver.dto.AuditRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public class PersistenceServiceImpl implements PersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceServiceImpl.class);

    private final Map<Key, String> data;
    private final ObjectMapper objectMapper;
    private final Map<Path, Deque<AuditRecord>> records;

    public PersistenceServiceImpl() {
        this.data = new HashMap<>();
        this.objectMapper = new ObjectMapper();
        this.records = new HashMap<>();
    }

    @Override
    public void persist(Path path, Object data) throws IOException {
        LOG.info("persist: {} {}", path.toString(), data.getClass().getName());
        String stringData = objectMapper.writeValueAsString(data);
        this.data.put(new Key(data.getClass(), path), stringData);
    }

    @Override
    public <T> T restore(Path path, Class<T> type) throws IOException {
        LOG.info("restore: {} {}", path.toString(), type.getName());
        Key key = new Key(type, path);
        String stringData = this.data.get(key);
        return objectMapper.readValue(stringData, type);
    }

    @Override
    public void append(Path path, AuditRecord data) throws IOException {
        Deque<AuditRecord> auditRecords = records.get(path);
        if (auditRecords == null) {
            auditRecords  = new ConcurrentLinkedDeque<>();
            records.put(path, auditRecords);
        }
        auditRecords.add(data);
    }

    @Override
    public Collection<AuditRecord> filterAudits(Path path, AuditQuery auditQuery) throws IOException {
        Deque<AuditRecord> auditRecords = records.get(path);
        if (auditRecords != null) {
            AuditQueryFilter auditQueryFilter = new AuditQueryFilter(auditQuery);
            return auditRecords.stream().filter(auditQueryFilter).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    private class Key {

        private final Class<?> type;
        private final Path path;

        public Key(Class<?> type, Path path) {
            this.type = type;
            this.path = path;
        }

        public Class getType() {
            return type;
        }

        public Path getPath() {
            return path;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(type, key.type) &&
                    Objects.equals(path, key.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, path);
        }

    }

}
