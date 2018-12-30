package itx.fileserver.services.data.filesystem;

import java.io.IOException;
import java.nio.file.Path;

public interface PersistenceService {

    void persist(Path path, Object data) throws IOException;

    <T> T restore(Path path, Class<T> type) throws IOException;

}
