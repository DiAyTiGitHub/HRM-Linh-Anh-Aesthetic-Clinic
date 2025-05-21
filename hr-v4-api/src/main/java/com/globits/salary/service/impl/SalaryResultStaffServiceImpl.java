package com.globits.salary.service.impl;

import com.globits.core.domain.FileDescription;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchStaffSalaryTemplateDto;
import com.globits.hr.repository.StaffLabourAgreementRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffSignatureRepository;
import com.globits.hr.repository.StaffSocialInsuranceRepository;
import com.globits.hr.service.StaffSocialInsuranceService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.ExpressionUtil;
import com.globits.hr.utils.RoleUtils;
import com.globits.salary.domain.*;
import com.globits.salary.dto.*;
import com.globits.salary.dto.excel.ImportSalaryResultStaffDto;
import com.globits.salary.dto.excel.ImportSalaryStaffItemValueDto;
import com.globits.salary.dto.search.SearchSalaryResultDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import com.globits.salary.repository.*;
import com.globits.salary.service.*;
import com.globits.security.dto.UserDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SalaryResultStaffServiceImpl extends GenericServiceImpl<SalaryResultStaff, UUID> implements SalaryResultStaffService {

    private static final Logger logger = LoggerFactory.getLogger(SalaryResultStaffServiceImpl.class);

    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    @Autowired
    private SalaryResultService salaryResultService;

    @Autowired
    private SalaryResultRepository salaryResultRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SalaryResultStaffRepository salaryResultStaffRepository;

    @Autowired
    private SalaryResultStaffItemRepository salaryResultStaffItemRepository;

    @Autowired
    private SalaryTemplateRepository salaryTemplateRepository;

    @Autowired
    private SalaryTemplateItemRepository salaryTemplateItemRepository;

    @Autowired
    private SalaryResultItemRepository salaryResultItemRepository;

    @Autowired
    private SalaryItemService salaryItemService;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Autowired
    private StaffSocialInsuranceRepository staffSocialInsuranceRepository;

    @Autowired
    private StaffSocialInsuranceService staffSocialInsuranceService;

    @Autowired
    private StaffSignatureRepository staffSignatureRepository;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private StaffLabourAgreementRepository staffLabourAgreementRepository;

    @Autowired
    private StaffSalaryItemValueRepository staffSalaryItemValueRepository;

    @Autowired
    private StaffSalaryTemplateRepository staffSalaryTemplateRepository;

    @Autowired
    private StaffSalaryTemplateService staffSalaryTemplateService;

    @Autowired
    private SalaryAutoCalculationService salaryAutoCalculationService;

    @Autowired
    private EntityManager entityManager;


    @Override
    public Boolean updateApprovalStatus(SearchSalaryResultStaffDto dto) throws Exception {
        if (dto == null) return false;
        if (dto.getSalaryResultStaffIds() == null || dto.getSalaryResultStaffIds().isEmpty()) return false;

        for (UUID recordId : dto.getSalaryResultStaffIds()) {
            SalaryResultStaff entity = salaryResultStaffRepository.findById(recordId).orElse(null);
            if (entity == null) throw new Exception("Record is not existed!");

            entity.setApprovalStatus(dto.getApprovalStatus());

            salaryResultStaffRepository.save(entity);

            // tạo/bỏ phiếu chi trả bảo hiểm
//            this.handleSocialInsuranceByChangingStatus(entity, dto.getApprovalStatus());
        }

        return true;
    }

    @Override
    @Transactional
    public Boolean updatePaidStatus(SearchSalaryResultStaffDto dto) throws Exception {
        if (dto == null || CollectionUtils.isEmpty(dto.getSalaryResultStaffIds())) {
            return false;
        }

        List<UUID> ids = dto.getSalaryResultStaffIds();
        List<SalaryResultStaff> entities = salaryResultStaffRepository.findAllById(ids);

        if (entities.size() != ids.size()) {
            //throw new Exception("Some records do not exist!");
            logger.error("Some records do not exist!");
            return false;
        }

        for (SalaryResultStaff entity : entities) {
            entity.setPaidStatus(dto.getPaidStatus());
        }
        salaryResultStaffRepository.saveAll(entities);
        return true;
    }


    @Override
    public void generateSalaryResultStaffs(SalaryResultDto dto, SalaryResult entity) {
        if (entity.getSalaryResultStaffs() == null) {
            entity.setSalaryResultStaffs(new HashSet<>());
        }

        Set<SalaryResultStaff> tableRows = new HashSet<>();

        if (dto.getStaffs() != null && !dto.getStaffs().isEmpty()) {
            for (int i = 0; i < dto.getStaffs().size(); i++) {
                StaffDto chosenStaff = dto.getStaffs().get(i);

                SalaryResultStaff row = null;

                List<SalaryResultStaff> allValidRows = salaryResultStaffRepository.findBySalaryResultIdAndStaffId(entity.getId(), chosenStaff.getId());
                if (allValidRows != null && !allValidRows.isEmpty()) {
                    row = allValidRows.get(0);
                }
                if (row == null) {
                    row = new SalaryResultStaff();

                    Staff staffEntity = staffRepository.findById(chosenStaff.getId()).orElse(null);
                    if (staffEntity == null) continue;
                    row.setStaff(staffEntity);
                    row.setSalaryResult(entity);

                }
                row.setDisplayOrder(i);
                row.setApprovalStatus(HrConstants.SalaryResulStaffApprovalStatus.NOT_APPROVED_YET.getValue());

                row = salaryResultStaffRepository.save(row);

                salaryResultStaffItemService.generateResultStaffItems(row, entity);

                tableRows.add(row);
            }
        }

        entity.getSalaryResultStaffs().clear();
        entity.getSalaryResultStaffs().addAll(tableRows);
    }

    @Override
    public SalaryResultStaffDto saveResultStaff(SalaryResultStaffDto dto) {
        if (dto == null) {
            return null;
        }

        SalaryResultStaff entity = null;
        if (dto.getId() != null) {
            entity = salaryResultStaffRepository.findById(dto.getId()).orElse(null);
        }

        if (entity == null || dto.getSalaryResultStaffItems() == null || dto.getSalaryResultStaffItems().isEmpty())
            return null;

        Set<SalaryResultStaffItem> cellsInRow = new HashSet<>();
        for (SalaryResultStaffItemDto cellDto : dto.getSalaryResultStaffItems()) {
            if (cellDto.getId() == null) continue;
            SalaryResultStaffItem cell = salaryResultStaffItemRepository.findById(cellDto.getId()).orElse(null);
            if (cell == null) continue;

            cell.setValue(cellDto.getValue());
            cell = salaryResultStaffItemRepository.save(cell);

            cellsInRow.add(cell);
        }

        if (entity.getSalaryResultStaffItems() == null) {
            entity.setSalaryResultStaffItems(new HashSet<>());
        }
        entity.getSalaryResultStaffItems().clear();
        entity.getSalaryResultStaffItems().addAll(cellsInRow);

        entity = salaryResultStaffRepository.save(entity);

        SalaryResultStaffDto response = new SalaryResultStaffDto(entity, true);

        return response;
    }


    @Override
    @Modifying
    @Transactional
    public Boolean deleteSalaryResultStaff(UUID resultStaffId) {
        if (resultStaffId == null) return false;

        SalaryResultStaff entity = salaryResultStaffRepository.findById(resultStaffId).orElse(null);
        if (entity == null) return false;

        UUID salaryBoardId = null;
        if (entity.getSalaryResult() != null && entity.getSalaryResult().getId() != null) {
            entity.getSalaryResult().getId();
        }
        salaryResultStaffRepository.delete(entity);

        if (salaryBoardId != null) {
            // when successfully delete a staff from salaryResult, if the salaryBoard contains column STT => rerender this column
            List<SalaryResultStaff> resultStaffs = salaryResultStaffRepository.getAllBySalaryResultId(salaryBoardId);
            if (resultStaffs == null || resultStaffs.isEmpty()) return true;

            int indexOrder = 0;
            for (SalaryResultStaff resultStaff : resultStaffs) {
                if (resultStaff.getId().equals(entity.getId())) continue;

                indexOrder++;
                resultStaff.setDisplayOrder(indexOrder);

                if (resultStaff.getSalaryResultStaffItems() != null && !resultStaff.getSalaryResultStaffItems().isEmpty()) {
                    for (SalaryResultStaffItem resultStaffItem : resultStaff.getSalaryResultStaffItems()) {
                        if (resultStaffItem.getReferenceCode().equals(HrConstants.SalaryItemCodeSystemDefault.STT_SYSTEM.getValue())) {
                            resultStaffItem.setValue(String.valueOf(indexOrder));
                            break;
                        }
                    }
                }

                salaryResultStaffRepository.save(resultStaff);

            }
        }

        return true;
    }


    @Override
    public Boolean removeMultiple(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID id : ids) {
            boolean deleteRes = this.deleteSalaryResultStaff(id);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }


    PdfPCell createCell(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        return cell;
    }

    public byte[] generatePayslipPdf(UUID id, UUID staffSignatureId) {
        SalaryResultStaff entity = salaryResultStaffRepository.findById(id).orElse(null);
        if (entity == null) return null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            //front chữ:
            // Đường dẫn tới file font (chỉnh sửa đường dẫn phù hợp với dự án của bạn)
            String fontPath = "/fronts/vuArial.ttf";
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font vietnameseFont = new Font(baseFont, 12, Font.NORMAL);
            Font vietnameseItalicFont = new Font(baseFont, 12, Font.NORMAL);
            Font vietnameseBoldFont = new Font(baseFont, 12, Font.BOLD);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);

            Paragraph title = new Paragraph("PHIẾU LƯƠNG", titleFont);
            if (entity.getSalaryPeriod() != null && entity.getSalaryPeriod().getName() != null) {
                title = new Paragraph(entity.getSalaryPeriod().getName(), titleFont);
            }
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Ngày tháng năm
            String currentDate = new SimpleDateFormat("'Hà Nội, ngày' dd 'tháng' MM 'năm' yyyy").format(new Date());
            Paragraph dateParagraph = new Paragraph(currentDate, vietnameseItalicFont);
            dateParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(dateParagraph);

            // Thông tin nhân viên
            if (entity.getStaff() == null) return null;
            // Bảng thông tin nhân viên (4 cột)
            PdfPTable staffTable = new PdfPTable(4);
            staffTable.setWidthPercentage(100);
            staffTable.setSpacingBefore(10f);
            staffTable.setWidths(new float[]{3, 5, 3, 5}); // Chia tỷ lệ cột

            Staff staff = entity.getStaff();
            // Hàm tạo ô với nội dung và kiểu font
            Set<Position> listPosition = staff.getCurrentPositions();
            Position mainPosition = listPosition.stream().filter(x -> x.getIsMain()).findFirst().get();
            PositionTitle positionTitle = null;
            RankTitle rankTitle = null;
            HRDepartment department = null;
            if (mainPosition != null) {
                positionTitle = mainPosition.getTitle();

                if (positionTitle != null) {
                    rankTitle = positionTitle.getRankTitle();
                }
                department = mainPosition.getDepartment();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
// Hàng 1: Mã nhân viên - Level
            staffTable.addCell(createCell("Mã nhân viên:", vietnameseBoldFont, Element.ALIGN_LEFT));
            staffTable.addCell(createCell(entity.getStaff().getStaffCode(), vietnameseFont, Element.ALIGN_LEFT));
            staffTable.addCell(createCell("Level:", vietnameseBoldFont, Element.ALIGN_LEFT));
            if (rankTitle != null) {
                staffTable.addCell(createCell(rankTitle.getName(), vietnameseFont, Element.ALIGN_LEFT));
            } else {
                staffTable.addCell(createCell("#N/A", vietnameseFont, Element.ALIGN_LEFT));
            }

// Hàng 2: Họ và tên - Chức danh
            staffTable.addCell(createCell("Họ và tên:", vietnameseBoldFont, Element.ALIGN_LEFT));
            staffTable.addCell(createCell(entity.getStaff().getDisplayName() != null ? entity.getStaff().getDisplayName() : "#N/A", vietnameseFont, Element.ALIGN_LEFT));
            staffTable.addCell(createCell("Vị trí làm việc:", vietnameseBoldFont, Element.ALIGN_LEFT));
            if (mainPosition != null) {
                staffTable.addCell(createCell(mainPosition.getName(), vietnameseFont, Element.ALIGN_LEFT));
            } else {
                staffTable.addCell(createCell("#N/A", vietnameseFont, Element.ALIGN_LEFT));
            }

// Hàng 3: Bộ phận/Nhóm - Phòng/BP/Cơ sở
            staffTable.addCell(createCell("Phòng/BP/Cơ sở:", vietnameseBoldFont, Element.ALIGN_LEFT));
            if (department != null) {
                staffTable.addCell(createCell(department.getName(), vietnameseFont, Element.ALIGN_LEFT));
            } else {
                staffTable.addCell(createCell("#N/A", vietnameseFont, Element.ALIGN_LEFT));
            }
            staffTable.addCell(createCell("Ngày vào làm:", vietnameseBoldFont, Element.ALIGN_LEFT));
            // Giả sử đây là trong một phương thức tạo bảng PDF
            if (staff.getRecruitmentDate() != null) {
                String formattedDate = dateFormat.format(staff.getRecruitmentDate());
                staffTable.addCell(createCell(formattedDate, vietnameseFont, Element.ALIGN_LEFT));
            } else {
                staffTable.addCell(createCell("#N/A", vietnameseFont, Element.ALIGN_LEFT));
            }

            document.add(staffTable);
            document.add(new Paragraph("\n"));
//            document.add(new Paragraph("--------------------------------------", vietnameseFont));
            SalaryTemplate template = entity.getSalaryTemplate();
            if (template == null) {
                return null;
            }
            Set<SalaryTemplateItem> templateItems = template.getTemplateItems();
            if (templateItems == null) {
                return null;
            }

            // Định dạng tiền tệ
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

            // Khai báo mảng index
            String[] listIndex = {
                    "I. ", "II. ", "III. ", "IV. ", "V. ", "VI. ", "VII. ", "VIII. ", "IX. ", "X. ",
                    "XI. ", "XII. ", "XIII. ", "XIV. ", "XV. ", "XVI. ", "XVII. ", "XVIII. ", "XIX. ", "XX. ",
                    "XXI. ", "XXII. ", "XXIII. ", "XXIV. ", "XXV. ", "XXVI. ", "XXVII. ", "XXVIII. ", "XXIX. ", "XXX. ",
                    "XXXI. ", "XXXII. ", "XXXIII. ", "XXXIV. ", "XXXV. ", "XXXVI. ", "XXXVII. ", "XXXVIII. ", "XXXIX. ", "XL. ",
                    "XLI. ", "XLII. ", "XLIII. ", "XLIV. ", "XLV. ", "XLVI. ", "XLVII. ", "XLVIII. ", "XLIX. ", "L. "
            };

// Bảng 1: Các item không có group
            PdfPTable noGroupTable = new PdfPTable(2); // Tăng lên 3 cột
            noGroupTable.setWidthPercentage(100);
            noGroupTable.setSpacingBefore(10f);

            PdfPCell header1NoGroup = new PdfPCell(new Phrase("Khoản mục", vietnameseBoldFont));
            PdfPCell header2NoGroup = new PdfPCell(new Phrase("Giá trị", vietnameseBoldFont));
//            PdfPCell header3NoGroup = new PdfPCell(new Phrase("Kiểu dữ liệu", vietnameseBoldFont));
            header1NoGroup.setHorizontalAlignment(Element.ALIGN_CENTER);
            header2NoGroup.setHorizontalAlignment(Element.ALIGN_CENTER);
//            header3NoGroup.setHorizontalAlignment(Element.ALIGN_CENTER);
            header1NoGroup.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header2NoGroup.setBackgroundColor(BaseColor.LIGHT_GRAY);
//            header3NoGroup.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header1NoGroup.setPadding(6);
            header2NoGroup.setPadding(6);
//            header3NoGroup.setPadding(6);
            noGroupTable.addCell(header1NoGroup);
            noGroupTable.addCell(header2NoGroup);
//            noGroupTable.addCell(header3NoGroup);

// Bảng 2: Các item có group
            PdfPTable groupTable = new PdfPTable(2); // Tăng lên 3 cột
            groupTable.setWidthPercentage(100);
            groupTable.setSpacingBefore(10f);

            PdfPCell header1Group = new PdfPCell(new Phrase("Khoản mục", vietnameseBoldFont));
            PdfPCell header2Group = new PdfPCell(new Phrase("Giá trị", vietnameseBoldFont));
//            PdfPCell header3Group = new PdfPCell(new Phrase("Kiểu dữ liệu", vietnameseBoldFont));
            header1Group.setHorizontalAlignment(Element.ALIGN_CENTER);
            header2Group.setHorizontalAlignment(Element.ALIGN_CENTER);
//            header3Group.setHorizontalAlignment(Element.ALIGN_CENTER);
            header1Group.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header2Group.setBackgroundColor(BaseColor.LIGHT_GRAY);
//            header3Group.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header1Group.setPadding(6);
            header2Group.setPadding(6);
//            header3Group.setPadding(6);
            groupTable.addCell(header1Group);
            groupTable.addCell(header2Group);
//            groupTable.addCell(header3Group);

            int index = 0;
            String currentGroup = "";

            for (SalaryResultStaffItem item : entity.getSalaryResultStaffItems()) {
                if (item.getSalaryTemplateItem() != null &&
                        item.getSalaryTemplateItem().getHiddenOnPayslip() != null &&
                        item.getSalaryTemplateItem().getHiddenOnPayslip()) {
                    continue;
                }

                if (item.getSalaryTemplateItem() != null &&
                        item.getSalaryTemplateItem().getTemplateItemGroup() != null) {
                    // Xử lý cho bảng có group
                    String nameOfGroup = item.getSalaryTemplateItem().getTemplateItemGroup().getName();
                    if (!nameOfGroup.equals(currentGroup)) {
                        String writeGroupName = (index < listIndex.length ? listIndex[index] : "") + nameOfGroup;
                        PdfPCell groupCell = new PdfPCell(new Phrase(writeGroupName, vietnameseBoldFont));
                        groupCell.setColspan(3); // Tăng colspan lên 3
                        groupCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        groupCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        groupCell.setPadding(6);
                        groupTable.addCell(groupCell);

                        currentGroup = nameOfGroup;
                        index++;
                    }

                    PdfPCell nameCell = new PdfPCell(new Phrase(item.getSalaryTemplateItem().getDisplayName(), vietnameseFont));
                    nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    nameCell.setPadding(4);
                    groupTable.addCell(nameCell);

                    double value = Double.parseDouble(item.getValue().replaceAll("[^0-9.]", ""));
                    PdfPCell valueCell = new PdfPCell(new Phrase(formatter.format(value), vietnameseBoldFont));
                    valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    valueCell.setPadding(4);
                    groupTable.addCell(valueCell);

//                    PdfPCell typeCell = new PdfPCell(new Phrase(getValueTypeName(item), vietnameseFont));
//                    typeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    typeCell.setPadding(4);
//                    groupTable.addCell(typeCell);
                } else {
                    // Xử lý cho bảng không group
                    PdfPCell nameCell = new PdfPCell(new Phrase(item.getSalaryTemplateItem().getDisplayName(), vietnameseFont));
                    nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    nameCell.setPadding(4);
                    noGroupTable.addCell(nameCell);

                    double value = Double.parseDouble(item.getValue().replaceAll("[^0-9.]", ""));
                    PdfPCell valueCell = new PdfPCell(new Phrase(formatter.format(value), vietnameseBoldFont));
                    valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    valueCell.setPadding(4);
                    noGroupTable.addCell(valueCell);

//                    PdfPCell typeCell = new PdfPCell(new Phrase(getValueTypeName(item), vietnameseFont));
//                    typeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    typeCell.setPadding(4);
//                    noGroupTable.addCell(typeCell);
                }
            }

            document.add(noGroupTable);
            document.add(groupTable);

            // Ký tên
            if (staffSignatureId != null) {
                StaffSignature signature = staffSignatureRepository.findById(staffSignatureId).orElse(null);

                if (signature != null && signature.getStaff() != null) {
                    addSignatureSection(document, signature.getFile(), signature.getStaff().getDisplayName());
                } else {
                    addSignatureSection(document, null, null);
                }
            } else {
                addSignatureSection(document, null, null);
            }
            // Thêm phần ký tên
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getValueTypeName(SalaryResultStaffItem item) {
        if (item.getSalaryTemplateItem() == null || item.getSalaryTemplateItem().getSalaryItem() == null) {
            return "Không xác định";
        }

        Integer valueType = item.getSalaryTemplateItem().getSalaryItem().getValueType();
        if (valueType == null) {
            return "Không xác định";
        }

        switch (valueType) {
            case 1: // Giả sử TEXT có value = 1 theo enum
                return "Chữ";
            case 2: // MONEY = 2
                return "VNĐ";
            case 3: // NUMBER = 3
                return "";
            case 4: // PERCENT = 4
                return "%";
            case 5: // OTHERS = 5
                return "Khác";
            default:
                return "Không xác định";
        }
    }

    private void addSignatureSection(Document document, FileDescription signatureFile, String approverName) throws Exception {
        // Định dạng font chữ tiếng Việt
        String fontPath = "/fronts/vuArial.ttf";
        BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font vietnameseFont = new Font(baseFont, 12, Font.NORMAL);
        Font titleFont = new Font(baseFont, 18, Font.BOLD);

        document.add(new Paragraph("\n"));
        Paragraph approver = new Paragraph("Người duyệt lương", vietnameseFont);
        approver.setAlignment(Element.ALIGN_RIGHT);
        document.add(approver);

        Paragraph sign = new Paragraph("(Ký tên, đóng dấu)", vietnameseFont);
        sign.setAlignment(Element.ALIGN_RIGHT);
        document.add(sign);

        // Thêm chữ ký từ file
        if (signatureFile != null && signatureFile.getFilePath() != null) {
            try {
                File file = new File(signatureFile.getFilePath());
                if (file.exists()) {
                    Image signature = Image.getInstance(signatureFile.getFilePath());
                    signature.setAlignment(Element.ALIGN_RIGHT);
                    signature.scaleAbsolute(100, 50);
                    document.add(signature);
                } else {
                    System.err.println("File chữ ký không tồn tại: " + signatureFile.getFilePath());
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi thêm chữ ký từ file: " + e.getMessage());
            }
        }

        // Thêm tên người duyệt
        if (approverName != null && !approverName.isEmpty()) {
            Paragraph name = new Paragraph(approverName, vietnameseFont);
            name.setAlignment(Element.ALIGN_RIGHT);
            document.add(name);
        }
    }


    @Override
    public SalaryResultDto viewSalaryResult(SalaryResultDto dto) {
        if (dto != null) {
            SalaryResultDto result = new SalaryResultDto();
            SalaryPeriod salaryPeriod = null;
            if (dto.getSalaryPeriod() != null) {
                salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriod().getId()).orElse(null);
            }
            SalaryTemplate salaryTemplate = null;
            if (dto.getSalaryTemplate() != null) {
                salaryTemplate = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId()).orElse(null);
            }
            if (salaryPeriod == null || salaryTemplate == null) {
                return null;
            }
            List<SalaryResultStaffDto> salaryResultStaffs = salaryResultStaffRepository.getByPeriodIdAndTemplateId(salaryPeriod.getId(), salaryTemplate.getId());
            result.setSalaryResultStaffs(salaryResultStaffs);
            List<SalaryTemplateItemDto> templateItemDtos = salaryTemplate.getTemplateItems().stream().map(item -> new SalaryTemplateItemDto(item)).collect(Collectors.toList());
            result.setTemplateItems(templateItemDtos);
            return result;
        }
        return null;
    }

    // code đã tối ưu
    @Override
    public void importSalaryResultStaffItemTemplate(InputStream is, List<ImportSalaryResultStaffDto> list) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            int numSheets = workbook.getNumberOfSheets();
            int colIndexDefault = 3;

            for (int sheetIndex = 0; sheetIndex < numSheets; sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    logger.info("Sheet " + sheetIndex + " không có header, bỏ qua.");
                    continue;
                }

                // Xử lý header: Lấy danh sách các cột chứa dữ liệu (dành cho salary item) sau cột mặc định
                List<ImportSalaryStaffItemValueDto> headerItems = new ArrayList<>();
                int lastCellNum = headerRow.getLastCellNum();
                int validHeaderCount = 0;
                for (int col = 0; col < lastCellNum; col++) {
                    Cell cell = headerRow.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell == null || cell.toString().trim().isEmpty()) {
                        break; // Dừng lại khi gặp ô trống
                    }
                    validHeaderCount++;
                    if (col > colIndexDefault) {
                        ImportSalaryStaffItemValueDto item = new ImportSalaryStaffItemValueDto();
                        item.setCellIndex(col);
                        item.setSalaryItemCode(cell.toString());
                        headerItems.add(item);
                    }
                }
                logger.info("Sheet " + sheetIndex + " - validHeaderCount: " + validHeaderCount);

                // Nếu header không hợp lệ (không có cột salary item) thì bỏ qua sheet này
                if (validHeaderCount <= colIndexDefault || headerItems.isEmpty()) {
                    logger.info("Sheet " + sheetIndex + " không có dữ liệu salary item, bỏ qua.");
                    continue;
                }

                // Xử lý các dòng dữ liệu (bắt đầu từ dòng 1)
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) {
                        break; // Nếu dòng null thì kết thúc xử lý của sheet
                    }
                    // Kiểm tra cell thứ 1 để xác định dòng hợp lệ
                    Cell checkCell = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (checkCell == null || checkCell.toString().trim().isEmpty()) {
                        break;
                    }

                    // Tạo đối tượng DTO và gán giá trị các cột cố định
                    ImportSalaryResultStaffDto dto = new ImportSalaryResultStaffDto();
                    dto.setImportOrder(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString());
