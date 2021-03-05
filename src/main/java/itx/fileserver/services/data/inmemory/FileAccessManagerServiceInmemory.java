package itx.fileserver.services.data.inmemory;

import itx.fileserver.config.FileServerConfig;
import itx.fileserver.services.data.base.FileAccessManagerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class FileAccessManagerServiceInmemory extends FileAccessManagerServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(FileAccessManagerServiceInmemory.class);

    public FileAccessManagerServiceInmemory(FileServerConfig fileServerConfig) {
        this.filters = new ConcurrentHashMap<>();
        this.filterConfigs = new HashSet<>();
        fileServerConfig.getFilters().forEach(this::addFilter);
    }

    @Override
    public void persist() {
        LOG.debug("persist: in-memory");
    }

}
