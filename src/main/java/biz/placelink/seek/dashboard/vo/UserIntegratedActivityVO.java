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

import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.vo.DefaultVO;

public class UserIntegratedActivityVO extends DefaultVO {

    /** 사용자 ID */
    private String userId;

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

    private Duration doubleToDuration(Double seconds) {
        long longSeconds = seconds.longValue(); // 정수 초 부분: 1971
        long nano = (long) ((seconds - longSeconds) * 1_000_000_000); // 소수점 이하 나노초 변환: 740581000

        return Duration.ofSeconds(longSeconds, nano);
    }

    public String getAllFunctionalMaxDisconnectDurationStatus() {
        if (this.allFunctionalMaxDisconnectDuration == null) {
            return Constants.CD_STATUS_UNKNOWN;
        } else {
            Duration duration = this.doubleToDuration(this.allFunctionalMaxDisconnectDuration);
            if (Duration.ofMinutes(5).compareTo(duration) > 0) {
                return Constants.CD_STATUS_NORMAL;
            } else if (Duration.ofMinutes(10).compareTo(duration) > 0) {
                return Constants.CD_STATUS_INSPECT;
            } else {
                return Constants.CD_STATUS_WARNING;
            }
        }
    }

    public String getAllFunctionalMaxBusinessDisconnectDurationStatus() {
        if (this.allFunctionalMaxBusinessDisconnectDuration == null) {
            return Constants.CD_STATUS_UNKNOWN;
        } else {
            Duration duration = this.doubleToDuration(this.allFunctionalMaxBusinessDisconnectDuration);
            if (Duration.ofMinutes(5).compareTo(duration) > 0) {
                return Constants.CD_STATUS_NORMAL;
            } else if (Duration.ofMinutes(10).compareTo(duration) > 0) {
                return Constants.CD_STATUS_INSPECT;
            } else {
                return Constants.CD_STATUS_WARNING;
            }
        }
    }

    public String getAnyFunctionalMaxDisconnectDurationStatus() {
        if (this.anyFunctionalMaxDisconnectDuration == null) {
            return Constants.CD_STATUS_UNKNOWN;
        } else {
            Duration duration = this.doubleToDuration(this.anyFunctionalMaxDisconnectDuration);
            if (Duration.ofMinutes(5).compareTo(duration) > 0) {
                return Constants.CD_STATUS_NORMAL;
            } else if (Duration.ofMinutes(10).compareTo(duration) > 0) {
                return Constants.CD_STATUS_INSPECT;
            } else {
                return Constants.CD_STATUS_WARNING;
            }
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
