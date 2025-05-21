package com.globits.hr.service.impl;

import com.globits.core.domain.FileDescription;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.core.repository.FileDescriptionRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.importExcel.HrOrganizationImport;
import com.globits.hr.dto.importExcel.HrOrganizationImportResult;
import com.globits.hr.dto.search.SearchStaffLabourAgreementDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.PositionTitleService;
import com.globits.hr.service.StaffLabourAgreementService;
import com.globits.hr.service.StaffService;
import com.globits.hr.utils.ExcelUtils;
import com.globits.salary.domain.SalaryArea;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.domain.SalaryUnit;
import com.globits.salary.repository.SalaryAreaRepository;
import com.globits.salary.repository.SalaryTemplateRepository;
import com.globits.salary.repository.SalaryUnitRepository;
import com.globits.security.domain.User;

import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Service
public class StaffLabourAgreementServiceImpl extends GenericServiceImpl<StaffLabourAgreement, UUID>
        implements StaffLabourAgreementService {
    private static final Logger logger = LoggerFactory.getLogger(StaffLabourAgreementServiceImpl.class);

    @Autowired
    private StaffLabourAgreementRepository staffLabourAgreementRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private StaffService staffService;
    @Autowired
    private LabourAgreementTypeRepository agreementTypeRepository;
    @Autowired
    private SalaryAreaRepository salaryAreaRepository;
    @Autowired
    private SalaryUnitRepository salaryUnitRepository;
    @Autowired
    private StaffLabourAgreementAttachmentRepository staffLabourAgreementAttachmentRepository;
    @Autowired
    private FileDescriptionRepository fileDescriptionRepository;
    @Autowired
    private SalaryTemplateRepository salaryTemplateRepository;
    @Autowired
    private ContractTypeRepository contractTypeRepository;
    @Autowired
    private HrOrganizationRepository hrOrganizationRepository;
    @Autowired
    private PositionRepository positionRepository;

    @jakarta.annotation.Resource
    private ResourceLoader resourceLoader;
    @Autowired
    private PositionTitleService positionTitleService;

    @Autowired
    private LabourAgreementTypeRepository labourAgreementTypeRepository;

    @Override
    public StaffLabourAgreementDto getById(UUID id) {
        if (id == null)
            return null;
        StaffLabourAgreement entity = staffLabourAgreementRepository.findById(id).orElse(null);
        if (entity == null)
            return null;
        // return all detail info of agreement
        return new StaffLabourAgreementDto(entity, true);
    }

    @Override
    @Transactional
    public StaffLabourAgreementDto saveOrUpdate(StaffLabourAgreementDto dto) {
        if (dto == null || dto.getStaff() == null || dto.getStaff().getId() == null)
            return null;
        Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        if (staff == null)
            return null;
//        if (dto.getAgreementStatus()!=null && dto.getAgreementStatus() == HrConstants.StaffLabourAgreementStatus.TERMINATED.getValue()) {
////            staff.getStatus().setCode(HrConstants.EmployeeStatusCodeEnum.CONTRACT_TERMINATED.getValue());
//            staffRepository.save(staff);
//        }
        StaffLabourAgreement entity = null;
        if (dto.getId() != null) {
            entity = staffLabourAgreementRepository.findById(dto.getId()).orElse(null);
            if (entity == null)
                return null;
        }
        if (entity == null) {
            entity = new StaffLabourAgreement();
        }
        entity.setAgreementStatus(dto.getAgreementStatus());
        entity.setDurationMonths(dto.getDurationMonths());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setSignedDate(dto.getSignedDate());
        entity.setStaff(staff);

        // cac truong moi
        entity.setLabourAgreementNumber(dto.getLabourAgreementNumber());
        entity.setWorkingPlace(dto.getWorkingPlace());
        if (dto.getSalaryArea() != null && dto.getSalaryArea().getId() != null) {
            SalaryArea salaryArea = salaryAreaRepository.findById(dto.getSalaryArea().getId()).orElse(null);
            if (salaryArea == null)
                return null;
            entity.setSalaryArea(salaryArea);
        }
        if (dto.getSalaryUnit() != null && dto.getSalaryUnit().getId() != null) {
            SalaryUnit salaryUnit = salaryUnitRepository.findById(dto.getSalaryUnit().getId()).orElse(null);
            if (salaryUnit == null)
                return null;
            entity.setSalaryUnit(salaryUnit);
        }

        entity.setWorkingHour(dto.getWorkingHour());
        entity.setWorkingHourWeekMin(dto.getWorkingHourWeekMin());
        entity.setWorkingHourWeekMax(dto.getWorkingHourWeekMax());
        entity.setSalary(dto.getSalary());
        if (dto.getContractOrganization() != null && dto.getContractOrganization().getId() != null) {
            HrOrganization contractOrganization = hrOrganizationRepository.findById(dto.getContractOrganization().getId()).orElse(null);
            entity.setContractOrganization(contractOrganization);
        } else {
            entity.setContractOrganization(null);
        }
        if (dto.getWorkOrganization() != null && dto.getWorkOrganization().getId() != null) {
            HrOrganization workOrganization = hrOrganizationRepository.findById(dto.getWorkOrganization().getId()).orElse(null);
            entity.setWorkOrganization(workOrganization);
        } else {
            entity.setWorkOrganization(null);
        }

        LabourAgreementType labourAgreementType = null;
        if (dto.getLabourAgreementType() != null && dto.getLabourAgreementType().getId() != null) {
            labourAgreementType = agreementTypeRepository.findById(dto.getLabourAgreementType().getId()).orElse(null);
        }
        if (dto.getContractTypeCode() != null && labourAgreementType == null) {
            List<LabourAgreementType> list = this.agreementTypeRepository.findByCode(dto.getContractTypeCode());
            if (list != null && !list.isEmpty()) {
                labourAgreementType = list.get(0);
            }
        }
        entity.setLabourAgreementType(labourAgreementType);

        if (dto.getContractType() != null) {
            ContractType contractType = contractTypeRepository.findById(dto.getContractType().getId()).orElse(null);
            if (contractType != null) {
                entity.setContractType(contractType);
            }
        }

        SalaryTemplate salaryTemplate = null;
        if (dto.getSalaryTemplate() != null && dto.getSalaryTemplate().getId() != null) {
            salaryTemplate = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId()).orElse(null);
        }
        entity.setSalaryTemplate(salaryTemplate);

        // BHXH
        if (dto.getHasSocialIns() != null && dto.getHasSocialIns().equals(true)) {
            entity.setSocialInsuranceNumber(dto.getSocialInsuranceNumber());
            entity.setHasSocialIns(dto.getHasSocialIns());
            if (dto.getSalaryInsuranceUnit() != null && dto.getSalaryInsuranceUnit().getId() != null) {
                SalaryUnit salaryInsuranceUnit = salaryUnitRepository.findById(dto.getSalaryInsuranceUnit().getId())
                        .orElse(null);

                if (salaryInsuranceUnit == null)
                    return null;
                entity.setSalaryInsuranceUnit(salaryInsuranceUnit);
            } else {
                entity.setSalaryInsuranceUnit(null);
            }

            entity.setInsuranceSalary(dto.getInsuranceSalary());
            entity.setStaffSocialInsurancePercentage(dto.getStaffSocialInsurancePercentage());
            entity.setStaffHealthInsurancePercentage(dto.getStaffHealthInsurancePercentage());
            entity.setStaffUnemploymentInsurancePercentage(dto.getStaffUnemploymentInsurancePercentage());

            entity.setOrgSocialInsurancePercentage(dto.getOrgSocialInsurancePercentage());
            entity.setOrgHealthInsurancePercentage(dto.getOrgHealthInsurancePercentage());
            entity.setOrgUnemploymentInsurancePercentage(dto.getOrgUnemploymentInsurancePercentage());
            entity.setPaidStatus(dto.getPaidStatus());
            entity.setInsuranceStartDate(dto.getInsuranceStartDate());
            entity.setInsuranceEndDate(dto.getInsuranceEndDate());
            if (dto.getInsuranceSalary() != null) {
                Double insuranceSalary = dto.getInsuranceSalary();
                // ===== Nhân viên đóng =====
                Double employeeSocialInsuranceAmount = dto.getStaffSocialInsurancePercentage() != null
                        ? insuranceSalary * dto.getStaffSocialInsurancePercentage() / 100 : 0.0;
                Double employeeHealthInsuranceAmount = dto.getStaffHealthInsurancePercentage() != null
                        ? insuranceSalary * dto.getStaffHealthInsurancePercentage() / 100 : 0.0;
                Double employeeUnemploymentInsuranceAmount = dto.getStaffUnemploymentInsurancePercentage() != null
                        ? insuranceSalary * dto.getStaffUnemploymentInsurancePercentage() / 100 : 0.0;
                Double employeeTotalInsuranceAmount = employeeSocialInsuranceAmount + employeeHealthInsuranceAmount + employeeUnemploymentInsuranceAmount;

                entity.setStaffTotalInsuranceAmount(employeeTotalInsuranceAmount);

                // ===== Công ty đóng =====
                Double orgSocialInsuranceAmount = dto.getOrgSocialInsurancePercentage() != null
                        ? insuranceSalary * dto.getOrgSocialInsurancePercentage() / 100 : 0.0;
                Double orgHealthInsuranceAmount = dto.getOrgHealthInsurancePercentage() != null
                        ? insuranceSalary * dto.getOrgHealthInsurancePercentage() / 100 : 0.0;
                Double orgUnemploymentInsuranceAmount = dto.getOrgUnemploymentInsurancePercentage() != null
                        ? insuranceSalary * dto.getOrgUnemploymentInsurancePercentage() / 100 : 0.0;
                Double orgTotalInsuranceAmount = orgSocialInsuranceAmount + orgHealthInsuranceAmount + orgUnemploymentInsuranceAmount;

                entity.setOrgTotalInsuranceAmount(orgTotalInsuranceAmount);

                entity.setTotalInsuranceAmount(orgTotalInsuranceAmount + employeeTotalInsuranceAmount);
                if (dto.getPaidStatus() != null) {
                    entity.setPaidStatus(dto.getPaidStatus());
                } else {
                    entity.setPaidStatus(HrConstants.StaffSocialInsurancePaidStatus.PAID.getValue());
                }
            }
        } else {
            // this labour agreement doesn't have social insurance => clear all releated
            // fields to null

            entity.setHasSocialIns(false);
            entity.setSocialInsuranceNumber(null);
            entity.setStartInsDate(null);
            entity.setSalaryInsuranceUnit(null);
            entity.setInsuranceSalary(null);
            entity.setStaffSocialInsurancePercentage(null);
            entity.setStaffHealthInsurancePercentage(null);
            entity.setStaffUnemploymentInsurancePercentage(null);
            entity.setStaffTotalInsuranceAmount(null);
            entity.setOrgSocialInsurancePercentage(null);
            entity.setOrgHealthInsurancePercentage(null);
            entity.setOrgUnemploymentInsurancePercentage(null);
            entity.setOrgTotalInsuranceAmount(null);
            entity.setPaidStatus(null);
            entity.setTotalInsuranceAmount(null);
            entity.setInsuranceStartDate(null);
            entity.setInsuranceEndDate(null);
        }

        // save multiple labour agreement's attachments in this labour agreement
        Set<StaffLabourAgreementAttachment> attachments = new HashSet<>();
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            for (FileDescriptionDto file : dto.getFiles()) {
                if (file == null || file.getId() == null)
                    continue;

                // find whether the relationship of this attachment and this file is existed in
                // database first
                List<StaffLabourAgreementAttachment> existedRelationships = staffLabourAgreementAttachmentRepository
                        .findByFileIdAndAgreementId(file.getId(), entity.getId());
                StaffLabourAgreementAttachment fileRelaEntity = null;
                if (existedRelationships != null && !existedRelationships.isEmpty()) {
                    // in this section, relationship is already existed
                    fileRelaEntity = existedRelationships.get(0);
                }
                if (fileRelaEntity == null) {
                    // in this section, relationship is NOT EXISTED => CREATE NEW
                    fileRelaEntity = new StaffLabourAgreementAttachment();

                    FileDescription fileEntity = fileDescriptionRepository.findById(file.getId()).orElse(null);
                    if (fileEntity == null)
                        continue;
                    fileRelaEntity.setFile(fileEntity);
                    fileRelaEntity.setStaffLabourAgreement(entity);
                }

                attachments.add(fileRelaEntity);
            }
        }
        if (entity.getAttachments() == null)
            entity.setAttachments(attachments);
        else {
            entity.getAttachments().clear();
            entity.getAttachments().addAll(attachments);
        }

        entity = this.staffLabourAgreementRepository.save(entity);
        return new StaffLabourAgreementDto(entity, true);
    }

    @Override
    public StaffLabourAgreementDto deleteById(UUID id) {
        if (id == null)
            return null;
        StaffLabourAgreement entity = staffLabourAgreementRepository.findById(id).orElse(null);
        if (entity == null)
            return null;

        staffLabourAgreementRepository.delete(entity);

        return new StaffLabourAgreementDto(entity);
    }

    @Override
    public boolean deleteMultiple(List<UUID> ids) {
        for (UUID id : ids) {
            StaffLabourAgreementDto deletedItem = this.deleteById(id);
            if (deletedItem == null)
                return false;
        }

        return true;
    }

    @Override
    public Page<StaffLabourAgreementDto> pagingLabourAgreement(SearchStaffLabourAgreementDto dto) {
        if (dto == null) {
            return Page.empty();
        }

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "select count(distinct entity.id) from StaffLabourAgreement as entity ";
        String sql = "select distinct new com.globits.hr.dto.StaffLabourAgreementDto(entity) from StaffLabourAgreement as entity ";
        String joinPositionStaff = "";

        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
        }

        whereClause = this.addWhereCondition(whereClause, dto);

        sql += joinPositionStaff + whereClause + orderBy;
        sqlCount += joinPositionStaff + whereClause;

        Query query = manager.createQuery(sql, StaffLabourAgreementDto.class);
        Query qCount = manager.createQuery(sqlCount);

        this.setParameterForQuery(query, qCount, dto);

        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        List<StaffLabourAgreementDto> entities = query.getResultList();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        return new PageImpl<>(entities, pageable, count);
    }

    private String addWhereCondition(String whereClause, SearchStaffLabourAgreementDto dto) {
        if (Boolean.TRUE.equals(dto.getHasSocialIns())) {
            // lay hop dong da ki hoac cham dut
            whereClause += " AND entity.agreementStatus IN (2,3) ";
            whereClause += " AND (entity.hasSocialIns IS NOT NULL AND entity.hasSocialIns = true) ";

            if (StringUtils.hasText(dto.getKeyword())) {
                whereClause += " AND (entity.staff.staffCode LIKE :text OR entity.staff.displayName LIKE :text) ";
            }
            // giao trong khoang thoi gian tim kiem
            if (dto.getInsuranceStartDate() != null && dto.getInsuranceEndDate() != null) {
                whereClause += " AND (" +
                        " (entity.insuranceStartDate IS NULL OR DATE(:insuranceEndDate) >= DATE(entity.insuranceStartDate)) AND " +
                        " (entity.insuranceEndDate IS NULL OR DATE(:insuranceStartDate) <= DATE(entity.insuranceEndDate))" +
                        ") ";
            } else if (dto.getInsuranceStartDate() != null) {
                whereClause += " AND (" +
                        " entity.insuranceEndDate IS NULL OR DATE(:insuranceStartDate) <= DATE(entity.insuranceEndDate) " +
                        ") ";
            } else if (dto.getInsuranceEndDate() != null) {
                whereClause += " AND (" +
                        " entity.insuranceStartDate IS NULL OR DATE(:insuranceEndDate) >= DATE(entity.insuranceStartDate) " +
                        ") ";
            }
        } else {
            if (StringUtils.hasText(dto.getKeyword())) {
                whereClause += " AND (entity.staff.staffCode LIKE :text OR entity.staff.displayName LIKE :text " +
                        "OR entity.labourAgreementNumber LIKE :text) ";
            }
        }
        if (dto.getFromDate() != null) {
            whereClause += " AND (entity.startDate >= :fromDate) ";
        }
        if (dto.getToDate() != null) {
            whereClause += " AND (entity.endDate <= :toDate) ";
        }
        if (dto.getContractOrganization() != null && dto.getContractOrganization().getId() != null) {
            whereClause += " AND (entity.contractOrganization.id = :contractOrganizationId) ";
        }
        if (dto.getWorkOrganization() != null && dto.getWorkOrganization().getId() != null) {
            whereClause += " AND (entity.workOrganization.id = :workOrganizationId) ";
        }
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            whereClause += " AND (entity.staff.id = :staffId) ";
        }
        if (dto.getStaffOrganization() != null && dto.getStaffOrganization().getId() != null) {
            whereClause += " AND (positions.hrDepartment.organization.id = :staffOrganizationId) ";
        }
        if (dto.getStaffDepartment() != null && dto.getStaffDepartment().getId() != null) {
            whereClause += " AND (positions.hrDepartment.id = :staffDepartmentId) ";
        }
        if (dto.getStaffPosition() != null && dto.getStaffPosition().getId() != null) {
            whereClause += " AND (positions.position.id = :staffPositionId) ";
        }
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                whereClause += " AND ( pos.department.organization.id = :organizationId ) ";
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                whereClause += " AND ( pos.department.id = :departmentId ) ";
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                whereClause += " AND ( pos.title.id = :positionTitleId ) ";
            }
        }

        if (dto.getAgreementStatus() != null) {
            if (dto.getAgreementStatus().equals(HrConstants.StaffLabourAgreementStatus.UNSIGNED.getValue())) {
                whereClause += " AND entity.agreementStatus = 1 ";
            } else if (dto.getAgreementStatus().equals(HrConstants.StaffLabourAgreementStatus.SIGNED.getValue())) {
                whereClause += " AND entity.agreementStatus = 2 ";
            } else if (dto.getAgreementStatus().equals(HrConstants.StaffLabourAgreementStatus.TERMINATED.getValue())) {
                whereClause += " AND entity.agreementStatus = 3 ";
            }
        }

        if (Boolean.TRUE.equals(dto.getIsOverdueContract())) {
            whereClause += " AND entity.agreementStatus = 2 " +
                    " AND entity.endDate IS NOT NULL AND entity.endDate >= CURRENT_DATE " +
                    " AND DATEDIFF(entity.endDate, CURRENT_DATE) <= :expiryDays ";
        }

        return whereClause;
    }

    private void setParameterForQuery(Query query, Query queryCount, SearchStaffLabourAgreementDto dto) {
        if (dto.getFromDate() != null) {
            query.setParameter("fromDate", dto.getFromDate());
            if (queryCount != null) queryCount.setParameter("fromDate", dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            query.setParameter("toDate", dto.getToDate());
            if (queryCount != null) queryCount.setParameter("toDate", dto.getToDate());
        }
        if (dto.getContractOrganization() != null && dto.getContractOrganization().getId() != null) {
            query.setParameter("contractOrganizationId", dto.getContractOrganization().getId());
            if (queryCount != null)
                queryCount.setParameter("contractOrganizationId", dto.getContractOrganization().getId());
        }
        if (dto.getWorkOrganization() != null && dto.getWorkOrganization().getId() != null) {
            query.setParameter("workOrganizationId", dto.getWorkOrganization().getId());
            if (queryCount != null) queryCount.setParameter("workOrganizationId", dto.getWorkOrganization().getId());
        }
        if (StringUtils.hasText(dto.getKeyword())) {
            String keyword = '%' + dto.getKeyword() + '%';
            query.setParameter("text", keyword);
            if (queryCount != null) queryCount.setParameter("text", keyword);
        }
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            query.setParameter("staffId", dto.getStaff().getId());
            if (queryCount != null) queryCount.setParameter("staffId", dto.getStaff().getId());
        }
        if (dto.getOrganizationId() != null) {
            query.setParameter("organizationId", dto.getOrganizationId());
            if (queryCount != null) queryCount.setParameter("organizationId", dto.getOrganizationId());
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("departmentId", dto.getDepartmentId());
            if (queryCount != null) queryCount.setParameter("departmentId", dto.getDepartmentId());
        }
        if (dto.getPositionTitleId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitleId());
            if (queryCount != null) queryCount.setParameter("positionTitleId", dto.getPositionTitleId());
        }
        if (Boolean.TRUE.equals(dto.getIsOverdueContract())) {
            Integer expiryDays = dto.getContractPreExpiryDays() != null
                    ? dto.getContractPreExpiryDays()
                    : HrConstants.CONTRACT_PRE_EXPIRY_DAYS;
            query.setParameter("expiryDays", expiryDays);
            if (queryCount != null) queryCount.setParameter("expiryDays", expiryDays);
        }
        if (Boolean.TRUE.equals(dto.getHasSocialIns())) {
            if (dto.getInsuranceStartDate() != null) {
                query.setParameter("insuranceStartDate", dto.getInsuranceStartDate());
                if (queryCount != null) queryCount.setParameter("insuranceStartDate", dto.getInsuranceStartDate());
            }
            if (dto.getInsuranceEndDate() != null) {
                query.setParameter("insuranceEndDate", dto.getInsuranceEndDate());
                if (queryCount != null) queryCount.setParameter("insuranceEndDate", dto.getInsuranceEndDate());
            }
        }
    }

    @Override
    public StaffLabourAgreementDto saveAgreement(StaffLabourAgreementDto agreementDto, UUID id) {
        Staff staff = null;
        LabourAgreementType agreementType = null;
        if (agreementDto != null && agreementDto.getStaff() != null && agreementDto.getStaff().getId() != null) {
            staff = this.staffRepository.getOne(agreementDto.getStaff().getId());
        }
        if (agreementDto != null && agreementDto.getStaffCode() != null) {
            List<Staff> list = this.staffRepository.getByCode(agreementDto.getStaffCode());
            if (list != null && list.size() > 0) {
                staff = list.get(0);
            }
        }
        if (staff == null) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User modifiedUser;
        LocalDateTime currentDate = LocalDateTime.now();
        String currentUserName = "Unknown User";
        if (authentication != null) {
            modifiedUser = (User) authentication.getPrincipal();
            currentUserName = modifiedUser.getUsername();
        }
        StaffLabourAgreement entity = null;
        if (id != null) {
            Optional<StaffLabourAgreement> optional = staffLabourAgreementRepository.findById(id);
            if (optional.isPresent()) {
                entity = optional.get();
            }
        } else if (agreementDto.getId() != null) {
            Optional<StaffLabourAgreement> optional = staffLabourAgreementRepository.findById(agreementDto.getId());
            if (optional.isPresent()) {
                entity = optional.get();
            }
        }
        if (entity == null) {
            entity = new StaffLabourAgreement();
            entity.setCreateDate(currentDate);
            entity.setCreatedBy(currentUserName);
        }
        entity.setModifyDate(currentDate);
        entity.setModifiedBy(currentUserName);

        if (agreementDto.getStartDate() != null)
            entity.setStartDate(agreementDto.getStartDate());

        if (agreementDto.getEndDate() != null)
            entity.setEndDate(agreementDto.getEndDate());

        if (agreementDto.getSignedDate() != null)
            entity.setSignedDate(agreementDto.getSignedDate());

        entity.setStaff(staff);

        if (agreementDto.getLabourAgreementType() != null && agreementDto.getLabourAgreementType().getId() != null) {
            agreementType = this.agreementTypeRepository.getOne(agreementDto.getLabourAgreementType().getId());
        }
        if (agreementDto.getContractTypeCode() != null) {
            List<LabourAgreementType> list = this.agreementTypeRepository
                    .findByCode(agreementDto.getContractTypeCode());
            if (list != null && list.size() > 0) {
                agreementType = list.get(0);
            }

        }
        entity.setLabourAgreementType(agreementType);
        entity = staffLabourAgreementRepository.save(entity);
        return new StaffLabourAgreementDto(entity);
    }

    @Override
    public XWPFDocument generateDocx(StaffLabourAgreementDto agreementDto) throws IOException {
        if (agreementDto == null) {
            throw new IllegalArgumentException("StaffLabourAgreementDto không thể null");
        }

        // Xác định loại hợp đồng và template tương ứng
        Set<String> contractTypes = Set.of("TC4", "TC10", "TC5", "HĐTV");
        String contractCode = agreementDto.getContractType() != null && agreementDto.getContractType().getCode() != null
                ? agreementDto.getContractType().getCode() : "";

        String HDLD = "";
        boolean isProbationary = false;
        if (contractTypes.contains(contractCode)) {
            HDLD = "LabourAgreement/ProbationaryContract.docx";
        } else {
            HDLD = "LabourAgreement/LaborContract.docx";
        }

        // Đọc template
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(HDLD);
        if (inputStream == null) {
            throw new IOException("Không tìm thấy template " + HDLD);
        }

        XWPFDocument document = new XWPFDocument(inputStream);
        inputStream.close();

        // Khởi tạo các định dạng và biến cần thiết
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String signedDateYear = "";
        if (agreementDto.getSignedDate() != null) {
            signedDateYear = String.valueOf(agreementDto.getSignedDate().getYear());
        }
        Map<String, String> replacements = new HashMap<>();

        // Xử lý thông tin cơ bản - ngày tháng
        String replaceStartDate = agreementDto.getStartDate() != null ? convertDate(agreementDto.getStartDate()) : "";
        String replaceEndDate = agreementDto.getEndDate() != null ? convertDate(agreementDto.getEndDate()) : "";
        String replaceSignedDate = convertDate(agreementDto.getSignedDate() != null ? agreementDto.getSignedDate() : null);

        // Xử lý thông tin lương
        Double salary = agreementDto.getSalary();
        long salaryValue = (salary != null) ? salary.longValue() : 0L;
        String replaceSalary = String.format("%,d", salaryValue);
        String replaceSalaryString = convertMoneyToWords(salaryValue);

        // Xử lý thông tin công ty và hợp đồng
        String replaceContractType = agreementDto.getContractType() != null && agreementDto.getContractType().getName() != null
                ? agreementDto.getContractType().getName() : "";
        String replaceContractOrganization = agreementDto.getContractOrganization() != null && agreementDto.getContractOrganization().getName() != null
                ? agreementDto.getContractOrganization().getName() : "";

        String replaceWorkingPlace = agreementDto.getWorkingPlace() != null ? agreementDto.getWorkingPlace() : "";

        // Thêm các thông tin cơ bản vào map replacements
        replacements.put("replaceStartDate", replaceStartDate);
        replacements.put("replaceEndDate", replaceEndDate);
        replacements.put("replaceSignedDate", replaceSignedDate);
        replacements.put("replaceSalary", replaceSalary);
        replacements.put("replaceSalaryString", replaceSalaryString);
        replacements.put("replaceContractType", replaceContractType);
        replacements.put("replaceContractOrganization", replaceContractOrganization);
        replacements.put("replaceWorkingPlace", replaceWorkingPlace);
        if (agreementDto.getStartDate() != null && agreementDto.getEndDate() != null) {
            Date startDate = agreementDto.getStartDate();
            Date endDate = agreementDto.getEndDate();

            long diffInMillies = endDate.getTime() - startDate.getTime();
            long daysBetween = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            replacements.put("replaceDaysBetween", String.valueOf(daysBetween));
        }

        // Xử lý thông tin nhân viên
        if (agreementDto.getStaff() != null && agreementDto.getStaff().getId() != null) {
            StaffDto staff = new StaffDto(staffRepository.findById(agreementDto.getStaff().getId()).orElse(null));
            // Số hợp đồng
            String nameOrganization = "";
            if (agreementDto.getContractOrganization() != null && agreementDto.getContractOrganization().getName() != null) {
                nameOrganization = agreementDto.getContractOrganization().getName();
            }
            String replaceNumberContract = staff.getStaffCode() != null ? generateContractNumber(staff.getStaffCode(), signedDateYear, nameOrganization, isProbationary) : "";

            // Địa chỉ đầy đủ
            String replaceFullAddressStaff = buildFullAddress(staff);

            // Thông tin cá nhân khác
            String replacePhoneNumberStaff = staff.getPhoneNumber() != null ? staff.getPhoneNumber() : "";
            String replaceStaffDisplayName = staff.getDisplayName() != null ? staff.getDisplayName() : "";
            String replaceGender = staff.getGender() != null ? ("F".equals(staff.getGender()) ? "Nữ" : "Nam") : "";

            // Quốc tịch
            String replaceCountry = "";
            if (staff.getNationality() != null && staff.getNationality().getName() != null) {
                replaceCountry = staff.getNationality().getName();
            }

            // Thông tin giấy tờ
            String replaceIdNumber = StringUtils.hasText(staff.getPersonalIdentificationNumber()) ? staff.getPersonalIdentificationNumber().trim() : "";
            String replaceBirthDay = staff.getBirthDate() != null ? dateFormat.format(staff.getBirthDate()) : "";
            String replaceIssueDate = staff.getPersonalIdentificationIssueDate() != null ? dateFormat.format(staff.getPersonalIdentificationIssueDate()) : "";
            String replaceIssueBy = staff.getPersonalIdentificationIssuePlace() != null ? staff.getPersonalIdentificationIssuePlace() : "";
            String replacePositionTitleStaff = "";
            String replaceRankTitleStaff = "";

            if (staff.getMainPositionId() != null) {
                Position position = positionRepository.findById(staff.getMainPositionId()).orElse(null);
                if (position != null && position.getTitle() != null) {
                    replacePositionTitleStaff = position.getTitle().getName() != null ?
                            position.getTitle().getName() : "";

                    if (position.getTitle().getRankTitle() != null) {
                        replaceRankTitleStaff = position.getTitle().getRankTitle().getName() != null ?
                                position.getTitle().getRankTitle().getName() : "";
                    }
                }
            }


            Set<PositionTitle> positionTitles = positionTitleService.getMainPositionByStaffId(staff.getId());
            String replaceWorkingMission = ""; // Mặc định là chuỗi rỗng

            if (positionTitles != null && !positionTitles.isEmpty()) {
                StringBuilder builder = new StringBuilder();

                for (PositionTitle positionTitle : positionTitles) {
                    String description = positionTitle.getDescription();
                    if (StringUtils.hasText(description)) {
                        List<String> descriptions = extractTextNodes(description);
                        if (descriptions != null && !descriptions.isEmpty()) {
                            for (String item : descriptions) {
                                if (item != null && !item.trim().isEmpty()) {
                                    builder.append(" - ").append(item.trim()).append("\n");
                                }
                            }
                        }
                    }
                }

                if (builder.length() > 0) {
                    replaceWorkingMission = "\n" + "-" + builder.toString().trim();
                }
            }
            replacements.put("replaceWorkingMission", replaceWorkingMission);
            // Thêm thông tin nhân viên vào map replacements
            replacements.put("replaceNumberContract", replaceNumberContract);
            replacements.put("replaceFullAddressStaff", replaceFullAddressStaff);
            replacements.put("replacePhoneNumberStaff", replacePhoneNumberStaff);
            replacements.put("replaceStaffDisplayName", replaceStaffDisplayName);
            replacements.put("replaceGender", replaceGender);
            replacements.put("replaceCountry", replaceCountry);
            replacements.put("replaceIdNumber", replaceIdNumber);
            replacements.put("replaceBirthDay", replaceBirthDay);
            replacements.put("replaceIssueDate", replaceIssueDate);
            replacements.put("replaceIssueBy", replaceIssueBy);
            replacements.put("replacePositionTitleStaff", replacePositionTitleStaff);
            replacements.put("replaceRankTitleStaff", replaceRankTitleStaff);
        } else {
            // Đặt giá trị mặc định cho tất cả trường hợp staff null
            replacements.put("replaceNumberContract", "");
            replacements.put("replaceFullAddressStaff", "");
            replacements.put("replacePhoneNumberStaff", "");
            replacements.put("replaceStaffDisplayName", "");
            replacements.put("replaceGender", "");
            replacements.put("replaceCountry", "");
            replacements.put("replaceIdNumber", "");
            replacements.put("replaceBirthDay", "");
            replacements.put("replaceIssueDate", "");
            replacements.put("replaceIssueBy", "");
            replacements.put("replacePositionTitleStaff", "");
            replacements.put("replaceRankTitleStaff", "");
        }

        // Xử lý thông tin tổ chức
        String replaceCompanySignsTheContract = "";
        String replaceTaxCode = "";
        String replaceFullAddress = "";
        String replacePhoneNumber = "";
        String replaceLawRepresentative = "";
        String replaceLawRepresentativePositionTitle = "";
//        String replaceAuthorizedRepresentative = "";
//        String replaceAuthorizedRepresentativePositionTitle = "";

        if (agreementDto.getContractOrganization() != null && agreementDto.getContractOrganization().getId() != null) {
            HrOrganizationDto contractOrganization = new HrOrganizationDto(hrOrganizationRepository.findById(agreementDto.getContractOrganization().getId()).orElse(null));
            // Tên công ty ký hợp đồng
            replaceCompanySignsTheContract = contractOrganization.getName() != null ?
                    contractOrganization.getName() : "";

            // Mã số thuế
            if (contractOrganization.getTaxCode() != null) {
                replaceTaxCode = contractOrganization.getTaxCode();
            }
            // Địa chỉ đầy đủ của tổ chức ký hơp đồng
            StringBuilder fullAddress = new StringBuilder();
            if (contractOrganization.getAddressDetail() != null) {
                fullAddress.append(contractOrganization.getAddressDetail());
            }
            if (contractOrganization.getAdministrativeUnit() != null && contractOrganization.getAdministrativeUnit().getName() != null) {
                if (!fullAddress.isEmpty()) fullAddress.append(", ");
                fullAddress.append(contractOrganization.getAdministrativeUnit().getName());
            }
            if (contractOrganization.getDistrict() != null && contractOrganization.getDistrict().getName() != null) {
                if (!fullAddress.isEmpty()) fullAddress.append(", ");
                fullAddress.append(contractOrganization.getDistrict().getName());
            }
            if (contractOrganization.getProvince() != null && contractOrganization.getProvince().getName() != null) {
                if (!fullAddress.isEmpty()) fullAddress.append(", ");
                fullAddress.append(contractOrganization.getProvince().getName());
            }
            replaceFullAddress = fullAddress.toString();


            // Thông tin người đại diện
            if (contractOrganization.getRepresentative() != null && contractOrganization.getRepresentative().getId() != null) {
                StaffDto representative = new StaffDto(staffRepository.findById(contractOrganization.getRepresentative().getId()).orElse(null));

                // Số điện thoại
                replacePhoneNumber = representative.getPhoneNumber() != null ?
                        representative.getPhoneNumber() : "";

                // Tên người đại diện
                replaceLawRepresentative = representative.getDisplayName() != null ?
                        representative.getDisplayName() : "";

                // Chức vụ người đại diện
                if (representative.getMainPositionId() != null) {
                    Position position = positionRepository.findById(representative.getMainPositionId()).orElse(null);
                    replaceLawRepresentativePositionTitle = "";
                    if (position != null && position.getTitle() != null) {
                        replaceLawRepresentativePositionTitle = position.getTitle().getName() != null ?
                                position.getTitle().getName() : "";
                    }
                }
            }
        }

        // Thêm thông tin tổ chức vào map replacements
        replacements.put("replaceCompanySignsTheContract",
                replaceCompanySignsTheContract != null ? replaceCompanySignsTheContract.toUpperCase() : "");
        replacements.put("replaceTaxCode", replaceTaxCode);
        replacements.put("replaceFullAddress", replaceFullAddress);
        replacements.put("replacePhoneNumber", replacePhoneNumber);
        replacements.put("replaceLawRepresentative", replaceLawRepresentative);
        replacements.put("replaceLawRepresentativePositionTitle", replaceLawRepresentativePositionTitle);
        replacements.put("replaceEmployer", !replaceLawRepresentative.isEmpty() ? replaceLawRepresentative.toUpperCase() : "");

        // Thay thế tất cả placeholder trong document
        List<String> replaceBold = Arrays.asList("replaceCompanySignsTheContract", "replaceEmployer");
        replacePlaceholdersInDocument(document, replacements, replaceBold);

        return document;
    }

    public static List<String> extractTextNodes(String html) {
        List<String> result = new ArrayList<>();
        if (!StringUtils.hasText(html)) return result;

        Pattern pattern = Pattern.compile(">([^<>]+)<");
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String text = matcher.group(1).trim();
            if (!text.isEmpty()) {
                result.add(text);
            }
        }

        return result;
    }


    private String generateContractNumber(String staffCode, String signedDateYear, String companyName, boolean isProbationary) {
        // Xử lý mã nhân viên
        String processedStaffCode = "000000"; // fallback nếu null

        if (staffCode != null && !staffCode.isEmpty()) {
            if (staffCode.length() > 6) {
                processedStaffCode = staffCode.substring(staffCode.length() - 6);
            } else {
                processedStaffCode = staffCode;
            }
        }

        // Xử lý tên công ty để lấy 3 ký tự đầu của 3 từ cuối
        String companyPrefix = "";
        if (companyName != null && !companyName.trim().isEmpty()) {
            String[] words = companyName.trim().split("\\s+");

            if (words.length >= 3) {
                StringBuilder prefix = new StringBuilder();
                for (int i = words.length - 3; i < words.length; i++) {
                    String word = words[i];
                    if (!word.isEmpty()) {
                        prefix.append(Character.toUpperCase(word.charAt(0)));
                    }
                }
                if (!prefix.isEmpty()) {
                    companyPrefix = prefix.toString();
                }
            } else {
                StringBuilder prefix = new StringBuilder();
                for (String word : words) {
                    if (!word.isEmpty()) {
                        prefix.append(Character.toUpperCase(word.charAt(0)));
                    }
                }
                if (!prefix.isEmpty()) {
                    companyPrefix = prefix.toString();
                }
            }
        }

        // Ghép mã hợp đồng
        String contractType = isProbationary ? "HĐTV" : "HĐLĐ";
        if (!companyPrefix.isEmpty()) {
            return processedStaffCode + "/" + signedDateYear + "/" + contractType + "-" + companyPrefix;
        }
        return processedStaffCode + "/" + signedDateYear + "/" + contractType;
    }


    private String buildFullAddress(StaffDto staff) {
        StringBuilder fullAddress = new StringBuilder();
        if (staff.getPermanentResidence() != null) {
            fullAddress.append(staff.getPermanentResidence());
        }
        if (staff.getAdministrativeunit() != null && staff.getAdministrativeunit().getName() != null) {
            if (!fullAddress.isEmpty()) fullAddress.append(", ");
            fullAddress.append(staff.getAdministrativeunit().getName());
        }
        if (staff.getDistrict() != null && staff.getDistrict().getName() != null) {
            if (!fullAddress.isEmpty()) fullAddress.append(", ");
            fullAddress.append(staff.getDistrict().getName());
        }
        if (staff.getProvince() != null && staff.getProvince().getName() != null) {
            if (!fullAddress.isEmpty()) fullAddress.append(", ");
            fullAddress.append(staff.getProvince().getName());
        }
        return fullAddress.toString();
    }

    private void replacePlaceholdersInDocument(XWPFDocument document, Map<String, String> replacements, List<String> replaceBold) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceInParagraph(paragraph, replacements, replaceBold);
        }

        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceInParagraph(paragraph, replacements, replaceBold);
                    }
                }
            }
        }
    }

    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> replacements, List<String> replaceBold) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            if (run.getText(0) != null) {
                fullText.append(run.getText(0));
            }
        }

        String text = fullText.toString();

        boolean hasPlaceholder = replacements.keySet().stream().anyMatch(k -> text.contains("{{" + k + "}}"));
        if (!hasPlaceholder) return;

        for (int i = runs.size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }

        int currentIndex = 0;
        while (currentIndex < text.length()) {
            int nextPlaceholderStart = text.indexOf("{{", currentIndex);

            if (nextPlaceholderStart == -1) {
                String remainingText = text.substring(currentIndex);
                if (!remainingText.isEmpty()) {
                    XWPFRun run = paragraph.createRun();
                    run.setText(remainingText);
                    run.setFontFamily("Times New Roman");
                    run.setFontSize(13);
                }
                break;
            }

            if (nextPlaceholderStart > currentIndex) {
                String beforeText = text.substring(currentIndex, nextPlaceholderStart);
                XWPFRun beforeRun = paragraph.createRun();
                beforeRun.setText(beforeText);
                beforeRun.setFontFamily("Times New Roman");
                beforeRun.setFontSize(13);
            }

            int nextPlaceholderEnd = text.indexOf("}}", nextPlaceholderStart);
            if (nextPlaceholderEnd == -1) break;

            String placeholderKey = text.substring(nextPlaceholderStart + 2, nextPlaceholderEnd);
            String replacement = replacements.getOrDefault(placeholderKey, "");

            XWPFRun replaceRun = paragraph.createRun();
            replaceRun.setText(replacement);
            replaceRun.setFontFamily("Times New Roman");
            replaceRun.setFontSize(13);
            if (replaceBold.contains(placeholderKey)) {
                replaceRun.setBold(true);
            }

            currentIndex = nextPlaceholderEnd + 2;
        }
    }

    public static String convertDate(Date date) {
        if (date == null) return "ngày ... tháng ... năm ....";
        SimpleDateFormat outputFormat = new SimpleDateFormat("'ngày' dd 'tháng' MM 'năm' yyyy");

        return outputFormat.format(date);
    }

    public static String convertMoneyToWords(long amount) {
        String[] unitWords = {"", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ"};
        if (amount == 0) {
            return "Không đồng";
        }

        // Handle negative numbers
        if (amount < 0) {
            return "Âm " + convertMoneyToWords(-amount);
        }

        String result = "";
        int unitIndex = 0;

        // Break down the amount into blocks of three digits
        while (amount > 0) {
            long block = amount % 1000;
            if (block > 0) {
                String temp = readThreeDigits(block);
                result = temp + (unitIndex > 0 ? " " + unitWords[unitIndex] + " " : "") + result;
            }
            amount = amount / 1000;
            unitIndex++;
        }

        // Normalize the result string
        result = result.trim();

        // Capitalize first letter
        result = Character.toUpperCase(result.charAt(0)) + result.substring(1);

        // Add currency unit
        return result + " đồng";
    }

    private static String readThreeDigits(long number) {
        String[] numberWords = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        String result = "";
        int hundreds = (int) (number / 100);
        int tens = (int) ((number % 100) / 10);
        int ones = (int) (number % 10);

        if (hundreds == 0 && tens == 0 && ones == 0) {
            return "";
        }

        // Read hundreds
        if (hundreds > 0) {
            result += numberWords[hundreds] + " trăm ";
        }

        // Read tens
        if (tens > 0) {
            if (tens == 1) {
                result += "mười ";
            } else {
                result += numberWords[tens] + " mươi ";
            }
        } else if (hundreds > 0 && ones > 0) {
            result += "lẻ ";
        }

        // Read ones
        if (ones > 0) {
            // Special cases
            if (tens > 1 && ones == 1) {
                result += "mốt";
            } else if (tens >= 1 && ones == 5) {
                result += "lăm";
            } else {
                result += numberWords[ones];
            }
        }

        return result;
    }


    @Override
    public StaffLabourAgreementDto getTotalHasSocialIns(SearchStaffLabourAgreementDto dto) {
        if (dto.getHasSocialIns() != null && dto.getHasSocialIns()) {
            StaffLabourAgreementDto sumIns = this.getTotalLabourAgreement(dto);
            return sumIns;
        }
        return new StaffLabourAgreementDto();
    }

    /*
     * logic of whereClause extend pagingLabourAgreement
     */
    public List<StaffLabourAgreementDto> getListLabourAgreement(SearchStaffLabourAgreementDto dto) {
        if (dto == null) {
            return Collections.emptyList();
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.createDate ";

        String sql = "select distinct new com.globits.hr.dto.StaffLabourAgreementDto(entity) from StaffLabourAgreement as entity ";

        String joinPositionStaff = "";
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
        }
        whereClause = this.addWhereCondition(whereClause, dto);

        sql += joinPositionStaff + whereClause + orderBy;

        Query query = manager.createQuery(sql, StaffLabourAgreementDto.class);

        this.setParameterForQuery(query, null, dto);

        return query.getResultList();
    }

    /*
     * logic of whereClause extend pagingLabourAgreement
     */
    public StaffLabourAgreementDto getTotalLabourAgreement(SearchStaffLabourAgreementDto dto) {
        if (dto == null) {
            return new StaffLabourAgreementDto();
        }
        // đã kí hợp đồng
        String whereClause = " where (1=1) ";

        String sql = "SELECT new com.globits.hr.dto.StaffLabourAgreementDto( "
                + "SUM(entity.staffTotalInsuranceAmount), "
                + "SUM(entity.orgTotalInsuranceAmount), "
                + "SUM(entity.insuranceSalary)) "
                + "FROM StaffLabourAgreement as entity ";

        String joinPositionStaff = "";
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
        }

        whereClause = this.addWhereCondition(whereClause, dto);

        sql += joinPositionStaff + whereClause;

        Query query = manager.createQuery(sql, StaffLabourAgreementDto.class);

        this.setParameterForQuery(query, null, dto);

        StaffLabourAgreementDto result = (StaffLabourAgreementDto) query.getSingleResult();

        return result != null ? result : new StaffLabourAgreementDto();
    }


    @Override
    public Workbook handleExcel(SearchStaffLabourAgreementDto dto) {
        if (dto == null) {
            return null;
        }
        dto.setHasSocialIns(true);
        String templatePath = "Empty.xlsx";
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (fileInputStream == null) {
                throw new IOException("File '" + templatePath + "' không tìm thấy trong classpath");
            }
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            configExcelStyle(workbook);

            List<StaffLabourAgreementDto> listStaffLabourAgreement = this.getListLabourAgreement(dto);

            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.createRow(0);

            List<String> headers = Arrays.asList(
                    "Mã nhân viên",
                    "Tên nhân viên",
                    //"Công ty/tổ chức",
                    "Phòng ban",
                    //"Vị trí/chức vụ",
                    "Mức lương đóng BHXH",
                    "Tỷ lệ cá nhân đóng (%)",
                    "Số tiền cá nhân đóng (VNĐ)",
                    "Tỷ lệ đơn vị đóng (%)",
                    "Số tiền đơn vị đóng (VNĐ)",
                    "Tỷ lệ đóng phí công đoàn (%)",
                    "Số tiền đóng phí công đoàn (VNĐ)",
                    "Tổng (VNĐ)"
            );

            for (int i = 0; i < headers.size(); i++) {
                Cell headerCell = headerRow.createCell(i);
                headerCell.setCellValue(headers.get(i));
            }

            int rowIndex = 1;

            if (listStaffLabourAgreement != null && !listStaffLabourAgreement.isEmpty()) {
                for (StaffLabourAgreementDto staffLabourAgreementDto : listStaffLabourAgreement) {
                    if (staffLabourAgreementDto != null) {
                        Row dataRow = sheet.createRow(rowIndex); // Tạo một hàng mới
                        if (staffLabourAgreementDto.getStaff() != null && staffLabourAgreementDto.getStaff().getStaffCode() != null) {
                            int indexCell = 0;
                            Cell staffCodeCell1 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell2 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell3 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell4 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell5 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell6 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell7 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell8 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell9 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell10 = dataRow.createCell(indexCell++);
                            Cell staffCodeCell11 = dataRow.createCell(indexCell++);
                            staffCodeCell1.setCellValue(staffLabourAgreementDto.getStaff().getStaffCode());

                            if (staffLabourAgreementDto.getStaff().getDisplayName() != null) {
                                staffCodeCell2.setCellValue(staffLabourAgreementDto.getStaff().getDisplayName());
                            }
                            if (staffLabourAgreementDto.getStaff().getDepartment() != null && staffLabourAgreementDto.getStaff().getDepartment().getName() != null) {
                                staffCodeCell3.setCellValue(staffLabourAgreementDto.getStaff().getDepartment().getName());
                            }

//                            if (staffLabourAgreementDto.getSalaryInsurance() != null) {
//                                staffCodeCell4.setCellValue(staffLabourAgreementDto.getSalaryInsurance());
//                            }
//                            if (staffLabourAgreementDto.getStaffPercentage() != null) {
//                                staffCodeCell5.setCellValue(staffLabourAgreementDto.getStaffPercentage());
//                            }
//                            if (staffLabourAgreementDto.getStaffInsuranceAmount() != null) {
//                                staffCodeCell6.setCellValue(staffLabourAgreementDto.getStaffInsuranceAmount());
//                            }
//                            if (staffLabourAgreementDto.getOrgPercentage() != null) {
//                                staffCodeCell7.setCellValue(staffLabourAgreementDto.getOrgPercentage());
//                            }
//                            if (staffLabourAgreementDto.getOrgInsuranceAmount() != null) {
//                                staffCodeCell8.setCellValue(staffLabourAgreementDto.getOrgInsuranceAmount());
//                            }
//                            if (staffLabourAgreementDto.getUnionDuesPercentage() != null) {
//                                staffCodeCell9.setCellValue(staffLabourAgreementDto.getUnionDuesPercentage());
//                            }
//                            if (staffLabourAgreementDto.getUnionDuesAmount() != null) {
//                                staffCodeCell10.setCellValue(staffLabourAgreementDto.getUnionDuesAmount());
//                            }
                            if (staffLabourAgreementDto.getTotalInsuranceAmount() != null) {
                                staffCodeCell11.setCellValue(staffLabourAgreementDto.getTotalInsuranceAmount());
                            }
                        }
                        rowIndex++;
                    }
                }
                // hàng tổng cộng cuối
                rowIndex++;
                StaffLabourAgreementDto sumIns = this.getTotalLabourAgreement(dto);
                if (sumIns != null) {
                    Row dataRow = sheet.createRow(rowIndex);
                    int indexCell = 0;
                    Cell staffCodeCell1 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell2 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell3 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell4 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell5 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell6 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell7 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell8 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell9 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell10 = dataRow.createCell(indexCell++);
                    Cell staffCodeCell11 = dataRow.createCell(indexCell++);

                    staffCodeCell2.setCellValue("Tổng cộng");

//                    if (sumIns.getSalaryInsurance() != null) {
//                        staffCodeCell4.setCellValue(sumIns.getSalaryInsurance());
//                    }
//                    if (sumIns.getStaffPercentage() != null) {
//                        staffCodeCell5.setCellValue(sumIns.getStaffPercentage());
//                    }
//                    if (sumIns.getStaffInsuranceAmount() != null) {
//                        staffCodeCell6.setCellValue(sumIns.getStaffInsuranceAmount());
//                    }
//                    if (sumIns.getOrgPercentage() != null) {
//                        staffCodeCell7.setCellValue(sumIns.getOrgPercentage());
//                    }
//                    if (sumIns.getOrgInsuranceAmount() != null) {
//                        staffCodeCell8.setCellValue(sumIns.getOrgInsuranceAmount());
//                    }
//                    if (sumIns.getUnionDuesPercentage() != null) {
//                        staffCodeCell9.setCellValue(sumIns.getUnionDuesPercentage());
//                    }
//                    if (sumIns.getUnionDuesAmount() != null) {
//                        staffCodeCell10.setCellValue(sumIns.getUnionDuesAmount());
//                    }
                    if (sumIns.getTotalInsuranceAmount() != null) {
                        staffCodeCell11.setCellValue(sumIns.getTotalInsuranceAmount());
                    }
                }
            }

            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            return workbook;
        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<StaffLabourAgreementDto> getAllStaffLabourAgreementWithSearch(SearchStaffLabourAgreementDto dto) {
        return this.getListLabourAgreement(dto);
    }

    public static void configExcelStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Times New Roman");
        font.setBold(true);

        cellStyle.setWrapText(true);

        Font fontNoBorder = workbook.createFont();
        fontNoBorder.setFontHeightInPoints((short) 10);
        fontNoBorder.setBold(true);
        fontNoBorder.setFontName("Times New Roman");

        CellStyle cellStyleNoBoder = workbook.createCellStyle();
        cellStyleNoBoder.setWrapText(true);
        cellStyleNoBoder.setFont(fontNoBorder);

        CellStyle cellStyleBoldTable = workbook.createCellStyle();
        cellStyleBoldTable.setWrapText(true);
        cellStyleBoldTable.setFont(font);
    }

    @Override
    public void exportHICInfoToWord(HttpServletResponse response, UUID staffId) throws IOException {
        staffService.exportHICInfoToWord(response, staffId);
    }

    @Override
    public List<StaffLabourAgreementDto> getAll() {
        List<StaffLabourAgreement> datas = staffLabourAgreementRepository.findAll();
        List<StaffLabourAgreementDto> response = new ArrayList<>();
        for (StaffLabourAgreement item : datas) {
            response.add(new StaffLabourAgreementDto(item));
        }
        return response;
    }

    @Override
    public Boolean checkOverdueContract(SearchStaffLabourAgreementDto searchDto) {
        Boolean result = null;
        Integer expiryDays = HrConstants.CONTRACT_PRE_EXPIRY_DAYS;
        if (searchDto != null && searchDto.getContractPreExpiryDays() != null) {
            expiryDays = searchDto.getContractPreExpiryDays();
        }
        List<StaffLabourAgreementDto> listData = staffLabourAgreementRepository
                .getListOverdueContract(HrConstants.StaffLabourAgreementStatus.SIGNED.getValue(), expiryDays);
        if (listData != null && !listData.isEmpty()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public HashMap<UUID, LabourAgreementDto> getLabourAgreementLatestMap() {
        HashMap<UUID, LabourAgreementDto> result = new HashMap<>();
        try {
            List<Object[]> queryResults = staffLabourAgreementRepository.findLatestLabourAgreements();

            for (Object[] row : queryResults) {
                try {
                    UUID staffId = row[0] != null ? UUID.fromString(row[0].toString()) : null;
                    if (staffId == null) {
                        continue;
                    }
                    String nameOrg = (String) row[1];
                    String codeOrg = (String) row[2];
                    String labourAgreementNumber = (String) row[3];
                    Date startDate = (Date) row[4];
                    Date endDate = (Date) row[5];
                    int totalDays = row[6] != null ? ((Number) row[6]).intValue() : 0;
                    Double insuranceSalary = row[7] != null ? ((Number) row[7]).doubleValue() : 0D;
                    String contactTypeName = (String) row[8];
                    Date signDate = (Date) row[9];
                    Double salary = row[10] != null ? ((Number) row[10]).doubleValue() : 0D;

                    LabourAgreementDto dto = new LabourAgreementDto(
                            staffId,
                            nameOrg,
                            codeOrg,
                            labourAgreementNumber,
                            startDate,
                            endDate,
                            totalDays,
                            insuranceSalary,
                            contactTypeName,
                            signDate,
                            salary
                    );

                    result.put(staffId, dto);
                } catch (Exception rowEx) {
                    System.err.println("Error processing row in getLabourAgreementLatestMap: " + rowEx.getMessage());
                    //rowEx.printStackTrace();
                    return null;
                }
            }

        } catch (Exception ex) {
            System.err.println("Error executing getLabourAgreementLatestMap: " + ex.getMessage());
            //ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public ApiResponse<StaffLabourAgreementDto> getLastLabourAgreement(UUID staffId) {
        try {
            List<StaffLabourAgreementDto> result = staffLabourAgreementRepository.getLastLabourAgreement(staffId);
            if (result != null && !result.isEmpty()) {
                return new ApiResponse<>(HttpStatus.SC_OK, "OK", result.get(0));
            } else {
                return new ApiResponse<>(HttpStatus.SC_OK, "OK", null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ApiResponse<>(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Internal Server Error", null);
        }
    }

    @Override
    public List<StaffLabourAgreementDto> readDataFromExcel(ByteArrayInputStream bis) {
        try (Workbook workbook = new XSSFWorkbook(bis)) {
            // Đọc dữ liệu import danh sách
            Sheet datatypeSheet = workbook.getSheetAt(0);

            try {
                int rowIndex = 0;
                int num = datatypeSheet.getLastRowNum();
                List<StaffLabourAgreementDto> result = new ArrayList<>();
                String errMessage = "";
                while (rowIndex <= num) {
                    rowIndex++;
                    Row currentRow = datatypeSheet.getRow(rowIndex);
                    Cell currentCell = null;
                    StaffDto staff = new StaffDto();
                    StaffLabourAgreementDto staffLabourAgreement = new StaffLabourAgreementDto();
                    ContractTypeDto contractType = new ContractTypeDto();
                    HrOrganizationDto contractOrganization = new HrOrganizationDto();
                    HrOrganizationDto workOrganization = new HrOrganizationDto();

                    if (currentRow != null) {
                        // 0. Mã nhân viên*
                        int index = 0;
                        currentCell = currentRow.getCell(index);
                        String staffCode = ExcelUtils.getCellValue(currentCell, String.class);
                        staff.setStaffCode(staffCode);
                        staffLabourAgreement.setStaff(staff);

                        // 1. Tên nhân viên
                        index = 1;

                        // 2. Số hợp đồng*
                        index = 2;
                        currentCell = currentRow.getCell(index);
                        String numberContract = ExcelUtils.getCellValue(currentCell, String.class);
                        staffLabourAgreement.setLabourAgreementNumber(numberContract);

                        // 3. Loại hợp đồng*
                        index = 3;
                        currentCell = currentRow.getCell(index);
                        String contractTypeCode = ExcelUtils.getCellValue(currentCell, String.class);
                        contractType.setCode(contractTypeCode);
                        staffLabourAgreement.setContractType(contractType);

                        // 4. Số tháng hợp đồng
                        index = 4;
                        currentCell = currentRow.getCell(index);
                        Integer durationMonths = ExcelUtils.getCellValue(currentCell, Integer.class);
                        staffLabourAgreement.setDurationMonths(durationMonths);

                        // 5 "Ngày bắt đầu hiệu lực*
                        //(mm/dd/yyyy)"
                        index = 5;
                        currentCell = currentRow.getCell(index);
                        Date startDate = ExcelUtils.getCellValue(currentCell, Date.class);
                        staffLabourAgreement.setStartDate(startDate);

                        // 6. "Ngày hết hạn
                        //(mm/dd/yyyy)"
                        index = 6;
                        currentCell = currentRow.getCell(index);
                        Date endDate = ExcelUtils.getCellValue(currentCell, Date.class);
                        staffLabourAgreement.setEndDate(endDate);

                        // 7. "Ngày thiết lập/ngày ký*
                        //(mm/dd/yyyy)"
                        index = 7;
                        currentCell = currentRow.getCell(index);
                        Date signedDate = ExcelUtils.getCellValue(currentCell, Date.class);
                        staffLabourAgreement.setSignedDate(signedDate);

                        // 8. Nơi làm việc
                        index = 8;
                        currentCell = currentRow.getCell(index);
                        String workingPlace = ExcelUtils.getCellValue(currentCell, String.class);
                        staffLabourAgreement.setWorkingPlace(workingPlace);

                        // 9. Mức lương
                        index = 9;
                        currentCell = currentRow.getCell(index);
                        Double salary = ExcelUtils.getCellValue(currentCell, Double.class);
                        staffLabourAgreement.setSalary(salary);

                        // 10. Mã đơn vị ký hợp đồng*
                        index = 10;
                        currentCell = currentRow.getCell(index);
                        String contractOrganizationCode = ExcelUtils.getCellValue(currentCell, String.class);
                        contractOrganization.setCode(contractOrganizationCode);
                        staffLabourAgreement.setContractOrganization(contractOrganization);

                        // 11. Tên đơn vị ký hợp đồng
                        index = 11;

                        // 12. Mã đơn vị làm việc
                        index = 12;
                        currentCell = currentRow.getCell(index);
                        String workOrganizationCode = ExcelUtils.getCellValue(currentCell, String.class);
                        workOrganization.setCode(workOrganizationCode);
                        staffLabourAgreement.setWorkOrganization(workOrganization);

                        // 13. Tên đơn vị làm việc
                        index = 13;

                        // 14. Tình trạng hợp đồng
                        index = 14;
                        currentCell = currentRow.getCell(index);
                        Integer agreementStatus = ExcelUtils.getCellValue(currentCell, Integer.class);
                        if (agreementStatus != null && (agreementStatus.equals(HrConstants.StaffLabourAgreementStatus.SIGNED.getValue())
                                || agreementStatus.equals(HrConstants.StaffLabourAgreementStatus.UNSIGNED.getValue())
                                || agreementStatus.equals(HrConstants.StaffLabourAgreementStatus.TERMINATED.getValue()))
                        ) {
                            staffLabourAgreement.setAgreementStatus(agreementStatus);
                        }
                        //nếu null tất cả các trường bắt buộc thì bỏ qua dòng đó
                        if (!StringUtils.hasText(staffCode) && !StringUtils.hasText(numberContract) && !StringUtils.hasText(contractTypeCode) && startDate == null && signedDate == null && !StringUtils.hasText(contractOrganizationCode)) {
                            continue;
                        }
                        if (!StringUtils.hasText(staffCode)) {
                            errMessage = "Mã nhân viên là bắt buộc, lỗi tại dòng " + (rowIndex + 1);
                            rowIndex = num + 1;
                            break;
                        }
                        if (!StringUtils.hasText(numberContract)) {
                            errMessage = "Số hợp đồng là bắt buộc, lỗi tại dòng " + (rowIndex + 1);
                            rowIndex = num + 1;
                            break;
                        }
                        if (!StringUtils.hasText(contractTypeCode)) {
                            errMessage = "Loại hợp đồng là bắt buộc, lỗi tại dòng " + (rowIndex + 1);
                            rowIndex = num + 1;
                            break;
                        }
                        if (durationMonths == null && !contractTypeCode.equals("HĐLĐ_KXĐTH")) {
                            errMessage = "Số tháng hợp đồng là bắt buộc với loại hợp đồng khác HĐLĐ_KXĐTH, lỗi tại dòng " + (rowIndex + 1);
                            rowIndex = num + 1;
                            break;
                        }
                        if (startDate == null) {
                            errMessage = "Ngày bắt đầu hiệu lực là bắt buộc, lỗi tại dòng " + (rowIndex + 1);
                            rowIndex = num + 1;
                            break;
                        }
                        if (signedDate == null) {
                            errMessage = "Ngày thiết lập/ngày ký là bắt buộc, lỗi tại dòng " + (rowIndex + 1);
                            rowIndex = num + 1;
                            break;
                        }
                        if (!StringUtils.hasText(contractOrganizationCode)) {
                            errMessage = "Mã đơn vị ký hợp đồng là bắt buộc, lỗi tại dòng " + (rowIndex + 1);
                            rowIndex = num + 1;
                            break;
                        }
                    }
                    result.add(staffLabourAgreement);
                }
                if (StringUtils.hasText(errMessage)) {
                    StaffLabourAgreementDto staffLabourAgreement = new StaffLabourAgreementDto();
                    staffLabourAgreement.setErrorMessage(errMessage);
                    return Collections.singletonList(staffLabourAgreement);
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer saveStaffLabourAgreementImportFromExcel(List<StaffLabourAgreementDto> importResults) {
        if (importResults == null || importResults.isEmpty()) {
            return null;
        }
        List<StaffLabourAgreement> entities = new ArrayList<>();
        for (StaffLabourAgreementDto item : importResults) {
            StaffLabourAgreement entity = null;
            if (item.getLabourAgreementNumber() != null && item.getStaff() != null && item.getStaff().getStaffCode() != null) {
                List<StaffLabourAgreement> staffLabourAgreements = staffLabourAgreementRepository.getStaffLabourAgreementByStaffCodeAndNumberContract(item.getStaff().getStaffCode(), item.getLabourAgreementNumber());
                if (staffLabourAgreements != null && !staffLabourAgreements.isEmpty()) {
                    entity = staffLabourAgreements.get(0);
                }
            }
            if (entity == null) {
                entity = new StaffLabourAgreement();
            }
            if (item.getStaff() != null && item.getStaff().getStaffCode() != null) {
                List<Staff> staffList = staffRepository.getByCode(item.getStaff().getStaffCode());
                if (staffList != null && !staffList.isEmpty()) {
                    entity.setStaff(staffList.get(0));
                }
            }
            if (entity.getStaff() == null) {
                continue;
            }


            if (item.getLabourAgreementNumber() != null) {
                entity.setLabourAgreementNumber(item.getLabourAgreementNumber());
            } else {
                return null;
            }

            if (item.getContractType() != null && item.getContractType().getCode() != null) {
                List<ContractType> contractTypes = contractTypeRepository
                        .findByCode(item.getContractType().getCode());
                if (contractTypes != null && !contractTypes.isEmpty()) {
                    entity.setContractType(contractTypes.get(0));
                }
            }

            if (entity.getContractType() == null) {
                continue;
            }
            if (!item.getContractType().getCode().equals("HĐLĐ_KXĐTH")) {
                if (item.getDurationMonths() != null) {
                    entity.setDurationMonths(item.getDurationMonths());
                }
            } else {
                entity.setDurationMonths(null);
            }

            if (item.getStartDate() != null) {
                entity.setStartDate(item.getStartDate());
            } else {
                return null;
            }

            if (item.getEndDate() != null) {
                entity.setEndDate(item.getEndDate());
            }
            if (item.getSignedDate() != null) {
                entity.setSignedDate(item.getSignedDate());
            } else {
                return null;
            }

            if (item.getWorkingPlace() != null) {
                entity.setWorkingPlace(item.getWorkingPlace());
            }
            if (item.getSalary() != null) {
                entity.setSalary(item.getSalary());
            }
            if (item.getContractOrganization() != null && item.getContractOrganization().getCode() != null) {
                List<HrOrganization> contractOrganizationList = hrOrganizationRepository
                        .findByCode(item.getContractOrganization().getCode());
                if (contractOrganizationList != null && !contractOrganizationList.isEmpty()) {
                    entity.setContractOrganization(contractOrganizationList.get(0));
                }
            }
            if (entity.getContractOrganization() == null) {
                return null;
            }
            if (item.getWorkOrganization() != null && item.getWorkOrganization().getCode() != null) {
                List<HrOrganization> workOrganizationList = hrOrganizationRepository
                        .findByCode(item.getWorkOrganization().getCode());
                if (workOrganizationList != null && !workOrganizationList.isEmpty()) {
                    entity.setWorkOrganization(workOrganizationList.get(0));
                }
            }
            if (item.getAgreementStatus() != null) {
                entity.setAgreementStatus(item.getAgreementStatus());
            }
            entities.add(entity);
        }
        try {
            staffLabourAgreementRepository.saveAll(entities);
            return entities.size();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Workbook exportExcelStaffLabourAgreement(SearchStaffLabourAgreementDto dto) {
        if (dto == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("Excel/HOP_DONG.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File '" + "Excel/HOP_DONG.xlsx" + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);

            Sheet staffSheet = workbook.getSheetAt(0);
            CellStyle dataCellStyle = createDataCellStyle(workbook);

            int pageIndex = 1;
            int rowIndex = 1;
            boolean hasNextPage = true;
            long startTime = System.nanoTime();

            while (hasNextPage) {
                // searchStaffDto = new SearchStaffDto();
                dto.setPageIndex(pageIndex);
                dto.setPageSize(100);

                Page<StaffLabourAgreementDto> staffLabourAgreements = this.pagingLabourAgreement(dto);
                if (staffLabourAgreements == null || staffLabourAgreements.isEmpty()) {
                    break;
                }

                for (StaffLabourAgreementDto item : staffLabourAgreements) {
                    if (item == null) continue;

                    Row dataRow = staffSheet.createRow(rowIndex);
                    int cellIndex = 0;

                    // 0. Mã nhân viên*
                    String staffCode = null;
                    String staffName = null;
                    if (item.getStaff() != null) {
                        staffCode = item.getStaff().getStaffCode();
                        staffName = item.getStaff().getDisplayName();
                    }
                    this.createCell(dataRow, cellIndex++, staffCode, dataCellStyle);
                    // 1. Tên nhân viên
                    this.createCell(dataRow, cellIndex++, staffName, dataCellStyle);
                    // 2. Số hợp đồng*
                    this.createCell(dataRow, cellIndex++, item.getLabourAgreementNumber(), dataCellStyle);
                    // 3. Loại hợp đồng*
                    String contractTypeCode = null;
                    if (item.getContractType() != null) {
                        contractTypeCode = item.getContractType().getCode();
                    }
                    this.createCell(dataRow, cellIndex++, contractTypeCode, dataCellStyle);
                    // 4. Số tháng hợp đồng
                    this.createCell(dataRow, cellIndex++, item.getDurationMonths(), dataCellStyle);
                    // 5. Ngày bắt đầu hiệu lực*
                    this.createCell(dataRow, cellIndex++, formatDate(item.getStartDate()), dataCellStyle);
                    //  6. Ngày hết hạn
                    this.createCell(dataRow, cellIndex++, formatDate(item.getEndDate()), dataCellStyle);
                    // 7. Ngày thành lập
                    this.createCell(dataRow, cellIndex++, formatDate(item.getSignedDate()), dataCellStyle);
                    // 8. Nơi làm việc
                    this.createCell(dataRow, cellIndex++, item.getWorkingPlace(), dataCellStyle);

                    // 9. Mức lương
                    this.createCell(dataRow, cellIndex++, item.getSalary(), dataCellStyle);
                    // 10. Mã đơn vị ký hợp đồng*
                    String contractOrganizationCode = null;
                    String contractOrganizationName = null;
                    if (item.getContractOrganization() != null) {
                        contractOrganizationCode = item.getContractOrganization().getCode();
                        contractOrganizationName = item.getContractOrganization().getName();
                    }
                    this.createCell(dataRow, cellIndex++, contractOrganizationCode, dataCellStyle);
                    // 11. Tên đơn vị ký hợp đồng
                    this.createCell(dataRow, cellIndex++, contractOrganizationName, dataCellStyle);

                    String contractWorkingCode = null;
                    String contractWorkingName = null;
                    if (item.getWorkOrganization() != null) {
                        contractWorkingCode = item.getWorkOrganization().getCode();
                        contractWorkingName = item.getWorkOrganization().getName();
                    }
                    // 12. Mã đơn vị làm việc
                    this.createCell(dataRow, cellIndex++, contractWorkingCode, dataCellStyle);
                    // 13. Tên đơn vị làm việc
                    this.createCell(dataRow, cellIndex++, contractWorkingName, dataCellStyle);
                    // 14. Tình trạng hợp đồng
                    this.createCell(dataRow, cellIndex++, item.getAgreementStatus(), dataCellStyle);

                    rowIndex++;
                }

                hasNextPage = staffLabourAgreements.hasNext(); // Kiểm tra xem còn trang tiếp theo không
                pageIndex++; // Tăng pageIndex để lấy trang tiếp theo
            }
            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất tất cả đơn vị - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;

        } catch (IOException e) {
            logger.error("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    /**
     * Tạo style cho cell: border và font Times New Roman
     */
    private CellStyle createDataCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();

        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Tạo font Times New Roman
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);

        return cellStyle;
    }

    private void createCell(Row row, int cellIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

}
