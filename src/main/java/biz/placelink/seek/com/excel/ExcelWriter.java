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
 *
 * =========================================================================
 *
 * 상업적 이용 또는 AGPL-3.0의 공개 의무를 면제받기
 * 위해서는, placelink로부터 별도의 상업용 라이선스(Commercial License)를 구매해야 합니다.
 * For commercial use or to obtain an exemption from the AGPL-3.0 license
 * requirements, please purchase a commercial license from placelink.
 * *** 문의처: help@placelink.shop (README.md 참조)
 */
package biz.placelink.seek.com.excel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.placelink.seek.com.excel.vo.ExcelCellVO;
import jakarta.servlet.http.HttpServletResponse;
import kr.s2.ext.util.S2FileUtil;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일                         수정자             수정내용

 *  ------------       --------    ---------------------------

 *  2025. 10. 13.       s2          최초생성
 * </pre>
 */
public class ExcelWriter {

    private static final Logger logger = LoggerFactory.getLogger(ExcelWriter.class);

    public static final String FILE_NAME = "fileName";
    public static final String HEAD = "head";
    public static final String BODY = "body";

    public static final String HEAD_VO = "headVO";
    public static final String BODY_VO = "bodyVO";

    public static final String XLS = "xls";
    public static final String XLSX = "xlsx";
    public static final String XLSX_STREAM = "xlsx-stream";

    private final int startCol = 1;
    private final int startRow = 1;

    private final Workbook workbook;
    private final Map<String, Object> model;
    private final HttpServletResponse response;

    public ExcelWriter(Workbook workbook, Map<String, Object> model, HttpServletResponse response) {
        this.workbook = workbook;
        this.model = model;
        this.response = response;
    }

    public void create() {
        setFileName(response, mapToFileName());

        Sheet sheet = workbook.createSheet(mapToFileName());

        createHead(sheet, mapToHeadList());

        if (model.get(BODY_VO) != null) {
            createBodyVO(sheet, mapToBodyVOList());
        } else {
            createBody(sheet, mapToBodyList());
        }

        // 컬럼자동정렬
        int size = mapToHeadList().size();
        for (int i = startCol; i < (size + startCol); i++) {
            if (workbook instanceof XSSFWorkbook || workbook instanceof HSSFWorkbook) {
                sheet.autoSizeColumn(i);
            }
            sheet.setColumnWidth(i, Math.min(255 * 256, sheet.getColumnWidth(i) + 1000));
        }

    }

    private String mapToFileName() {
        return (String) model.get(FILE_NAME);
    }

    @SuppressWarnings("unchecked")
    private List<String> mapToHeadList() {
        return (List<String>) model.get(HEAD);
    }

    @SuppressWarnings("unchecked")
    private List<List<String>> mapToBodyList() {
        return (List<List<String>>) model.get(BODY);
    }

    @SuppressWarnings("unchecked")
    private List<List<ExcelCellVO>> mapToBodyVOList() {
        return (List<List<ExcelCellVO>>) model.get(BODY_VO);
    }

