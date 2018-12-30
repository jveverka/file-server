package itx.fileserver.services.data.filesystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Path;

public class PersistenceServiceImpl implements PersistenceService {

    private final ObjectMapper objectMapper;

    public PersistenceServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void persist(Path path, Object data) throws IOException {
        objectMapper.writeValue(path.toFile(), data);
    }

    @Override
    public <T> T restore(Path path, Class<T> type) throws IOException {
        return objectMapper.readValue(path.toFile(), type);
    }

}
