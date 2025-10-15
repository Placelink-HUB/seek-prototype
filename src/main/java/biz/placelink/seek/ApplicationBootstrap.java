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

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

@EnableAsync
@EnableCaching
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"biz.placelink.seek"})
@MapperScan(basePackages = "biz.placelink.seek.**.service")
public class ApplicationBootstrap extends SpringBootServletInitializer {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ApplicationBootstrap.class);

    public static void main(String[] args) {
        File pidFile = null;
        File stateFile = null;
        String pidFilePath = System.getProperty("pid.file.path", "./var/run/pid");
        String stateFilePath = System.getProperty("state.file.path", "./var/run/state");
        if (StringUtils.hasText(pidFilePath))
            pidFile = new File(pidFilePath);
        if (StringUtils.hasText(stateFilePath)) {
            stateFile = new File(stateFilePath);
            stateFile.deleteOnExit();
        }
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ApplicationBootstrap.class);
        if (pidFile != null)
            builder.listeners(new ApplicationPidFileWriter(pidFile));
        if (stateFile != null)
            builder.listeners(new ApplicationStartingListener(stateFile), // SpringApplicationEvent 1
                    new ApplicationEnvironmentPreparedListener(stateFile), // SpringApplicationEvent 2
                    new ApplicationContextInitializedListener(stateFile), // SpringApplicationEvent 3
                    new ApplicationPreparedListener(stateFile), // SpringApplicationEvent 4
                    new ContextRefreshedListener(stateFile), // ApplicationContextEvent 5
                    new ApplicationStartedListener(stateFile), // SpringApplicationEvent 6
                    new ApplicationReadyListener(stateFile), // SpringApplicationEvent 7
                    new ContextClosedListener(stateFile), // ApplicationContextEvent 8
                    new ApplicationFailedListener(stateFile) // SpringApplicationEvent
            );
        // noinspection unused
        @SuppressWarnings("unused")
        ConfigurableApplicationContext applicationContext = builder.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.sources(ApplicationBootstrap.class);
        return builder;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
    }

}
