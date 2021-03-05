package itx.fileserver.services.data.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import itx.fileserver.services.data.base.AuditQueryFilter;
import itx.fileserver.dto.AuditQuery;
import itx.fileserver.dto.AuditRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PersistenceServiceImpl implements PersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceServiceImpl.class);

    private final ObjectMapper objectMapper;
    private final ObjectMapper objectMapperAppender;

    public PersistenceServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapperAppender = new ObjectMapper();
    }

    @Override
    public void persist(Path path, Object data) throws IOException {
        objectMapper.writeValue(path.toFile(), data);
    }

    @Override
    public <T> T restore(Path path, Class<T> type) throws IOException {
        return objectMapper.readValue(path.toFile(), type);
    }

    @Override
    public void append(Path path, AuditRecord record) throws IOException {
        String recordData = objectMapperAppender.writeValueAsString(record) + "\n";
        if (path.toFile().isFile()) {
            Files.write(path, recordData.getBytes(), StandardOpenOption.APPEND);
        } else {
            Files.write(path, recordData.getBytes(), StandardOpenOption.CREATE);
        }
    }

    @Override
    public Collection<AuditRecord> filterAudits(Path path, AuditQuery auditQuery) throws IOException {
        AuditQueryFilter queryFilter = new AuditQueryFilter(auditQuery);
        List<AuditRecord> result = new ArrayList<>();
        Files.lines(path).forEach(recordData -> {
            try {
                AuditRecord auditRecord = objectMapperAppender.readValue(recordData, AuditRecord.class);
                if (queryFilter.test(auditRecord)) {
                    result.add(auditRecord);
                }
            } catch (IOException e) {
                LOG.error("AuditRecord deserialization error: ", e);
            }
        });
        return Collections.unmodifiableList(result);
    }

}
