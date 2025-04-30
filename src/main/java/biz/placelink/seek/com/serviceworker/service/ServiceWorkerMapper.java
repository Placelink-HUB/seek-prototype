package biz.placelink.seek.com.serviceworker.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import biz.placelink.seek.com.serviceworker.vo.SubscriptionVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 29.      s2          최초생성
 * </pre>
 */
@Mapper
public interface ServiceWorkerMapper {

    /**
     * 서비스워커 구독 정보를 등록한다.
     *
     * @param paramVO 등록할 구독 정보
     * @return 등록 결과
     */
    int mergeSubscription(SubscriptionVO paramVO);

    /**
     * 서비스워커 구독 목록을 조회한다.
     *
     * @param userIdArr 조회할 사용자 관리번호 배열
     * @return 서비스워커 구독 목록
     */
    List<SubscriptionVO> selectSubscriptionList(@Param("userIdArr") Long... userIdArr);

    /**
     * 서비스워커 구독 정보를 삭제한다.
     *
     * @param userIdArr 삭제할 사용자 관리번호 배열
     * @return 삭제 결과
     */
    int deleteSubscription(@Param("userIdArr") Long... userIdArr);

}
