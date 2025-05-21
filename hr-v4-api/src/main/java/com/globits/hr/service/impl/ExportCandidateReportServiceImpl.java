package com.globits.hr.service.impl;

import com.globits.core.repository.*;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Recruitment;
import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.HrTaskHistoryDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchCandidateDto;
import com.globits.hr.dto.search.SearchTaskDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.ExportCandidateReportService;
import com.globits.hr.service.RecruitmentService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.security.repository.UserRepository;
import com.globits.task.dto.KanbanDto;
import jakarta.persistence.Query;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ExportCandidateReportServiceImpl extends GenericServiceImpl<Recruitment, UUID>
        implements ExportCandidateReportService {
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private RecruitmentRepository recruitmentRepository;
    @Autowired
    private PersonAddressRepository personAddressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ReligionRepository religionRepository;
    @Autowired
    private StaffWorkingHistoryRepository staffWorkingHistoryRepository;
    @Autowired
    private EthnicsRepository ethnicsRepository;
    @Autowired
    private AdministrativeUnitRepository administrativeUnitRepository;
    @Autowired
    private HRDepartmentRepository hRDepartmentRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private FileDescriptionRepository fileDescriptionRepository;
    @Autowired
    private PersonCertificateRepository personCertificateRepository;
    @Autowired
    private CertificateRepository certificateRepository;

    @Override
    public Page<CandidateDto> pagingExportCandidate(SearchCandidateDto dto) {
        if (dto == null) {
            return null;
        }
        formalizeSearchObject(dto);

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String sqlCount = "select count(distinct entity.id) from Candidate as entity ";
        String sql = "select distinct new com.globits.hr.dto.CandidateDto(entity) from Candidate as entity ";
        String whereClause = " where (1=1) AND (entity.voided IS NULL OR entity.voided = FALSE) ";
        String orderBy = " ORDER BY entity.modifyDate desc ";
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.displayName LIKE :text OR entity.candidateCode LIKE :text ) ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " and (entity.recruitment.organization.id = :organizationId) ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.recruitmentRequest.hrDepartment.id = :departmentId) ";
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            whereClause += " and (entity.positionTitle.id = :positionTitleId) ";
        }
        if (dto.getRecruitmentRequest() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.recruitmentRequest.id = :recruitmentRequestId) ";
        }
        if (dto.getRecruitmentPlan() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.id = :recruitmentPlanId) ";
        }
        if (dto.getRecruitment() != null) {
            whereClause += " and (entity.recruitment.id = :recruitmentId) ";
        }
        if (dto.getSubmissionDateFrom() != null) {
            whereClause += " and (entity.submissionDate >= :submissionDateFrom) ";
        }
        if (dto.getSubmissionDateTo() != null) {
            whereClause += " and (entity.submissionDate <= :submissionDateTo) ";
        }
        if (dto.getInterviewDateFrom() != null) {
            whereClause += " and (entity.interviewDate >= :interviewDateFrom) ";
        }
        if (dto.getInterviewDateTo() != null) {
            whereClause += " and (entity.interviewDate <= :interviewDateTo) ";
        }
        if (dto.getOnboardDateFrom() != null) {
            whereClause += " and (entity.onboardDate >= :onboardDateFrom) ";
        }
        if (dto.getOnboardDateTo() != null) {
            whereClause += " and (entity.onboardDate <= :onboardDateTo) ";
        }
        if (dto.getOnboardDate() != null) {
            whereClause += " and (Date(entity.onboardDate) = :onboardDate) ";
        }
        if (dto.getSubmissionDate() != null) {
            whereClause += " and (Date(entity.submissionDate) = :submissionDate) ";
        }
        if (dto.getInterviewDate() != null) {
            whereClause += " and (Date(entity.interviewDate) = :interviewDate) ";
        }
        if (dto.getStatus() != null) {
            whereClause += " and (entity.status = :status) ";
        } else {
            whereClause += " and (entity.status in :status) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, CandidateDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            qCount.setParameter("organizationId", dto.getOrganizationId());
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
            qCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitle().getId());
            qCount.setParameter("positionTitleId", dto.getPositionTitle().getId());
        }
        if (dto.getRecruitmentRequest() != null) {
            query.setParameter("recruitmentRequestId", dto.getRecruitmentRequest().getId());
            qCount.setParameter("recruitmentRequestId", dto.getRecruitmentRequest().getId());
        }
        if (dto.getRecruitmentPlan() != null) {
            query.setParameter("recruitmentPlanId", dto.getRecruitmentPlan().getId());
            qCount.setParameter("recruitmentPlanId", dto.getRecruitmentPlan().getId());
        }
        if (dto.getRecruitment() != null) {
            query.setParameter("recruitmentId", dto.getRecruitment().getId());
            qCount.setParameter("recruitmentId", dto.getRecruitment().getId());
        }
        if (dto.getSubmissionDateFrom() != null) {
            query.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
            qCount.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
        }
        if (dto.getSubmissionDateTo() != null) {
            query.setParameter("submissionDateTo", dto.getSubmissionDateTo());
            qCount.setParameter("submissionDateTo", dto.getSubmissionDateTo());
        }
        if (dto.getInterviewDateFrom() != null) {
            query.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
            qCount.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
        }
        if (dto.getInterviewDateTo() != null) {
            query.setParameter("interviewDateTo", dto.getInterviewDateTo());
            qCount.setParameter("interviewDateTo", dto.getInterviewDateTo());
        }
        if (dto.getOnboardDateFrom() != null) {
            query.setParameter("onboardDateFrom", dto.getOnboardDateFrom());
            qCount.setParameter("onboardDateFrom", dto.getOnboardDateFrom());
        }
        if (dto.getOnboardDateTo() != null) {
            query.setParameter("onboardDateTo", dto.getOnboardDateTo());
            qCount.setParameter("onboardDateTo", dto.getOnboardDateTo());
        }
        if (dto.getOnboardDate() != null) {
            query.setParameter("onboardDate", dto.getOnboardDate());
            qCount.setParameter("onboardDate", dto.getOnboardDate());
        }
        if (dto.getSubmissionDate() != null) {
            query.setParameter("submissionDate", dto.getSubmissionDate());
            qCount.setParameter("submissionDate", dto.getSubmissionDate());
        }
        if (dto.getInterviewDate() != null) {
            query.setParameter("interviewDate", dto.getInterviewDate());
            qCount.setParameter("interviewDate", dto.getInterviewDate());
        }
        if (dto.getStatus() != null) {
            query.setParameter("status", dto.getStatus());
            qCount.setParameter("status", dto.getStatus());
        } else {
            List<Integer> statusList = new ArrayList<>() {{
                add(HrConstants.CandidateStatus.PENDING_ASSIGNMENT.getValue());
                add(HrConstants.CandidateStatus.ACCEPTED_ASSIGNMENT.getValue());
                add(HrConstants.CandidateStatus.DECLINED_ASSIGNMENT.getValue());
            }};
            query.setParameter("status", statusList);
            qCount.setParameter("status", statusList);
        }

        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<CandidateDto> entities = query.getResultList();
        Page<CandidateDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    private void formalizeSearchObject(SearchCandidateDto dto) {
        if (dto.getFromDate() != null) {
            dto.setFromDate(DateTimeUtil.getStartOfDay(dto.getFromDate()));
        }
        if (dto.getToDate() != null) {
            dto.setToDate(DateTimeUtil.getEndOfDay(dto.getToDate()));
        }
        if (dto.getSubmissionDateFrom() != null) {
            dto.setSubmissionDateFrom(DateTimeUtil.getStartOfDay(dto.getSubmissionDateFrom()));
        }
        if (dto.getSubmissionDateTo() != null) {
            dto.setSubmissionDateTo(DateTimeUtil.getEndOfDay(dto.getSubmissionDateTo()));
        }
        if (dto.getInterviewDateFrom() != null) {
            dto.setInterviewDateFrom(DateTimeUtil.getStartOfDay(dto.getInterviewDateFrom()));
        }
        if (dto.getInterviewDateTo() != null) {
            dto.setInterviewDateTo(DateTimeUtil.getEndOfDay(dto.getInterviewDateTo()));
        }
    }

    private List<CandidateDto> getDetailInfoOfChosenCandidats(SearchCandidateDto dto) {
        String sql = "select distinct new com.globits.hr.dto.CandidateDto(entity) from Candidate as entity where entity.id in :candidateIds";
        String orderBy = " ORDER BY entity.modifyDate desc ";

        sql += orderBy;

        Query query = manager.createQuery(sql, CandidateDto.class);
        query.setParameter("candidateIds", dto.getCandidateIds());

        List<CandidateDto> result = query.getResultList();

        return result;
    }

    private List<CandidateDto> getListCandidatesByFilter(SearchCandidateDto dto) {
        String whereClause = " where (1=1) " + "and (entity.approvalStatus = "
                + HrConstants.CandidateApprovalStatus.APPROVED.getValue() + ") " + " and (entity.examStatus = "
                + HrConstants.CandidateExamStatus.PASSED.getValue() + ") and (entity.receptionStatus = "
                + HrConstants.CandidateReceptionStatus.RECEPTED.getValue() + ") ";
        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sql = "select distinct new com.globits.hr.dto.CandidateDto(entity) from Candidate as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.displayName LIKE :text OR entity.candidateCode LIKE :text ) ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.recruitmentRequest.hrDepartment.id = :departmentId) ";
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            whereClause += " and (entity.positionTitle.id = :positionTitleId) ";
        }
        if (dto.getRecruitmentRequest() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.recruitmentRequest.id = :recruitmentRequestId) ";
        }
        if (dto.getRecruitmentPlan() != null) {
            whereClause += " and (entity.recruitment.recruitmentPlan.id = :recruitmentPlanId) ";
        }
        if (dto.getRecruitment() != null) {
            whereClause += " and (entity.recruitment.id = :recruitmentId) ";
        }
        if (dto.getSubmissionDateFrom() != null) {
            whereClause += " and (entity.submissionDate >= :submissionDateFrom) ";
        }
        if (dto.getSubmissionDateTo() != null) {
            whereClause += " and (entity.submissionDate <= :submissionDateTo) ";
        }
        if (dto.getInterviewDateFrom() != null) {
            whereClause += " and (entity.interviewDate >= :interviewDateFrom) ";
        }
        if (dto.getInterviewDateTo() != null) {
            whereClause += " and (entity.interviewDate <= :interviewDateTo) ";
        }
        if (dto.getOnboardDateFrom() != null) {
            whereClause += " and (entity.onboardDate >= :onboardDateFrom) ";
        }
        if (dto.getOnboardDateTo() != null) {
            whereClause += " and (entity.onboardDate <= :onboardDateTo) ";
        }
        if (dto.getOnboardDate() != null) {
            whereClause += " and (Date(entity.onboardDate) = :onboardDate) ";
        }
        if (dto.getSubmissionDate() != null) {
            whereClause += " and (Date(entity.submissionDate) = :submissionDate) ";
        }
        if (dto.getInterviewDate() != null) {
            whereClause += " and (Date(entity.interviewDate) = :interviewDate) ";
        }
        if (dto.getOnboardStatus() != null) {
            whereClause += " and (entity.onboardStatus = :onboardStatus) ";
        }

        sql += whereClause + orderBy;

        Query query = manager.createQuery(sql, CandidateDto.class);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getPositionTitle() != null && dto.getPositionTitle().getId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitle().getId());
        }
        if (dto.getRecruitmentRequest() != null) {
            query.setParameter("recruitmentRequestId", dto.getRecruitmentRequest().getId());
        }
        if (dto.getRecruitmentPlan() != null) {
            query.setParameter("recruitmentPlanId", dto.getRecruitmentPlan().getId());
        }
        if (dto.getRecruitment() != null) {
            query.setParameter("recruitmentId", dto.getRecruitment().getId());
        }
        if (dto.getSubmissionDateFrom() != null) {
            query.setParameter("submissionDateFrom", dto.getSubmissionDateFrom());
        }
        if (dto.getSubmissionDateTo() != null) {
            query.setParameter("submissionDateTo", dto.getSubmissionDateTo());
        }
        if (dto.getInterviewDateFrom() != null) {
            query.setParameter("interviewDateFrom", dto.getInterviewDateFrom());
        }
        if (dto.getInterviewDateTo() != null) {
            query.setParameter("interviewDateTo", dto.getInterviewDateTo());
        }
        if (dto.getOnboardDateFrom() != null) {
            query.setParameter("onboardDateFrom", dto.getOnboardDateFrom());
        }
        if (dto.getOnboardDateTo() != null) {
            query.setParameter("onboardDateTo", dto.getOnboardDateTo());
        }
        if (dto.getOnboardDate() != null) {
            query.setParameter("onboardDate", dto.getOnboardDate());
        }
        if (dto.getSubmissionDate() != null) {
            query.setParameter("submissionDate", dto.getSubmissionDate());
        }
        if (dto.getInterviewDate() != null) {
            query.setParameter("interviewDate", dto.getInterviewDate());
        }
        if (dto.getOnboardStatus() != null) {
            query.setParameter("onboardStatus", dto.getOnboardStatus());
        }

        List<CandidateDto> result = query.getResultList();

        return result;
    }

    // this function only used for exporting excel => get all data
    @Override
    public List<CandidateDto> getListExportCandidatesByFilter(SearchCandidateDto dto) {
        if (dto == null) {
            return null;
        }
        formalizeSearchObject(dto);

        if (dto.getCandidateIds() != null && dto.getCandidateIds().size() > 0)
            return getDetailInfoOfChosenCandidats(dto);
        return getListCandidatesByFilter(dto);
    }

    @Override
    public ByteArrayResource exportExcelCandidatesByFilter(List<CandidateDto> dataList) throws IOException {
//        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
//            // Tạo các hàng cột dữ liệu
//            XSSFRow tableDataRow;
//            if (data != null && !data.isEmpty()) {
//                for (int i = 0; i < data.size(); i++) {
//                    tableDataRow = sheet.createRow(i + 1);
//                    CandidateDto dataItem = data.get(i);
//
//                    if (dataItem != null) {
//                        if (dataItem.getCandidateCode() != null) {
//                            tableDataRow.createCell(0).setCellValue(dataItem.getCandidateCode());
//                        } else {
//                            tableDataRow.createCell(0).setCellValue("");
//                        }
//
//                        if (dataItem.getLastName() != null) {
//                            tableDataRow.createCell(1).setCellValue(dataItem.getLastName());
//                        } else {
//                            tableDataRow.createCell(1).setCellValue("");
//                        }
//
//                        if (dataItem.getFirstName() != null) {
//                            tableDataRow.createCell(2).setCellValue(dataItem.getFirstName());
//                        } else {
//                            tableDataRow.createCell(2).setCellValue("");
//                        }
//
//                        if (dataItem.getGender() != null) {
//                            String value = "";
//                            if (dataItem.getGender().equals("M"))
//                                value = "Nam";
//                            else if (dataItem.getGender().equals("F"))
//                                value = "Nữ";
//
//                            tableDataRow.createCell(3).setCellValue(value);
//                        } else {
//                            tableDataRow.createCell(3).setCellValue("");
//                        }
//
//                        if (dataItem.getBirthDate() != null) {
//                            tableDataRow.createCell(4).setCellValue(
//                                    ExportExcelUtil.convertTimestampToDate(dataItem.getBirthDate().getTime()));
//                        } else {
//                            tableDataRow.createCell(4).setCellValue("");
//                        }
//
//                        if (dataItem.getNationality() != null) {
//                            tableDataRow.createCell(5).setCellValue(dataItem.getNationality().getName());
//                        } else {
//                            tableDataRow.createCell(5).setCellValue("");
//                        }
//
//                        if (dataItem.getNativeVillage() != null) {
//                            tableDataRow.createCell(6).setCellValue(dataItem.getNativeVillage().getName());
//                        } else {
//                            tableDataRow.createCell(6).setCellValue("");
//                        }
//
//                        if (dataItem.getPhoneNumber() != null) {
//                            tableDataRow.createCell(7).setCellValue(dataItem.getPhoneNumber());
//                        } else {
//                            tableDataRow.createCell(7).setCellValue("");
//                        }
//
//                        if (dataItem.getEmail() != null) {
//                            tableDataRow.createCell(8).setCellValue(dataItem.getEmail());
//                        } else {
//                            tableDataRow.createCell(8).setCellValue("");
//                        }
//
//                        if (dataItem.getIdNumber() != null) {
//                            tableDataRow.createCell(9).setCellValue(dataItem.getIdNumber());
//                        } else {
//                            tableDataRow.createCell(9).setCellValue("");
//                        }
//
//                        if (dataItem.getSubmissionDate() != null) {
//                            tableDataRow.createCell(10).setCellValue(
//                                    ExportExcelUtil.convertTimestampToDate(dataItem.getSubmissionDate().getTime()));
//                        } else {
//                            tableDataRow.createCell(10).setCellValue("");
//                        }
//
//                        if (dataItem.getRecruitment() != null) {
//                            tableDataRow.createCell(11).setCellValue(dataItem.getRecruitment().getName());
//                        } else {
//                            tableDataRow.createCell(11).setCellValue("");
//                        }
//
//                        if (dataItem.getRecruitment() != null && dataItem.getRecruitment().getHrDepartmentCS() != null) {
//                            tableDataRow.createCell(12)
//                                    .setCellValue(dataItem.getRecruitment().getHrDepartmentCS().getName());
//                        } else {
//                            tableDataRow.createCell(12).setCellValue("");
//                        }
//
//                        if (dataItem.getPosition() != null) {
//                            tableDataRow.createCell(13).setCellValue(dataItem.getPosition().getName());
//                        } else {
//                            tableDataRow.createCell(13).setCellValue("");
//                        }
//
//                        if (dataItem.getInterviewDate() != null) {
//                            tableDataRow.createCell(14).setCellValue(
//                                    ExportExcelUtil.convertTimestampToDate(dataItem.getInterviewDate().getTime()));
//                        } else {
//                            tableDataRow.createCell(14).setCellValue("");
//                        }
//
//                        if (dataItem.getOnboardDate() != null) {
//                            tableDataRow.createCell(15).setCellValue(
//                                    ExportExcelUtil.convertTimestampToDate(dataItem.getOnboardDate().getTime()));
//                        } else {
//                            tableDataRow.createCell(15).setCellValue("");
//                        }
//
//                        if (dataItem.getOnboardStatus() != null) {
//                            tableDataRow.createCell(16)
//                                    .setCellValue(this.getOnboardStatusExcelName(dataItem.getOnboardStatus()));
//                        } else {
//                            tableDataRow.createCell(16).setCellValue("");
//                        }
//
//                        if (dataItem.getRefusalReason() != null) {
//                            tableDataRow.createCell(17).setCellValue(dataItem.getRefusalReason());
//                        } else {
//                            tableDataRow.createCell(17).setCellValue("");
//                        }
//
//                    }
//
//                    sheet.autoSizeColumn(i);
//                }
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                workbook.write(out);
//                out.close();
//                return new ByteArrayResource(out.toByteArray());
//            }
//        }


        //////////////////////////////////////////////////

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        XSSFFont fontBold = workbook.createFont();
        fontBold.setBold(true);
        fontBold.setFontHeight(10);

        XSSFFont fontBoldTitle = workbook.createFont();
        fontBoldTitle.setBold(true);
        fontBoldTitle.setFontHeight(11);

        XSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setFont(fontBoldTitle);

        XSSFCellStyle tableHeadCellStyle = workbook.createCellStyle();
        tableHeadCellStyle.setFont(fontBoldTitle);
        tableHeadCellStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeadCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFRow row = sheet.createRow(0);
        XSSFCell cell;

        cell = row.createCell(0);
        cell.setCellValue("Mã ứng viên");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Họ");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Tên ứng viên");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Giới tính");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Ngày sinh");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Quốc tịch");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Nguyên quán/Nơi sinh");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(7);
        cell.setCellValue("SĐT");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(8);
        cell.setCellValue("Email");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(9);
        cell.setCellValue("Số CMND/CCCD");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(10);
        cell.setCellValue("Ngày nộp hồ sơ");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(11);
        cell.setCellValue("Đợt tuyển dụng");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(12);
        cell.setCellValue("Phòng ban ứng tuyển");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(13);
        cell.setCellValue("Vị trí ứng tuyển");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(14);
        cell.setCellValue("Ngày thi tuyển");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(15);
        cell.setCellValue("Ngày nhận việc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(16);
        cell.setCellValue("Trạng thái nhận việc");
        cell.setCellStyle(tableHeadCellStyle);

        cell = row.createCell(17);
        cell.setCellValue("Lý do từ chối (đối với ứng viên không nhận việc)");
        cell.setCellStyle(tableHeadCellStyle);

        for (int col = 0; col <= 17; col++) {
            sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col));
        }

        XSSFCellStyle tableDataRowStyle = workbook.createCellStyle();
        tableDataRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableDataRowStyle.setAlignment(HorizontalAlignment.CENTER);
        XSSFRow tableDataRow;

        if (dataList != null && !dataList.isEmpty()) {
            SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm dd-MM-yyyy");
            SimpleDateFormat formatDateWork = new SimpleDateFormat("dd-MM-yyyy");
            int increaseRow = 2;
            for (int i = 0; i < dataList.size(); i++) {
                XSSFCell cellData;
                CandidateDto data = dataList.get(i);
                tableDataRow = sheet.createRow(increaseRow);

                cellData = tableDataRow.createCell(0);
                cellData.setCellValue(i + 1);
                cellData.setCellStyle(tableDataRowStyle);

                if (data.getCandidateCode() != null) {
                    cellData = tableDataRow.createCell(0);
                    cellData.setCellValue(data.getCandidateCode());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getLastName() != null) {
                    cellData = tableDataRow.createCell(1);
                    cellData.setCellValue(data.getLastName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getFirstName() != null) {
                    cellData = tableDataRow.createCell(2);
                    cellData.setCellValue(data.getFirstName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getGender() != null) {
                    String value = "";
                    if (data.getGender().equals("M"))
                        value = "Nam";
                    else if (data.getGender().equals("F"))
                        value = "Nữ";

                    cellData = tableDataRow.createCell(3);
                    cellData.setCellValue(value);
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getBirthDate() != null) {
                    cellData = tableDataRow.createCell(4);
                    cellData.setCellValue(formatDate.format(data.getBirthDate()));
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getNationality() != null) {
                    cellData = tableDataRow.createCell(5);
                    cellData.setCellValue(data.getNationality().getName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getNativeVillage() != null) {
                    cellData = tableDataRow.createCell(6);
                    cellData.setCellValue(data.getNativeVillage().getName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getPhoneNumber() != null) {
                    cellData = tableDataRow.createCell(7);
                    cellData.setCellValue(data.getPhoneNumber());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getEmail() != null) {
                    cellData = tableDataRow.createCell(8);
                    cellData.setCellValue(data.getEmail());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getIdNumber() != null) {
                    cellData = tableDataRow.createCell(9);
                    cellData.setCellValue(data.getIdNumber());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getSubmissionDate() != null) {
                    cellData = tableDataRow.createCell(10);
                    cellData.setCellValue(formatDate.format(data.getSubmissionDate()));
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getRecruitment() != null) {
                    cellData = tableDataRow.createCell(11);
                    cellData.setCellValue(data.getRecruitment().getName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getRecruitment() != null && data.getRecruitment().getHrDepartmentCS() != null) {
                    cellData = tableDataRow.createCell(12);
                    cellData.setCellValue(data.getRecruitment().getHrDepartmentCS().getName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getPositionTitle() != null) {
                    cellData = tableDataRow.createCell(13);
                    cellData.setCellValue(data.getPositionTitle().getName());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getInterviewDate() != null) {
                    cellData = tableDataRow.createCell(14);
                    cellData.setCellValue(formatDate.format(data.getInterviewDate()));
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getOnboardDate() != null) {
                    cellData = tableDataRow.createCell(15);
                    cellData.setCellValue(formatDate.format(data.getOnboardDate()));
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getOnboardStatus() != null) {
                    cellData = tableDataRow.createCell(16);
                    cellData.setCellValue(this.getOnboardStatusExcelName(data.getOnboardStatus()));
                    cellData.setCellStyle(tableDataRowStyle);
                }

                if (data.getRefusalReason() != null) {
                    cellData = tableDataRow.createCell(17);
                    cellData.setCellValue(data.getRefusalReason());
                    cellData.setCellStyle(tableDataRowStyle);
                }

                increaseRow++;
            }
            for (int i = 1; i <= 17; i++) {
//                sheet.setColumnWidth(i, 20);
                sheet.setColumnWidth(i, 22 * 256);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            return new ByteArrayResource(out.toByteArray());
        }
        return null;
    }

    private String getOnboardStatusExcelName(Integer onboardStatus) {
        if (onboardStatus == null)
            return "";
        if (onboardStatus.equals(HrConstants.CandidateOnboardStatus.WAITING.getValue()))
            return "Chờ nhận việc";
        if (onboardStatus.equals(HrConstants.CandidateOnboardStatus.NOT_COME.getValue()))
            return "Không nhận việc";
        if (onboardStatus.equals(HrConstants.CandidateOnboardStatus.ONBOARDED.getValue()))
            return "Đã nhận việc";
        return "";
    }
}

