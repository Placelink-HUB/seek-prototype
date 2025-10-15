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
