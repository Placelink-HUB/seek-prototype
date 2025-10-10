package biz.placelink.seek.analysis.vo;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import biz.placelink.seek.com.vo.DefaultVO;
import kr.s2.ext.util.S2DateUtil;
import kr.s2.ext.util.S2JsonUtil;

public class AgentVO extends DefaultVO {

    /** 클라이언트 IP */
    private String clientIp;
    /** 사용자 ID */
    private String userId;
    /** 호스트 */
    private String host;
    /** MAC 주소 */
    private String macAddr;
    /** 조직 코드 */
    private String orgCode;
    /** 이벤트 일시 */
    private String eventTime;
    private LocalDateTime eventDateTime;
    /** 에이전트 컴포넌트 */
    private String components;
    /** minispy.sys 활성화 여부 */
    private String minispySysYn;
    /** mspyUser.exe 활성화 여부 */
    private String mspyUserExeYn;
    /** WfpBlocker.exe 활성화 여부 */
    private String wfpBlockerExeYn;
    /** ClickDomainAgent.exe 활성화 여부 */
    private String clickDomainAgentExeYn;
    /** 상태 수준 공통코드 */
    private String conditionLevelCcd;

    public AgentVO(String clientIp, String userId, String host, String macAddr, String orgCode, String eventTime, String components) {
        this.setClientIp(Optional.ofNullable(clientIp).filter(ip -> !ip.trim().isEmpty()).orElse(host));
        this.setUserId(userId);
        this.setHost(host);
        this.setMacAddr(macAddr);
        this.setOrgCode(orgCode);
        this.setEventTime(eventTime);
        this.setComponents(components);
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
        this.setEventDateTime(Optional.ofNullable(eventTime).map(dt -> S2DateUtil.parseToLocalDateTime(dt.replaceAll("[^0-9]", ""), "yyyyMMddHHmmss", false)).orElse(null));
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getComponents() {
        return components;
    }

    public void setComponents(String components) {
        this.components = components;

        Map<String, Boolean> componentsMap = S2JsonUtil.parseJsonTo(components, new TypeReference<Map<String, Boolean>>() {
        });
        if (componentsMap != null) {
            this.setMinispySysYn(Optional.ofNullable(componentsMap.get("minispy.sys")).orElse(false) ? "Y" : "N");
            this.setMspyUserExeYn(Optional.ofNullable(componentsMap.get("mspyUser.exe")).orElse(false) ? "Y" : "N");
            this.setWfpBlockerExeYn(Optional.ofNullable(componentsMap.get("WfpBlocker.exe")).orElse(false) ? "Y" : "N");
            this.setClickDomainAgentExeYn(Optional.ofNullable(componentsMap.get("ClickDomainAgent.exe")).orElse(false) ? "Y" : "N");
        }
    }

    public String getMinispySysYn() {
        return minispySysYn;
    }

    public void setMinispySysYn(String minispySysYn) {
        this.minispySysYn = minispySysYn;
    }

    public String getMspyUserExeYn() {
        return mspyUserExeYn;
    }

    public void setMspyUserExeYn(String mspyUserExeYn) {
        this.mspyUserExeYn = mspyUserExeYn;
    }

    public String getWfpBlockerExeYn() {
        return wfpBlockerExeYn;
    }

    public void setWfpBlockerExeYn(String wfpBlockerExeYn) {
        this.wfpBlockerExeYn = wfpBlockerExeYn;
    }

    public String getClickDomainAgentExeYn() {
        return clickDomainAgentExeYn;
    }

    public void setClickDomainAgentExeYn(String clickDomainAgentExeYn) {
        this.clickDomainAgentExeYn = clickDomainAgentExeYn;
    }

    public String getConditionLevelCcd() {
        return conditionLevelCcd;
    }

    public void setConditionLevelCcd(String conditionLevelCcd) {
        this.conditionLevelCcd = conditionLevelCcd;
    }

    public String getConditionLevel() {
        String conditionLevel = "";
        switch (Optional.ofNullable(conditionLevelCcd).orElse("")) {
            case "ACTIVE":
                conditionLevel = "정상";
                break;
            case "CHECK":
                conditionLevel = "점검";
                break;
            case "ALERT":
                conditionLevel = "경고";
                break;
        }
        return conditionLevel;
    }

}
