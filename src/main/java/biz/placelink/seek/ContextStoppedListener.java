package biz.placelink.seek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.util.Assert;

import jakarta.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unused")
public class ContextStoppedListener implements ApplicationListener<ContextStoppedEvent> {

    private static final Logger log = LoggerFactory.getLogger(ContextStoppedListener.class);

    private final File file;

    public ContextStoppedListener(File file) {
        Assert.notNull(file, "File must not be null");
        this.file = file;
    }

    @Override
    public void onApplicationEvent(@Nonnull ContextStoppedEvent event) {
        try (FileWriter writer = new FileWriter(this.file)) {
            writer.write("context stopped");
            log.debug("context stopped");
        } catch (IOException e) {
            log.warn("Failed to write to file {}", this.file, e);
        }
    }
}
