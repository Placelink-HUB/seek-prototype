package biz.placelink.seek.com.excel.view;

import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsView;

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
@Component("excelXlsxView")
public class ExcelXlsxView extends AbstractXlsView {
    @Override
    protected void buildExcelDocument(@NonNull Map<String, Object> model, @NonNull Workbook workbook, @NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
        XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
        new ExcelWriter(xssfWorkbook, model, response).create();
    }

    @Override
    @NonNull
    protected XSSFWorkbook createWorkbook(@NonNull Map<String, Object> model, @NonNull HttpServletRequest request) {
        return new XSSFWorkbook(); // 메모리에 유지할 행 수 설정
    }
}
