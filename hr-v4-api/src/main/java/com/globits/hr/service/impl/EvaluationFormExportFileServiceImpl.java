package com.globits.hr.service.impl;

import com.globits.hr.HrConstants;
import com.globits.hr.domain.EvaluationForm;
import com.globits.hr.domain.StaffLabourAgreement;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.EvaluationFormDto;
import com.globits.hr.dto.StaffEvaluationDto;
import com.globits.hr.dto.search.EvaluationFormSearchDto;
import com.globits.hr.service.EvaluationFormFileExportService;
import com.globits.hr.service.EvaluationFormService;
import com.globits.hr.utils.Const;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EvaluationFormExportFileServiceImpl implements EvaluationFormFileExportService {

    @Resource
    private ResourceLoader resourceLoader;

    @Resource
    private EvaluationFormService evaluationFormService;

    @Override
    public XWPFDocument exportWord(UUID formId) throws IOException {
        org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:template_evaluation_form.docx");
        InputStream inputStream = resource.getInputStream();
        XWPFDocument doc = new XWPFDocument(inputStream);
        ApiResponse<EvaluationFormDto> apiForm = evaluationFormService.getById(formId);
        if (apiForm == null || apiForm.getData() == null) {
            return new XWPFDocument();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        EvaluationFormDto form = apiForm.getData();

        Map<String, String> replacements = new HashMap<>();
        replacements.put("$1", getSafe(form.getStaffName()));
        replacements.put("$2", getSafe(form.getStaffCode()));
        replacements.put("$3", getSafe(form.getPosition()));
        replacements.put("$4", getSafe(form.getDepartment()));
        replacements.put("$5", getSafe(form.getDivision()));
        replacements.put("$6", getSafe(form.getTeam()));
        replacements.put("$7", getSafe(form.getDirectManagerName()));
        replacements.put("$8", formatDate(form.getHireDate(), sdf));
        replacements.put("$9", formatDate(form.getPreviousContractDuration(), sdf));

        if (form.getContractTypeId() != null) {
            String typeCode = form.getContractTypeCode();
            replacements.put("$10", "XĐTH".equals(typeCode) ? "☑" : "☐");
            replacements.put("$11", "KXĐTH".equals(typeCode) ? "☑" : "☐");
        } else {
            replacements.put("$10", "☐");
            replacements.put("$11", "☐");
        }

        replacements.put("$12", getSafe(form.getAdvantage()));
        replacements.put("$13", getSafe(form.getDisadvantage()));
        replacements.put("$14", getSafe(form.getCompanyPolicyCompliance()));
        replacements.put("$15", getSafe(form.getCoworkerRelationship()));
        replacements.put("$16", getSafe(form.getSenseOfResponsibility()));
        for (int i = 17; i <= 30; i++) {
            replacements.put("$" + i, i == 17 || i == 25 || i == 26 || i == 28 ? "☐" : "");
        }
        if (form.getContractRecommendation() != null) {
            if (form.getContractRecommendation()) {
                replacements.put("$17", "☑");
                replacements.put("$18", formatDate(form.getContractRecommendationDateFrom(), sdf));
                replacements.put("$19", formatDate(form.getContractRecommendationDateTo(), sdf));
                replacements.put("$20", getSafe(form.getPositionTitleName()));
                replacements.put("$21", getSafe(form.getRankTitleName()));
                replacements.put("$22", form.getBaseSalary() != null ? String.valueOf(form.getBaseSalary()) : "");
                replacements.put("$23", form.getAllowanceAmount() != null ? String.valueOf(form.getAllowanceAmount()) : "");
                replacements.put("$24", formatDate(form.getEffectiveFromDate(), sdf));
            } else {
                replacements.put("$25", "☑");
                if (form.getCooperationStatus() != null) {
                    if (form.getCooperationStatus()) {
                        replacements.put("$26", "☑");
                        replacements.put("$27", formatDate(form.getCollaborationEndDate(), sdf));
                    } else {
                        replacements.put("$28", "☑");
                        replacements.put("$29", getSafe(form.getNewPositionName()));
                        replacements.put("$30", formatDate(form.getNewPositionTransferDate(), sdf));
                    }
                }
            }
        }

        // Replace placeholders in table 0 (staff info)
        XWPFTable tableInfoStaff = doc.getTableArray(0);
        for (XWPFTableRow row : tableInfoStaff.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        String text = run.getText(0);
                        if (text != null) {
                            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                                if (text.trim().contains(entry.getKey())) {
                                    text = text.replace(entry.getKey(), entry.getValue());
                                }
                            }
                            run.setText(text, 0);
                        }
                    }
                }
            }
        }

        // Fill table 1 (evaluation items)
        XWPFTable tableEvaluationItem = doc.getTableArray(1);
        List<StaffEvaluationDto> items = form.getItems();
        if (!CollectionUtils.isEmpty(items)) {
            int contentCell = 1, selfCell = 2, managerCell = 3;

            for (int rowIndex = 0; rowIndex < items.size(); rowIndex++) {
                while (tableEvaluationItem.getNumberOfRows() <= rowIndex + 1) {
                    tableEvaluationItem.createRow();
                }
                XWPFTableRow row = tableEvaluationItem.getRow(rowIndex + 1);
                while (row.getTableCells().size() < 4) {
                    row.addNewTableCell();
                }

                StaffEvaluationDto item = items.get(rowIndex);

                fillCell(row.getCell(contentCell), item.getItemName(), ParagraphAlignment.LEFT);
                fillCell(row.getCell(selfCell), item.getSelfEvaluate() == Const.EVALUATION.PASS ? "Đạt" : item.getSelfEvaluate() == Const.EVALUATION.FAIL ? "Không đạt" : "", ParagraphAlignment.CENTER);
                fillCell(row.getCell(managerCell), item.getManagementEvaluate() == Const.EVALUATION.PASS ? "Đạt" : item.getManagementEvaluate() == Const.EVALUATION.FAIL ? "Không đạt" : "", ParagraphAlignment.CENTER);
            }
        }
        // Thay thế văn bản trong toàn bộ tài liệu (ngoài bảng)
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            List<XWPFRun> runs = paragraph.getRuns();
            StringBuilder paragraphText = new StringBuilder();

            for (XWPFRun run : runs) {
                if (run.getText(0) != null) {
                    paragraphText.append(run.getText(0));
                }
            }

            String replacedText = paragraphText.toString();

            List<Map.Entry<String, String>> entries = new ArrayList<>(replacements.entrySet());
            entries.sort((a, b) -> Integer.compare(b.getKey().length(), a.getKey().length()));

            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                String regex = Pattern.quote(key);
                replacedText = replacedText.replaceAll(regex, Matcher.quoteReplacement(value));
            }
            // Xoá hết các run cũ
            for (int i = runs.size() - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            // Tạo run mới chứa nội dung đã thay
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(replacedText);
        }
        return doc;
    }

    @Override
    public Workbook exportContractApprovalList(EvaluationFormSearchDto searchDto) throws IOException {
        org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:DS_TRINH_KY_HDLD.xlsx");
        InputStream inputStream = resource.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int pageIndex = 1;
        int pageSize = 1000;
        int total = 1;
        int startRowIndex = 4;
        int no = 1;
        // Lấy style từ dòng thứ 3 (index 2)
        Row templateRow = sheet.getRow(3);
        List<CellStyle> cellStyles = new ArrayList<>();
        for (int i = 0; i < 17; i++) { // Số cột bạn muốn áp dụng (0 đến 12)
            Cell cell = templateRow.getCell(i);
            cellStyles.add(cell != null ? cell.getCellStyle() : null);
        }
        while (pageIndex <= total) {
            no = ((pageIndex - 1) * pageSize) + 1;
            searchDto.setPageIndex(pageIndex);
            searchDto.setPageSize(pageSize);
            ApiResponse<Page<EvaluationForm>> pageApiResponse = evaluationFormService.pageForExcel(searchDto);
            if (pageApiResponse != null && pageApiResponse.getData() != null) {
                if (pageApiResponse.getData().getTotalPages() != 0 && total == 1) {
                    total = pageApiResponse.getData().getTotalPages();
                    int sourceStartRow = 7;
                    int targetStartRow = (int) (sourceStartRow + 1 + pageApiResponse.getData().getTotalElements());
                    for (int i = 0; i < 22; i++) {
                        copyRow(workbook, sheet, sourceStartRow + i, targetStartRow + i);
                    }
                }
                if (!CollectionUtils.isEmpty(pageApiResponse.getData().getContent())) {
                    setContentExcel(sheet, pageApiResponse.getData().getContent(), startRowIndex, no, cellStyles);
                }
            }
            pageIndex++;
        }
        return workbook;
    }

    private void setContentExcel(Sheet sheet, List<EvaluationForm> evaluationForms, int startRowIndex, int no, List<CellStyle> cellStyles) {
        for (EvaluationForm evaluationForm : evaluationForms) {
            StaffLabourAgreement staffLabourAgreement = null;
            if (!CollectionUtils.isEmpty(evaluationForm.getStaff().getAgreements())) {
                staffLabourAgreement = evaluationForm.getStaff().getAgreements()
                        .stream()
                        .max(Comparator.comparing(StaffLabourAgreement::getStartDate))
                        .orElse(null);
            }

            Row row = sheet.createRow(startRowIndex++);

            int col = 0;
            Cell cell;

            // STT
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, no++);

            // MÃ NV
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, evaluationForm.getStaffCode());

            // HỌ VÀ TÊN
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, evaluationForm.getStaffName());

            // CHỨC DANH
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, evaluationForm.getPositionTitleName());

            // CẤP BẬC
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, evaluationForm.getRankTitleName());

            // BAN
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, evaluationForm.getDepartment());

            // CƠ SỞ/PHÒNG
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, evaluationForm.getDivision());

            // BỘ PHẬN/NHÓM
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, evaluationForm.getTeam());

            // NGÀY VÀO CÔNG TY
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, evaluationForm.getStaff().getRecruitmentDate());

            // LOẠI HĐLĐ HIỆN TẠI
            String contractStatus = "";
            if (staffLabourAgreement != null && staffLabourAgreement.getAgreementStatus() != null) {
                HrConstants.StaffLabourAgreementStatus statusEnum =
                        HrConstants.StaffLabourAgreementStatus.fromValue(staffLabourAgreement.getAgreementStatus());
                if (statusEnum != null) {
                    contractStatus = statusEnum.getName();
                }
            }
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, contractStatus);

            // THỜI HẠN HĐLĐ HIỆN TẠI
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet,
                    staffLabourAgreement != null && staffLabourAgreement.getContractType() != null ?
                            staffLabourAgreement.getContractType().getName() : null);

            // SỐ LẦN KÝ
            long countSigned = 0;
            if (staffLabourAgreement != null) {
                countSigned = evaluationForm.getStaff().getAgreements()
                        .stream()
                        .filter(agreement -> agreement.getAgreementStatus() != null &&
                                !agreement.getAgreementStatus().equals(HrConstants.StaffLabourAgreementStatus.UNSIGNED.getValue()))
                        .count();
            }
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, countSigned > 0 ? countSigned : null);
            // LOẠI HĐLĐ MỚI
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, evaluationForm.getContractTypeName());
            //THỜI HẠN HĐLĐ MỚI
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, "");
            // LCB
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, "");
            //PHÁP NHÂN
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, "");
            // GHI CHÚ
            cell = row.createCell(col);
            applyStyleAndValue(cell, cellStyles.get(col++), sheet, "");
        }
    }

    private void applyStyleAndValue(Cell cell, CellStyle style, Sheet sheet, Object value) {
        if (value != null) {

        }
        if (style != null) {
            cell.setCellStyle(style);
        }
        if (value == null) return;

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Date) {
            // Nếu là ngày -> áp dụng định dạng ngày
            // Tạo sẵn style định dạng ngày một lần
            CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
            CellStyle dateStyle = sheet.getWorkbook().createCellStyle();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            dateStyle.cloneStyleFrom(style); // giữ nguyên style cũ
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            cell.setCellStyle(dateStyle);
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private void fillCell(XWPFTableCell cell, String text, ParagraphAlignment alignment) {
        clearCell(cell);
        XWPFParagraph para = cell.addParagraph();
        para.setAlignment(alignment);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        XWPFRun run = para.createRun();
        run.setText(text != null ? text : "");
    }

    private void clearCell(XWPFTableCell cell) {
        cell.removeParagraph(0);
    }

    private String getSafe(String val) {
        return val != null ? val : "";
    }

    private String formatDate(Date date, SimpleDateFormat sdf) {
        return date != null ? sdf.format(date) : "";
    }

    private static void copyRow(Workbook workbook, Sheet sheet, int sourceRowNum, int targetRowNum) {
        Row sourceRow = sheet.getRow(sourceRowNum);
        if (sourceRow == null) return;
        Row targetRow = sheet.createRow(targetRowNum);
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            if (sourceCell == null) {
                continue;
            }
            Cell targetCell = targetRow.createCell(i);

            // Copy cell value
            switch (sourceCell.getCellTypeEnum()) {
                case STRING:
                    targetCell.setCellValue(sourceCell.getStringCellValue());
                    break;
                case NUMERIC:
                    targetCell.setCellValue(sourceCell.getNumericCellValue());
                    break;
                case BOOLEAN:
                    targetCell.setCellValue(sourceCell.getBooleanCellValue()); // Đúng ở đây
                    break;
                case FORMULA:
                    targetCell.setCellFormula(sourceCell.getCellFormula());
                    break;
                case BLANK:
                    targetCell.setCellValue("");  // Đặt giá trị trống cho cell
                    break;
                default:
                    break;
            }
            // Copy style
            CellStyle newStyle = workbook.createCellStyle();
            newStyle.cloneStyleFrom(sourceCell.getCellStyle());
            targetCell.setCellStyle(newStyle);
            sourceCell.setCellValue("");
        }
    }

    // Hàm xóa dòng
    private static void removeRow(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row != null) {
            sheet.removeRow(row);
        }

        // Điều chỉnh lại các dòng bên dưới sau khi xóa
        for (int i = rowIndex + 1; i <= sheet.getLastRowNum(); i++) {
            Row rowToShift = sheet.getRow(i);
            if (rowToShift != null) {
                // Di chuyển dòng lên trên
                sheet.shiftRows(i, i, -1);
            }
        }
    }
}
