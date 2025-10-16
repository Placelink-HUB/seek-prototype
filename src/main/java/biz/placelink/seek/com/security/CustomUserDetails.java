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
package biz.placelink.seek.com.security;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import biz.placelink.seek.system.user.vo.UserVO;

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
