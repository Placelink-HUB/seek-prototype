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
package biz.placelink.seek.dashboard.vo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.vo.DefaultVO;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Supplier;

public class UserActivityVO extends DefaultVO {

    /** 사용자 ID */
    private String userId;

    /** Agent: 모든 기능 활성화 상태의 하트비트 이력 개수 */
    private Integer allFunctionalRealCount;
    /** Agent: 모든 기능 활성화 상태에서의 최초 하트비트 시각 */
    private LocalDateTime allFunctionalFirstEventDt;
    /** Agent: 모든 기능 활성화 상태에서의 최종 하트비트 시각 */
    private LocalDateTime allFunctionalLastEventDt;
    /** Agent: 모든 기능 활성화 상태에서 측정된 최대 하트비트 단절 기간 (총 시간) */
    private Double allFunctionalMaxDisconnectDuration;
    /** Agent: 모든 기능 활성화 상태에서 측정된 최대 하트비트 단절 기간 (업무 시간 기준) */
    private Double allFunctionalMaxBusinessDisconnectDuration;
    /** Agent: 모든 기능 활성화 상태에서 발생한 하트비트 단절 기간의 총합 (총 시간) */
    private Double allFunctionalTotalDisconnectDuration;
    /** Agent: 모든 기능 활성화 상태에서 발생한 하트비트 단절 기간의 총합 (업무 시간 기준) */
    private Double allFunctionalTotalBusinessDisconnectDuration;
    /** Agent: 모든 기능 활성화 상태에서 하트비트 단절이 특정 시간(5분)을 초과한 총 횟수 (총 시간) */
    private Long allFunctionalCountDisconnectDurationOver;
    /** Agent: 모든 기능 활성화 상태에서 하트비트 단절이 특정 시간(5분)을 초과한 총 횟수 (업무 시간 기준) */
    private Long allFunctionalCountBusinessDisconnectDurationOver;

    /** Agent: 하나 이상 기능 활성화 상태의 하트비트 이력 개수 */
    private Integer anyFunctionalRealCount;
    /** Agent: 하나 이상 기능 활성화 상태에서의 최초 하트비트 시각 */
    private LocalDateTime anyFunctionalFirstEventDt;
    /** Agent: 하나 이상 기능 활성화 상태에서의 최종 하트비트 시각 */
    private LocalDateTime anyFunctionalLastEventDt;
    /** Agent: 하나 이상 기능 활성화 상태에서 측정된 최대 하트비트 단절 기간 (총 시간) */
    private Double anyFunctionalMaxDisconnectDuration;
    /** Agent: 하나 이상 기능 활성화 상태에서 측정된 최대 하트비트 단절 기간 (업무 시간 기준) */
    private Double anyFunctionalMaxBusinessDisconnectDuration;
    /** Agent: 하나 이상 기능 활성화 상태에서 발생한 하트비트 단절 기간의 총합 (총 시간) */
    private Double anyFunctionalTotalDisconnectDuration;
    /** Agent: 하나 이상 기능 활성화 상태에서 발생한 하트비트 단절 기간의 총합 (업무 시간 기준) */
    private Double anyFunctionalTotalBusinessDisconnectDuration;
    /** Agent: 하나 이상 기능 활성화 상태에서 하트비트 단절이 특정 시간(5분)을 초과한 총 횟수 (총 시간) */
    private Long anyFunctionalCountDisconnectDurationOver;
    /** Agent: 하나 이상 기능 활성화 상태에서 하트비트 단절이 특정 시간(5분)을 초과한 총 횟수 (업무 시간 기준) */
    private Long anyFunctionalCountBusinessDisconnectDurationOver;

    /** Unmask: 언마스킹 총 횟수 */
    private Long unmaskCount;
    /** Unmask: 비 업무 시간 언마스킹 횟수 */
    private Long unmaskNonBusinessCount;
    /** Unmask: 언마스킹된 아이템 총 개수 */
    private Long unmaskItemCount;
    /** Unmask: 비 업무 시간 언마스킹된 아이템 개수 */
    private Long unmaskNonBusinessItemCount;

