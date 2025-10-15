/*
 * SEEK
 * Copyright (C) 2025 placelink
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package biz.placelink.seek;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;

import jakarta.annotation.Nonnull;

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
