package biz.placelink.seek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.Assert;

import jakarta.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unused")
public class ContextClosedListener implements ApplicationListener<ContextClosedEvent> {

    private static final Logger log = LoggerFactory.getLogger(ContextClosedListener.class);

    private final File file;

    public ContextClosedListener(File file) {
        Assert.notNull(file, "File must not be null");
        this.file = file;
    }

    @Override
    public void onApplicationEvent(@Nonnull ContextClosedEvent event) {
        try (FileWriter writer = new FileWriter(this.file)) {
            writer.write("context closed");
            log.debug("context closed");
        } catch (IOException e) {
            log.warn("Failed to write to file {}", this.file, e);
        }
    }
}
