package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.importExcel.RecruitmentRequestReportDto;
import com.globits.hr.dto.search.RecruitmentRequestSummarySearch;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.hr.projection.RecruitmentRequestSummary;
import com.globits.hr.repository.*;
import com.globits.hr.service.*;
import com.globits.hr.utils.*;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.globits.hr.HrConstants.*;

@Service
public class RecruitmentRequestServiceImpl extends GenericServiceImpl<RecruitmentRequest, UUID>
        implements RecruitmentRequestService {
    private static final Logger logger = LoggerFactory.getLogger(RecruitmentRequestServiceImpl.class);

    @Autowired
    private RecruitmentRequestRepository recruitmentRequestRepository;

    @Autowired
    private HrOrganizationRepository hrOrganizationRepository;

    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PositionTitleRepository positionTitleRepository;

    @Autowired
    RecruitmentRequestItemRepository recruitmentRequestItemRepository;
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    StaffHierarchyService staffHierarchyService;

    @Autowired
    RecruitmentRequestPositionService recruitmentRequestPositionService;

    @Autowired
    UserExtService userExtService;

    @Autowired
    HrRoleService roleService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private HrResourcePlanRepository hrResourcePlanRepository;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Override
    public Boolean isValidCode(RecruitmentRequestDto dto) {
        if (dto == null)
            return false;

        // ID of RecruitmentRequest is null => Create new RecruitmentRequest
        // => Assure that there's no other RecruitmentRequests using this code of new RecruitmentRequest
        // if there was any RecruitmentRequest using new RecruitmentRequest code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<RecruitmentRequest> entities = recruitmentRequestRepository.findByCode(dto.getCode());
            return entities == null || entities.isEmpty();

        }
        // ID of RecruitmentRequest is NOT null => RecruitmentRequest is modified
        // => Assure that the modified code is not same to OTHER any RecruitmentRequest's code
        // if there was any RecruitmentRequest using new RecruitmentRequest code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<RecruitmentRequest> entities = recruitmentRequestRepository.findByCode(dto.getCode());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (RecruitmentRequest entity : entities) {
                if (!entity.getId().equals(dto.getId()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public RecruitmentRequestDto getById(UUID id) {
        RecruitmentRequest entity = recruitmentRequestRepository.findById(id).orElse(null);
        if (entity == null)
            return null;
        return new RecruitmentRequestDto(entity);
    }

    @Override
    @Modifying
    @Transactional
    public ApiResponse<RecruitmentRequestDto> saveRecruitmentRequest(RecruitmentRequestDto dto) {
        if (dto == null) {
            return null;
        }

        RecruitmentRequest entity = new RecruitmentRequest();
        StaffDto staff = userExtService.getCurrentStaff();
        UserDto userDto = userExtService.getCurrentUser();
        List<String> list = userDto.getRoles().stream().filter(Objects::nonNull).map(RoleDto::getName).toList();
        try {
            if (!list.contains(HrConstants.HR_CREATE_RECRUITMENT_REQUEST)
                    && !list.contains(HrConstants.SUPER_HR)
                    && !list.contains(HrConstants.ROLE_ADMIN)
                    && !list.contains(HrConstants.ROLE_HR_MANAGEMENT)) {
                return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Bạn không có quyền tạo phiếu Y/c tuyển dụng", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Bạn không có quyền tạo phiếu Y/c tuyển dụng", null);
        }
        if (dto.getId() != null)
            entity = recruitmentRequestRepository.findById(dto.getId()).orElse(null);
        if (entity == null) {
            entity = new RecruitmentRequest();
        }
        if (entity.getStatus() != null && !entity.getStatus().equals(HrConstants.RecruitmentRequestStatus.CREATED.getValue()) && !list.contains(HrConstants.SUPER_HR) && !list.contains(HrConstants.ROLE_ADMIN)) {
            return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Phiếu này đã được gửi nên không được sửa", new RecruitmentRequestDto(entity));
        }
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        entity.setRequest(dto.getRequest());
        entity.setStatus(dto.getStatus());
        entity.setRecruitingStartDate(dto.getRecruitingStartDate());
        entity.setRecruitingEndDate(dto.getRecruitingEndDate());
        if (dto.getOrganization() != null && dto.getOrganization().getId() != null) {
            HrOrganization organization = hrOrganizationRepository.findById(dto.getOrganization().getId()).orElse(null);
            if (organization == null)
                return null;
            entity.setHrOrganization(organization);
        } else {
            entity.setHrOrganization(null);
        }
        HRDepartment hrDepartment = null;
        if (dto.getHrDepartment() != null && dto.getHrDepartment().getId() != null) {
            hrDepartment = hrDepartmentRepository.findById(dto.getHrDepartment().getId()).orElse(null);
            if (hrDepartment == null)
                return null;
            entity.setHrDepartment(hrDepartment);
            entity.setWorkPlace(hrDepartment.getWorkplace());
        } else {
            entity.setHrDepartment(null);
        }
        if (dto.getWorkPlace() != null) {
            entity.setWorkPlace(workplaceRepository.findById(dto.getWorkPlace().getId()).orElse(null));
        } else {
            entity.setWorkPlace(null);
        }
        if (dto.getTeam() != null && dto.getTeam().getId() != null) {
            HRDepartment team = hrDepartmentRepository.findById(dto.getTeam().getId()).orElse(null);
            if (team != null) {
                entity.setTeam(team);
            }
        } else {
            entity.setTeam(null);
        }
        if (entity.getRecruitmentRequestItems() != null) {
            entity.getRecruitmentRequestItems().clear();
        } else {
            entity.setRecruitmentRequestItems(new HashSet<>());
        }
        if (dto.getRecruitmentRequestItems() != null) {
            if (!CollectionUtils.isEmpty(entity.getRecruitmentRequestItems())) {
                entity.getRecruitmentRequestItems().clear();
            }
            for (RecruitmentRequestItemDto itemDto : dto.getRecruitmentRequestItems()) {
                RecruitmentRequestItem item = null;
                if (itemDto.getId() != null) {
                    item = recruitmentRequestItemRepository.findById(itemDto.getId()).orElse(null);
                }
                if (item == null) {
                    item = new RecruitmentRequestItem();
                }
                item.setRecruitmentRequest(entity);
                PositionTitle positionTitle = null;
                if (itemDto.getPositionTitle() != null && itemDto.getPositionTitle().getId() != null) {
                    positionTitle = positionTitleRepository.findById(itemDto.getPositionTitle().getId()).orElse(null);
                }
                if (positionTitle == null) continue;
                item.setPositionTitle(positionTitle);
                item.setAnnouncementQuantity(itemDto.getAnnouncementQuantity());
                item.setInPlanQuantity(itemDto.getInPlanQuantity());
                item.setExtraQuantity(itemDto.getExtraQuantity());
                item.setProfessionalLevel(itemDto.getProfessionalLevel());
                item.setProfessionalSkills(itemDto.getProfessionalSkills());
                item.setGender(itemDto.getGender());
                item.setWeight(itemDto.getWeight());
                item.setHeight(itemDto.getHeight());
                item.setYearOfExperience(itemDto.getYearOfExperience());
                item.setOtherRequirements(itemDto.getOtherRequirements());
                item.setMinimumAge(itemDto.getMinimumAge());
                item.setMaximumAge(itemDto.getMaximumAge());
                item.setMinimumIncome(itemDto.getMinimumIncome());
                item.setMaximumIncome(itemDto.getMaximumIncome());
                item.setWithinHeadcount(itemDto.getIsWithinHeadcount());
                item.setReplacementRecruitment(itemDto.getIsReplacementRecruitment());
                if (itemDto.getReplacedPerson() != null && itemDto.getReplacedPerson().getId() != null) {
                    item.setReplacedPerson(staffRepository.findOneById(itemDto.getReplacedPerson().getId()));
                } else {
                    item.setReplacedPerson(null);
                }
                item.setReason(itemDto.getReason());
                item.setWorkType(itemDto.getWorkType());
                item.setDescription(itemDto.getDescription());
                item.setRequest(itemDto.getRequest());
                entity.getRecruitmentRequestItems().add(item);
            }
        }
        if (entity.getPositionRequests() != null) {
            entity.getPositionRequests().clear();
        } else {
            entity.setPositionRequests(new HashSet<>());
        }

        if (dto.getPositionRequests() != null && !dto.getPositionRequests().isEmpty()) {
            for (PositionRecruitmentRequestDto prrDto : dto.getPositionRequests()) {
                PositionRecruitmentRequest positionRequest = new PositionRecruitmentRequest();

                // If existing ID is provided, find and update the existing entity
                if (prrDto.getId() != null) {
                    positionRequest = entity.getPositionRequests().stream()
                            .filter(pr -> pr.getId().equals(prrDto.getId()))
                            .findFirst()
                            .orElse(positionRequest);
                }

                // Set position
                if (prrDto.getPosition() != null && prrDto.getPosition().getId() != null) {
                    Position position = positionRepository.findById(prrDto.getPosition().getId())
                            .orElse(null);
                    if (position == null) {
                        continue;
                    }
                    positionRequest.setPosition(position);
                }
                positionRequest.setRequest(entity);
                // Set previous staff information (matching frontend logic)
                positionRequest.setPreviousStaffId(prrDto.getPreviousStaffId());
                positionRequest.setPreviousStaffDisplayName(prrDto.getPreviousStaffDisplayName());

                // Add to entity's collection
                entity.getPositionRequests().add(positionRequest);
            }
        }
        entity.setStatus(HrConstants.RecruitmentRequestStatus.CREATED.getValue());
        entity.setProposalDate(dto.getProposalDate());
        entity.setProposalReceiptDate(dto.getProposalReceiptDate());

        if (dto.getProposer() != null && dto.getProposer().getId() != null) {
            Staff proposer = staffRepository.findById(dto.getProposer().getId()).orElse(null);
            if (proposer == null) return null;
            entity.setProposer(proposer);
        } else {
            entity.setProposer(null);
        }

        entity = recruitmentRequestRepository.save(entity);
        recruitmentRequestPositionService.deleteByRecruitmentRequest(entity.getId());
        if (userDto.getRoles().stream().map(RoleDto::getName).toList().contains(HrConstants.SUPER_HR)) {
            if (!CollectionUtils.isEmpty(staff.getPositionList())) {
                for (PositionDto positionDto : staff.getPositionList()) {
                    if (positionDto.getDepartment().getCode().equals(HrConstants.PB_HCNS)) {
                        Position position = positionRepository.findById(positionDto.getId()).orElse(null);
                        recruitmentRequestPositionService.save(position, entity);
                    }
                }
            }
        }
        if (hrDepartment != null) {
            ApiResponse<Position> departmentP = null;
            if (entity.getTeam() != null) {
                departmentP = staffHierarchyService.getManagerPosition(entity.getTeam().getId(), HR_VIEW_RECRUITMENT_REQUEST);
                logger.info("Tìm trưởng phòng");
                // trưởng phòng của team chỉ có quyền xem thôi
                recruitmentRequestPositionService.save(departmentP.getData(), entity);
            }
            if (entity.getHrDepartment() != null) {
                departmentP = staffHierarchyService.getManagerPosition(entity.getHrDepartment().getId(), HR_CREATE_RECRUITMENT_REQUEST);
                logger.info(String.valueOf(entity.getHrDepartment().getId()));
            }
            if (departmentP != null) {
                if (departmentP.getStatus() == HttpStatus.SC_OK) {
                    logger.info("Tìm trưởng phòng");
                    recruitmentRequestPositionService.save(departmentP.getData(), entity);
                    entity.setApprovedPosition(departmentP.getData());
                    logger.info("Tìm trưởng phòng của phòng ban cha đã có trưởng phòng");
                    ApiResponse<Position> departmentPP = staffHierarchyService.getManagerPosition(departmentP.getData().getDepartment().getParent().getId(), HR_APPROVAL_RECRUITMENT_REQUEST);
                    if (departmentPP.getStatus() == HttpStatus.SC_OK) {
                        entity.setNextApprovePosition(departmentPP.getData());
                    } else {
                        logger.error(departmentPP.getMessage());
                    }
                    entity = recruitmentRequestRepository.save(entity);
                } else {
                    logger.error(departmentP.getMessage());
                }
            }
        }
        return new ApiResponse<>(HttpStatus.SC_OK, "Lưu thành công", new RecruitmentRequestDto(entity));
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteRecruitmentRequest(UUID id) {
        if (id == null)
            return false;

        RecruitmentRequest recruitmentRequest = recruitmentRequestRepository.findById(id).orElse(null);
        if (recruitmentRequest == null)
            return false;
        recruitmentRequest.setVoided(true);

        recruitmentRequestRepository.save(recruitmentRequest);
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultipleRecruitmentRequest(List<UUID> ids) {
        if (ids == null)
            return false;
        boolean isValid = true;
        for (UUID id : ids) {
            boolean deleteRes = this.deleteRecruitmentRequest(id);
            if (!deleteRes)
                isValid = false;
        }
        return isValid;
    }

    private void formalizeSearchObject(SearchRecruitmentDto dto) {
        if (dto.getFromDate() != null) {
            dto.setFromDate(DateTimeUtil.getStartOfDay(dto.getFromDate()));
        }
        if (dto.getToDate() != null) {
            dto.setToDate(DateTimeUtil.getEndOfDay(dto.getToDate()));
        }
    }

    @Override
    public Page<RecruitmentRequestDto> pagingRecruitmentRequest(SearchRecruitmentDto dto) {
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
        StaffDto staffDto = userExtService.getCurrentStaff();
        UserDto userDto = userExtService.getCurrentUser();
        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isManager = RoleUtils.hasRoleHrManager(userDto);
        boolean hasSuperHr = RoleUtils.hasRole(userDto, HrConstants.SUPER_HR);
        List<UUID> list = null;
        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(entity.id) from RecruitmentRequest as entity ";
        String sql = "select new com.globits.hr.dto.RecruitmentRequestDto(entity) from RecruitmentRequest as entity ";
        String whereClause = " where (entity.voided = false or entity.voided is null) ";
        if (staffDto != null && !isAdmin && !isManager) {
            whereClause += " AND (entity.proposer.id = :staffId OR entity.personInCharge.id = :staffId OR EXISTS (SELECT 1 FROM RecruitmentRequestPosition rrq WHERE entity.id = rrq.recruitment.id and rrq.position.id in :staffPositionIds)) ";
        }
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.code LIKE :text OR entity.name LIKE :text ) ";
        }
        if (dto.getStatus() != null) {
            whereClause += " and entity.status = :status ";
        }
        if (dto.getOrganizationId() != null) {
            whereClause += " and entity.hrOrganization.id = :hrOrganizationId ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " and entity.hrDepartment.id = :hrDepartmentId ";
        }
        if (dto.getPositionTitleId() != null) {
            whereClause += " and EXISTS (SELECT 1 FROM entity.recruitmentRequestItems item WHERE entity.id = item.recruitmentRequest.id and item.positionTitle.id = :positionTitleId) ";
        }
        if (dto.getPersonInChargeId() != null) {
            whereClause += " and entity.personInCharge.id = :personInChargeId ";
        }
        if (!CollectionUtils.isEmpty(dto.getRecruitmentRequestStatus())) {
            whereClause += " and entity.status in :status ";
        }
        if (dto.getToDate() != null) {
            whereClause += " and entity.recruitingStartDate <= :toDate ";
        }
        if (dto.getFromDate() != null) {
            whereClause += " and entity.recruitingStartDate >= :fromDate ";
        }
        if (dto.getToEndDate() != null) {
            whereClause += " and entity.recruitingEndDate <= :toEndDate ";
        }
        if (dto.getFromEndDate() != null) {
            whereClause += " and entity.recruitingEndDate >= :fromEndDate ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;
        TypedQuery<RecruitmentRequestDto> query = manager.createQuery(sql, RecruitmentRequestDto.class);
        TypedQuery<Long> qCount = manager.createQuery(sqlCount, Long.class);
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getStatus() != null) {
            query.setParameter("status", dto.getStatus());
            qCount.setParameter("status", dto.getStatus());
        }

        if (dto.getOrganizationId() != null) {
            query.setParameter("hrOrganizationId", dto.getOrganizationId());
            qCount.setParameter("hrOrganizationId", dto.getOrganizationId());
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("hrDepartmentId", dto.getDepartmentId());
            qCount.setParameter("hrDepartmentId", dto.getDepartmentId());
        }
        if (dto.getPositionTitleId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitleId());
            qCount.setParameter("positionTitleId", dto.getPositionTitleId());
        }
        if (staffDto != null && !isAdmin && !isManager) {
            list = staffDto.getPositionList().stream().map(PositionDto::getId).toList();
            query.setParameter("staffPositionIds", list);
            qCount.setParameter("staffPositionIds", list);
        }
        if (dto.getPersonInChargeId() != null) {
            query.setParameter("personInChargeId", dto.getPersonInChargeId());
            qCount.setParameter("personInChargeId", dto.getPersonInChargeId());
        }
        if (staffDto != null && !isAdmin && !isManager) {
            query.setParameter("staffId", staffDto.getId());
            qCount.setParameter("staffId", staffDto.getId());
        }
        if (!CollectionUtils.isEmpty(dto.getRecruitmentRequestStatus())) {
            List<Integer> recruitmentRequestStatus = dto.getRecruitmentRequestStatus().stream().map(HrConstants.RecruitmentRequestStatus::getValue).toList();
            query.setParameter("status", recruitmentRequestStatus);
            qCount.setParameter("status", recruitmentRequestStatus);
        }
        if (dto.getToDate() != null) {
            query.setParameter("toDate", DateTimeUtil.getEndOfDay(dto.getToDate()));
            qCount.setParameter("toDate", DateTimeUtil.getEndOfDay(dto.getToDate()));
        }
        if (dto.getFromDate() != null) {
            query.setParameter("fromDate", DateTimeUtil.getStartOfDay(dto.getFromDate()));
            qCount.setParameter("fromDate", DateTimeUtil.getStartOfDay(dto.getFromDate()));
        }
        if (dto.getToEndDate() != null) {
            query.setParameter("toEndDate", DateTimeUtil.getEndOfDay(dto.getToEndDate()));
            qCount.setParameter("toEndDate", DateTimeUtil.getEndOfDay(dto.getToEndDate()));
        }
        if (dto.getFromEndDate() != null) {
            query.setParameter("fromEndDate", DateTimeUtil.getStartOfDay(dto.getFromEndDate()));
            qCount.setParameter("fromEndDate", DateTimeUtil.getStartOfDay(dto.getFromEndDate()));
        }
        long count = qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<RecruitmentRequestDto> entities = query.getResultList();
        if ((!CollectionUtils.isEmpty(list) || isAdmin || isManager || hasSuperHr) && !CollectionUtils.isEmpty(entities)) {
            for (RecruitmentRequestDto recruitmentRequestDto : entities) {
                if (isAdmin || isManager || hasSuperHr) {
                    recruitmentRequestDto.setApprovePermission(true);
                    recruitmentRequestDto.setEditPermission(true);

                } else {
                    if (list.contains(recruitmentRequestDto.getNextApprovePosition()) && !recruitmentRequestDto.getStatus().equals(HrConstants.RecruitmentRequestStatus.CREATED.getValue())) {
                        recruitmentRequestDto.setApprovePermission(true);
                    }
                    // chỉ được sửa khi chưa gửi lên
                    if (recruitmentRequestDto.getStatus().equals(HrConstants.RecruitmentRequestStatus.CREATED.getValue())) {
                        recruitmentRequestDto.setEditPermission(true);
                        if (list.contains(recruitmentRequestDto.getApprovedPosition())) {
                            recruitmentRequestDto.setSentPermission(true);
                        }
                    }
                }
            }
        }
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    @Modifying
    public ApiResponse<Boolean> updateRequestsStatus(SearchRecruitmentDto dto) {
        if (dto == null)
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Thất bại", Boolean.FALSE);
        if (dto.getStatus() == null || dto.getChosenIds() == null || dto.getChosenIds().isEmpty())
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Thất bại", Boolean.FALSE);
        for (UUID requestId : dto.getChosenIds()) {
            if (dto.getStatus().equals(HrConstants.ApproveStatus.Approve.getValue())) {
                return approveRecruitmentRequest(requestId, Boolean.TRUE);
            } else {
                return approveRecruitmentRequest(requestId, Boolean.FALSE);
            }
        }
        return new ApiResponse<>(HttpStatus.SC_OK, "Thành công", Boolean.TRUE);
    }

    // hàm chỉ dùng cho Linh Anh vì Linh Anh chỉ tạo 1 bản ghi cho 1 vị trí tuyển dụng
    @Override
    public ApiResponse<Boolean> approveRecruitmentRequest(UUID recruitmentRequestId, Boolean isApproved) {
        if (recruitmentRequestId != null) {
            RecruitmentRequest request = recruitmentRequestRepository.findById(recruitmentRequestId).orElse(null);
            if (request != null) {
                StaffDto staff = userExtService.getCurrentStaff();
                UserDto userDto = userExtService.getCurrentUser();
                boolean approve = RoleUtils.hasRole(userDto, HR_APPROVAL_RECRUITMENT_REQUEST);
                boolean create = RoleUtils.hasRole(userDto, HR_CREATE_RECRUITMENT_REQUEST);
                boolean hasSuperHr = RoleUtils.hasRole(userDto, SUPER_HR);
                boolean isAdmin = roleService.hasRoleAdmin();
                if (staff != null) {
                    List<UUID> list = new ArrayList<>();
                    if (staff.getPositionList() != null && !staff.getPositionList().isEmpty()) {
                        list = staff.getPositionList().stream().map(PositionDto::getId).toList();
                    }
                    if (isAdmin || hasSuperHr || !CollectionUtils.isEmpty(list)) {
                        if (isAdmin || approve || hasSuperHr || create) {
                            if (isApproved != null) {
                                if (isApproved) {
                                    if (request.getStatus().equals(HrConstants.RecruitmentRequestStatus.CREATED.getValue())) {
                                        // tài khoản có role super admin có quyền làm hộ
                                        if (hasSuperHr || isAdmin) {
                                            // Nếu y/c ko có vị trí khi ngay lúc tạo thì gửi thẳng cho HR leader
                                            if (request.getApprovedPosition() != null) {
                                                ApiResponse<Position> nextPositionApprove = staffHierarchyService.getManagerPosition(request.getApprovedPosition().getDepartment().getParent().getId(), HR_APPROVAL_RECRUITMENT_REQUEST);
                                                if (nextPositionApprove.getStatus() == HttpStatus.SC_OK) {
                                                    request.setNextApprovePosition(nextPositionApprove.getData());
                                                    recruitmentRequestPositionService.save(request.getNextApprovePosition(), request);
                                                    request.setStatus(HrConstants.RecruitmentRequestStatus.HR_LEADER.getValue());
                                                    recruitmentRequestRepository.save(request);
                                                    assignToHrLeader(request);
                                                    return new ApiResponse<>(HttpStatus.SC_OK, "Đã chuyển cho vị trí trưởng phòng HR", true);
                                                }
                                            } else {
                                                request.setStatus(HrConstants.RecruitmentRequestStatus.HR_LEADER.getValue());
                                                assignToHrLeader(request);
                                                recruitmentRequestRepository.save(request);
                                                return new ApiResponse<>(HttpStatus.SC_OK, "Đã chuyển cho vị trí trưởng phòng HR", true);
                                            }
                                        }
                                        // tk bth phê quyệt
                                        if (list.contains(request.getApprovedPosition().getId())) {
                                            ApiResponse<Position> nextPositionApprove = staffHierarchyService.getManagerPosition(request.getApprovedPosition().getDepartment().getParent().getId(), HR_APPROVAL_RECRUITMENT_REQUEST);
                                            if (nextPositionApprove.getStatus() == HttpStatus.SC_OK) {
                                                request.setStatus(HrConstants.RecruitmentRequestStatus.SENT.getValue());
                                                request.setNextApprovePosition(nextPositionApprove.getData());
                                                recruitmentRequestPositionService.save(request.getNextApprovePosition(), request);
                                                recruitmentRequestRepository.save(request);
                                                return new ApiResponse<>(HttpStatus.SC_OK, "Đã chuyển cho vị trí trưởng phòng cấp trên", true);
                                            } else {
                                                logger.error(nextPositionApprove.getMessage());
                                                return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, nextPositionApprove.getMessage(), true);
                                            }
                                        } else {
                                            return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Bạn không có quyền chuyển phiếu này", Boolean.FALSE);
                                        }
                                    } else {
                                        if (!CollectionUtils.isEmpty(request.getRecruitmentRequestItems())) {
                                            for (RecruitmentRequestItem item : request.getRecruitmentRequestItems()) {
                                                if (item.getWithinHeadcount() != null) {
                                                    if (item.getWithinHeadcount()) {
                                                        request.setStatus(HrConstants.RecruitmentRequestStatus.HR_LEADER.getValue());
                                                        recruitmentRequestRepository.save(request);
                                                        assignToHrLeader(request);
                                                        return new ApiResponse<>(HttpStatus.SC_OK, "Đã chuyển cho vị trí trưởng phòng HR", true);
                                                    } else {
                                                        if (hasSuperHr || isAdmin) {
                                                            ApiResponse<Position> nextPositionApprove = staffHierarchyService.getManagerPosition(request.getApprovedPosition().getDepartment().getParent().getId(), HR_APPROVAL_RECRUITMENT_REQUEST);
                                                            if (nextPositionApprove.getStatus() == HttpStatus.SC_OK) {
                                                                request.setNextApprovePosition(nextPositionApprove.getData());
                                                                recruitmentRequestPositionService.save(request.getNextApprovePosition(), request);
                                                            }
                                                            while (nextPositionApprove != null && nextPositionApprove.getStatus() != HttpStatus.SC_OK) {
                                                                request.setNextApprovePosition(nextPositionApprove.getData());
                                                                recruitmentRequestRepository.save(request);
                                                                recruitmentRequestPositionService.save(nextPositionApprove.getData(), request);
                                                                nextPositionApprove = nextPositionApprove.getData().getDepartment() != null ? staffHierarchyService.getManagerPosition(nextPositionApprove.getData().getDepartment().getParent().getId(), HR_APPROVAL_RECRUITMENT_REQUEST) : null;
                                                            }
                                                            request.setStatus(HrConstants.RecruitmentRequestStatus.HR_LEADER.getValue());
                                                            assignToHrLeader(request);
                                                            recruitmentRequestRepository.save(request);
                                                            return new ApiResponse<>(HttpStatus.SC_OK, "Đã chuyển cho vị trí trưởng phòng HR", true);
                                                        }
                                                        ApiResponse<Position> nextPositionApprove = null;
                                                        if (request.getNextApprovePosition() == null) {
                                                            nextPositionApprove = staffHierarchyService.getManagerPosition(request.getApprovedPosition().getDepartment().getParent().getId(), HR_APPROVAL_RECRUITMENT_REQUEST);
                                                        } else {
                                                            nextPositionApprove = staffHierarchyService.getManagerPosition(request.getNextApprovePosition().getDepartment().getParent().getId(), HR_APPROVAL_RECRUITMENT_REQUEST);
                                                        }
                                                        if (nextPositionApprove != null && nextPositionApprove.getStatus() == HttpStatus.SC_OK) {
                                                            request.setNextApprovePosition(nextPositionApprove.getData());
                                                            recruitmentRequestRepository.save(request);
                                                            recruitmentRequestPositionService.save(nextPositionApprove.getData(), request);
                                                            return new ApiResponse<>(HttpStatus.SC_OK, "Đã chuyển cho vị trí cấp trên thành công", true);
                                                        } else {
                                                            logger.info("Đã là cấp cuối ko còn cấp cao hơn nên đưa về cho trưởng phòng HR");
                                                            request.setStatus(HrConstants.RecruitmentRequestStatus.HR_LEADER.getValue());
                                                            assignToHrLeader(request);
                                                            recruitmentRequestRepository.save(request);
                                                            return new ApiResponse<>(HttpStatus.SC_OK, "Đã chuyển cho vị trí trưởng phòng HR", true);
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    request.setStatus(HrConstants.RecruitmentRequestStatus.REJECTED.getValue());
                                    recruitmentRequestRepository.save(request);
                                    return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Từ chối duyệt thành công", true);
                                }
                            }
                        } else {
                            return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Tài khoản của bạn không có quyền duyệt phiếu tuyển dụng này", true);
                        }
                    } else {
                        return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Tài khoản của bạn có không có quyền phê duyệt", false);
                    }
                } else {
                    return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Tài khoản này chưa gắn với nhân viên nào nên không thể duyệt", false);
                }
            }
        }
        return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Có lỗi xảy ra", false);
    }


    @Override
    public ApiResponse<List<RecruitmentRequestDto>> personInCharge(SearchRecruitmentDto searchDto) {
        if (searchDto.getStaffId() == null)
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Chưa chọn nhân viên được gán", null);

        Staff staff = staffRepository.findById(searchDto.getStaffId()).orElse(null);
        if (staff == null)
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tồn tại nhân viên được gán", null);

        if (searchDto.getChosenIds() == null || searchDto.getChosenIds().isEmpty()) {
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Chưa chọn yêu cầu tuyển dụng", null);
        }

        List<RecruitmentRequest> recruitmentRequests = recruitmentRequestRepository.findAllById(searchDto.getChosenIds());
        for (RecruitmentRequest recruitmentRequest : recruitmentRequests) {
            recruitmentRequest.setPersonInCharge(staff);
            if (searchDto.getRecruitingStartDate() != null) {
                recruitmentRequest.setRecruitingStartDate(searchDto.getRecruitingStartDate());
            } else {
                recruitmentRequest.setRecruitingStartDate(new Date());
            }
            recruitmentRequest.setStatus(HrConstants.RecruitmentRequestStatus.START_RECRUITING.getValue());
        }

        recruitmentRequestRepository.saveAll(recruitmentRequests);
        List<RecruitmentRequestDto> recruitmentRequestDtos = new ArrayList<>();
        for (RecruitmentRequest recruitmentRequest : recruitmentRequests) {
            recruitmentRequestDtos.add(new RecruitmentRequestDto(recruitmentRequest));
        }
        return new ApiResponse<>(HttpStatus.SC_OK, "Gán nhân viên thành công", recruitmentRequestDtos);
    }

    @Override
    public Workbook exportExcel(SearchRecruitmentDto dto) {
        if (dto == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("Excel/MAU_YEU_CAU_TUYEN_DUNG.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File '" + "Excel/MAU_YEU_CAU_TUYEN_DUNG.xlsx" + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet staffSheet = workbook.getSheetAt(0);
            CellStyle dataCellStyle = ExcelUtils.createDataCellStyle(workbook);

            // Start with root level departments
            dto.setPageIndex(1);
            dto.setPageSize(Integer.MAX_VALUE);
            Page<RecruitmentRequestDto> recruitmentpage = this.pagingRecruitmentRequest(dto);
            if (recruitmentpage == null || recruitmentpage.isEmpty()) {
                return workbook; // Trả về workbook trống nếu không có dữ liệu
            }
            // Lấy danh sách phòng ban đã phân cấp
            List<RecruitmentRequestDto> recruitmentList = recruitmentpage.getContent();
            if (recruitmentList.isEmpty()) {
                return workbook; // Trả về workbook trống nếu không có dữ liệu
            }
            // Tạo một hàng mới trong sheet
            int rowIndex = 1; // Bắt đầu từ hàng thứ 2 (hàng đầu tiên là tiêu đề)
            for (RecruitmentRequestDto recruitmentRequestDto : recruitmentList) {
                // Tạo một hàng mới trong sheet
                Row dataRow = staffSheet.createRow(rowIndex);
                int cellIndex = 0;
//                stt
                ExportExcelUtil.createCell(dataRow, cellIndex++, rowIndex, dataCellStyle);

                // tên yêu cầu
                ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentRequestDto.getName(), dataCellStyle);
                // ma yêu cầu
                ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentRequestDto.getCode(), dataCellStyle);
                //trang thai
                ExportExcelUtil.createCell(dataRow, cellIndex++, HrConstants.RecruitmentRequestStatus.getDescriptionByValue(recruitmentRequestDto.getStatus()), dataCellStyle);
                //mo ta
                ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentRequestDto.getDescription(), dataCellStyle);
                //yeu cau
                ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentRequestDto.getRequest(), dataCellStyle);
                // đia diem
                ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentRequestDto.getWorkPlace(), dataCellStyle);
                //ma bo phan
                // tên bo phan
                if (recruitmentRequestDto.getTeam() != null) {
                    ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentRequestDto.getTeam().getCode(), dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentRequestDto.getTeam().getName(), dataCellStyle);
                } else {
                    ExportExcelUtil.createCell(dataRow, cellIndex++, "", dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, "", dataCellStyle);
                }
                //ma phong banD
                // tên phòng ban
                if (recruitmentRequestDto.getHrDepartment() != null) {
                    ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentRequestDto.getHrDepartment().getCode(), dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentRequestDto.getHrDepartment().getName(), dataCellStyle);
                } else {
                    ExportExcelUtil.createCell(dataRow, cellIndex++, "", dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, "", dataCellStyle);
                }
                //ma don vi
                // tên dơn vị
                if (recruitmentRequestDto.getOrganization() != null) {
                    ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentRequestDto.getOrganization().getCode(), dataCellStyle);
                    ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentRequestDto.getOrganization().getName(), dataCellStyle);
                } else {
                    ExportExcelUtil.createCell(dataRow, cellIndex++, "", dataCellStyle);
                }

                rowIndex++;
            }
            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    private void assignToHrLeader(RecruitmentRequest request) {
        List<HRDepartment> department = hrDepartmentRepository.findByCode(HrConstants.PB_HCNS);
        if (!CollectionUtils.isEmpty(department)) {
            ApiResponse<Position> position = staffHierarchyService.getManagerPosition(department.get(0).getId(), HR_APPROVAL_RECRUITMENT_REQUEST);
            if (position.getStatus() == HttpStatus.SC_OK) {
                recruitmentRequestPositionService.save(position.getData(), request);
            } else {
                logger.info(position.getMessage());
            }
        }
    }


    @Override
    public XWPFDocument generateDocx(UUID id) throws IOException {
        if (id == null) {
            return null;
        }
        RecruitmentRequest entity = recruitmentRequestRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        // Đọc template
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("PHIEU_DE_XUAT_TUYEN_DUNG.docx");

        assert inputStream != null;
        XWPFDocument document = new XWPFDocument(inputStream);
        inputStream.close();
        Map<String, String> replacements = new HashMap<>();
        // Thay thế các placeholder trong document
        String positionTitle = "";
        String hrDepartment = "";
        String quantity = "";
        String workType = "";
        String organization = "";
        String team = "";
        String workplace = "";
        String professionalLevel = "";
        String professionalSkills = "";
        String age = "";
        String gender = "";
        String weight = "";
        String height = "";
        String yearOfExperience = "";
        String otherRequirements = "";
        String replacedPerson = "";
        String reason = "";
        String fullNameApproved = "";
        String positionAppoved = "";
        String fullNameNextApprove = "";
        String positionNextApprove = "";
        String tick = "☑";
        String notTick = "☐";
        String fullNameReviewer = "";
        String positionReviewer = "";
        String income = "0(vnd)";
        String code = "";
        StringBuilder descriptions = new StringBuilder();
        if (entity.getRecruitmentRequestItems() != null && !entity.getRecruitmentRequestItems().isEmpty()) {
            RecruitmentRequestItem recruitmentRequestItem = entity.getRecruitmentRequestItems().iterator().next();

            if (recruitmentRequestItem.getPositionTitle() != null) {
                positionTitle = recruitmentRequestItem.getPositionTitle().getName();
            }

            if (recruitmentRequestItem.getAnnouncementQuantity() != null) {
                quantity = recruitmentRequestItem.getAnnouncementQuantity().toString();
            }

            if (recruitmentRequestItem.getWorkType() != null) {
                workType = String.valueOf(recruitmentRequestItem.getWorkType());
            }

            if (recruitmentRequestItem.getProfessionalLevel() != null) {
                professionalLevel = recruitmentRequestItem.getProfessionalLevel();
            }

            if (recruitmentRequestItem.getProfessionalSkills() != null) {
                professionalSkills = recruitmentRequestItem.getProfessionalSkills();
            }
            String minimumAge = null;
            String maximumAge = null;

            if (recruitmentRequestItem.getMinimumAge() != null) {
                minimumAge = String.valueOf(recruitmentRequestItem.getMinimumAge());
            }
            if (recruitmentRequestItem.getMaximumAge() != null) {
                maximumAge = String.valueOf(recruitmentRequestItem.getMaximumAge());
            }
            if (minimumAge != null && maximumAge != null) {
                age = minimumAge + " - " + maximumAge;
            } else if (minimumAge != null) {
                age = minimumAge;
            } else if (maximumAge != null) {
                age = maximumAge;
            }

            if (recruitmentRequestItem.getGender() != null) {
                gender = recruitmentRequestItem.getGender();
            }
            if (recruitmentRequestItem.getWeight() != null) {
                weight = String.valueOf(recruitmentRequestItem.getWeight());
            }
            if (recruitmentRequestItem.getHeight() != null) {
                height = String.valueOf(recruitmentRequestItem.getHeight());
            }
            if (recruitmentRequestItem.getYearOfExperience() != null) {
                yearOfExperience = recruitmentRequestItem.getYearOfExperience().toString() + " năm";
            }
            if (recruitmentRequestItem.getYearOfExperience() != null) {
                otherRequirements = String.valueOf(recruitmentRequestItem.getOtherRequirements());
            }
            if (recruitmentRequestItem.getReplacedPerson() != null && recruitmentRequestItem.getReplacedPerson().getDisplayName() != null) {
                replacedPerson = recruitmentRequestItem.getReplacedPerson().getDisplayName();
            }
            if (recruitmentRequestItem.getReason() != null) {
                reason = recruitmentRequestItem.getReason();
            }
            if (recruitmentRequestItem.getReason() != null) {
                reason = recruitmentRequestItem.getReason();
            }

            if (recruitmentRequestItem.getWithinHeadcount() != null && recruitmentRequestItem.getWithinHeadcount()) {
                replacements.put("isWithinHeadcount", tick);
                replacements.put("notWithinHeadcount", notTick);
                if (entity.getNextApprovePosition() != null) {
                    if (entity.getNextApprovePosition().getStaff() != null && entity.getNextApprovePosition().getStaff().getDisplayName() != null) {
                        fullNameNextApprove = entity.getNextApprovePosition().getStaff().getDisplayName();
                    }
                    if (entity.getNextApprovePosition().getName() != null) {
                        positionNextApprove = entity.getNextApprovePosition().getName();
                    }
                }
            } else if (recruitmentRequestItem.getReplacementRecruitment() != null && !recruitmentRequestItem.getWithinHeadcount()) {
                replacements.put("notWithinHeadcount", tick);
                replacements.put("isWithinHeadcount", notTick);
                if (entity.getNextApprovePosition() != null) {
                    ApiResponse<Position> nextPositionApprove = staffHierarchyService.getManagerPosition(entity.getNextApprovePosition().getDepartment().getParent().getId(), HR_APPROVAL_RECRUITMENT_REQUEST);
                    Position position = entity.getNextApprovePosition();
                    while (nextPositionApprove != null && nextPositionApprove.getStatus() != HttpStatus.SC_OK) {
                        nextPositionApprove = staffHierarchyService.getManagerPosition(nextPositionApprove.getData().getDepartment().getParent().getId(), HR_APPROVAL_RECRUITMENT_REQUEST);
                        if (nextPositionApprove.getStatus() == HttpStatus.SC_OK) {
                            position = nextPositionApprove.getData();
                        }
                    }
                    fullNameNextApprove = position.getStaff().getDisplayName();
                    positionNextApprove = position.getName();
                }
            } else {
                replacements.put("isWithinHeadcount", notTick);
                replacements.put("notWithinHeadcount", notTick);
            }

            if (recruitmentRequestItem.getReplacementRecruitment() != null && recruitmentRequestItem.getReplacementRecruitment()) {
                replacements.put("replaceIcon", tick);
                replacements.put("newIcon", notTick);
            } else if (recruitmentRequestItem.getReplacementRecruitment() != null && !recruitmentRequestItem.getReplacementRecruitment()) {
                replacements.put("newIcon", tick);
                replacements.put("replaceIcon", notTick);
            } else {
                replacements.put("replaceIcon", notTick);
                replacements.put("newIcon", notTick);
            }

            if (recruitmentRequestItem.getMinimumIncome() != null && recruitmentRequestItem.getMaximumIncome() != null) {
                long minimumIncomeValue = recruitmentRequestItem.getMinimumIncome().longValue();
                long maximumIncomeValue = recruitmentRequestItem.getMaximumIncome().longValue();
                income = String.format("%,d", minimumIncomeValue) + " - " + String.format("%,d", maximumIncomeValue) + " (vnd)";
            } else if (recruitmentRequestItem.getMinimumIncome() != null) {
                long minimumIncomeValue = recruitmentRequestItem.getMinimumIncome().longValue();
                income = String.format("%,d", minimumIncomeValue) + " (vnd)";
            } else if (recruitmentRequestItem.getMaximumIncome() != null) {
                long maximumIncomeValue = recruitmentRequestItem.getMaximumIncome().longValue();
                income = String.format("%,d", maximumIncomeValue) + " (vnd)";
            }
            if (recruitmentRequestItem.getDescription() != null) {
                List<String> listDescription = extractTextNodes(recruitmentRequestItem.getDescription()); // <-- lấy danh sách text từ HTML
                for (String item : listDescription) {
                    if (item != null && !item.trim().isEmpty()) {
                        descriptions.append(" - ").append(item.trim()).append("\n"); // mỗi dòng bắt đầu bằng " - "
                    }
                }
            }

        }
        if (entity.getCode() != null) {
            code = entity.getCode().toUpperCase();
        }
        if (entity.getHrDepartment() != null) {
            hrDepartment = entity.getHrDepartment().getName();
        }

        if (entity.getHrOrganization() != null && entity.getHrOrganization().getName() != null) {
            organization = entity.getHrOrganization().getName();
        }

        if (entity.getTeam() != null && entity.getTeam().getName() != null) {
            team = entity.getTeam().getName();
        }

        if (entity.getWorkPlace() != null) {
            workplace = entity.getWorkPlace().getName();
        }
        if (entity.getApprovedPosition() != null) {
            if (entity.getApprovedPosition().getStaff() != null && entity.getApprovedPosition().getStaff().getDisplayName() != null) {
                fullNameApproved = entity.getApprovedPosition().getStaff().getDisplayName();
            }
            if (entity.getApprovedPosition().getName() != null) {
                positionAppoved = entity.getApprovedPosition().getName();
            }
        }
        if (entity.getPersonInCharge() != null) {
            if (entity.getPersonInCharge().getDisplayName() != null) {
                fullNameReviewer = entity.getPersonInCharge().getDisplayName();
            }

            if (entity.getPersonInCharge().getCurrentPosition() != null &&
                    entity.getPersonInCharge().getCurrentPosition().getName() != null) {
                positionReviewer = entity.getPersonInCharge().getCurrentPosition().getName();
            }
        }

        // Thêm các thông tin chi tiết vào map replacements
        replacements.put("positionTitle", positionTitle);
        replacements.put("hrDepartment", hrDepartment);
        replacements.put("quantity", quantity);
        replacements.put("workType", workType);
        replacements.put("organization", organization);
        replacements.put("team", team);
        replacements.put("workplace", workplace);
        replacements.put("professionalLevel", professionalLevel);
        replacements.put("professionalSkills", professionalSkills);
        replacements.put("age", age);
        replacements.put("gender", gender);
        replacements.put("weight", weight);
        replacements.put("height", height);
        replacements.put("yearOfExperience", yearOfExperience);
        replacements.put("otherRequirements", otherRequirements != null ? otherRequirements : "");
        replacements.put("replacedPerson", replacedPerson);
        replacements.put("reason", reason);
        replacements.put("fullNameApproved", fullNameApproved);
        replacements.put("positionAppoved", positionAppoved);
        replacements.put("fullNameNextApprove", fullNameNextApprove);
        replacements.put("positionNextApprove", positionNextApprove);
        replacements.put("fullNameReviewer", fullNameReviewer);
        replacements.put("positionReviewer", positionReviewer);
        replacements.put("income", income);
        replacements.put("code", code);
        replacements.put("description", descriptions.toString());


        // Thay thế tất cả placeholder trong document
        List<String> replaceBold = Arrays.asList("code");
        Map<String, String> replaceColor = new HashMap<>();
        replaceColor.put("code", "ff0000"); // Mã "code" có màu đỏ

        // Gọi phương thức thay thế
        replacePlaceholdersInDocument(document, replacements, replaceBold, replaceColor);

        return document;
    }

    private void replacePlaceholdersInDocument(XWPFDocument document, Map<String, String> replacements, List<String> replaceBold, Map<String, String> replaceColor) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceInParagraph(paragraph, replacements, replaceBold, replaceColor);
        }

        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    if (cell == null) continue; // <- thêm dòng này
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        if (paragraph == null) continue; // <- thêm dòng này
                        replaceInParagraph(paragraph, replacements, replaceBold, replaceColor);
                    }
                }
            }
        }

    }

    private void replaceInParagraph(XWPFParagraph paragraph,
                                    Map<String, String> replacements,
                                    List<String> replaceBold,
                                    Map<String, String> replaceColor) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            if (run.getText(0) != null) {
                fullText.append(run.getText(0));
            }
        }

        String text = fullText.toString();

        boolean hasPlaceholder = replacements.keySet().stream()
                .anyMatch(k -> text.contains("{{" + k + "}}"));
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
                    run.setFontSize(12);
                }
                break;
            }

            if (nextPlaceholderStart > currentIndex) {
                String beforeText = text.substring(currentIndex, nextPlaceholderStart);
                XWPFRun beforeRun = paragraph.createRun();
                beforeRun.setText(beforeText);
                beforeRun.setFontFamily("Times New Roman");
                beforeRun.setFontSize(12);
            }

            int nextPlaceholderEnd = text.indexOf("}}", nextPlaceholderStart);
            if (nextPlaceholderEnd == -1) break;

            String placeholderKey = text.substring(nextPlaceholderStart + 2, nextPlaceholderEnd);
            String replacement = replacements.getOrDefault(placeholderKey, "");

            if (replacement.contains("\n")) {
                String[] lines = replacement.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    XWPFRun lineRun = paragraph.createRun();
                    lineRun.setText(lines[i]);
                    lineRun.setFontFamily("Times New Roman");
                    lineRun.setFontSize(12);

                    if (replaceBold != null && replaceBold.contains(placeholderKey)) {
                        lineRun.setBold(true);
                    }
                    if (replaceColor != null && replaceColor.containsKey(placeholderKey)) {
                        lineRun.setColor(replaceColor.get(placeholderKey));
                    }

                    if (i < lines.length - 1) {
                        lineRun.addBreak();  // Xuống dòng
                    }
                }
            } else {
                XWPFRun replaceRun = paragraph.createRun();
                replaceRun.setText(replacement);
                replaceRun.setFontFamily("Times New Roman");
                replaceRun.setFontSize(12);

                if (replaceBold != null && replaceBold.contains(placeholderKey)) {
                    replaceRun.setBold(true);
                }
                if (replaceColor != null && replaceColor.containsKey(placeholderKey)) {
                    replaceRun.setColor(replaceColor.get(placeholderKey));
                }
            }

            currentIndex = nextPlaceholderEnd + 2;
        }
    }

    public static List<String> extractTextNodes(String html) {
        List<String> result = new ArrayList<>();

        // Regex tìm nội dung giữa các thẻ: <tag>nội_dung</tag>
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


    @Override
    public Workbook exportRecruitmentRequestReport(SearchRecruitmentDto dto) throws IOException {
        if (dto == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("Excel/BAO_CAO_TUYEN_DUNG.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File '" + "BAO_CAO_TUYEN_DUNG" + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);

            Sheet reportSheet = workbook.getSheetAt(0);
            CellStyle dataCellStyle = ExportExcelUtil.createDataCellStyle(workbook);

            int pageIndex = 1;
            int rowIndex = 1;
            int orderNumber = 1;
            boolean hasNextPage = true;
            long startTime = System.nanoTime();

            while (hasNextPage) {
                // searchStaffDto = new SearchStaffDto();
                dto.setPageIndex(pageIndex);
                dto.setPageSize(50);

                Page<RecruitmentRequestReportDto> recruitmentRequestsPage = this.pagingRecruitmentRequestReport(dto);
                if (recruitmentRequestsPage == null || recruitmentRequestsPage.isEmpty()) {
                    break;
                }

                for (RecruitmentRequestReportDto recruitmentRequest : recruitmentRequestsPage) {
                    if (recruitmentRequest == null) continue;

                    Row dataRow = reportSheet.createRow(rowIndex);
                    int cellIndex = 0;

                    // 0. STT
                    ExportExcelUtil.createCell(dataRow, 0, orderNumber, dataCellStyle);

                    // 1. ĐƠN VỊ
                    ExportExcelUtil.createCell(dataRow, 1, recruitmentRequest.getUnit(), dataCellStyle);

                    // 2. Ban
                    ExportExcelUtil.createCell(dataRow, 2, recruitmentRequest.getDivision(), dataCellStyle);

                    // 3. PHÒNG/CƠ SỞ
                    ExportExcelUtil.createCell(dataRow, 3, recruitmentRequest.getDepartment(), dataCellStyle);

                    // 4. BỘ PHẬN
                    ExportExcelUtil.createCell(dataRow, 4, recruitmentRequest.getSubDepartment(), dataCellStyle);

                    // 5. NGƯỜI ĐỀ XUẤT
                    ExportExcelUtil.createCell(dataRow, 5, recruitmentRequest.getProposer(), dataCellStyle);

                    // 6. VỊ TRÍ ĐỀ XUẤT TUYỂN DỤNG --> Chức danh tuyển dụng
                    ExportExcelUtil.createCell(dataRow, 6, recruitmentRequest.getProposedPosition(), dataCellStyle);

                    // 7. NGÀY NHẬN ĐỀ XUẤT
                    ExportExcelUtil.createCell(dataRow, 7, recruitmentRequest.getProposalDate(), dataCellStyle);

                    // 8. THỜI HẠN TUYỂN DỤNG ( Đơn vị ngày)
                    ExportExcelUtil.createCell(dataRow, 8, recruitmentRequest.getRecruitmentDurationDays(), dataCellStyle);

                    // 9. NGÀY HẾT HẠN TUYỂN DỤNG
                    ExportExcelUtil.createCell(dataRow, 9, recruitmentRequest.getRecruitmentDeadline(), dataCellStyle);

                    // 10. TÌNH TRẠNG
                    ExportExcelUtil.createCell(dataRow, 10, recruitmentRequest.getStatus(), dataCellStyle);

                    // 11. TUYỂN MỚI
                    ExportExcelUtil.createCell(dataRow, 11, recruitmentRequest.getIsNewRecruitment(), dataCellStyle);

                    // 12. TUYỂN THAY THẾ
                    ExportExcelUtil.createCell(dataRow, 12, recruitmentRequest.getIsReplacementRecruitment(), dataCellStyle);

                    // 13. Tuyển trong định biên
                    ExportExcelUtil.createCell(dataRow, 13, recruitmentRequest.getIsWithinHeadcount(), dataCellStyle);

                    // 14. TUYỂN LỌC --> tuyển ngoài định biên
                    ExportExcelUtil.createCell(dataRow, 14, recruitmentRequest.getIsOutOfHeadcount(), dataCellStyle);

                    // 15. SỐ LƯỢNG YÊU CẦU TUYỂN DỤNG
                    ExportExcelUtil.createCell(dataRow, 15, recruitmentRequest.getRequestedQuantity(), dataCellStyle);

                    // 16. SỐ LƯỢNG NHÂN SỰ NHẬN VIỆC
                    ExportExcelUtil.createCell(dataRow, 16, recruitmentRequest.getOnboardedQuantity(), dataCellStyle);

                    // 17. SỐ LƯỢNG NHÂN SỰ CÒN LẠI CẦN TUYỂN DỤNG
                    ExportExcelUtil.createCell(dataRow, 17, recruitmentRequest.getRemainingQuantity(), dataCellStyle);

                    // 18. Số LƯỢNG NHÂN SỰ CHỜ NHẬN VIỆC
                    ExportExcelUtil.createCell(dataRow, 18, recruitmentRequest.getPendingOnboardQuantity(), dataCellStyle);

                    // 19. Nguồn đăng tuyển
                    ExportExcelUtil.createCell(dataRow, 19, recruitmentRequest.getRecruitmentSource(), dataCellStyle);

                    // 20. NGÀY CHỐT OFFER VỚI ỨNG VIÊN
                    ExportExcelUtil.createCell(dataRow, 20, recruitmentRequest.getOfferClosedDate(), dataCellStyle);

                    // 21. NGÀY ỨNG VIÊN ONBOARD
                    ExportExcelUtil.createCell(dataRow, 21, recruitmentRequest.getOnboardDate(), dataCellStyle);

                    // 22. SỐ LƯỢNG NHÂN SỰ TỪ CHỐI OFFER
                    ExportExcelUtil.createCell(dataRow, 22, recruitmentRequest.getOfferDeclinedQuantity(), dataCellStyle);

                    // 23. SỐ LƯỢNG NHÂN SỰ NGHỈ VIỆC TRONG THỜI GIAN THỬ VIỆC
                    ExportExcelUtil.createCell(dataRow, 23, recruitmentRequest.getProbationQuitQuantity(), dataCellStyle);

                    // 24. HR PHỤ TRÁCH
                    ExportExcelUtil.createCell(dataRow, 24, recruitmentRequest.getHrInCharge(), dataCellStyle);

                    // 25. NOTE
                    ExportExcelUtil.createCell(dataRow, 25, recruitmentRequest.getNote(), dataCellStyle);


                    // thêm dòng tiếp theo
                    rowIndex++;
                    ++orderNumber;

                }

                hasNextPage = recruitmentRequestsPage.hasNext(); // Kiểm tra xem còn trang tiếp theo không
                pageIndex++; // Tăng pageIndex để lấy trang tiếp theo
            }
            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất sổ quản lý lao động nhân viên - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ApiResponse<Boolean> checkNumberIsWithinHeadcount(UUID departmentId, UUID positionTileId, Integer announcementQuantity) {
        //announcementQuantity
        HrResourcePlan hrResourcePlan = hrResourcePlanRepository.findByDepartment(departmentId);
        if (hrResourcePlan != null) {
            if (!CollectionUtils.isEmpty(hrResourcePlan.getResourcePlanItems())) {
                for (HrResourcePlanItem hrResourcePlanItem : hrResourcePlan.getResourcePlanItems()) {
                    if (hrResourcePlanItem.getPositionTitle() != null) {
                        if (hrResourcePlanItem.getPositionTitle().getId().equals(positionTileId)) {
                            if (announcementQuantity > hrResourcePlanItem.getAdditionalNumber()) {
                                return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Cảnh báo chỉ có " + hrResourcePlanItem.getAdditionalNumber() + " còn trống", false);
                            }
                        }
                    }
                }
            }
        }
        return new ApiResponse<>(HttpStatus.SC_OK, null, true);
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = recruitmentRequestRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }

    @Override
    public ApiResponse<Boolean> changeStatus(List<UUID> ids, RecruitmentRequestStatus status) {
        if (ids == null || ids.isEmpty()) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Thất bại", false);
        }
        for (UUID id : ids) {
            RecruitmentRequest recruitmentRequest = recruitmentRequestRepository.findById(id).orElse(null);
            if (recruitmentRequest == null) {
                continue;
            }
            recruitmentRequest.setStatus(status.getValue());
            recruitmentRequestRepository.save(recruitmentRequest);
        }
        return new ApiResponse<>(HttpStatus.SC_OK, "Thành công", true);
    }


    @Override
    public Page<RecruitmentRequestReportDto> pagingRecruitmentRequestReport(SearchRecruitmentDto dto) {
        if (dto == null) return null;

        Page<RecruitmentRequest> requestPage = this.pagingRecruitmentRequestEntity(dto);
        List<RecruitmentRequest> requestList = requestPage.getContent();

        List<RecruitmentRequestReportDto> response = new ArrayList<>();

        for (RecruitmentRequest recruitmentRequest : requestList) {
            RecruitmentRequestReportDto responseItem = new RecruitmentRequestReportDto(recruitmentRequest);
            response.add(responseItem);
            String postingSources = this.getStringJoinedPostingSource(recruitmentRequest);
            responseItem.setRecruitmentSource(postingSources);
        }

        Pageable pageable = PageRequest.of(requestPage.getPageable().getPageNumber(), requestPage.getPageable().getPageSize());
        return new PageImpl<>(response, pageable, requestPage.getTotalElements());
    }

    private String getStringJoinedPostingSource(RecruitmentRequest recruitmentRequest) {
        Set<String> response = new HashSet<>();

        if (recruitmentRequest == null || recruitmentRequest.getRecruitmentPlans() == null || recruitmentRequest.getRecruitmentPlans().isEmpty()) {
            return "";
        }

        for (RecruitmentPlan recruitmentPlan : recruitmentRequest.getRecruitmentPlans()) {
            if (recruitmentPlan != null && StringUtils.hasText(recruitmentPlan.getPostingSource())) {
                String normalized = normalizePostingSource(recruitmentPlan.getPostingSource());
                if (!normalized.isEmpty()) {
                    response.add(normalized);
                }
            }
        }

        return String.join(", ", response);
    }


    // Hàm chuẩn hóa chuỗi
    private String normalizePostingSource(String input) {
        if (input == null) return "";

        // Xóa khoảng trắng thừa, chỉ giữ lại 1 khoảng giữa các từ
        String trimmed = input.trim().replaceAll("\\s+", " ");

        // Viết hoa chữ cái đầu tiên, giữ nguyên các ký tự còn lại
        if (trimmed.isEmpty()) return "";

        return trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1);
    }


    @Override
    public Page<RecruitmentRequest> pagingRecruitmentRequestEntity(SearchRecruitmentDto dto) {
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
        StaffDto staffDto = userExtService.getCurrentStaff();
        UserDto userDto = userExtService.getCurrentUser();
        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isManager = RoleUtils.hasRoleHrManager(userDto);
        boolean hasSuperHr = RoleUtils.hasRole(userDto, HrConstants.SUPER_HR);
        boolean isHrAccount = false;
        if (staffDto != null) {
            if (staffDto.getDepartment() != null && staffDto.getDepartment().getCode().equals(HrConstants.PB_HCNS)) {
                isHrAccount = true;
            }
        }
        List<UUID> list = null;
        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(entity.id) from RecruitmentRequest as entity ";
        String sql = "select (entity) from RecruitmentRequest as entity ";
        String whereClause = " where (entity.voided = false or entity.voided is null) ";
        if (staffDto != null && !isAdmin && !isManager && !hasSuperHr && !isHrAccount) {
            whereClause += " AND (entity.personInCharge.id = :staffId OR EXISTS (SELECT 1 FROM RecruitmentRequestPosition rrq WHERE entity.id = rrq.recruitment.id and rrq.position.id in :staffPositionIds)) ";
        }
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.code LIKE :text OR entity.name LIKE :text ) ";
        }
        if (dto.getStatus() != null) {
            whereClause += " and entity.status = :status ";
        }

        if (dto.getOrganizationId() != null) {
            whereClause += " and entity.hrOrganization.id = :hrOrganizationId ";
        }
        if (dto.getDepartmentId() != null) {
            whereClause += " and entity.hrDepartment.id = :hrDepartmentId ";
        }
        if (dto.getPositionTitleId() != null) {
            whereClause += " and EXISTS (SELECT 1 FROM entity.recruitmentRequestItems item WHERE entity.id = item.recruitmentRequest.id and item.positionTitle.id = :positionTitleId) ";
        }
        if (dto.getPersonInChargeId() != null) {
            whereClause += " and entity.personInCharge.id = :personInChargeId ";
        }
        if (dto.getToDate() != null) {
            whereClause += " and entity.recruitingStartDate <= :toDate ";
        }
        if (dto.getFromDate() != null) {
            whereClause += " and entity.recruitingStartDate >= :fromDate ";
        }
        if (dto.getToEndDate() != null) {
            whereClause += " and entity.recruitingEndDate <= :toEndDate ";
        }
        if (dto.getFromEndDate() != null) {
            whereClause += " and entity.recruitingEndDate >= :fromEndDate ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;
        TypedQuery<RecruitmentRequest> query = manager.createQuery(sql, RecruitmentRequest.class);
        TypedQuery<Long> qCount = manager.createQuery(sqlCount, Long.class);
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getStatus() != null) {
            query.setParameter("status", dto.getStatus());
            qCount.setParameter("status", dto.getStatus());
        }

        if (dto.getOrganizationId() != null) {
            query.setParameter("hrOrganizationId", dto.getOrganizationId());
            qCount.setParameter("hrOrganizationId", dto.getOrganizationId());
        }
        if (dto.getDepartmentId() != null) {
            query.setParameter("hrDepartmentId", dto.getDepartmentId());
            qCount.setParameter("hrDepartmentId", dto.getDepartmentId());
        }
        if (dto.getPositionTitleId() != null) {
            query.setParameter("positionTitleId", dto.getPositionTitleId());
            qCount.setParameter("positionTitleId", dto.getPositionTitleId());
        }
        if (staffDto != null && !isAdmin && !isManager) {
            list = staffDto.getPositionList().stream().map(PositionDto::getId).toList();
            query.setParameter("staffPositionIds", list);
            qCount.setParameter("staffPositionIds", list);
        }
        if (dto.getPersonInChargeId() != null) {
            query.setParameter("personInChargeId", dto.getPersonInChargeId());
            qCount.setParameter("personInChargeId", dto.getPersonInChargeId());
        }
        if (staffDto != null && !isAdmin && !isManager && !hasSuperHr && !isHrAccount) {
            query.setParameter("staffId", staffDto.getId());
            qCount.setParameter("staffId", staffDto.getId());
        }
        if (dto.getToDate() != null) {
            query.setParameter("toDate", DateTimeUtil.getEndOfDay(dto.getToDate()));
            qCount.setParameter("toDate", DateTimeUtil.getEndOfDay(dto.getToDate()));
        }
        if (dto.getFromDate() != null) {
            query.setParameter("fromDate", DateTimeUtil.getStartOfDay(dto.getFromDate()));
            qCount.setParameter("fromDate", DateTimeUtil.getStartOfDay(dto.getFromDate()));
        }
        if (dto.getToEndDate() != null) {
            query.setParameter("toEndDate", DateTimeUtil.getEndOfDay(dto.getToEndDate()));
            qCount.setParameter("toEndDate", DateTimeUtil.getEndOfDay(dto.getToEndDate()));
        }
        if (dto.getFromEndDate() != null) {
            query.setParameter("fromEndDate", DateTimeUtil.getStartOfDay(dto.getFromEndDate()));
            qCount.setParameter("fromEndDate", DateTimeUtil.getStartOfDay(dto.getFromEndDate()));
        }
        long count = qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<RecruitmentRequest> entities = query.getResultList();

        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    @Transactional
    public Integer saveListRecruitmentRequest(List<RecruitmentRequestDto> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }

        int savedCount = 0;

        for (RecruitmentRequestDto dto : list) {
            if (!StringUtils.hasText(dto.getCode())) {
                continue; // Bỏ qua nếu không có mã
            }

            RecruitmentRequest entity = null;
            List<RecruitmentRequest> recruitmentRequestList = recruitmentRequestRepository.findByCode(dto.getCode());
            if (recruitmentRequestList != null && !recruitmentRequestList.isEmpty()) {
                entity = recruitmentRequestList.get(0);
            }

            if (entity == null) {
                entity = new RecruitmentRequest();
            }

            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            if (dto.getWorkPlace() != null) {
                entity.setWorkPlace(workplaceRepository.findById(dto.getWorkPlace().getId()).orElse(null));
            } else {
                entity.setWorkPlace(null);
            }
            entity.setDescription(dto.getDescription());
            entity.setRequest(dto.getRequest());
            entity.setStatus(HrConstants.RecruitmentRequestStatus.CREATED.getValue());

            // Set tổ chức
            HrOrganization organization = null;
            if (dto.getOrganization() != null && StringUtils.hasText(dto.getOrganization().getCode())) {
                List<HrOrganization> organizationList = hrOrganizationRepository.findByCode(dto.getOrganization().getCode());
                if (organizationList != null && !organizationList.isEmpty()) {
                    organization = organizationList.get(0);
                }
            }
            entity.setHrOrganization(organization);
            // Set phòng ban
            HRDepartment hrDepartment = null;
            if (dto.getHrDepartment() != null && StringUtils.hasText(dto.getHrDepartment().getCode())) {
                List<HRDepartment> departmentList = hrDepartmentRepository.findByCode(dto.getHrDepartment().getCode());
                if (departmentList != null && !departmentList.isEmpty()) {
                    hrDepartment = departmentList.get(0);
                }
            }
            entity.setHrDepartment(hrDepartment);
            // Set nhóm / team
            HRDepartment team = null;
            if (dto.getTeam() != null && StringUtils.hasText(dto.getTeam().getCode())) {
                List<HRDepartment> teamList = hrDepartmentRepository.findByCode(dto.getTeam().getCode());
                if (teamList != null && !teamList.isEmpty()) {
                    team = teamList.get(0);
                }
            }
            entity.setTeam(team);

            // Xoá các item cũ
            if (entity.getRecruitmentRequestItems() != null) {
                entity.getRecruitmentRequestItems().clear();
            } else {
                entity.setRecruitmentRequestItems(new HashSet<>());
            }

            // Thêm item mới
            if (dto.getRecruitmentRequestItems() != null) {
                for (RecruitmentRequestItemDto itemDto : dto.getRecruitmentRequestItems()) {
                    RecruitmentRequestItem item = new RecruitmentRequestItem();
                    item.setRecruitmentRequest(entity);

                    if (itemDto.getPositionTitle() != null && StringUtils.hasText(itemDto.getPositionTitle().getCode())) {
                        List<PositionTitle> positionTitleList = positionTitleRepository.findByCode(itemDto.getPositionTitle().getCode());
                        if (positionTitleList != null && !positionTitleList.isEmpty()) {
                            item.setPositionTitle(positionTitleList.get(0));
                        }
                    }

                    item.setAnnouncementQuantity(itemDto.getAnnouncementQuantity());
                    item.setProfessionalLevel(itemDto.getProfessionalLevel());
                    item.setProfessionalSkills(itemDto.getProfessionalSkills());
                    item.setGender(itemDto.getGender());
                    item.setWeight(itemDto.getWeight());
                    item.setHeight(itemDto.getHeight());
                    item.setYearOfExperience(itemDto.getYearOfExperience());
                    item.setOtherRequirements(itemDto.getOtherRequirements());
                    item.setMinimumAge(itemDto.getMinimumAge());
                    item.setMaximumAge(itemDto.getMaximumAge());
                    item.setMinimumIncome(itemDto.getMinimumIncome());
                    item.setMaximumIncome(itemDto.getMaximumIncome());
                    item.setReplacementRecruitment(itemDto.getIsReplacementRecruitment());
                    item.setWithinHeadcount(itemDto.getIsWithinHeadcount());
                    item.setDescription(itemDto.getDescription());
                    item.setRequest(itemDto.getRequest());
                    item.setReason(itemDto.getReason());
                    item.setWorkType(itemDto.getWorkType());

                    // Nhân viên được thay thế
                    Staff staff = null;
                    if (itemDto.getReplacedPerson() != null && StringUtils.hasText(itemDto.getReplacedPerson().getStaffCode())) {
                        List<Staff> staffList = staffRepository.getByCode(itemDto.getReplacedPerson().getStaffCode());
                        if (staffList != null && !staffList.isEmpty()) {
                            staff = staffList.get(0);
                        }
                    }
                    item.setReplacedPerson(staff);
                    item.setRecruitmentRequest(entity);
                    entity.getRecruitmentRequestItems().add(item);
                }
            }
            entity.setVoided(false);
            recruitmentRequestRepository.save(entity);
            savedCount++;
        }

        return savedCount;
    }

    @Override
    public Page<RecruitmentRequestSummary> getRecruitmentRequestSummaries(RecruitmentRequestSummarySearch summary) {
        int pageIndex = summary.getPageIndex() > 0 ? summary.getPageIndex() - 1 : 0;
        int pageSize = summary.getPageIndex() < 0 ? 10 : summary.getPageSize();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return recruitmentRequestRepository.getRecruitmentRequestSummaries(summary.getRequestId(),
                summary.getPlainId(),
                summary.getKeyword(),
                DateTimeUtil.getStartOfDay(summary.getFromDate()),
                DateTimeUtil.getEndOfDay(summary.getToDate()),
                DateTimeUtil.getStartOfDay(summary.getFromEndDate()),
                DateTimeUtil.getEndOfDay(summary.getToEndDate()),
                pageable);
    }
}
