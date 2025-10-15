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
package biz.placelink.seek.dashboard.vo;

import biz.placelink.seek.com.vo.DefaultVO;

public class SchAnalysisStatisticsVO extends DefaultVO {

    /* 조회 일자 */
    private String searchStartDe;

    public String getSearchStartDe() {
        return searchStartDe;
    }

    public void setSearchStartDe(String searchStartDe) {
        this.searchStartDe = searchStartDe;
    }

}
