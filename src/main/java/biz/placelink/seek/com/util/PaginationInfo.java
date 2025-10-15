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
package biz.placelink.seek.com.util;

import java.util.List;

import kr.s2.ext.pagination.S2PaginationInfo;
import kr.s2.ext.pagination.vo.S2SearchVO;

public class PaginationInfo extends S2PaginationInfo {

    public PaginationInfo(S2SearchVO searchVO, List<?> dataList, int totalRecordCount) {
        super(searchVO, dataList, totalRecordCount);
    }

}
