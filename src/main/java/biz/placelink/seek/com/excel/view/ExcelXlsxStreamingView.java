package biz.placelink.seek.com.excel.view;

import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import biz.placelink.seek.com.excel.ExcelWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일                         수정자             수정내용

 *  ------------       --------    ---------------------------

 *  2025. 10. 13.       s2          최초생성
 * </pre>
 */
@Component("excelXlsxStreamingView")
public class ExcelXlsxStreamingView extends AbstractXlsxStreamingView {
    @Override
    protected void buildExcelDocument(@NonNull Map<String, Object> model, @NonNull Workbook workbook, @NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
        SXSSFWorkbook sxssfWorkbook = (SXSSFWorkbook) workbook;
        sxssfWorkbook.setCompressTempFiles(true);
        new ExcelWriter(sxssfWorkbook, model, response).create();
    }

    @Override
    @NonNull
    protected SXSSFWorkbook createWorkbook(@NonNull Map<String, Object> model, @NonNull HttpServletRequest request) {
        return new SXSSFWorkbook(100); // 메모리에 유지할 행 수 설정
    }
}
