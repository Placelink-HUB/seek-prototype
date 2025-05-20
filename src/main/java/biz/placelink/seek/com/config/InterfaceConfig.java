package biz.placelink.seek.com.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import kr.s2.ext.file.FileManager;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 01. 06.      s2          최초생성
 * </pre>
 */
@Configuration
public class InterfaceConfig {

    @Value("${file.manager.impl.name}")
    private String fileManagerQualifier;

    private final ApplicationContext applicationContext;

    public InterfaceConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    @Primary
    public FileManager fileManager() {
        return (FileManager) applicationContext.getBean(fileManagerQualifier);
    }

}
