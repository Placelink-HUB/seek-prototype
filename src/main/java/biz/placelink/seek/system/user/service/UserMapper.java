package biz.placelink.seek.system.user.service;

import biz.placelink.seek.system.user.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 01. 20.      s2          최초생성
 * </pre>
 */
@Mapper
public interface UserMapper {

    /**
     * 사용자 정보를 조회한다.
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    UserVO selectUser(String userId);

}