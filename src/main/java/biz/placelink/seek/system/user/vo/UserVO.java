package biz.placelink.seek.system.user.vo;

import biz.placelink.seek.com.security.CustomUserDetails;
import biz.placelink.seek.com.vo.DefaultVO;

public class UserVO extends DefaultVO {

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

    public UserVO() {
    }

    public UserVO(CustomUserDetails customUserDetails) {
        if (customUserDetails != null) {
            this.userUid = customUserDetails.getUserUid();
            this.userId = customUserDetails.getUserId();
            this.name = customUserDetails.getName();
            this.authGroupCd = customUserDetails.getAuthGroupCd();
        }
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthGroupCd() {
        return authGroupCd;
    }

    public void setAuthGroupCd(String authGroupCd) {
        this.authGroupCd = authGroupCd;
    }

}