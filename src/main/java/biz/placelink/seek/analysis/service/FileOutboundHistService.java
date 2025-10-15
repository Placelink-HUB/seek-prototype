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
package biz.placelink.seek.analysis.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import biz.placelink.seek.analysis.vo.FileOutboundHistVO;
import biz.placelink.seek.analysis.vo.SchFileOutboundHistVO;
import biz.placelink.seek.com.util.PaginationInfo;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 06. 24.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class FileOutboundHistService {

    private final FileOutboundHistMapper fileOutboundHistMapper;

    public FileOutboundHistService(FileOutboundHistMapper fileOutboundHistMapper) {
        this.fileOutboundHistMapper = fileOutboundHistMapper;
    }

    /**
     * 파일 외부전송 이력 정보를 등록한다.
     *
     * @param paramVO 파일 외부전송 정보
     * @return 등록 개수
     */
    public int insertFileOutboundHist(FileOutboundHistVO paramVO) {
        return fileOutboundHistMapper.insertFileOutboundHist(paramVO);
    }

    /**
     * 파일 외부전송 이력 목록 현황을 조회한다.
     *
     * @param searchVO 조회조건
     * @return 파일 외부전송 이력 목록 현황
     */
    public FileOutboundHistVO selectFileOutboundHistListStatus(SchFileOutboundHistVO searchVO) {
        return fileOutboundHistMapper.selectFileOutboundHistListStatus(searchVO);
    }

    /**
     * 파일 외부전송 이력 목록을 조회한다.
     *
     * @param searchVO 조회조건
     * @return 파일 외부전송 이력 목록
     */
    public PaginationInfo selectFileOutboundHistListWithPagination(SchFileOutboundHistVO searchVO) {
        List<FileOutboundHistVO> list = fileOutboundHistMapper.selectFileOutboundHistList(searchVO);
        int count = fileOutboundHistMapper.selectFileOutboundHistListCount(searchVO);
        return new PaginationInfo(searchVO, list, count);
    }

}
