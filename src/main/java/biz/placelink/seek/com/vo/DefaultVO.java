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
 */
package biz.placelink.seek.com.vo;

import java.time.LocalDateTime;

public class DefaultVO implements SeekVO {

    /* 배제일시 */
    private LocalDateTime exclusionDt;

    /* 등록일시 */
    private LocalDateTime createDt;

    /* 등록자 */
    private String createUid;
    private String createName;

    /* 수정일시 */
    private LocalDateTime modifyDt;

    /* 수정자 */
    private String modifyUid;
    private String modifyName;

    /* 삭제일시 */
    private LocalDateTime deleteDt;

    /* 삭제자 */
    private String deleteUid;
    private String deleteName;

    /* 사용 여부 */
    private String useYn;

    public LocalDateTime getExclusionDt() {
        return exclusionDt;
    }

    public void setExclusionDt(LocalDateTime exclusionDt) {
        this.exclusionDt = exclusionDt;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
    }

    public String getCreateUid() {
        return createUid;
    }

    public void setCreateUid(String createUid) {
        this.createUid = createUid;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public LocalDateTime getModifyDt() {
        return modifyDt;
    }

    public void setModifyDt(LocalDateTime modifyDt) {
        this.modifyDt = modifyDt;
    }

    public String getModifyUid() {
        return modifyUid;
    }

    public void setModifyUid(String modifyUid) {
        this.modifyUid = modifyUid;
    }

    public String getModifyName() {
        return modifyName;
    }

    public void setModifyName(String modifyName) {
        this.modifyName = modifyName;
    }

    public boolean isDeleted() {
        return deleteDt != null;
    }

    public LocalDateTime getDeleteDt() {
        return deleteDt;
    }

    public void setDeleteDt(LocalDateTime deleteDt) {
        this.deleteDt = deleteDt;
    }

    public String getDeleteUid() {
        return deleteUid;
    }

    public void setDeleteUid(String deleteUid) {
        this.deleteUid = deleteUid;
    }

    public String getDeleteName() {
        return deleteName;
    }

    public void setDeleteName(String deleteName) {
        this.deleteName = deleteName;
    }

    public String getUseYn() {
        return useYn;
    }

    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }

}
