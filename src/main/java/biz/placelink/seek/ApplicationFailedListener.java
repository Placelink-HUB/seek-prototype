package biz.placelink.seek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;

import jakarta.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unused")
public class ApplicationFailedListener implements ApplicationListener<ApplicationFailedEvent> {

    private static final Logger log = LoggerFactory.getLogger(ApplicationFailedListener.class);

    private final File file;

    public ApplicationFailedListener(File file) {
        Assert.notNull(file, "File must not be null");
        this.file = file;
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationFailedEvent event) {
        try (FileWriter writer = new FileWriter(this.file)) {
            writer.write("application failed");
            log.debug("application failed");
        } catch (IOException e) {
            log.warn("Failed to write to file {}", this.file, e);
        }
    }
}
