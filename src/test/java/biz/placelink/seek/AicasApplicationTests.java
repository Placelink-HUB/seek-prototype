package biz.placelink.seek;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class AicasApplicationTests {

    @Test
    void contextLoads() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "pl@123";  // 원하는 비밀번호
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("Encoded Password: " + encodedPassword);

        // 검증 테스트
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("Password Matches: " + matches);
    }

}
