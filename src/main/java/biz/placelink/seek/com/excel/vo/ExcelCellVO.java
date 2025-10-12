package biz.placelink.seek.com.excel.vo;

import groovy.transform.ToString;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일                         수정자             수정내용

 *  ------------       --------    ---------------------------

 *  2025. 10. 13.       s2          최초생성
 * </pre>
 */
@ToString
public class ExcelCellVO {

    private String cellValue;
    private String cellBgColor;

    public String getCellValue() {
        return cellValue;
    }

    public void setCellValue(String cellValue) {
        this.cellValue = cellValue;
    }

    public String getCellBgColor() {
        return cellBgColor;
    }

    public void setCellBgColor(String cellBgColor) {
        this.cellBgColor = cellBgColor;
    }

}