    private void setFileName(HttpServletResponse response, String fileName) {

        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        try {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + getFileExtension(URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20")) + "\";charset=\"UTF-8\"");
        } catch (UnsupportedEncodingException e) {
            logger.debug(e.getMessage());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + getFileExtension(fileName) + "\"");
        }

    }

    private String getFileExtension(String fileName) {
        if (workbook instanceof XSSFWorkbook) {
            if (!"xlsx".equals(S2FileUtil.getExtension(fileName, true)))
                fileName += ".xlsx";
        }
        if (workbook instanceof SXSSFWorkbook) {
            if (!"xlsx".equals(S2FileUtil.getExtension(fileName, true)))
                fileName += ".xlsx";
        }
        if (workbook instanceof HSSFWorkbook) {
            if (!"xls".equals(S2FileUtil.getExtension(fileName, true)))
                fileName += ".xls";
        }

        return fileName;
    }

    private void createHead(Sheet sheet, List<String> headList) {

        // 스타일 객체 생성
        CellStyle styleHd = this.workbook.createCellStyle(); // 제목 스타일

        // 제목 폰트
        Font font = this.workbook.createFont();
        // font.setFontHeightInPoints((short)15);
        font.setBold(true);
        font.setFontName("Noto Sans KR"); // 폰트 이름 설정

        // 제목 스타일에 폰트 적용, 정렬
        styleHd.setFont(font);
        styleHd.setAlignment(HorizontalAlignment.CENTER);
        styleHd.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHd.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        styleHd.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 테두리 라인
        styleHd.setBorderRight(BorderStyle.THIN);
        styleHd.setBorderLeft(BorderStyle.THIN);
        styleHd.setBorderTop(BorderStyle.THIN);
        styleHd.setBorderBottom(BorderStyle.DOUBLE);

        int size = headList.size();
        Row row = sheet.createRow(startRow);
        row.setHeight((short) 1000);

        for (int i = 0; i < size; i++) {
            Cell cell = row.createCell(i + startCol);
            cell.setCellStyle(styleHd);
            cell.setCellValue(headList.get(i));
        }
    }

    private void createBody(Sheet sheet, List<List<String>> bodyList) {
        int rowSize = bodyList.size();
        for (int i = 0; i < rowSize; i++) {
            createRow(sheet, bodyList.get(i), i + (startRow + 1));
        }
    }

    private void createRow(Sheet sheet, List<String> cellList, int rowNum) {

        // 스타일 객체 생성
        CellStyle styleBody = this.workbook.createCellStyle(); // 내용 스타일

        // 제목 폰트
        Font font = this.workbook.createFont();
        // font.setFontHeightInPoints((short) 15);
        // font.setBold(true);
        // 제목 스타일에 폰트 적용, 정렬
        styleBody.setFont(font);
        styleBody.setAlignment(HorizontalAlignment.CENTER);
        styleBody.setVerticalAlignment(VerticalAlignment.CENTER);

        styleBody.setWrapText(true); // 줄 바꿈 설정 추가 (텍스트가 여러 줄에 걸쳐 표시되고 셀 높이가 자동으로 늘어나도록 설정)

        // 테두리 라인
        styleBody.setBorderRight(BorderStyle.THIN);
        styleBody.setBorderLeft(BorderStyle.THIN);
        // styleBody.setBorderTop(BorderStyle.THIN);
        styleBody.setBorderBottom(BorderStyle.THIN);

        int size = cellList.size();
        Row row = sheet.createRow(rowNum);
        row.setHeight((short) 510);

        for (int i = 0; i < size; i++) {
            Cell cell = row.createCell(i + startCol);
            cell.setCellStyle(styleBody);
            cell.setCellValue(cellList.get(i));
        }
    }

    private void createBodyVO(Sheet sheet, List<List<ExcelCellVO>> bodyList) {
        int rowSize = bodyList.size();
        for (int i = 0; i < rowSize; i++) {
            createRowVO(sheet, bodyList.get(i), i + (startRow + 1));
        }
    }

    private void createRowVO(Sheet sheet, List<ExcelCellVO> cellList, int rowNum) {

        // 스타일 객체 생성
        XSSFWorkbook wb = (XSSFWorkbook) this.workbook;
        // 제목 폰트
        Font font = this.workbook.createFont();

        int size = cellList.size();
        Row row = sheet.createRow(rowNum);
        row.setHeight((short) 510);

        for (int i = 0; i < size; i++) {

            XSSFCellStyle styleBody = wb.createCellStyle(); // 내용 스타일
            styleBody.setFont(font);
            styleBody.setAlignment(HorizontalAlignment.CENTER);
            styleBody.setVerticalAlignment(VerticalAlignment.CENTER);
            styleBody.setWrapText(true); // 줄 바꿈 설정 추가 (텍스트가 여러 줄에 걸쳐 표시되고 셀 높이가 자동으로 늘어나도록 설정)
            // 테두리 라인
            styleBody.setBorderRight(BorderStyle.THIN);
            styleBody.setBorderLeft(BorderStyle.THIN);
            // styleBody.setBorderTop(BorderStyle.THIN);
            styleBody.setBorderBottom(BorderStyle.THIN);

            ExcelCellVO excelCellVO = cellList.get(i);
            String bgColor = excelCellVO.getCellBgColor();
            if (!StringUtils.isBlank(bgColor)) {
                String[] colors = bgColor.split(",");
                if (colors.length == 3) {
                    int r = Integer.parseInt(colors[0]);
                    int g = Integer.parseInt(colors[1]);
                    int b = Integer.parseInt(colors[2]);

                    IndexedColorMap colorMap = wb.getStylesSource().getIndexedColors();
                    XSSFColor cellBgColor = new XSSFColor(new java.awt.Color(r, g, b), colorMap);
                    styleBody.setFillForegroundColor(cellBgColor);
                    styleBody.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 색 패턴 설정
                }
            }
            Cell cell = row.createCell(i + startCol);
            cell.setCellStyle(styleBody);
            cell.setCellValue(excelCellVO.getCellValue());
        }
    }
}
