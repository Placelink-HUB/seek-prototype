package biz.placelink.seek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;

import jakarta.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unused")
public class ApplicationPreparedListener implements ApplicationListener<ApplicationPreparedEvent> {

    private static final Logger log = LoggerFactory.getLogger(ApplicationPreparedListener.class);

    private final File file;

    public ApplicationPreparedListener(File file) {
        Assert.notNull(file, "File must not be null");
        this.file = file;
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationPreparedEvent event) {
        try (FileWriter writer = new FileWriter(this.file)) {
            writer.write("application prepared");
            log.debug("application prepared");
        } catch (IOException e) {
            log.warn("Failed to write to file {}", this.file, e);
        }
    }
}
