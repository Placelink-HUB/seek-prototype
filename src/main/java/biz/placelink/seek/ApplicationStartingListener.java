package biz.placelink.seek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;

import jakarta.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unused")
public class ApplicationStartingListener implements ApplicationListener<ApplicationStartingEvent> {

    private static final Logger log = LoggerFactory.getLogger(ApplicationStartingListener.class);

    private final File file;

    public ApplicationStartingListener(File file) {
        Assert.notNull(file, "File must not be null");
        this.file = file;
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationStartingEvent event) {
        try (FileWriter writer = new FileWriter(this.file)) {
            writer.write("application starting");
            log.debug("application starting");
        } catch (IOException e) {
            log.warn("Failed to write to file {}", this.file, e);
        }
    }
}
