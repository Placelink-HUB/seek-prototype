package biz.placelink.seek;


import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
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

import java.io.File;

@EnableAsync
@EnableCaching
@EnableScheduling
@SpringBootApplication(scanBasePackages = { "biz.placelink.seek" })
@MapperScan(basePackages = "biz.placelink.seek.**.service")
public class ApplicationBootstrap extends SpringBootServletInitializer {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ApplicationBootstrap.class);

    public static void main(String[] args) {
        File pidFile = null;
        File stateFile = null;
        String pidFilePath = System.getProperty("pid.file.path", "./var/run/pid");
        String stateFilePath = System.getProperty("state.file.path", "./var/run/state");
        if (StringUtils.hasText(pidFilePath)) pidFile = new File(pidFilePath);
        if (StringUtils.hasText(stateFilePath)) {
            stateFile = new File(stateFilePath);
            stateFile.deleteOnExit();
        }
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ApplicationBootstrap.class);
        if (pidFile != null) builder.listeners(
                new ApplicationPidFileWriter(pidFile)
        );
        if (stateFile != null) builder.listeners(
                new ApplicationStartingListener(stateFile), // SpringApplicationEvent 1
                new ApplicationEnvironmentPreparedListener(stateFile), // SpringApplicationEvent 2
                new ApplicationContextInitializedListener(stateFile), // SpringApplicationEvent 3
                new ApplicationPreparedListener(stateFile), // SpringApplicationEvent 4
                new ContextRefreshedListener(stateFile), // ApplicationContextEvent 5
                new ApplicationStartedListener(stateFile), // SpringApplicationEvent 6
                new ApplicationReadyListener(stateFile), // SpringApplicationEvent 7
                new ContextClosedListener(stateFile), // ApplicationContextEvent 8
                new ApplicationFailedListener(stateFile) // SpringApplicationEvent
        );
        //noinspection unused
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
