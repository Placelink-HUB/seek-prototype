package biz.placelink.seek.com.security;

import biz.placelink.seek.system.user.vo.UserVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    /* 사용자 식별자 */
    private String userUid;
    /* 사용자 ID */
    private String userId;
    /* 비밀번호 */
    private String password;
    /* 이름 */
    private String name;

    /* 권한 그룹 코드 */
    private String authGroupCd;

    /* 삭제 일시 */
    private LocalDateTime deleteDt;

    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;

    public CustomUserDetails(UserVO userVO, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked) {
        if (userVO != null) {
            this.userUid = userVO.getUserUid();
            this.userId = userVO.getUserId();
            this.password = userVO.getPassword();
            this.name = userVO.getName();
            this.authGroupCd = userVO.getAuthGroupCd();
            this.deleteDt = userVO.getDeleteDt();

            this.accountNonExpired = accountNonExpired;
            this.credentialsNonExpired = credentialsNonExpired;
            this.accountNonLocked = accountNonLocked;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (authGroupCd != null && !authGroupCd.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + authGroupCd));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isEnabled() {
        return deleteDt == null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }


    public String getUserUid() {
        return userUid;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getAuthGroupCd() {
        return authGroupCd;
    }

}
