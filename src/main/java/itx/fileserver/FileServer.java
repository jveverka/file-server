package itx.fileserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;

@SpringBootApplication
public class FileServer {

    private static final Logger LOG = LoggerFactory.getLogger(FileServer.class);

    private ConfigurableApplicationContext context;

    public void start(String[] args) {
        LOG.info("Spring file server demo started");
        if (context == null) {
            context = SpringApplication.run(FileServer.class, args);
            context.registerShutdownHook();
        }
    }

    public void stop() {
        if (context != null) {
            context.stop();
            context = null;
        }
    }

    public Optional<ConfigurableApplicationContext> getContext() {
        return Optional.ofNullable(context);
    }

    public static void main(String[] args) {
        FileServer fileServer = new FileServer();
        fileServer.start(args);
    }

}
