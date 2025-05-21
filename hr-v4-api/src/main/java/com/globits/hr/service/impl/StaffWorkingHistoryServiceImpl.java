package com.globits.hr.service.impl;

import com.globits.core.repository.DepartmentRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.LeaveHistoryDto;
import com.globits.hr.dto.StaffWorkingHistoryDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffWorkingHistoryDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.PositionService;
import com.globits.hr.service.StaffWorkingHistoryService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class StaffWorkingHistoryServiceImpl extends GenericServiceImpl<StaffWorkingHistory, UUID> implements StaffWorkingHistoryService {
    @Autowired
    StaffWorkingHistoryRepository staffWorkingHistoryRepository;
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    HRDepartmentRepository hrDepartmentRepository;
    @Autowired
    HrOrganizationRepository hrOrganizationRepository;
    @Autowired
    PositionRepository positionRepository;
    @Autowired
    private PositionService positionService;

    @Override
    public Page<StaffWorkingHistoryDto> getPage(SearchStaffWorkingHistoryDto searchDto) {
        if (searchDto == null)
            return null;

        int pageIndex = searchDto.getPageIndex();
        int pageSize = searchDto.getPageSize();

        if (pageIndex > 0)
            pageIndex--;
        else
            pageIndex = 0;

        String sqlCount = "select count(entity.id) from StaffWorkingHistory entity where (1=1)";
        String sql = "select new com.globits.hr.dto.StaffWorkingHistoryDto(entity) from StaffWorkingHistory as entity where (1=1)";

        String whereClause = "";


//        if (searchDto.getStartDate() != null && searchDto.getEndDate() != null) {
//            whereClause += " AND DATE(entity.startDate) BETWEEN DATE(:startDate) AND DATE(:endDate)";
//        } else if (searchDto.getStartDate() != null) {
//            whereClause += " AND DATE(entity.startDate) >= DATE(:startDate)";
//        } else if (searchDto.getEndDate() != null) {
//            whereClause += " AND DATE(entity.endDate) <= DATE(:endDate)";
//        }

        if (searchDto.getStartDate() != null) {
            whereClause += " AND DATE(entity.startDate) =  DATE(:startDate) ";
        }
        if (searchDto.getEndDate() != null) {
            whereClause += " AND DATE(entity.endDate) =  DATE(:endDate) ";
        }

        if (searchDto.getStaffId() != null) {
            whereClause += " AND entity.staff.id = :staffId";
        }
        if (searchDto.getFromOrganization() != null) {
            whereClause += " AND entity.fromOrganization.id = :fromOrgId";
        }
        if (searchDto.getToOrganization() != null) {
            whereClause += " AND entity.toOrganization.id = :toOrgId";
        }
        if (searchDto.getFromDepartment() != null) {
            whereClause += " AND entity.fromDepartment.id = :fromDeptId";
        }
        if (searchDto.getToDepartment() != null) {
            whereClause += " AND entity.toDepartment.id = :toDeptId";
        }
        if (searchDto.getFromPosition() != null) {
            whereClause += " AND entity.fromPosition.id = :fromPosId";
        }
        if (searchDto.getToPosition() != null) {
            whereClause += " AND entity.toPosition.id = :toPosId";
        }
        if (searchDto.getTransferType() != null) {
            whereClause += " AND entity.transferType = :transferType";
        }

        // Thêm điều kiện ORDER BY để sắp xếp theo modifyDate mới nhất trước
        String orderBy = " ORDER BY entity.createDate DESC";

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, StaffWorkingHistoryDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getStartDate() != null) {
            q.setParameter("startDate", searchDto.getStartDate());
            qCount.setParameter("startDate", searchDto.getStartDate());
        }
        if (searchDto.getEndDate() != null) {
            q.setParameter("endDate", searchDto.getEndDate());
            qCount.setParameter("endDate", searchDto.getEndDate());
        }
        if (searchDto.getStaffId() != null) {
            q.setParameter("staffId", searchDto.getStaffId());
            qCount.setParameter("staffId", searchDto.getStaffId());
        }
        if (searchDto.getFromOrganization() != null) {
            q.setParameter("fromOrgId", searchDto.getFromOrganization().getId());
            qCount.setParameter("fromOrgId", searchDto.getFromOrganization().getId());
        }
        if (searchDto.getToOrganization() != null) {
            q.setParameter("toOrgId", searchDto.getToOrganization().getId());
            qCount.setParameter("toOrgId", searchDto.getToOrganization().getId());
        }
        if (searchDto.getFromDepartment() != null) {
            q.setParameter("fromDeptId", searchDto.getFromDepartment().getId());
            qCount.setParameter("fromDeptId", searchDto.getFromDepartment().getId());
        }
        if (searchDto.getToDepartment() != null) {
            q.setParameter("toDeptId", searchDto.getToDepartment().getId());
            qCount.setParameter("toDeptId", searchDto.getToDepartment().getId());
        }
        if (searchDto.getFromPosition() != null) {
            q.setParameter("fromPosId", searchDto.getFromPosition().getId());
            qCount.setParameter("fromPosId", searchDto.getFromPosition().getId());
        }
        if (searchDto.getToPosition() != null) {
            q.setParameter("toPosId", searchDto.getToPosition().getId());
            qCount.setParameter("toPosId", searchDto.getToPosition().getId());
        }
        if (searchDto.getTransferType() != null) {
            q.setParameter("transferType", searchDto.getTransferType());
            qCount.setParameter("transferType", searchDto.getTransferType());
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<StaffWorkingHistoryDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }


    @Override
    public StaffWorkingHistoryDto saveStaffWorkingHistory(StaffWorkingHistoryDto dto) {
        if (dto == null) return null;

        StaffWorkingHistory staffWorkingHistory = null;
        if (dto.getId() != null) {
            staffWorkingHistory = staffWorkingHistoryRepository.findById(dto.getId()).orElse(null);
        }
        if (staffWorkingHistory == null) {
            staffWorkingHistory = new StaffWorkingHistory();
        }

        StringBuilder errorNotes = new StringBuilder();

        // Kiểm tra startDate
        if (dto.getStartDate() == null) {
            errorNotes.append("Ngày bắt đầu không được để trống. ");
        }

        // Kiểm tra endDate phải lớn hơn startDate
        if (dto.getEndDate() != null && dto.getStartDate() != null && dto.getEndDate().before(dto.getStartDate())) {
            errorNotes.append("Ngày kết thúc phải lớn hơn ngày bắt đầu. ");
        }

        // Kiểm tra staff
        Staff staff = null;
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }
        if (staff == null) {
            errorNotes.append("Không tìm thấy nhân viên. ");
        }

        // Nếu có lỗi, không lưu, trả về DTO với note lỗi
        if (errorNotes.length() > 0) {
            dto.setNote("ERROR: " + errorNotes.toString().trim());
            return dto;
        }

        // chi tao moi thi moi duoc
        if (!Objects.equals(dto.getTransferType(), HrConstants.StaffWorkingHistoryTransferType.PAUSE_TEMPORARY.getValue())) {
            // xỷ ly vi tri hien tai
            Position fromPosition = null;
            if (dto.getFromPosition() != null && dto.getFromPosition().getId() != null) {
                fromPosition = positionRepository.findById(dto.getFromPosition().getId()).orElse(null);
                staffWorkingHistory.setFromPosition(fromPosition);
            }

            if (fromPosition != null) {
                positionService.findHistoryAndUpdateFromPosition(fromPosition);
            }

            if (dto.getId() == null && fromPosition != null && fromPosition.getStaff() == null) {
                dto.setNote("ERROR: " + "Vị trí hiện tại không có nhân viên đảm nhiệm");
                return dto;
            }

            if (dto.getId() == null && fromPosition != null && !fromPosition.getStaff().getId().equals(staff.getId())) {
                dto.setNote("ERROR: " + "Vị trí hiện tại và nhân viên điều chuyển không trùng khớp");
                return dto;
            }

            HRDepartment fromDepartment = null;
            HrOrganization fromOrg = null;
            if (fromPosition != null && fromPosition.getDepartment() != null) {
                fromDepartment = fromPosition.getDepartment();
                staffWorkingHistory.setFromDepartment(fromDepartment);

                if (fromDepartment.getOrganization() != null) {
                    fromOrg = fromDepartment.getOrganization();
                    staffWorkingHistory.setFromOrganization(fromOrg);
                } else {
                    staffWorkingHistory.setFromOrganization(null);
                }
            } else {
                staffWorkingHistory.setFromOrganization(null);
                staffWorkingHistory.setFromDepartment(null);
            }
            // xử lý vị trí đích
            Position toPos = null;
            if (dto.getToPosition() != null && dto.getToPosition().getId() != null) {
                toPos = positionRepository.findById(dto.getToPosition().getId()).orElse(null);
                staffWorkingHistory.setToPosition(toPos);
            }

            // nếu vị trí đích có nhân viên th tạo lsct cho nhân vien cũ
            if (toPos != null && dto.getId() == null) {
                Staff currentStaffInToPos = toPos.getStaff();
                if (currentStaffInToPos != null && !currentStaffInToPos.equals(staff)) {
                    // Tạo lịch sử công tác cho nhân viên bị thay thế
                    positionService.findHistoryAndUpdateFromPosition(toPos);

                    StaffWorkingHistory historyForOldStaff = new StaffWorkingHistory();
                    historyForOldStaff.setStaff(currentStaffInToPos);
                    historyForOldStaff.setFromPosition(toPos);
                    if (toPos.getDepartment() != null) {
                        historyForOldStaff.setFromDepartment(toPos.getDepartment());
                        if (toPos.getDepartment().getOrganization() != null) {
                            historyForOldStaff.setFromOrganization(toPos.getDepartment().getOrganization());
                        }
                    }
                    historyForOldStaff.setToPosition(null);
                    historyForOldStaff.setStartDate(new Date()); // Hoặc ngày hiện tại
                    historyForOldStaff.setTransferType(HrConstants.StaffWorkingHistoryTransferType.EXTERNAL_ORG.getValue()); // Loại điều chuyển đặc biệt
                    staffWorkingHistoryRepository.save(historyForOldStaff);
                }
            }

            HRDepartment toDept = null;
            HrOrganization toOrg = null;
            if (toPos != null && toPos.getDepartment() != null) {
                toDept = toPos.getDepartment();
                staffWorkingHistory.setToDepartment(toDept);

                if (toDept.getOrganization() != null) {
                    toOrg = toDept.getOrganization();
                    staffWorkingHistory.setToOrganization(toOrg);
                } else {
                    staffWorkingHistory.setToOrganization(null);
                }
            } else {
                staffWorkingHistory.setToOrganization(null);
                staffWorkingHistory.setToDepartment(null);
            }

            // update staff nếu là dieu chuyen
            if (dto.getId() == null) {
                if (fromPosition != null) {
                    if(fromPosition.getStaff() != null) {
                        fromPosition.setPreviousStaff(fromPosition.getStaff());
                    }
                    fromPosition.setStaff(null);
                }
                if (toPos != null) {
                    if(toPos.getStaff() != null) {
                        toPos.setPreviousStaff(toPos.getStaff());
                    }
                    toPos.setStaff(staff);
                }
            }

            if (fromPosition != null && fromPosition.getId() != null) {
                fromPosition = positionRepository.save(fromPosition);
            }
            if (toPos != null) {
                toPos = positionRepository.save(toPos);
            }
        } else {
            Set<Position> listPos = staff.getCurrentPositions();
            if (listPos != null && listPos.size() > 0) {
                for (Position p : listPos) {
                    if (p.getIsMain()) {
                        staffWorkingHistory.setFromPosition(p);
                        if (p.getDepartment() != null) {
                            staffWorkingHistory.setFromDepartment(p.getDepartment());

                            if (p.getDepartment().getOrganization() != null) {
                                staffWorkingHistory.setFromOrganization(p.getDepartment().getOrganization());
                            }
                        }
                        break;
                    }
                }
            }
        }
        // set các trường staffWorkingHistory
        staffWorkingHistory.setStartDate(dto.getStartDate());
        staffWorkingHistory.setEndDate(dto.getEndDate());
        staffWorkingHistory.setStaff(staff);
        staffWorkingHistory.setNote(dto.getNote());
        staffWorkingHistory.setTransferType(dto.getTransferType());
        staffWorkingHistory = staffWorkingHistoryRepository.save(staffWorkingHistory);

        return new StaffWorkingHistoryDto(staffWorkingHistory);
    }


    @Override
    public StaffWorkingHistoryDto getStaffWorkingHistory(UUID id) {
        // TODO Auto-generated method stub
        StaffWorkingHistory staffWorkingHistory = staffWorkingHistoryRepository.findById(id).orElse(null);
        return new StaffWorkingHistoryDto(staffWorkingHistory);
    }

    @Override
    public Boolean deleteStaffWorkingHistory(UUID id) {
        // TODO Auto-generated method stub
        if (id != null) {
            StaffWorkingHistory entity = staffWorkingHistoryRepository.findById(id).orElse(null);
            if (entity != null) {
                staffWorkingHistoryRepository.deleteById(id);
                return true;
            }
        }
        return false;

    }

    @Override
    public StaffWorkingHistoryDto getRecentStaffWorkingHistory(UUID staffId) {
        List<StaffWorkingHistory> historyList = staffWorkingHistoryRepository.findByStaffIdOrderByCreateDateDesc(staffId);

        if (!historyList.isEmpty()) {
            return new StaffWorkingHistoryDto(historyList.get(0));
        }

        return new StaffWorkingHistoryDto();
    }

    @Override
    public HashMap<UUID, LeaveHistoryDto> getLeaveHistoryMap() {
        HashMap<UUID, LeaveHistoryDto> result = new HashMap<>();

        try {
            List<Object[]> queryResults = staffWorkingHistoryRepository.findLatestStaffWorkingHistory(HrConstants.StaffWorkingHistoryTransferType.PAUSE_TEMPORARY.getValue());

            for (Object[] row : queryResults) {
                try {
                    UUID staffId = row[0] != null ? UUID.fromString(row[0].toString()) : null;
                    if (staffId == null) {
                        continue;
                    }
                    Date startDate = (Date) row[1];
                    Date endDate = (Date) row[2];
                    String reason = (String) row[3];
                    LeaveHistoryDto dto = new LeaveHistoryDto(
                            staffId,
                            startDate,
                            endDate,
                            reason
                    );
                    result.put(staffId, dto);
                } catch (Exception rowEx) {
                    System.err.println("Error processing row in getLeaveHistoryMap: " + rowEx.getMessage());
                    // rowEx.printStackTrace();
                    return null;
                }
            }

        } catch (Exception ex) {
            System.err.println("Error executing getLeaveHistoryMap: " + ex.getMessage());
            // ex.printStackTrace();
            return null;
        }

        return result;
    }


}