    /** FileOutbound: 파일 외부전송 총 횟수 */
    private Long fileOutboundTotalCount;
    /** FileOutbound: 비 업무 시간 파일 외부전송 총 횟수 */
    private Long fileOutboundNonBusinessTotalCount;
    /** FileOutbound: 파일 외부전송 성공 횟수 */
    private Long fileOutboundSentCount;
    /** FileOutbound: 비 업무 시간 파일 외부전송 성공 횟수 */
    private Long fileOutboundNonBusinessSentCount;
    /** FileOutbound: 파일 외부전송 차단 횟수 */
    private Long fileOutboundBlockedCount;
    /** FileOutbound: 비 업무 시간 파일 외부전송 차단 횟수 */
    private Long fileOutboundNonBusinessBlockedCount;
    /** FileOutbound: 파일 외부전송 총 파일 개수 */
    private Long fileOutboundTotalFileCount;
    /** FileOutbound: 파일 외부전송 성공 파일 개수 */
    private Long fileOutboundSentFileCount;
    /** FileOutbound: 비 업무 시간 파일 외부전송 성공 파일 개수 */
    private Long fileOutboundNonBusinessSentFileCount;
    /** FileOutbound: 파일 외부전송 차단 파일 개수 */
    private Long fileOutboundBlockedFileCount;
    /** FileOutbound: 비 업무 시간 파일 외부전송 차단 파일 개수 */
    private Long fileOutboundNonBusinessBlockedFileCount;
    /** FileOutbound: 파일 외부전송 총 파일 크기 */
    private Long fileOutboundTotalFileSize;
    /** FileOutbound: 파일 외부전송 성공 파일 크기 */
    private Long fileOutboundSentFileSize;
    /** FileOutbound: 비 업무 시간 파일 외부전송 성공 파일 크기 */
    private Long fileOutboundNonBusinessSentFileSize;
    /** FileOutbound: 파일 외부전송 차단 파일 크기 */
    private Long fileOutboundBlockedFileSize;
    /** FileOutbound: 비 업무 시간 파일 외부전송 차단 파일 크기 */
    private Long fileOutboundNonBusinessBlockedFileSize;
    /** FileOutbound: 가장 많은 중복 파일 개수 */
    private Long fileOutboundMaxDuplicateFileCount;

    /**
     * 측정된 시간(초 단위)과 분 단위 임계값을 기준으로 상태 코드(정상, 점검, 경고)를 결정한다.
     *
     * <p>
     * 상태 결정 로직은 다음과 같다:
     * </p>
     * <ul>
     * <li>{@code durationSeconds} < {@code normalThresholdMinutes}분: {@code CD_STATUS_NORMAL} (정상)</li>
     * <li>{@code normalThresholdMinutes}분 <= {@code durationSeconds} < {@code inspectThresholdMinutes}분: {@code CD_STATUS_INSPECT} (점검)</li>
     * <li>{@code durationSeconds} >= {@code inspectThresholdMinutes}분: {@code CD_STATUS_WARNING} (경고)</li>
     * <li>{@code durationSeconds}가 null일 경우: {@code CD_STATUS_UNKNOWN} (미확인/정보 없음)</li>
     * </ul>
     *
     * @param durationSeconds         측정된 시간 간격 (PostgreSQL INTERVAL에서 변환된 Double 형태의 총 초)
     * @param normalThresholdMinutes  정상으로 간주되는 최대 시간 임계값 (분 단위)
     * @param inspectThresholdMinutes 점검으로 간주되는 최대 시간 임계값 (분 단위)
     * @return 상태 코드 (Constants.CD_STATUS_NORMAL, CD_STATUS_INSPECT, CD_STATUS_WARNING, CD_STATUS_UNKNOWN 중 하나)
     */
    private String getDurationStatus(Double durationSeconds, int normalThresholdMinutes, int inspectThresholdMinutes) {
        if (durationSeconds == null) {
            return Constants.CD_STATUS_UNKNOWN;
        } else {
            long durationMillis = Math.round(durationSeconds * 1000);
            Duration duration = Duration.ofMillis(durationMillis);

            if (duration.compareTo(Duration.ofMinutes(inspectThresholdMinutes)) >= 0) {
                return Constants.CD_STATUS_WARNING;
            } else if (duration.compareTo(Duration.ofMinutes(normalThresholdMinutes)) >= 0) {
                return Constants.CD_STATUS_INSPECT;
            } else {
                return Constants.CD_STATUS_NORMAL;
            }
        }
    }

