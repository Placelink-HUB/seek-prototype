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
package biz.placelink.seek.analysis.vo;

import java.util.List;

import biz.placelink.seek.com.vo.SearchVO;

public class SchSensitiveInformationVO extends SearchVO {

    /* 민감 정보 ID */
    private String schSensitiveInformationId;
    /* 민감 정보 ID 목록 */
    private List<String> schSensitiveInformationIdList;

    public String getSchSensitiveInformationId() {
        return schSensitiveInformationId;
    }

    public void setSchSensitiveInformationId(String schSensitiveInformationId) {
        this.schSensitiveInformationId = schSensitiveInformationId;
    }

    public List<String> getSchSensitiveInformationIdList() {
        return schSensitiveInformationIdList;
    }

    public void setSchSensitiveInformationIdList(List<String> schSensitiveInformationIdList) {
        this.schSensitiveInformationIdList = schSensitiveInformationIdList;
    }

}
