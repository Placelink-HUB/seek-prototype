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
 *
 * =========================================================================
 *
 * 상업적 이용 또는 AGPL-3.0의 공개 의무를 면제받기
 * 위해서는, placelink로부터 별도의 상업용 라이선스(Commercial License)를 구매해야 합니다.
 * For commercial use or to obtain an exemption from the AGPL-3.0 license
 * requirements, please purchase a commercial license from placelink.
 * *** 문의처: help@placelink.shop (README.md 참조)
 */
package biz.placelink.seek;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;

import jakarta.annotation.Nonnull;

public class ApplicationEnvironmentPreparedListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Logger log = LoggerFactory.getLogger(ApplicationEnvironmentPreparedListener.class);

    private final File file;

    public ApplicationEnvironmentPreparedListener(File file) {
        Assert.notNull(file, "File must not be null");
        this.file = file;
    }

    @SuppressWarnings("null")
    @Override
    public void onApplicationEvent(@Nonnull ApplicationEnvironmentPreparedEvent event) {
        try (FileWriter writer = new FileWriter(this.file)) {
            writer.write("application environment prepared");
            log.debug("application environment prepared");
        } catch (IOException e) {
            log.warn("Failed to write to file {}", this.file, e);
        }
    }
}
