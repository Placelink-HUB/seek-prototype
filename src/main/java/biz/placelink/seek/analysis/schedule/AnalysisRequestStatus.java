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
package biz.placelink.seek.analysis.schedule;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import biz.placelink.seek.analysis.vo.AnalysisDetailVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 14.      s2          최초생성
 * </pre>
 */
@Component
public class AnalysisRequestStatus {

    private final Map<String, AnalysisDetailVO> processingAnalysisMap;

    AnalysisRequestStatus() {
        this.processingAnalysisMap = new ConcurrentHashMap<>();
    }

    public void add(List<AnalysisDetailVO> analysisList) {
        if (analysisList != null) {
            for (AnalysisDetailVO analysis : analysisList) {
                String analysisId = analysis.getAnalysisId();
                if (!processingAnalysisMap.containsKey(analysisId)) {
                    processingAnalysisMap.put(analysisId, analysis);
                }
            }
        }
    }

    public AnalysisDetailVO get() {
        AnalysisDetailVO result = null;
        if (!processingAnalysisMap.isEmpty()) {
            for (Map.Entry<String, AnalysisDetailVO> entry : processingAnalysisMap.entrySet()) {
                AnalysisDetailVO item = entry.getValue();
                if (item != null && !item.isInUse()) {
                    item.setInUse(true);
                    result = item;
                    break;
                }
            }
        }
        return result;
    }

    public void setInUse(String analysisId, boolean inUse) {
        AnalysisDetailVO analysis = processingAnalysisMap.get(analysisId);
        if (analysis != null) {
            analysis.setInUse(inUse);
        }
    }

    public void remove(String analysisId) {
        processingAnalysisMap.remove(analysisId);
    }

}
