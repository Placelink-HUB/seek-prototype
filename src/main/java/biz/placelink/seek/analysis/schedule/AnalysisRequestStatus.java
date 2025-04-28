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
                String analysisResultId = analysis.getAnalysisResultId();
                if (!processingAnalysisMap.containsKey(analysisResultId)) {
                    processingAnalysisMap.put(analysisResultId, analysis);
                }
            }
        }
    }

    public AnalysisDetailVO get() {
        AnalysisDetailVO result = null;
        if (!processingAnalysisMap.isEmpty()) {
            for (Map.Entry<String, AnalysisDetailVO> entry : processingAnalysisMap.entrySet()) {
                AnalysisDetailVO item = entry.getValue();
                if (item != null && !item.isRequesting()) {
                    result = item;
                    break;
                }
            }
        }
        return result;
    }

    public void setRequestStatus(String analysisResultId, boolean isRequesting) {
        AnalysisDetailVO analysis = processingAnalysisMap.get(analysisResultId);
        if (analysis != null) {
            analysis.setRequesting(isRequesting);
        }
    }

    public void remove(String analysisResultId) {
        processingAnalysisMap.remove(analysisResultId);
    }

}
