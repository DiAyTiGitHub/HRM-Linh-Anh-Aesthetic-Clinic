package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.AbsenceRequestDto;
import com.globits.hr.dto.search.AbsenceRequestSearchDto;
import com.globits.hr.repository.AbsenceRequestRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffWorkScheduleRepository;
import com.globits.hr.service.AbsenceRequestService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.dto.excel.SalaryResultStaffItemImportDto;
import com.globits.salary.repository.SalaryPeriodRepository;
import com.globits.salary.service.SalaryResultStaffItemService;
import com.globits.timesheet.domain.AbsenceRequest;
import jakarta.persistence.Query;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AbsenceRequestServiceImpl extends GenericServiceImpl<AbsenceRequest, UUID> implements AbsenceRequestService {
    @Autowired
    private AbsenceRequestRepository absenceRequestRepository;

    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    @Autowired
    private StaffRepository staffRepository;


    private void updateCorrespondingSalaryResultStaffItem(UUID staffId, Date requestDate) {
        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null) return;

        List<SalaryPeriod> salaryPeriods = salaryPeriodRepository.getActivePeriodsByDate(requestDate);
        if (CollectionUtils.isEmpty(salaryPeriods)) {
            return;
        }
        SalaryPeriod salaryPeriod = salaryPeriods.get(0);

        // Số ca làm việc nghỉ có phép được trả lương
        Double paidLeaveShifts = 0.0;
        // Số ca làm việc nghỉ có phép không được trả lương
        Double unpaidLeaveShifts = 0.0;

        List<Date> dates = DateTimeUtil.getDaysBetweenDates(salaryPeriod.getFromDate(), salaryPeriod.getToDate());
        for (Date date : dates) {
            paidLeaveShifts += staffWorkScheduleRepository.getTotalShiftsByStaffIdAbsenceTypeAndWorkingDate(staff.getId(), HrConstants.AbsenceRequestType.PAID_LEAVE.getValue(), date);
            unpaidLeaveShifts += staffWorkScheduleRepository.getTotalShiftsByStaffIdAbsenceTypeAndWorkingDate(staff.getId(), HrConstants.AbsenceRequestType.UNPAID_LEAVE.getValue(), date);
        }

        //tạo/update Số ca làm việc nghỉ có phép được trả lương trong phiếu lương kỳ đó
//        SalaryResultStaffItemImportDto importPaidLeaveDto = new SalaryResultStaffItemImportDto();
//        importPaidLeaveDto.setStaffCode(staff.getStaffCode());
//        importPaidLeaveDto.setSalaryPeriodCode(salaryPeriod.getCode());
//        importPaidLeaveDto.setSalaryItemCode(HrConstants.SalaryAutoMapField.SO_CA_NGHI_PHEP_CO_LUONG.getValue());
//        importPaidLeaveDto.setSalaryItemValue(paidLeaveShifts.toString());
//        importPaidLeaveDto.setRecalculateStaffPayslipAfterProcess(true);

//        salaryResultStaffItemService.importSalaryResultStaffItemValue(importPaidLeaveDto);
        
        //tạo/update Số ca làm việc nghỉ có phép không được trả lương trong phiếu lương kỳ đó
//        SalaryResultStaffItemImportDto importUnpaidLeaveDto = new SalaryResultStaffItemImportDto();
//        importUnpaidLeaveDto.setStaffCode(staff.getStaffCode());
//        importUnpaidLeaveDto.setSalaryPeriodCode(salaryPeriod.getCode());
//        importUnpaidLeaveDto.setSalaryItemCode(HrConstants.SalaryAutoMapField.SO_CA_NGHI_PHEP_KHONG_LUONG.getValue());
//        importUnpaidLeaveDto.setSalaryItemValue(unpaidLeaveShifts.toString());
//        importUnpaidLeaveDto.setRecalculateStaffPayslipAfterProcess(true);

//        salaryResultStaffItemService.importSalaryResultStaffItemValue(importUnpaidLeaveDto);
        
        //tạo/update Số ca làm việc nghỉ có phép được trả lương trong phiếu lương kỳ đó
//        salaryResultStaffItemService.createAndImportSalaryResultItem(staff, salaryPeriod, paidLeaveShifts, HrConstants.SalaryAutoMapField.SO_CA_NGHI_PHEP_CO_LUONG);

        //tạo/update Số ca làm việc nghỉ có phép không được trả lương trong phiếu lương kỳ đó
//        salaryResultStaffItemService.createAndImportSalaryResultItem(staff, salaryPeriod, unpaidLeaveShifts, HrConstants.SalaryAutoMapField.SO_CA_NGHI_PHEP_KHONG_LUONG);
        
    }


    @Override
    public Page<AbsenceRequestDto> pagingAbsenceRequestDto(AbsenceRequestSearchDto dto) {
        if (dto == null) return null;
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) pageIndex--;
        else pageIndex = 0;

        String sqlCount = "select count(entity.id) from AbsenceRequest entity where (1=1) ";
        String sql = "select new com.globits.hr.dto.AbsenceRequestDto(entity) from AbsenceRequest as entity where (1=1) ";

        String whereClause = "";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.workSchedule.staff.staffCode LIKE :text OR entity.workSchedule.staff.displayName LIKE :text ) ";
        }

        if (dto.getStaffId() != null) {
            whereClause += " and entity.workSchedule.staff.id = :staffId ";
        }

        if (dto.getApprovalStatus() != null) {
            if (dto.getApprovalStatus().equals(HrConstants.AbsenceRequestApprovalStatus.NOT_APPROVED_YET.getValue())) {
                whereClause += " and entity.approvalStatus = 1 ";
            } else if (dto.getApprovalStatus().equals(HrConstants.AbsenceRequestApprovalStatus.APPROVED.getValue())) {
                whereClause += " and entity.approvalStatus = 2 ";
            } else if (dto.getApprovalStatus().equals(HrConstants.AbsenceRequestApprovalStatus.NOT_APPROVED.getValue())) {
                whereClause += " and entity.approvalStatus = 3 ";
            }
        }

        String orderBy = " ORDER BY entity.createDate DESC";

        sql += whereClause + orderBy;
        sqlCount += whereClause;
        Query q = manager.createQuery(sql, AbsenceRequestDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", "%" + dto.getKeyword() + "%");
            qCount.setParameter("text", "%" + dto.getKeyword() + "%");
        }

        if (dto.getStaffId() != null) {
            q.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<AbsenceRequestDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public AbsenceRequestDto saveOrUpdate(AbsenceRequestDto dto) {
        if (dto == null) return null;
        AbsenceRequest entity = null;
        if (dto.getId() != null) {
            entity = absenceRequestRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            entity = new AbsenceRequest();
        }
        entity.setRequestDate(dto.getRequestDate());
        entity.setAbsenceReason(dto.getAbsenceReason());
        entity.setApprovalStatus(dto.getApprovalStatus());
        entity.setAbsenceType(dto.getAbsenceType());

        StaffWorkSchedule staffWorkSchedule = null;
        if (dto.getWorkSchedule() != null && dto.getWorkSchedule().getId() != null) {
            staffWorkSchedule = staffWorkScheduleRepository.findById(dto.getWorkSchedule().getId()).orElse(null);
            // nếu yêu cầu được phê duyệt thì cập nhật staffWorkSchedule với tt là LEAVE_WITH_PERMISSION
            if (dto.getApprovalStatus().equals(HrConstants.AbsenceRequestApprovalStatus.APPROVED.getValue())) {
//                staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.LEAVE_WITH_PERMISSION.getValue());
                staffWorkScheduleRepository.save(staffWorkSchedule);
            }
        }
        entity.setWorkSchedule(staffWorkSchedule);
        AbsenceRequest response = absenceRequestRepository.save(entity);

        // cập nhật số ca nghỉ phép được trả lương/không được trả lương của nhân viên vào phiếu lương
        if (entity.getWorkSchedule() != null && entity.getWorkSchedule().getStaff() != null && entity.getWorkSchedule().getWorkingDate() != null)
            updateCorrespondingSalaryResultStaffItem(entity.getWorkSchedule().getStaff().getId(), entity.getWorkSchedule().getWorkingDate());


        return new AbsenceRequestDto(response);

    }

    @Override
    public AbsenceRequestDto getById(UUID id) {
        if (id != null) {
            AbsenceRequest entity = absenceRequestRepository.findById(id).orElse(null);
            if (entity != null) {
                return new AbsenceRequestDto(entity);
            }
        }
        return null;
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id != null) {
            AbsenceRequest entity = absenceRequestRepository.findById(id).orElse(null);
            if (entity != null) {
                absenceRequestRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    @Modifying
    public List<UUID> updateRequestsApprovalStatus(AbsenceRequestSearchDto dto) {
        if (dto == null ||
                dto.getApprovalStatus() == null ||
                dto.getChosenIds() == null ||
                dto.getChosenIds().isEmpty()) {
            return null;
        }

        List<AbsenceRequest> onSaveRequests = new ArrayList<>();
        for (UUID requestId : dto.getChosenIds()) {
            if (requestId == null)
                return null;

            AbsenceRequest request = repository.findById(requestId).orElse(null);
            if (request == null)
                return null;

            request.setApprovalStatus(dto.getApprovalStatus());
            // nếu yêu cầu được phê duyệt thì cập nhật staffWorkSchedule với tt là LEAVE_WITH_PERMISSION
            if (dto.getApprovalStatus().equals(HrConstants.AbsenceRequestApprovalStatus.APPROVED.getValue())) {
                StaffWorkSchedule staffWorkSchedule = request.getWorkSchedule();
//                staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.LEAVE_WITH_PERMISSION.getValue());
                staffWorkScheduleRepository.save(staffWorkSchedule);
            }


            onSaveRequests.add(request);
        }

        if (onSaveRequests == null || onSaveRequests.size() == 0)
            return null;
        List<AbsenceRequest> savedRequests = repository.saveAll(onSaveRequests);

        for (AbsenceRequest absenceRequest : savedRequests) {
            // cập nhật số ca nghỉ phép được trả lương/không được trả lương của nhân viên vào phiếu lương
            if (absenceRequest.getWorkSchedule() != null && absenceRequest.getWorkSchedule().getStaff() != null && absenceRequest.getWorkSchedule().getWorkingDate() != null)
                updateCorrespondingSalaryResultStaffItem(absenceRequest.getWorkSchedule().getStaff().getId(), absenceRequest.getWorkSchedule().getWorkingDate());
        }

        if (savedRequests == null)
            return null;

        // return ids of updated request
        List<UUID> updatedRequestIds = new ArrayList<>();
        for (AbsenceRequest request : savedRequests) {
            updatedRequestIds.add(request.getId());
        }

        return updatedRequestIds;
    }


    @Override
    @Transactional
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        for (UUID id : ids) {
            this.deleteById(id);
        }
        return true;
    }
}