//                    dto.setStaffCode(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString());

                    Cell cellStaffCode = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String staffCode = "";

                    switch (cellStaffCode.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            staffCode = cellStaffCode.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            staffCode = String.valueOf((long) cellStaffCode.getNumericCellValue()); // Convert to long to remove decimal
                            break;
                        case Cell.CELL_TYPE_BOOLEAN:
                            staffCode = String.valueOf(cellStaffCode.getBooleanCellValue());
                            break;
                        default:
                            staffCode = "";
                    }

                    dto.setStaffCode(staffCode);

                    dto.setStaffDisplayName(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString());
                    dto.setSalaryPeriodCode(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString());

                    // Clone headerItems cho dòng hiện tại và gán giá trị từ các ô tương ứng
                    List<ImportSalaryStaffItemValueDto> clonedItems = new ArrayList<>();
                    for (ImportSalaryStaffItemValueDto headerItem : headerItems) {
                        ImportSalaryStaffItemValueDto clonedItem = new ImportSalaryStaffItemValueDto(headerItem); // Giả sử có constructor copy
                        int cellIdx = headerItem.getCellIndex();
                        Cell cell = row.getCell(cellIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        clonedItem.setSalaryItemValue(cell.toString());
                        clonedItems.add(clonedItem);
                    }
                    dto.setSalaryItemValues(clonedItems);
                    list.add(dto);
                }
            }
        }
    }

    private SearchStaffSalaryTemplateDto convertToSearchStaffSalaryTemplateDto(SearchSalaryResultDto dto) {
        SearchStaffSalaryTemplateDto response = new SearchStaffSalaryTemplateDto();

        response.setStaffId(dto.getStaffId());
        response.setSalaryTemplateId(dto.getSalaryTemplateId());
        response.setDepartmentId(dto.getDepartmentId());
        response.setOrganizationId(dto.getOrganizationId());
        response.setPositionTitleId(dto.getPositionTitleId());
        response.setPositionId(dto.getPositionId());

        return response;
    }

    @Override
    public SalaryResultDto calculateSalaryStaffs(SearchSalaryResultDto dto) {
        if (dto.getSalaryTemplateId() == null || dto.getSalaryPeriodId() == null) {
            return null;
        }

        SalaryResultDto result = new SalaryResultDto();

        SalaryPeriod salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriodId()).orElse(null);
        if (salaryPeriod == null) return null;
        SalaryPeriodDto salaryPeriodDto = new SalaryPeriodDto(salaryPeriod);
        result.setSalaryPeriod(salaryPeriodDto);

        SalaryTemplate salaryTemplate = salaryTemplateRepository.findById(dto.getSalaryTemplateId()).orElse(null);
        if (salaryTemplate == null) return null;
        SalaryTemplateDto salaryTemplateDto = new SalaryTemplateDto(salaryTemplate, true);

        result.setSalaryTemplate(salaryTemplateDto);
        result.setTemplateItems(salaryTemplateDto.getTemplateItems());
        result.setTemplateItemGroups(salaryTemplateDto.getTemplateItemGroups());

        List<SalaryResultStaffDto> salaryResultStaffDto = new ArrayList<SalaryResultStaffDto>();

        // Chuẩn hóa fromDate và toDate
        Date fromDate = DateTimeUtil.getStartOfDay(salaryPeriod.getFromDate());
        Date toDate = DateTimeUtil.getEndOfDay(salaryPeriod.getToDate());

        SearchStaffSalaryTemplateDto searchDto = this.convertToSearchStaffSalaryTemplateDto(dto);
        searchDto.setFromDate(fromDate);
        searchDto.setToDate(toDate);

        // Lấy ra toàn bộ danh sách nhân viên có thể tính lương theo kỳ lương và mẫu bảng lương hiện tại
        List<StaffSalaryTemplate> staffSalaryTemplates = staffSalaryTemplateService.findBySalaryTemplateIdAndRangeTime(searchDto);

        // Lọc danh sách Staff không trùng nhau
        List<UUID> staffIds = new ArrayList<>();

        for (StaffSalaryTemplate staffSalaryTemplate : staffSalaryTemplates) {
            if (staffSalaryTemplate.getStaff() == null) continue;

            staffIds.add(staffSalaryTemplate.getStaff().getId());
        }

        if (!CollectionUtils.isEmpty(staffIds)) {
            for (UUID staffId : staffIds) {
                SalaryResultStaffDto srdt = new SalaryResultStaffDto();

                StaffDto needCalStaff = new StaffDto();
                needCalStaff.setId(staffId);

                srdt.setStaff(needCalStaff);
                srdt.setSalaryTemplate(salaryTemplateDto);
                srdt.setSalaryPeriod(salaryPeriodDto);

                SalaryResultStaffDto srsDto = this.calculateSalaryStaff(srdt);


                salaryResultStaffDto.add(srsDto);
            }

            result.setSalaryResultStaffs(salaryResultStaffDto);
        }

        return result;
    }

    @Override
    public byte[] exportPdf(SearchSalaryResultStaffDto dto) {
        SalaryResultDto salaryResultDto = salaryResultService.searchSalaryResultBoard(dto);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            //front chữ:
            // Đường dẫn tới file font (chỉnh sửa đường dẫn phù hợp với dự án của bạn)
            String fontPath = "/fronts/vuArial.ttf";
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font vietnameseFont = new Font(baseFont, 12, Font.NORMAL);
            Font vietnameseItalicFont = new Font(baseFont, 12, Font.NORMAL);
            Font vietnameseBoldFont = new Font(baseFont, 12, Font.BOLD);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);

            Paragraph title = new Paragraph("BẢNG THANH TOÁN TIỀN LƯƠNG", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Ngày tháng năm
            String currentDate = new SimpleDateFormat("'Hà Nội, ngày' dd 'tháng' MM 'năm' yyyy").format(new Date());
            Paragraph dateParagraph = new Paragraph(currentDate, vietnameseItalicFont);
            dateParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(dateParagraph);
            document.add(new Paragraph("\n"));
//            document.add(new Paragraph("--------------------------------------", vietnameseFont));


            // Bảng chi tiết lương
            PdfPTable table = new PdfPTable(salaryResultDto.getTemplateItems().size() + 3);
            table.setWidthPercentage(100);

            List<SalaryTemplateItemDto> templateItems = salaryResultDto.getTemplateItems();
            List<SalaryTemplateItemGroupDto> templateItemGroups = salaryResultDto.getTemplateItemGroups();

            Map<UUID, Integer> colSpanMap = new HashMap<>();
            List<SalaryTemplateItemDto> columnGroups = new ArrayList<>();
            List<SalaryTemplateItemDto> remainItems = new ArrayList<>();
            List<SalaryTemplateItemDto> thutuCell = new ArrayList<>();
            for (SalaryTemplateItemDto item : templateItems) {
                if (item.getTemplateItemGroupId() == null) {
                    // Cột không thuộc nhóm, rowspan = 2
                    columnGroups.add(item);
                } else {
                    // Xử lý nhóm cột
                    if (!colSpanMap.containsKey(item.getTemplateItemGroupId())) {
                        colSpanMap.put(item.getTemplateItemGroupId(), 1);
                    } else {
                        colSpanMap.put(item.getTemplateItemGroupId(), colSpanMap.get(item.getTemplateItemGroupId()) + 1);
                    }
                    remainItems.add(item);
                }
            }

            // Thêm cột STT và Tên vào tiêu đề
            PdfPCell sttHeader = new PdfPCell(new Phrase("STT"));
            sttHeader.setPadding(4);
            sttHeader.setColspan(1);
            sttHeader.setRowspan(2);
            sttHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            sttHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
            sttHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(sttHeader);

            PdfPCell nameHeader = new PdfPCell(new Phrase("Tên"));
            nameHeader.setPadding(4);
            nameHeader.setColspan(1);
            nameHeader.setRowspan(2);
            nameHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
            nameHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            nameHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(nameHeader);

            // Thêm các cột riêng lẻ
            for (SalaryTemplateItemDto item : columnGroups) {
                PdfPCell cell = new PdfPCell(new Phrase(item.getDisplayName(), vietnameseFont));
                thutuCell.add(item);
                cell.setPadding(4);
                cell.setColspan(1);
                cell.setRowspan(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            // Thêm header nhóm cột
            for (SalaryTemplateItemGroupDto group : templateItemGroups) {
                if (colSpanMap.containsKey(group.getId())) {
                    PdfPCell cell = new PdfPCell(new Phrase(group.getName(), vietnameseFont));
                    cell.setPadding(4);
                    cell.setColspan(colSpanMap.get(group.getId()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    table.addCell(cell);
                }
            }

            // Ký nhận
            PdfPCell signatureHeader = new PdfPCell(new Phrase("Ký nhận", vietnameseFont));
            signatureHeader.setPadding(4);
            signatureHeader.setRowspan(2);
            signatureHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
            signatureHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            signatureHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(signatureHeader);

            // Thêm các cột con trong nhóm
            for (SalaryTemplateItemGroupDto group : templateItemGroups) {
                for (SalaryTemplateItemDto item : remainItems) {
                    if (item.getTemplateItemGroupId().equals(group.getId())) {
                        PdfPCell cell = new PdfPCell(new Phrase(item.getDisplayName(), vietnameseItalicFont));
                        thutuCell.add(item);
                        cell.setPadding(4);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(cell);
                    }
                }
            }

            // Định dạng tiền tệ
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

            // double amount = Double.parseDouble(value.replaceAll("[^0-9.]", ""));
            // Tạo Map để lưu tổng giá trị cho từng cột
            Map<String, Double> totalMap = new HashMap<>();
            // Duyệt từng nhân viên
            int index = 1;
            for (SalaryResultStaffDto staff : salaryResultDto.getSalaryResultStaffs()) {
                PdfPCell sttCell = new PdfPCell(new Phrase(String.valueOf(index++), vietnameseFont));
                sttCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                sttCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                sttCell.setPadding(4);
                table.addCell(sttCell);

                PdfPCell nameCell = new PdfPCell(new Phrase(staff.getStaffName(), vietnameseFont));
                nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                nameCell.setPadding(4);
                table.addCell(nameCell);
                // Tạo map để lưu giá trị theo cột
                Map<String, String> salaryDataMap = new HashMap<>();
                for (SalaryResultStaffItemDto salaryItem : staff.getSalaryResultStaffItems()) {
                    double value = Double.parseDouble(salaryItem.getValue().replaceAll("[^0-9.]", ""));
                    salaryDataMap.put(salaryItem.getReferenceCode(), formatter.format(value));

                    // Cộng dồn vào totalMap
                    totalMap.put(salaryItem.getReferenceCode(), totalMap.getOrDefault(salaryItem.getReferenceCode(), 0.0) + value);
                }


                // Duyệt theo thứ tự cột trong templateItems để đảm bảo dữ liệu đúng vị trí
                for (SalaryTemplateItemDto cellCode : thutuCell) {
                    String value = salaryDataMap.getOrDefault(cellCode.getCode(), cellCode.getDefaultValue());
                    PdfPCell valueCell = new PdfPCell(new Phrase(value));
                    valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    valueCell.setPadding(4);
                    table.addCell(valueCell);
                }

                PdfPCell signatureCell = new PdfPCell(new Phrase(""));
                signatureCell.setPadding(4);
                signatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(signatureCell);
            }


            // Thêm hàng tổng cộng
            PdfPCell totalLabelCell = new PdfPCell(new Phrase("Tổng cộng", vietnameseBoldFont));
            totalLabelCell.setColspan(2);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totalLabelCell.setPadding(4);
            totalLabelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(totalLabelCell);

            for (SalaryTemplateItemDto cellCode : thutuCell) {
                double totalValue = totalMap.getOrDefault(cellCode.getCode(), 0.0);
                PdfPCell totalCell = new PdfPCell(new Phrase(formatter.format(totalValue), vietnameseBoldFont));
                totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                totalCell.setPadding(4);
                table.addCell(totalCell);
            }

            PdfPCell signatureCell = new PdfPCell(new Phrase(""));
            signatureCell.setPadding(4);
            signatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(signatureCell);

            document.add(table);
            // Thêm phần ký tên
            addSignatureSection(document, null, null);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // Tạo và tính toán phiếu lương
    @Override
    public SalaryResultStaffDto calculateSalaryStaff(SalaryResultStaffDto dto) {
        // Nếu là phiếu lương cũ => Có ID => Thực hiện update phiếu lương
//        if (dto.getId() != null) {
//            return this.update(dto);
//        }
        // Nếu chưa có ID => Tính toán mới phiếu lương
        SalaryResultStaff entity = null;

        SalaryPeriod salaryPeriod = null;
        if (dto.getSalaryPeriod() != null && dto.getSalaryPeriod().getId() != null) {
            salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriod().getId()).orElse(null);
        }

        Staff staff = null;
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }

        SalaryTemplate salaryTemplate = null;
        if (dto.getSalaryTemplate() != null && dto.getSalaryTemplate().getId() != null) {
            salaryTemplate = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId()).orElse(null);
        }

        if (salaryPeriod == null || salaryTemplate == null || staff == null) {
            return null;
        }

        SalaryResult salaryResult = null;
        if (dto.getSalaryResultId() != null) {
            salaryResult = salaryResultRepository.findById(dto.getSalaryResultId()).orElse(null);

            // Bảng lương đã bị khóa
            if (salaryResult != null && salaryResult.getIsLocked() != null && salaryResult.getIsLocked().equals(true)) {
                return null;
            }
        }

        // Code cũ tìm phiếu lương theo nhân viên và kỳ lương
//        if (staff.getId() != null && salaryPeriod != null && salaryPeriod.getId() != null) {
//            List<SalaryResultStaffDto> salaryResultStaffs = salaryResultStaffRepository.getByStaffIdAndPeriodId(staff.getId(), salaryPeriod.getId());
//
//            if (!CollectionUtils.isEmpty(salaryResultStaffs)) {
//                SalaryResultStaffDto salaryResultStaffDto = salaryResultStaffs.get(0);
//                return this.update(salaryResultStaffDto);
//            }
//        }

        // Code mới tìm phiếu lương theo nhân viên, kỳ lương và mẫu bảng lương
        if (staff.getId() != null && salaryPeriod.getId() != null && salaryTemplate.getId() != null) {
            List<SalaryResultStaff> salaryResultStaffs = salaryResultStaffRepository.findByStaffIdPeriodIdAndTemplateId(staff.getId(), salaryPeriod.getId(), salaryTemplate.getId());

            if (!CollectionUtils.isEmpty(salaryResultStaffs)) {
                entity = salaryResultStaffs.get(0);

                entity.setSalaryResult(salaryResult);

                SalaryResultStaffDto salaryResultStaffDto = new SalaryResultStaffDto(entity);

                if (dto.getSalaryResultStaffItems() != null && !dto.getSalaryResultStaffItems().isEmpty()) {
                    salaryResultStaffDto.setSalaryResultStaffItems(dto.getSalaryResultStaffItems());
                    // tính toán lại
                    return this.recalculateSalaryStaff(salaryResultStaffDto);
                } else {
                    this.handleSetOrgStructureInfo(entity, salaryResultStaffDto);
                    return salaryResultStaffDto;
                }
            }
        }

        entity = new SalaryResultStaff();

        entity.setSalaryResult(salaryResult);
        entity.setStaff(staff);
        entity.setSalaryPeriod(salaryPeriod);
        entity.setApprovalStatus(HrConstants.SalaryResulStaffApprovalStatus.NOT_APPROVED_YET.getValue());
        entity.setSalaryTemplate(salaryTemplate);
        entity.setPaidStatus(HrConstants.SalaryResulStaffPaidStatus.UNPAID.getValue());

        Hashtable<String, Double> hashTable = new Hashtable<>();

        // Lấy danh sách cũ thay vì gán mới
        Set<SalaryResultStaffItem> salaryResultStaffItems = new HashSet<SalaryResultStaffItem>();

        if (!CollectionUtils.isEmpty(salaryTemplate.getTemplateItems())) {
            //Tạo hashTable cho toàn bộ thành phần của mẫu bảng lương
            hashTable = this.genHashTable(staff, salaryTemplate, salaryPeriod);

            DecimalFormat df = new DecimalFormat("0.####");
            df.setMaximumFractionDigits(4);

            for (SalaryTemplateItem salaryTemplateItem : salaryTemplate.getTemplateItems()) {
                String templateItemCode = salaryTemplateItem.getCode();
                if (!StringUtils.hasText(templateItemCode)) continue;
                templateItemCode = templateItemCode.trim();

                SalaryResultStaffItem salaryResultStaffItem = new SalaryResultStaffItem();

                salaryResultStaffItem.setSalaryResultStaff(entity);
                salaryResultStaffItem.setSalaryTemplateItem(salaryTemplateItem);
                salaryResultStaffItem.setReferenceDisplayOrder(salaryTemplateItem.getDisplayOrder());
                salaryResultStaffItem.setReferenceCode(templateItemCode);
                salaryResultStaffItem.setReferenceName(salaryTemplateItem.getDisplayName());

                Double result = 0D;
                String calculateFormula = removeCommas(salaryTemplateItem.getFormula());

                if (salaryTemplateItem.getCalculationType().equals(HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue())) {
                    try {
                        result = ExpressionUtil.eval(calculateFormula, hashTable);
                        hashTable.put(templateItemCode, result);
                    } catch (Exception exception) {
//                        exception.printStackTrace();
                        logger.error(exception.getMessage());

                        result = 0D;
                        hashTable.put(templateItemCode, result);
                    }
                } else if (salaryTemplateItem.getCalculationType().equals(HrConstants.SalaryItemCalculationType.THRESHOLD.getValue())) {
                    try {
                        Double inputValue = ExpressionUtil.eval(calculateFormula, hashTable);
                        // Tính lương theo mức ngưỡng cũ
                        //                    result = this.calculateSalaryByConfig(inputValue, salaryTemplateItem, hashTable);
                        // Tính lương theo mức ngưỡng mới (có kết hợp các toán tử so sánh)
                        result = this.calculateSalaryByThresholdsV2(inputValue, salaryTemplateItem, hashTable);

                        hashTable.put(templateItemCode, result);
                    } catch (Exception exception) {
//                        exception.printStackTrace();
                        logger.error(exception.getMessage());

                        result = 0D;
                        hashTable.put(templateItemCode, result);
                    }
                } else {
                    result = hashTable.getOrDefault(templateItemCode, result);
                }

                salaryResultStaffItem.setValue(df.format(result).replace(",", "."));
                salaryResultStaffItems.add(salaryResultStaffItem);
            }
        }

        // Không gán Set mới mà chỉ cập nhật danh sách cũ
        // Nếu danh sách đã tồn tại, clear nó để giữ liên kết Hibernate
        if (entity.getSalaryResultStaffItems() != null) {
            entity.getSalaryResultStaffItems().clear();
            entity.getSalaryResultStaffItems().addAll(salaryResultStaffItems);
        } else {
            entity.setSalaryResultStaffItems(salaryResultStaffItems);
        }


        // Lưu vào database
        entity = salaryResultStaffRepository.save(entity);
//        salaryResultStaffRepository.flush();

        SalaryResultStaffDto response = new SalaryResultStaffDto(entity);

        this.handleSetOrgStructureInfo(entity, response);

        entityManager.flush();
        entityManager.clear();

        return response;
    }


    private void handleSetOrgStructureInfo(SalaryResultStaff entity, SalaryResultStaffDto response) {
        Staff staff = entity.getStaff();
        if (staff != null && staff.getCurrentPositions() != null && !staff.getCurrentPositions().isEmpty()) {
            Position mainPosition = null;

            for (Position position : staff.getCurrentPositions()) {
                if (position != null && position.getIsMain() != null && position.getIsMain().equals(true)) {
                    mainPosition = position;
                    break;
                }
            }

            if (mainPosition != null) {
                response.setMainPosition(mainPosition.getName());

                if (mainPosition.getTitle() != null) {
                    response.setMainPositionTitle(mainPosition.getTitle().getName());
                }

                if (mainPosition.getDepartment() != null) {
                    response.setMainDepartment(mainPosition.getDepartment().getName());
                }

                if (mainPosition.getDepartment() != null && mainPosition.getDepartment().getOrganization() != null) {
                    response.setMainOrganization(mainPosition.getDepartment().getOrganization().getName());
                }
            }
        }
    }

    private Hashtable<String, Double> genHashTable(Staff staff, SalaryTemplate salaryTemplate, SalaryPeriod salaryPeriod) {
        Hashtable<String, Double> hashtable = new Hashtable<>();

        if (staff == null || salaryTemplate == null) {
            return null;
        }

        Set<SalaryTemplateItem> templateItems = salaryTemplate.getTemplateItems();

        if (!CollectionUtils.isEmpty(templateItems)) {
            for (SalaryTemplateItem templateItem : templateItems) {
                Double result = 0D;

                String code = templateItem.getCode();
                if (!StringUtils.hasText(code)) continue;

                code = code.trim();

                // Hard lấy các giá trị tự động
                if (salaryAutoCalculationService.isAutoConnectionCode(code)) {
                    result = salaryAutoCalculationService.detectConstantsAndGetValue(code, salaryPeriod, staff.getId());
                }

                if (templateItem.getCalculationType() != null && templateItem.getCalculationType().equals(HrConstants.SalaryItemCalculationType.FIX.getValue())) {
                    StaffSalaryItemValue staffSalaryItemValue = null;

//                    List<StaffSalaryItemValue> staffSalaryItemValues = staffSalaryItemValueRepository.findByStaffIdAndSalaryTemplateItemId(staff.getId(), templateItem.getId());
                    List<StaffSalaryItemValue> staffSalaryItemValues = new ArrayList<>();
                    if (templateItem.getSalaryItem() != null) {
                        staffSalaryItemValues = staffSalaryItemValueRepository.findCurrentByStaffIdAndSalaryItemId(staff.getId(), templateItem.getSalaryItem().getId());
                    }

                    if (!CollectionUtils.isEmpty(staffSalaryItemValues)) {
                        staffSalaryItemValue = staffSalaryItemValues.get(0);
                    }
                    if (staffSalaryItemValue != null) {
                        if (staffSalaryItemValue.getCalculationType().equals(HrConstants.SalaryItemCalculationType.FIX.getValue())) {
                            result = staffSalaryItemValue.getValue();
                        }
                    }
                } else if (templateItem.getCalculationType().equals(HrConstants.SalaryItemCalculationType.USER_FILL.getValue())) {

                } else if (templateItem.getCalculationType().equals(HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue())) {

                } else if (templateItem.getCalculationType().equals(HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue())) {

                }

                if (code != null && result != null) {
                    hashtable.put(code, result);
                } else if (code != null && result == null) {
                    hashtable.put(code, 0D);
                }
            }
        }
        return hashtable;
    }

    public static String removeCommas(String expression) {
        if (expression == null) {
            return "";
        }
        return expression.replace(",", "");
    }

    private Double calculateSalaryByThresholdsV2(Double inputValue, SalaryTemplateItem salaryTemplateItem, Hashtable<String, Double> hashTable) throws Exception {
        if (salaryTemplateItem == null || salaryTemplateItem.getTemplateItemConfigs() == null || inputValue == null) {
            return 0.0;
        }

        List<SalaryTemplateItemConfig> itemConfigs = new ArrayList<>(salaryTemplateItem.getTemplateItemConfigs());

        // Sắp xếp theo compareOrder tăng dần
        itemConfigs.sort(Comparator.comparing(SalaryTemplateItemConfig::getCompareOrder, Comparator.nullsLast(Integer::compareTo)));

        for (SalaryTemplateItemConfig config : itemConfigs) {
            boolean matched = true;

            boolean hasMin = config.getOperatorMinValue() != null && config.getMinValue() != null;
            boolean hasMax = config.getOperatorMaxValue() != null && config.getMaxValue() != null;

            // Bỏ qua nếu cả min và max đều null
            if (!hasMin && !hasMax) continue;

            if (hasMin) {
                matched = matched && evaluateCondition(inputValue, config.getMinValue(), config.getOperatorMinValue());
            }

            if (hasMax) {
                matched = matched && evaluateCondition(inputValue, config.getMaxValue(), config.getOperatorMaxValue());
            }

            if (matched) {
                if (config.getConfigType() == HrConstants.ConfigType.FIX.getValue()) {
                    return config.getItemValue() != null ? config.getItemValue() : 0D;
                } else if (config.getConfigType() == HrConstants.ConfigType.USING_FORMULA.getValue()) {
                    String calculateFormula = removeCommas(config.getFormula());
                    return ExpressionUtil.eval(calculateFormula, hashTable);
                }
                return 0D;
            }
        }

        return 0.0;
    }

    private boolean evaluateCondition(Double inputValue, Double threshold, Integer operator) {
        if (inputValue == null || threshold == null || operator == null) return true;

        switch (operator) {
            case 1:
                return inputValue.equals(threshold); // EQUALS
            case 2:
                return !inputValue.equals(threshold); // NOT_EQUALS
            case 3:
                return inputValue > threshold; // GREATER_THAN
            case 4:
                return inputValue < threshold; // LESS_THAN
            case 5:
                return inputValue >= threshold; // GREATER_THAN_OR_EQUALS
            case 6:
                return inputValue <= threshold; // LESS_THAN_OR_EQUALS
            default:
                return true; // fallback nếu operator không hợp lệ
        }
    }


    @Override
    public SalaryResultStaffDto recalculateSalaryStaff(SalaryResultStaffDto dto) {
        if (dto == null) return null;

        SalaryResultStaff entity = null;

        if (dto.getId() != null) {
            entity = salaryResultStaffRepository.findById(dto.getId()).orElse(null);
        }

        if (entity == null) {
            return null;
        }

        if (entity.getSalaryResult() != null && entity.getSalaryResult().getIsLocked() != null && entity.getSalaryResult().getIsLocked().equals(true)) {
            logger.info("Bảng lương chưa Phiếu lương đã bị khóa, không được tính toán lại: ID: " + entity.getId());
            return null;
        }

        if (entity.getPaidStatus() == null) {
            entity.setPaidStatus(HrConstants.SalaryResulStaffPaidStatus.UNPAID.getValue());
        }

        Hashtable<String, Double> hashTable = new Hashtable<>();

        for (SalaryResultStaffItemDto itemDto : dto.getSalaryResultStaffItems()) {
            if (itemDto == null || itemDto.getSalaryTemplateItem() == null || itemDto.getValue() == null) continue;

            String code = itemDto.getSalaryTemplateItem().getCode();
            Double result = this.parseDoubleInputValueString(itemDto.getValue());
            hashTable.put(code.trim(), result);
        }

        if (!CollectionUtils.isEmpty(dto.getSalaryResultStaffItems())) {
            for (SalaryResultStaffItemDto itemDto : dto.getSalaryResultStaffItems()) {
                SalaryResultStaffItem item = salaryResultStaffItemRepository.findById(itemDto.getId()).orElse(null);

                if (item == null || item.getSalaryTemplateItem() == null) continue;

                String calculateFormula = removeCommas(item.getSalaryTemplateItem().getFormula());

                String templateItemCode = item.getSalaryTemplateItem().getCode();
                if (!StringUtils.hasText(templateItemCode)) continue;
                templateItemCode = templateItemCode.trim();

                Double result = this.parseDoubleInputValueString(itemDto.getValue());
                hashTable.put(templateItemCode, result);

                if (item.getSalaryTemplateItem().getCalculationType().equals(HrConstants.SalaryItemCalculationType.USING_FORMULA.getValue())) {
                    try {
                        result = ExpressionUtil.eval(calculateFormula, hashTable);
                        hashTable.put(templateItemCode, result);
                    } catch (Exception exception) {
//                        exception.printStackTrace();
                        logger.error(exception.getMessage());

                        result = 0D;
                        hashTable.put(templateItemCode, result);
                    }
                } else if (item.getSalaryTemplateItem().getCalculationType().equals(HrConstants.SalaryItemCalculationType.THRESHOLD.getValue())) {
                    try {
                        Double inputValue = ExpressionUtil.eval(calculateFormula, hashTable);

                        // Tính lương theo mức ngưỡng cũ
//                    result = this.calculateSalaryByConfig(inputValue, item.getSalaryTemplateItem(), hashTable);

                        // Tính lương theo mức ngưỡng mới (có kết hợp các toán tử so sánh)
                        result = this.calculateSalaryByThresholdsV2(inputValue, item.getSalaryTemplateItem(), hashTable);

                        hashTable.put(templateItemCode, result);
                    } catch (Exception exception) {
//                        exception.printStackTrace();
                        logger.error(exception.getMessage());

                        result = 0D;
                        hashTable.put(templateItemCode, result);
                    }
                }

                DecimalFormat df = new DecimalFormat("0.####");
                df.setMaximumFractionDigits(4);
                item.setValue(df.format(result));

                item = salaryResultStaffItemRepository.save(item);
            }
        }

        entity = salaryResultStaffRepository.save(entity);


        SalaryResultStaffDto response = new SalaryResultStaffDto(entity);

        this.handleSetOrgStructureInfo(entity, response);

        entityManager.flush();
        entityManager.clear();

        return response;
    }


    @Override
    public SalaryResultStaffDto handleSetFullSalaryTemplate(SalaryResultStaffDto dto) {
        if (dto == null || dto.getSalaryTemplate() == null || dto.getSalaryTemplate().getId() == null) return null;

        SalaryTemplate salaryTemplate = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId()).orElse(null);
        if (salaryTemplate == null) {
            return dto;
        }

        dto.setSalaryTemplate(new SalaryTemplateDto(salaryTemplate, true));

        return dto;
    }


    @Override
    public Page<SalaryResultStaffDto> pagingSalaryResultStaff(SearchSalaryResultStaffDto searchDto) {
        if (searchDto == null) {
            return null;
        }

        int pageIndex = searchDto.getPageIndex();
        int pageSize = searchDto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        UserDto userDto = userExtService.getCurrentUser();
        Staff currentStaff = userExtService.getCurrentStaffEntity();
        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isManager = RoleUtils.hasRoleHrManager(userDto);
        if (!(isAdmin || isManager)) {
            searchDto.setStaffId(currentStaff.getId());
        }
        if (searchDto.getStaffId() != null) {
            searchDto.setStaffId(searchDto.getStaffId());
        }
        String whereClause = " where (1=1) ";
        if (searchDto.getIsPayslip() != null && searchDto.getIsPayslip().equals(true)) {
            whereClause += " and entity.salaryTemplate.isCreatePayslip = true ";
        }

        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(distinct entity.id) from SalaryResultStaff as entity ";
        String sql = "select distinct new com.globits.salary.dto.SalaryResultStaffDto(entity, true) from SalaryResultStaff as entity ";

        String joinPositionStaff = "";
        boolean hasJoinMainPosition = false;
        if (searchDto.getPositionTitleId() != null || searchDto.getDepartmentId() != null || searchDto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
            hasJoinMainPosition = true;
        }

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            whereClause += " AND (entity.staff.staffCode LIKE :text OR entity.staff.displayName like :text) ";
        }
        if (searchDto.getSalaryResult() != null || searchDto.getSalaryResultId() != null) {
            whereClause += " and (entity.salaryResult.id = :salaryResultId) ";
        }
        if (searchDto.getSalaryPeriod() != null || searchDto.getSalaryPeriodId() != null) {
            whereClause += " and (entity.salaryPeriod.id = :salaryPeriodId) ";
        }
        if (searchDto.getSalaryTemplateId() != null) {
            whereClause += " and (entity.salaryTemplate.id = :salaryTemplateId) ";
        }
        if (searchDto.getStaffId() != null) {
            whereClause += " and (entity.staff.id = :staffId) ";
        }
        if (searchDto.getApprovalStatus() != null) {
            whereClause += " and (entity.approvalStatus = :approvalStatus) ";
        }

        if (hasJoinMainPosition) {
            if (searchDto.getOrganizationId() != null && StringUtils.hasText(searchDto.getOrganizationId().toString())) {
                whereClause += " AND ( pos.department.organization.id  = :organizationId ) ";
            }
            if (searchDto.getDepartmentId() != null && StringUtils.hasText(searchDto.getDepartmentId().toString())) {
                whereClause += " AND ( pos.department.id  = :departmentId ) ";
            }
            if (searchDto.getPositionTitleId() != null && StringUtils.hasText(searchDto.getPositionTitleId().toString())) {
                whereClause += " AND ( pos.title.id  = :positionTitleId ) ";
            }
        }

        sql += joinPositionStaff + whereClause + orderBy;
        sqlCount += joinPositionStaff + whereClause;

        jakarta.persistence.Query query = manager.createQuery(sql, SalaryResultDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            query.setParameter("text", '%' + searchDto.getKeyword() + '%');
            qCount.setParameter("text", '%' + searchDto.getKeyword() + '%');
        }
        if (searchDto.getSalaryResult() != null || searchDto.getSalaryResultId() != null) {
            UUID salaryResultId = null;
            if (searchDto.getSalaryResult() != null) {
                salaryResultId = searchDto.getSalaryResult().getId();
            } else {
                salaryResultId = searchDto.getSalaryResultId();
            }

            query.setParameter("salaryResultId", salaryResultId);
            qCount.setParameter("salaryResultId", salaryResultId);
        }

        if (searchDto.getSalaryPeriod() != null || searchDto.getSalaryPeriodId() != null) {
            UUID salaryPeriodId = null;
            if (searchDto.getSalaryResult() != null) {
                salaryPeriodId = searchDto.getSalaryPeriod().getId();
            } else {
                salaryPeriodId = searchDto.getSalaryPeriodId();
            }

            query.setParameter("salaryPeriodId", salaryPeriodId);
            qCount.setParameter("salaryPeriodId", salaryPeriodId);
        }

        if (searchDto.getSalaryTemplateId() != null) {
            query.setParameter("salaryTemplateId", searchDto.getSalaryTemplateId());
            qCount.setParameter("salaryTemplateId", searchDto.getSalaryTemplateId());
        }
        if (searchDto.getStaffId() != null) {
            query.setParameter("staffId", searchDto.getStaffId());
            qCount.setParameter("staffId", searchDto.getStaffId());
        }
        if (searchDto.getApprovalStatus() != null) {
            query.setParameter("approvalStatus", searchDto.getApprovalStatus());
            qCount.setParameter("approvalStatus", searchDto.getApprovalStatus());
        }

        if (hasJoinMainPosition) {
            if (searchDto.getOrganizationId() != null && StringUtils.hasText(searchDto.getOrganizationId().toString())) {
                query.setParameter("organizationId", searchDto.getOrganizationId());
                qCount.setParameter("organizationId", searchDto.getOrganizationId());
            }
            if (searchDto.getDepartmentId() != null && StringUtils.hasText(searchDto.getDepartmentId().toString())) {
                query.setParameter("departmentId", searchDto.getDepartmentId());
                qCount.setParameter("departmentId", searchDto.getDepartmentId());
            }
            if (searchDto.getPositionTitleId() != null && StringUtils.hasText(searchDto.getPositionTitleId().toString())) {
                query.setParameter("positionTitleId", searchDto.getPositionTitleId());
                qCount.setParameter("positionTitleId", searchDto.getPositionTitleId());
            }
        }


        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;

        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<SalaryResultStaffDto> entities = query.getResultList();
        Page<SalaryResultStaffDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }


    @Override
    public SalaryResultStaffDto getTotalSalaryResultStaff(SearchSalaryResultStaffDto searchDto) {
        if (searchDto == null || searchDto.getSalaryTemplateId() == null) return null;

        SalaryResultStaffDto result = new SalaryResultStaffDto();

        // Xác định người dùng hiện tại
        UserDto userDto = userExtService.getCurrentUser();
        Staff currentStaff = userExtService.getCurrentStaffEntity();
        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isManager = RoleUtils.hasRoleHrManager(userDto);

        // Nếu không có quyền, set staffId hiện tại
        if (!(isAdmin || isManager)) {
            searchDto.setStaffId(currentStaff.getId());
        } else {
            if (searchDto.getSalaryResultStaffIds() == null) {
                return result;
            }
        }
        // Tạo where clause + join điều kiện
        boolean[] hasJoin = new boolean[1];
        String whereClause = buildWhereClauseAndJoin(searchDto, hasJoin);
        String joinClause = hasJoin[0] ? " JOIN Position pos ON pos.isMain = true AND pos.staff.id = s.staff.id " : "";

        // HQL chính
        String hql = """
                   SELECT 
                       i.referenceDisplayOrder, 
                       i.referenceCode, 
                       COALESCE(salaryResultItem.valueType, salaryTemplateItem.valueType),
                       SUM(i.value)
                   FROM SalaryResultStaff s
                   JOIN s.salaryResultStaffItems i
                   LEFT JOIN i.salaryResultItem salaryResultItem
                LEFT JOIN i.salaryTemplateItem salaryTemplateItem
                """ + joinClause + whereClause + """
                    GROUP BY i.referenceDisplayOrder, i.referenceCode, salaryResultItem.valueType, salaryTemplateItem.valueType
                    ORDER BY i.referenceDisplayOrder
                """;

        Query query = manager.createQuery(hql, Object[].class);
        setCommonParameters(query, searchDto);

        List<Object[]> list = query.getResultList();
        List<SalaryResultStaffItemDto> resultList = new ArrayList<>();

        List<SalaryTemplateItem> allTemplateItems = salaryTemplateItemRepository.getAllTemplateItemsOfTemplate(searchDto.getSalaryTemplateId());
        Queue<SalaryTemplateItemDto> templateItemQueue = new LinkedList<>();

        if (allTemplateItems != null && !allTemplateItems.isEmpty()) {
            for (SalaryTemplateItem templateItem : allTemplateItems) {
                SalaryTemplateItemDto templateItemDto = new SalaryTemplateItemDto(templateItem);

                templateItemQueue.add(templateItemDto);
            }
        }

        for (Object[] obj : list) {
            SalaryResultStaffItemDto dto = new SalaryResultStaffItemDto();

            dto.setReferenceDisplayOrder(obj[0] != null ? (Integer) obj[0] : null);
            dto.setReferenceCode(obj[1] != null ? (String) obj[1] : null);
            dto.setValueType(obj[2] != null ? (Integer) obj[2] : null);
            dto.setValue(obj[3] != null ? ((BigDecimal) obj[3]).toPlainString() : null);

            if (!templateItemQueue.isEmpty()) {
                SalaryTemplateItemDto front = templateItemQueue.poll();

                dto.setSalaryTemplateItem(front);
            }

            resultList.add(dto);
        }

        result.setSalaryResultStaffItems(resultList);
        return result;
    }


    public static String buildWhereClauseAndJoin(SearchSalaryResultStaffDto searchDto, boolean[] hasJoinMainPositionOut) {
        StringBuilder whereClause = new StringBuilder(" WHERE (1=1) ");

        if (searchDto.getIsPayslip() != null && searchDto.getIsPayslip()) {
            whereClause.append(" AND s.salaryTemplate.isCreatePayslip = true ");
        }

        if (StringUtils.hasText(searchDto.getKeyword())) {
            whereClause.append(" AND (s.staff.staffCode LIKE :text OR s.staff.displayName LIKE :text) ");
        }

        if (searchDto.getSalaryResult() != null || searchDto.getSalaryResultId() != null) {
            whereClause.append(" AND s.salaryResult.id = :salaryResultId ");
        }

        if (searchDto.getSalaryPeriod() != null || searchDto.getSalaryPeriodId() != null) {
            whereClause.append(" AND s.salaryPeriod.id = :salaryPeriodId ");
        }

        if (searchDto.getSalaryTemplateId() != null) {
            whereClause.append(" AND s.salaryTemplate.id = :salaryTemplateId ");
        }

        if (searchDto.getStaffId() != null) {
            whereClause.append(" AND s.staff.id = :staffId ");
        }

        if (searchDto.getSalaryResultStaffIds() != null && searchDto.getSalaryResultStaffIds().size() > 0) {
            whereClause.append(" AND s.id IN (:salaryResultStaffIds) ");
        }

        if (searchDto.getApprovalStatus() != null) {
            whereClause.append(" AND s.approvalStatus = :approvalStatus ");
        }

        boolean hasJoinMainPosition = false;
        if (searchDto.getPositionTitleId() != null || searchDto.getDepartmentId() != null || searchDto.getOrganizationId() != null) {
            hasJoinMainPosition = true;

            if (searchDto.getOrganizationId() != null) {
                whereClause.append(" AND pos.department.organization.id = :organizationId ");
            }

            if (searchDto.getDepartmentId() != null) {
                whereClause.append(" AND pos.department.id = :departmentId ");
            }

            if (searchDto.getPositionTitleId() != null) {
                whereClause.append(" AND pos.title.id = :positionTitleId ");
            }
        }

        hasJoinMainPositionOut[0] = hasJoinMainPosition;
        return whereClause.toString();
    }

    public static void setCommonParameters(Query query, SearchSalaryResultStaffDto searchDto) {
        if (StringUtils.hasText(searchDto.getKeyword())) {
            query.setParameter("text", "%" + searchDto.getKeyword() + "%");
        }

        if (searchDto.getSalaryResult() != null || searchDto.getSalaryResultId() != null) {
            UUID id = searchDto.getSalaryResult() != null ? searchDto.getSalaryResult().getId() : searchDto.getSalaryResultId();
            query.setParameter("salaryResultId", id);
        }

        if (searchDto.getSalaryPeriod() != null || searchDto.getSalaryPeriodId() != null) {
            UUID id = searchDto.getSalaryPeriod() != null ? searchDto.getSalaryPeriod().getId() : searchDto.getSalaryPeriodId();
            query.setParameter("salaryPeriodId", id);
        }

        if (searchDto.getSalaryTemplateId() != null) {
            query.setParameter("salaryTemplateId", searchDto.getSalaryTemplateId());
        }

        if (searchDto.getStaffId() != null) {
            query.setParameter("staffId", searchDto.getStaffId());
        }
        if (searchDto.getSalaryResultStaffIds() != null && searchDto.getSalaryResultStaffIds().size() > 0) {
            query.setParameter("salaryResultStaffIds", searchDto.getSalaryResultStaffIds());
        }

        if (searchDto.getApprovalStatus() != null) {
            query.setParameter("approvalStatus", searchDto.getApprovalStatus());
        }

        if (searchDto.getOrganizationId() != null) {
            query.setParameter("organizationId", searchDto.getOrganizationId());
        }

        if (searchDto.getDepartmentId() != null) {
            query.setParameter("departmentId", searchDto.getDepartmentId());
        }

        if (searchDto.getPositionTitleId() != null) {
            query.setParameter("positionTitleId", searchDto.getPositionTitleId());
        }
    }


    /**
     * Convert chuỗi số
     * Ví dụ: 31071,43  → 31071.43
     *
     * @param input Chuỗi cần chuyển
     * @return Double hoặc null nếu không convert được
     */
    private Double parseDoubleInputValueString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0.0;
        }

        try {
            // Chuẩn hóa chuỗi: xóa dấu ngăn cách hàng nghìn và đổi dấu thập phân
            String normalized = input.trim()
                    .replace(",", ".");   // đổi dấu , thành . (thập phân)

            return Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            // Ghi log hoặc xử lý lỗi nếu cần
            System.err.println("Lỗi convert chuỗi số: " + input + " - " + e.getMessage());
            return 0.0;
        }
    }

}