    /**
     * 에이전트 중지 패턴: 반복 중지
     *
     * @return 상태 코드
     */
    public String getAllFunctionalMaxDisconnectDurationStatus() {
        return this.getDurationStatus(Optional.ofNullable(this.allFunctionalRealCount).orElse(0) > 0 ? this.allFunctionalMaxDisconnectDuration : null, 5, 10);
    }

    /**
     * 에이전트 중지 패턴: 업무시간대 중지
     *
     * @return 상태 코드
     */
    public String getAllFunctionalMaxBusinessDisconnectDurationStatus() {
        return this.getDurationStatus(Optional.ofNullable(this.allFunctionalRealCount).orElse(0) > 0 ? this.allFunctionalMaxBusinessDisconnectDuration : null, 5, 10);
    }

    /**
     * 에이전트 중지 패턴: 일부 프로세스 중지
     *
     * @return 상태 코드
     */
    public String getAnyFunctionalMaxDisconnectDurationStatus() {
        return this.getDurationStatus(Optional.ofNullable(this.anyFunctionalRealCount).orElse(0) > 0 ? this.anyFunctionalMaxDisconnectDuration : null, 5, 10);
    }

    /**
     * Count 와 분 단위 임계값을 기준으로 상태 코드(정상, 점검, 경고)를 결정한다.
     *
     * <p>
     * 상태 결정 로직은 다음과 같다:
     * </p>
     * <ul>
     * <li>{@code count} < {@code normalThreshold}: {@code CD_STATUS_NORMAL} (정상)</li>
     * <li>{@code normalThreshold} <= {@code count} < {@code inspectThreshold}: {@code CD_STATUS_INSPECT} (점검)</li>
     * <li>{@code count} >= {@code inspectThreshold}: {@code CD_STATUS_WARNING} (경고)</li>
     * <li>{@code count}가 null일 경우: {@code CD_STATUS_UNKNOWN} (미확인/정보 없음)</li>
     * </ul>
     *
     * @param count            대상 개수
     * @param normalThreshold  정상으로 간주되는 최대 시간 임계값
     * @param inspectThreshold 점검으로 간주되는 최대 시간 임계값
     * @return 상태 코드 (Constants.CD_STATUS_NORMAL, CD_STATUS_INSPECT, CD_STATUS_WARNING, CD_STATUS_UNKNOWN 중 하나)
     */
    private String getCountStatus(Long count, int normalThreshold, int inspectThreshold) {
        if (count == null) {
            return Constants.CD_STATUS_UNKNOWN;
        } else if (count < normalThreshold) {
            return Constants.CD_STATUS_NORMAL;
        } else if (count < inspectThreshold) {
            return Constants.CD_STATUS_INSPECT;
        } else {
            return Constants.CD_STATUS_WARNING;
        }
    }

    /**
     * 복호화 시도 패턴: 비 업무시간대 복호화
     *
     * @return 상태 코드
     */
    public String getUnmaskNonBusinessCountStatus() {
        return this.getCountStatus(Optional.ofNullable(this.unmaskNonBusinessCount).orElse(0L), 5, 10);
    }

    /**
     * 복호화 시도 패턴: 대량 복호화
     *
     * @return 상태 코드
     */
    public String getUnmaskItemCountStatus() {
        return this.getCountStatus(Optional.ofNullable(this.unmaskItemCount).orElse(0L), 5, 10);
    }

