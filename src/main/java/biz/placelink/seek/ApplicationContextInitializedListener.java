package biz.placelink.seek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;

import jakarta.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unused")
public class ApplicationContextInitializedListener implements ApplicationListener<ApplicationContextInitializedEvent> {

    private static final Logger log = LoggerFactory.getLogger(ApplicationContextInitializedListener.class);

    private final File file;

    public ApplicationContextInitializedListener(File file) {
        Assert.notNull(file, "File must not be null");
        this.file = file;
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationContextInitializedEvent event) {
        try (FileWriter writer = new FileWriter(this.file)) {
            writer.write("application context initialized");
            log.debug("application context initialized");
        } catch (IOException e) {
            log.warn("Failed to write to file {}", this.file, e);
        }
    }
}
