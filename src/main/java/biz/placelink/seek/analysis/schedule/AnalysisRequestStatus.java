package biz.placelink.seek.analysis.schedule;

import biz.placelink.seek.analysis.vo.AnalysisVO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private final Map<String, AnalysisVO> processingAnalysisMap;

    AnalysisRequestStatus() {
        this.processingAnalysisMap = new ConcurrentHashMap<>();
    }

    public void add(List<AnalysisVO> analysisList) {
        if (analysisList != null) {
            for (AnalysisVO analysis : analysisList) {
                String analysisId = analysis.getAnalysisId();
                if (!processingAnalysisMap.containsKey(analysisId)) {
                    processingAnalysisMap.put(analysisId, analysis);
                }
            }
        }
    }

    public AnalysisVO get() {
        AnalysisVO result = null;
        if (!processingAnalysisMap.isEmpty()) {
            for (Map.Entry<String, AnalysisVO> entry : processingAnalysisMap.entrySet()) {
                AnalysisVO item = entry.getValue();
                if (item != null && !item.isRequesting()) {
                    result = item;
                    break;
                }
            }
        }
        return result;
    }

    public void setRequestStatus(String analysisId, boolean isRequesting) {
        AnalysisVO analysis = processingAnalysisMap.get(analysisId);
        if (analysis != null) {
            analysis.setRequesting(isRequesting);
        }
    }

    public void remove(String analysisId) {
        processingAnalysisMap.remove(analysisId);
    }

}
