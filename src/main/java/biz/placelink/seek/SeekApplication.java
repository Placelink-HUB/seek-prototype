package biz.placelink.seek;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"biz.placelink.seek"})
@MapperScan(basePackages = "biz.placelink.seek.**.service")

public class SeekApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeekApplication.class, args);
    }

}
