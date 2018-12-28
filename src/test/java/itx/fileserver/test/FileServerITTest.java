package itx.fileserver.test;

import itx.fileserver.FileServer;
import itx.fileserver.services.FileService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Path;
import java.util.Optional;

public class FileServerITTest {

    private static final Logger LOG = LoggerFactory.getLogger(FileServerITTest.class);

    private static FileServer fileServer;
    private static FileService fileService;
    private static Path basePath;

    @BeforeClass
    public static void init() {
        fileServer = new FileServer();
        fileServer.start(new String[] { "--spring.config.location=src/test/resources/application-test.yaml" });
        Optional<ConfigurableApplicationContext> context = fileServer.getContext();
        if (context.isPresent()) {
            NamedBeanHolder<FileService> fileAccessServiceNamedBeanHolder =
                    context.get().getAutowireCapableBeanFactory().resolveNamedBean(FileService.class);
            fileService = fileAccessServiceNamedBeanHolder.getBeanInstance();
            basePath = fileService.getBasePath();
            LOG.info("Test basePath: {}", basePath.toString());
        } else {
            Assert.fail();
        }
    }

    @Test
    @Ignore
    public void testApplication() {
        Assert.assertNotNull(fileService);
    }

    @AfterClass
    public static void shutdown() {
        fileServer.stop();
    }

}
