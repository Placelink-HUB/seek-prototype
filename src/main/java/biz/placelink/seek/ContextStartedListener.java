package biz.placelink.seek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.util.Assert;

import jakarta.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unused")
public class ContextStartedListener implements ApplicationListener<ContextStartedEvent> {

    private static final Logger log = LoggerFactory.getLogger(ContextStartedListener.class);

    private final File file;

    public ContextStartedListener(File file) {
        Assert.notNull(file, "File must not be null");
        this.file = file;
    }

    @Override
    public void onApplicationEvent(@Nonnull ContextStartedEvent event) {
        try (FileWriter writer = new FileWriter(this.file)) {
            writer.write("context started");
            log.debug("context started");
        } catch (IOException e) {
            log.warn("Failed to write to file {}", this.file, e);
        }
    }
}