    /**
     * 외부 유출 시도 패턴: 비 업무시간대 전송
     *
     * @return 상태 코드
     */
    public String getFileOutboundNonBusinessTotalCountStatus() {
        return this.getCountStatus(Optional.ofNullable(this.fileOutboundNonBusinessTotalCount).orElse(0L), 5, 10);
    }

    /**
     * 외부 유출 시도 패턴: 중복 전송
     *
     * @return 상태 코드
     */
    public String getFileOutboundMaxDuplicateFileCountStatus() {
        return this.getCountStatus(Optional.ofNullable(this.fileOutboundMaxDuplicateFileCount).orElse(0L), 5, 10);
    }

    /**
     * 외부 유출 시도 패턴: 대량 전송
     *
     * @return 상태 코드
     */
    public String getFileOutboundTotalCountStatus() {
        return this.getCountStatus(Optional.ofNullable(this.fileOutboundTotalCount).orElse(0L), 5, 10);
    }

    /**
     * 상태를 확인이 필요한 메소드 목록을 반환
     *
     * @return 상태를 확인이 필요한 메소드 목록
     */
    private List<Supplier<String>> statusSupplierList() {
        return Arrays.asList(
                this::getAllFunctionalMaxDisconnectDurationStatus,
                this::getAllFunctionalMaxBusinessDisconnectDurationStatus,
                this::getAnyFunctionalMaxDisconnectDurationStatus,
                this::getUnmaskNonBusinessCountStatus,
                this::getUnmaskItemCountStatus,
                this::getFileOutboundNonBusinessTotalCountStatus,
                this::getFileOutboundMaxDuplicateFileCountStatus,
                this::getFileOutboundTotalCountStatus);
    }

    /**
     * 비교를 위해 상태 코드를 숫자형으로 변경한다.
     *
     * @param status 문자형 상태 코드
     * @return 숫자형 상태 코드
     */
    private int getNumericStatus(String status) {
        return switch (status) {
            case Constants.CD_STATUS_WARNING -> 3;
            case Constants.CD_STATUS_INSPECT -> 2;
            case Constants.CD_STATUS_NORMAL -> 1;
            default -> 0;
        };
    }

    /**
     * 상위 위험 상태 코드를 가져온다.
     *
     * @param statusA 상태 코드 A
     * @param statusB 상태 코드 B
     * @return 상위 위험 상태 코드
     */
    private String getHigherRiskStatus(String statusA, String statusB) {
        return this.getNumericStatus(statusA) < this.getNumericStatus(statusB) ? statusB : statusA;
    }

    /**
     * 최고 위험 상태 코드를 가져온다.
     *
     * @return 최고 위험 상태 코드
     */
    public String getHighestStatus() {
        String higherRiskStatus = "";
        for (Supplier<String> statusSupplier : this.statusSupplierList()) {
            higherRiskStatus = this.getHigherRiskStatus(higherRiskStatus, statusSupplier.get());
        }
        return higherRiskStatus;
    }

