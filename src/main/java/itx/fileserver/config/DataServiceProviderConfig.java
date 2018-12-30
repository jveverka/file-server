package itx.fileserver.config;

import itx.fileserver.services.data.AuditService;
import itx.fileserver.services.data.FileAccessManagerService;
import itx.fileserver.services.data.UserManagerService;
import itx.fileserver.services.data.filesystem.AuditServiceFilesystem;
import itx.fileserver.services.data.filesystem.FileAccessManagerServiceFilesystem;
import itx.fileserver.services.data.filesystem.PersistenceService;
import itx.fileserver.services.data.filesystem.PersistenceServiceImpl;
import itx.fileserver.services.data.filesystem.UserManagerServiceFilesystem;
import itx.fileserver.services.data.inmemory.AuditServiceInmemory;
import itx.fileserver.services.data.inmemory.FileAccessManagerServiceInmemory;
import itx.fileserver.services.data.inmemory.UserManagerServiceInmemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "fileserver")
public class DataServiceProviderConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DataServiceProviderConfig.class);
    private static final String INMEMORY_DATA = "inmemory";
    private static final String FILESYSTEM_DATA = "filesystem";

    private final FileServerConfig fileServerConfig;
    private final FileAccessManagerService fileAccessManagerService;
    private final UserManagerService userManagerService;
    private final AuditService auditService;

    @Autowired
    public DataServiceProviderConfig(FileServerConfig fileServerConfig) throws IOException {
        this.fileServerConfig = fileServerConfig;
        LOG.info("DataServiceProviderConfig: {}", fileServerConfig.getDataStorage());
        if (INMEMORY_DATA.equals(fileServerConfig.getDataStorage())) {
            this.fileAccessManagerService = new FileAccessManagerServiceInmemory(fileServerConfig);
            this.userManagerService = new UserManagerServiceInmemory(fileServerConfig);
            this.auditService = new AuditServiceInmemory(2048);
        } else if (FILESYSTEM_DATA.equals(fileServerConfig.getDataStorage())) {
            LOG.info("DataServiceProviderConfig: basedir={}", fileServerConfig.getDataBasedir());
            Path basePath = Paths.get(fileServerConfig.getDataBasedir());
            PersistenceService persistenceService = new PersistenceServiceImpl();
            this.fileAccessManagerService =
                    new FileAccessManagerServiceFilesystem(basePath.resolve("file-access-manager-data.json").normalize(), persistenceService);
            this.userManagerService =
                    new UserManagerServiceFilesystem(basePath.resolve("user-manager-data.json").normalize(), persistenceService);
            this.auditService = new AuditServiceFilesystem(basePath.resolve("audit-data.log").normalize(), persistenceService);
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

    @Bean
    public AuditService getAuditService() {
        LOG.info("getAuditService: {}", fileServerConfig.getDataStorage());
        return auditService;
    }

}
