package itx.fileserver.config;

import itx.fileserver.services.data.FileAccessManagerService;
import itx.fileserver.services.data.UserManagerService;
import itx.fileserver.services.data.inmemory.FileAccessManagerServiceImpl;
import itx.fileserver.services.data.inmemory.UserManagerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataServiceProviderConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DataServiceProviderConfig.class);
    private static final String INMEMORY_DATA = "inmemory";

    private final FileServerConfig fileServerConfig;
    private final FileAccessManagerService fileAccessManagerService;
    private final UserManagerService userManagerService;

    @Autowired
    public DataServiceProviderConfig(FileServerConfig fileServerConfig) {
        this.fileServerConfig = fileServerConfig;
        LOG.info("DataServiceProviderConfig: {}", fileServerConfig.getDataStorage());
        if (INMEMORY_DATA.equals(fileServerConfig.getDataStorage())) {
            this.fileAccessManagerService = new FileAccessManagerServiceImpl(fileServerConfig);
            this.userManagerService = new UserManagerServiceImpl(fileServerConfig);
        } else {
            throw new UnsupportedOperationException("Unsupported data storage type!");
        }
    }

    @Bean
    public FileAccessManagerService getFileAccessManagerService() {
        LOG.info("getFileAccessManagerService: {}", fileServerConfig.getDataStorage());
        return fileAccessManagerService;
    }

    @Bean
    public UserManagerService getUserManagerService() {
        LOG.info("getUserManagerService: {}", fileServerConfig.getDataStorage());
        return userManagerService;
    }

}
