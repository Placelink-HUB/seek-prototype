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

 *  2025. 04. 29.      s2          최초생성
 * </pre>
 */
@ToString
@Alias("subscriptionVO")
public class SubscriptionVO extends Subscription {

    /** 사용자 관리번호 */
    private String userId;
    /** 만료일자 */
    private Date expirationDate;
    /** 등록일시 */
    private Date createDt;
    /** 수정일시 */
    private Date modifyDt;
    /** 중복 개수 */
    private Integer duplicateCount;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getCreateDt() {
        return createDt;
    }

    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }

    public Date getModifyDt() {
        return modifyDt;
    }

    public void setModifyDt(Date modifyDt) {
        this.modifyDt = modifyDt;
    }

    public Integer getDuplicateCount() {
        return duplicateCount;
    }

    public void setDuplicateCount(Integer duplicateCount) {
        this.duplicateCount = duplicateCount;
    }

}
