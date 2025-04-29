package biz.placelink.seek.com.serviceworker.vo;

import java.util.Date;

import org.apache.ibatis.type.Alias;

import groovy.transform.ToString;
import nl.martijndwars.webpush.Subscription;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일                         수정자             수정내용

 *  ------------       --------    ---------------------------

 *  2025. 11. 10.       CJS       최초생성
 * </pre>
 */
@ToString
@Alias("subscriptionVO")
public class SubscriptionVO extends Subscription {

    /** 사용자 관리번호 */
    private Long userMno;
    /** 만료일자 */
    private Date expirationDate;
    /** 등록일시 */
    private Date createDt;
    /** 수정일시 */
    private Date modifyDt;
    /** 중복 개수 */
    private Integer duplicateCount;

}
