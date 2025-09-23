package biz.placelink.seek.system.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import biz.placelink.seek.system.user.vo.UserVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 01. 09.      s2          최초생성
 * </pre>
 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserVO user) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // DB에 저장
        // userMapper.insertUser(user);
    }

    /**
     * 사용자 인증
     *
     * @param userId
     * @param password
     * @return
     */
    public UserVO validateUserCredentials(String userId, String password) {
        UserVO user = userMapper.selectUser(userId);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            user.setPassword(null);
            return user; // 인증 성공
        }
        return null; // Placeholder
    }

}
