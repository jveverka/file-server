package itx.fileserver.test;

import itx.fileserver.FileServer;
import itx.fileserver.services.FileService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class FileServerITTest {

    private static final Logger LOG = LoggerFactory.getLogger(FileServerITTest.class);

    private static FileServer fileServer;
    private static FileService fileService;
    private static Path basePath;

    @BeforeAll
    public static void init() {
        fileServer = new FileServer();
        fileServer.start(new String[] { "--spring.config.location=src/test/resources/application-test.yaml" });
        Optional<ConfigurableApplicationContext> context = fileServer.getContext();
        if (context.isPresent()) {
            NamedBeanHolder<FileService> fileAccessServiceNamedBeanHolder =
                    context.get().getAutowireCapableBeanFactory().resolveNamedBean(FileService.class);
            fileService = fileAccessServiceNamedBeanHolder.getBeanInstance();
            basePath = fileService.getFileStorageInfo().getBasePath();
            LOG.info("Test basePath: {}", basePath.toString());
        } else {
            fail();
        }
    }

    //@Test
    public void testApplication() {
        assertNotNull(fileService);
    }

    @AfterAll
    public static void shutdown() {
        fileServer.stop();
    }

}