    /**
     * 해당 상태의 개수를 가져온다
     *
     * @param status 상태 코드
     * @return 해당 상태 개수
     */
    public int getStatusCount(String status) {
        int count = 0;
        if (status != null) {
            for (Supplier<String> statusSupplier : this.statusSupplierList()) {
                count += status.equals(statusSupplier.get()) ? 1 : 0;
            }
        }
        return count;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getAllFunctionalRealCount() {
        return allFunctionalRealCount;
    }

    public void setAllFunctionalRealCount(Integer allFunctionalRealCount) {
        this.allFunctionalRealCount = allFunctionalRealCount;
    }

    public LocalDateTime getAllFunctionalFirstEventDt() {
        return allFunctionalFirstEventDt;
    }

    public void setAllFunctionalFirstEventDt(LocalDateTime allFunctionalFirstEventDt) {
        this.allFunctionalFirstEventDt = allFunctionalFirstEventDt;
    }

    public LocalDateTime getAllFunctionalLastEventDt() {
        return allFunctionalLastEventDt;
    }

    public void setAllFunctionalLastEventDt(LocalDateTime allFunctionalLastEventDt) {
        this.allFunctionalLastEventDt = allFunctionalLastEventDt;
    }

    public Double getAllFunctionalMaxDisconnectDuration() {
        return allFunctionalMaxDisconnectDuration;
    }

    public void setAllFunctionalMaxDisconnectDuration(Double allFunctionalMaxDisconnectDuration) {
        this.allFunctionalMaxDisconnectDuration = allFunctionalMaxDisconnectDuration;
    }

    public Double getAllFunctionalMaxBusinessDisconnectDuration() {
        return allFunctionalMaxBusinessDisconnectDuration;
    }

    public void setAllFunctionalMaxBusinessDisconnectDuration(Double allFunctionalMaxBusinessDisconnectDuration) {
        this.allFunctionalMaxBusinessDisconnectDuration = allFunctionalMaxBusinessDisconnectDuration;
    }

    public Double getAllFunctionalTotalDisconnectDuration() {
        return allFunctionalTotalDisconnectDuration;
    }

    public void setAllFunctionalTotalDisconnectDuration(Double allFunctionalTotalDisconnectDuration) {
        this.allFunctionalTotalDisconnectDuration = allFunctionalTotalDisconnectDuration;
    }

    public Double getAllFunctionalTotalBusinessDisconnectDuration() {
        return allFunctionalTotalBusinessDisconnectDuration;
    }

    public void setAllFunctionalTotalBusinessDisconnectDuration(Double allFunctionalTotalBusinessDisconnectDuration) {
        this.allFunctionalTotalBusinessDisconnectDuration = allFunctionalTotalBusinessDisconnectDuration;
    }

    public Long getAllFunctionalCountDisconnectDurationOver() {
        return allFunctionalCountDisconnectDurationOver;
    }

    public void setAllFunctionalCountDisconnectDurationOver(Long allFunctionalCountDisconnectDurationOver) {
        this.allFunctionalCountDisconnectDurationOver = allFunctionalCountDisconnectDurationOver;
    }

    public Long getAllFunctionalCountBusinessDisconnectDurationOver() {
        return allFunctionalCountBusinessDisconnectDurationOver;
    }

    public void setAllFunctionalCountBusinessDisconnectDurationOver(Long allFunctionalCountBusinessDisconnectDurationOver) {
        this.allFunctionalCountBusinessDisconnectDurationOver = allFunctionalCountBusinessDisconnectDurationOver;
    }

    public Integer getAnyFunctionalRealCount() {
        return anyFunctionalRealCount;
    }

    public void setAnyFunctionalRealCount(Integer anyFunctionalRealCount) {
        this.anyFunctionalRealCount = anyFunctionalRealCount;
    }

    public LocalDateTime getAnyFunctionalFirstEventDt() {
        return anyFunctionalFirstEventDt;
    }

    public void setAnyFunctionalFirstEventDt(LocalDateTime anyFunctionalFirstEventDt) {
        this.anyFunctionalFirstEventDt = anyFunctionalFirstEventDt;
    }

    public LocalDateTime getAnyFunctionalLastEventDt() {
        return anyFunctionalLastEventDt;
    }

    public void setAnyFunctionalLastEventDt(LocalDateTime anyFunctionalLastEventDt) {
        this.anyFunctionalLastEventDt = anyFunctionalLastEventDt;
    }

    public Double getAnyFunctionalMaxDisconnectDuration() {
        return anyFunctionalMaxDisconnectDuration;
    }

    public void setAnyFunctionalMaxDisconnectDuration(Double anyFunctionalMaxDisconnectDuration) {
        this.anyFunctionalMaxDisconnectDuration = anyFunctionalMaxDisconnectDuration;
    }

    public Double getAnyFunctionalMaxBusinessDisconnectDuration() {
        return anyFunctionalMaxBusinessDisconnectDuration;
    }

    public void setAnyFunctionalMaxBusinessDisconnectDuration(Double anyFunctionalMaxBusinessDisconnectDuration) {
        this.anyFunctionalMaxBusinessDisconnectDuration = anyFunctionalMaxBusinessDisconnectDuration;
    }

    public Double getAnyFunctionalTotalDisconnectDuration() {
        return anyFunctionalTotalDisconnectDuration;
    }

    public void setAnyFunctionalTotalDisconnectDuration(Double anyFunctionalTotalDisconnectDuration) {
        this.anyFunctionalTotalDisconnectDuration = anyFunctionalTotalDisconnectDuration;
    }

    public Double getAnyFunctionalTotalBusinessDisconnectDuration() {
        return anyFunctionalTotalBusinessDisconnectDuration;
    }

    public void setAnyFunctionalTotalBusinessDisconnectDuration(Double anyFunctionalTotalBusinessDisconnectDuration) {
        this.anyFunctionalTotalBusinessDisconnectDuration = anyFunctionalTotalBusinessDisconnectDuration;
    }

    public Long getAnyFunctionalCountDisconnectDurationOver() {
        return anyFunctionalCountDisconnectDurationOver;
    }

    public void setAnyFunctionalCountDisconnectDurationOver(Long anyFunctionalCountDisconnectDurationOver) {
        this.anyFunctionalCountDisconnectDurationOver = anyFunctionalCountDisconnectDurationOver;
    }

    public Long getAnyFunctionalCountBusinessDisconnectDurationOver() {
        return anyFunctionalCountBusinessDisconnectDurationOver;
    }

    public void setAnyFunctionalCountBusinessDisconnectDurationOver(Long anyFunctionalCountBusinessDisconnectDurationOver) {
        this.anyFunctionalCountBusinessDisconnectDurationOver = anyFunctionalCountBusinessDisconnectDurationOver;
    }

    public Long getUnmaskCount() {
        return unmaskCount;
    }

    public void setUnmaskCount(Long unmaskCount) {
        this.unmaskCount = unmaskCount;
    }

    public Long getUnmaskNonBusinessCount() {
        return unmaskNonBusinessCount;
    }

    public void setUnmaskNonBusinessCount(Long unmaskNonBusinessCount) {
        this.unmaskNonBusinessCount = unmaskNonBusinessCount;
    }

    public Long getUnmaskItemCount() {
        return unmaskItemCount;
    }

    public void setUnmaskItemCount(Long unmaskItemCount) {
        this.unmaskItemCount = unmaskItemCount;
    }

    public Long getUnmaskNonBusinessItemCount() {
        return unmaskNonBusinessItemCount;
    }

    public void setUnmaskNonBusinessItemCount(Long unmaskNonBusinessItemCount) {
        this.unmaskNonBusinessItemCount = unmaskNonBusinessItemCount;
    }

    public Long getFileOutboundTotalCount() {
        return fileOutboundTotalCount;
    }

    public void setFileOutboundTotalCount(Long fileOutboundTotalCount) {
        this.fileOutboundTotalCount = fileOutboundTotalCount;
    }

    public Long getFileOutboundNonBusinessTotalCount() {
        return fileOutboundNonBusinessTotalCount;
    }

    public void setFileOutboundNonBusinessTotalCount(Long fileOutboundNonBusinessTotalCount) {
        this.fileOutboundNonBusinessTotalCount = fileOutboundNonBusinessTotalCount;
    }

    public Long getFileOutboundSentCount() {
        return fileOutboundSentCount;
    }

    public void setFileOutboundSentCount(Long fileOutboundSentCount) {
        this.fileOutboundSentCount = fileOutboundSentCount;
    }

    public Long getFileOutboundNonBusinessSentCount() {
        return fileOutboundNonBusinessSentCount;
    }

    public void setFileOutboundNonBusinessSentCount(Long fileOutboundNonBusinessSentCount) {
        this.fileOutboundNonBusinessSentCount = fileOutboundNonBusinessSentCount;
    }

    public Long getFileOutboundBlockedCount() {
        return fileOutboundBlockedCount;
    }

    public void setFileOutboundBlockedCount(Long fileOutboundBlockedCount) {
        this.fileOutboundBlockedCount = fileOutboundBlockedCount;
    }

    public Long getFileOutboundNonBusinessBlockedCount() {
        return fileOutboundNonBusinessBlockedCount;
    }

    public void setFileOutboundNonBusinessBlockedCount(Long fileOutboundNonBusinessBlockedCount) {
        this.fileOutboundNonBusinessBlockedCount = fileOutboundNonBusinessBlockedCount;
    }

    public Long getFileOutboundTotalFileCount() {
        return fileOutboundTotalFileCount;
    }

    public void setFileOutboundTotalFileCount(Long fileOutboundTotalFileCount) {
        this.fileOutboundTotalFileCount = fileOutboundTotalFileCount;
    }

    public Long getFileOutboundSentFileCount() {
        return fileOutboundSentFileCount;
    }

    public void setFileOutboundSentFileCount(Long fileOutboundSentFileCount) {
        this.fileOutboundSentFileCount = fileOutboundSentFileCount;
    }

    public Long getFileOutboundNonBusinessSentFileCount() {
        return fileOutboundNonBusinessSentFileCount;
    }

    public void setFileOutboundNonBusinessSentFileCount(Long fileOutboundNonBusinessSentFileCount) {
        this.fileOutboundNonBusinessSentFileCount = fileOutboundNonBusinessSentFileCount;
    }

    public Long getFileOutboundBlockedFileCount() {
        return fileOutboundBlockedFileCount;
    }

    public void setFileOutboundBlockedFileCount(Long fileOutboundBlockedFileCount) {
        this.fileOutboundBlockedFileCount = fileOutboundBlockedFileCount;
    }

    public Long getFileOutboundNonBusinessBlockedFileCount() {
        return fileOutboundNonBusinessBlockedFileCount;
    }

    public void setFileOutboundNonBusinessBlockedFileCount(Long fileOutboundNonBusinessBlockedFileCount) {
        this.fileOutboundNonBusinessBlockedFileCount = fileOutboundNonBusinessBlockedFileCount;
    }

    public Long getFileOutboundTotalFileSize() {
        return fileOutboundTotalFileSize;
    }

    public void setFileOutboundTotalFileSize(Long fileOutboundTotalFileSize) {
        this.fileOutboundTotalFileSize = fileOutboundTotalFileSize;
    }

    public Long getFileOutboundSentFileSize() {
        return fileOutboundSentFileSize;
    }

    public void setFileOutboundSentFileSize(Long fileOutboundSentFileSize) {
        this.fileOutboundSentFileSize = fileOutboundSentFileSize;
    }

    public Long getFileOutboundNonBusinessSentFileSize() {
        return fileOutboundNonBusinessSentFileSize;
    }

    public void setFileOutboundNonBusinessSentFileSize(Long fileOutboundNonBusinessSentFileSize) {
        this.fileOutboundNonBusinessSentFileSize = fileOutboundNonBusinessSentFileSize;
    }

    public Long getFileOutboundBlockedFileSize() {
        return fileOutboundBlockedFileSize;
    }

    public void setFileOutboundBlockedFileSize(Long fileOutboundBlockedFileSize) {
        this.fileOutboundBlockedFileSize = fileOutboundBlockedFileSize;
    }

    public Long getFileOutboundNonBusinessBlockedFileSize() {
        return fileOutboundNonBusinessBlockedFileSize;
    }

    public void setFileOutboundNonBusinessBlockedFileSize(Long fileOutboundNonBusinessBlockedFileSize) {
        this.fileOutboundNonBusinessBlockedFileSize = fileOutboundNonBusinessBlockedFileSize;
    }

    public Long getFileOutboundMaxDuplicateFileCount() {
        return fileOutboundMaxDuplicateFileCount;
    }

    public void setFileOutboundMaxDuplicateFileCount(Long fileOutboundMaxDuplicateFileCount) {
        this.fileOutboundMaxDuplicateFileCount = fileOutboundMaxDuplicateFileCount;
    }

}
