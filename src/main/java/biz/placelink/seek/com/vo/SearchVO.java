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

import kr.s2.ext.pagination.vo.S2SearchVO;

public class SearchVO extends S2SearchVO implements SeekVO {

    /* 검색 분류 기준 */
    private String searchGroupingType;

    public String getSearchGroupingType() {
        return searchGroupingType;
    }

    public void setSearchGroupingType(String searchGroupingType) {
        this.searchGroupingType = searchGroupingType;
    }
}
