package itx.fileserver.services.data.filesystem;

import itx.fileserver.services.data.base.FileAccessManagerServiceImpl;
import itx.fileserver.dto.FilterAccessManagerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class FileAccessManagerServiceFilesystem extends FileAccessManagerServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(FileAccessManagerServiceFilesystem.class);

    private final Path dataPath;
    private final PersistenceService persistenceService;

    public FileAccessManagerServiceFilesystem(Path dataPath, PersistenceService persistenceService) throws IOException {
        this.dataPath = dataPath;
        this.persistenceService = persistenceService;
        this.filters = new ConcurrentHashMap<>();
        this.filterConfigs = new HashSet<>();

        LOG.info("dataPath={}", dataPath);
        FilterAccessManagerData filterAccessManagerData = persistenceService.restore(dataPath, FilterAccessManagerData.class);
        filterAccessManagerData.getFilterConfigs().forEach(f -> addFilter(f));
    }

    @Override
    public void persist() {
        LOG.debug("persist: filesystem");
        try {
            FilterAccessManagerData filterAccessManagerData = new FilterAccessManagerData(filterConfigs);
            persistenceService.persist(dataPath, filterAccessManagerData);
        } catch (IOException e) {
            LOG.error("Persist ERROR: ", e);
        }
    }

}
