package itx.fileserver.test.mocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.fileserver.services.data.filesystem.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersistenceServiceImpl implements PersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceServiceImpl.class);

    private final Map<Key, String> data;
    private final ObjectMapper objectMapper;

    public PersistenceServiceImpl() {
        this.data = new HashMap<>();
        this.objectMapper = new ObjectMapper();
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
