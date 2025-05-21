package com.globits.hr.service.impl;

import com.globits.core.repository.DepartmentRepository;
import com.globits.hr.domain.EvaluationForm;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffEvaluation;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.EvaluationFormSearchDto;
import com.globits.hr.dto.view.EvaluationFormViewDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.EvaluationFormService;
import com.globits.hr.service.HrRoleService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.Const;
import com.globits.security.dto.UserDto;
import org.apache.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EvaluationFormServiceImpl implements EvaluationFormService {

    private final EvaluationFormRepository evaluationFormRepository;

    private final StaffRepository staffRepository;

    private final DepartmentRepository departmentRepository;

    private final ContractTypeRepository contractTypeRepository;

    private final StaffEvaluationRepository staffEvaluationRepository;

    private final EvaluationItemRepository evaluationItemRepository;

    private final PositionTitleRepository positionTitleRepository;

    private final PositionRepository positionRepository;

    private final RankTitleRepository rankTitleRepository;

    private final HrRoleService hrRoleService;

    private final UserExtService userExtService;

    public EvaluationFormServiceImpl(EvaluationFormRepository evaluationFormRepository, StaffRepository staffRepository, DepartmentRepository departmentRepository, ContractTypeRepository contractTypeRepository, StaffEvaluationRepository staffEvaluationRepository, EvaluationItemRepository evaluationItemRepository, PositionTitleRepository positionTitleRepository, PositionRepository positionRepository, RankTitleRepository rankTitleRepository, HrRoleService hrRoleService, UserExtService userExtService) {
        this.evaluationFormRepository = evaluationFormRepository;
        this.staffRepository = staffRepository;
        this.departmentRepository = departmentRepository;
        this.contractTypeRepository = contractTypeRepository;
        this.staffEvaluationRepository = staffEvaluationRepository;
        this.evaluationItemRepository = evaluationItemRepository;
        this.positionTitleRepository = positionTitleRepository;
        this.positionRepository = positionRepository;
        this.rankTitleRepository = rankTitleRepository;
        this.hrRoleService = hrRoleService;
        this.userExtService = userExtService;
    }

    /**
     * Lấy Biểu mẫu đánh giá theo ID
     *
     * @param id ID của Biểu mẫu đánh giá cần lấy
     * @return ApiResponse chứa Biểu mẫu đánh giá hoặc thông báo lỗi nếu không tìm thấy
     */
    @Override
    public ApiResponse<EvaluationFormDto> getById(UUID id) {
        if (id == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "ID không hợp lệ", null);
        }
        EvaluationForm entity = evaluationFormRepository.findById(id).orElse(null);
        List<StaffEvaluationDto> staffEvaluationDtos = staffEvaluationRepository.findByFormId(id);
        if (entity == null) {
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy Biểu mẫu đánh giá với ID: " + id, null);
        }

        return new ApiResponse<>(HttpStatus.SC_OK, "Thành công", new EvaluationFormDto(entity, staffEvaluationDtos));
    }

    /**
     * Lưu hoặc cập nhật Biểu mẫu đánh giá
     *
     * @param dto Dữ liệu Biểu mẫu đánh giá cần lưu hoặc cập nhật
     * @return ApiResponse chứa Biểu mẫu đánh giá đã lưu hoặc cập nhật
     */
    @Override
    @Transactional
    public ApiResponse<EvaluationFormDto> saveOrUpdate(EvaluationFormDto dto) {
        if (dto == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Biểu mẫu đánh giá không hợp lệ", null);
        }
        if (dto.getId() == null) {
            if (!hrRoleService.hasRoleAdmin() && !hrRoleService.isHeadOfDepartment()) {
                return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Bạn không có quyền tạo biểu mẫu", null);
            }
        } else {
            if (!hrRoleService.hasRoleAdmin() && !hrRoleService.isHeadOfDepartment() && !userExtService.getCurrentStaff().getId().equals(dto.getStaffId())) {
                return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, "Bạn không có quyền sửa biểu mẫu", null);
            }
        }

        EvaluationForm entity = dto.getId() != null
                ? evaluationFormRepository.findById(dto.getId()).orElse(new EvaluationForm())
                : new EvaluationForm();

        // Fetch và thiết lập các entity liên quan nếu có ID trong DTO
        setRelatedEntities(dto, entity);

        // Gán các trường primitive
        entity.setHireDate(dto.getHireDate());
        entity.setPreviousContractDuration(dto.getPreviousContractDuration());
        entity.setDirectManagerName(dto.getDirectManagerName());
        //2. Nhận xét khác của Quản lý trực tiếp:
        entity.setAdvantage(dto.getAdvantage());
        entity.setDisadvantage(dto.getDisadvantage());
        entity.setCompanyPolicyCompliance(dto.getCompanyPolicyCompliance());
        entity.setCoworkerRelationship(dto.getCoworkerRelationship());
        entity.setSenseOfResponsibility(dto.getSenseOfResponsibility());
        //B. KẾT LUẬN:
        entity.setContractRecommendation(dto.getContractRecommendation());
        if (dto.getContractRecommendation() != null) {
            if (dto.getContractRecommendation()) {
                entity.setContractRecommendationDateFrom(dto.getContractRecommendationDateFrom());
                entity.setContractRecommendationDateTo(dto.getContractRecommendationDateTo());
                entity.setBaseSalary(dto.getBaseSalary());
                entity.setAllowanceAmount(dto.getAllowanceAmount());
                entity.setEffectiveFromDate(dto.getEffectiveFromDate());
            } else {
                clearFormContractRecommendation(entity);
            }
            if (!dto.getContractRecommendation()) {
                entity.setCooperationStatus(dto.getCooperationStatus());
                if (dto.getCooperationStatus() != null) {
                    if (dto.getCooperationStatus()) {
                        entity.setCollaborationEndDate(dto.getCollaborationEndDate());
                        entity.setNewPositionTransferDate(null);
                    } else {
                        entity.setCollaborationEndDate(null);
                        entity.setNewPositionTransferDate(dto.getNewPositionTransferDate());
                    }
                }
            }
        }
        if (entity.getEvaluationTransferStatus() == null) {
            entity.setEvaluationTransferStatus(Const.EVALUATION_TRANSFER_STATUS_ENUM.STAFF);
        }
        EvaluationForm savedEntity = evaluationFormRepository.saveAndFlush(entity);
        this.saveStaffEvaluation(savedEntity, dto.getItems());
        // Lưu tiêu chí đánh giá
        return new ApiResponse<>(HttpStatus.SC_OK, "Biểu mẫu đánh giá đã được lưu thành công", new EvaluationFormDto(savedEntity));
    }

    void clearFormContractRecommendation(EvaluationForm entity) {
        entity.setContractRecommendationDateFrom(null);
        entity.setContractRecommendationDateTo(null);
        entity.setBaseSalary(null);
        entity.setAllowanceAmount(null);
        entity.setEffectiveFromDate(null);
    }

    /**
     * Xóa Biểu mẫu đánh giá theo ID
     *
     * @param id ID của Biểu mẫu đánh giá cần xóa
     * @return ApiResponse chứa kết quả xóa
     */
    @Override
    @Transactional
    public ApiResponse<Boolean> deleteById(UUID id) {
        try {
            if (id == null || !evaluationFormRepository.existsById(id)) {
                return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy Biểu mẫu đánh giá với ID: " + id, false);
            }
            this.deleteStaffEvaluationByForm(id);
            evaluationFormRepository.deleteById(id);
            return new ApiResponse<>(HttpStatus.SC_OK, "Biểu mẫu đánh giá đã được xóa thành công", true);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), false);
        }
    }

    /**
     * Xóa tiêu chí Biểu mẫu đánh giá theo ID
     *
     * @param id ID của Biểu mẫu của các tiêu chí đánh giá cần xóa
     */
    @Transactional
    void deleteStaffEvaluationByForm(UUID id) {
        staffEvaluationRepository.deleteByFormId(id);
        evaluationFormRepository.flush();
    }

    /**
     * Lấy tất cả các Biểu mẫu đánh giá
     *
     * @return ApiResponse chứa danh sách Biểu mẫu đánh giá
     */
    @Override
    public ApiResponse<List<EvaluationFormDto>> getAll() {
        List<EvaluationFormDto> dtos = evaluationFormRepository.findAll().stream()
                .map(EvaluationFormDto::new)
                .collect(Collectors.toList());

        return new ApiResponse<>(HttpStatus.SC_OK, "Thành công", dtos);
    }

    /**
     * Đánh dấu Biểu mẫu đánh giá là đã xóa (soft delete)
     *
     * @param id ID của Biểu mẫu đánh giá cần đánh dấu xóa
     * @return ApiResponse chứa kết quả đánh dấu xóa
     */
    @Override
    public ApiResponse<Boolean> markDeleteById(UUID id) {
        if (id == null || !evaluationFormRepository.existsById(id)) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy Biểu mẫu đánh giá với ID: " + id, false);
        }

        // Tìm kiếm entity từ repository
        EvaluationForm entity = evaluationFormRepository.findById(id).orElse(null);

        if (entity == null) {
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy Biểu mẫu đánh giá với ID: " + id, false);
        }

        // Đánh dấu là đã xóa (soft delete)
        entity.setVoided(true);

        // Lưu lại thay đổi vào database
        evaluationFormRepository.save(entity);

        return new ApiResponse<>(HttpStatus.SC_OK, "Biểu mẫu đánh giá đã được đánh dấu xóa thành công", true);
    }

    @Override
    public ApiResponse<Page<EvaluationFormViewDto>> paging(EvaluationFormSearchDto searchDto) {
        int pageIndex = searchDto.getPageIndex() <= 1 ? 0 : searchDto.getPageIndex() - 1;
        int pageSize = searchDto.getPageSize() <= 0 ? 10 : searchDto.getPageSize();

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);

        if (!hrRoleService.hasRoleAdmin()) {
            Staff staff = userExtService.getCurrentStaffEntity();
            if (hrRoleService.isHeadOfDepartment()) {
                searchDto.setIsManager(Boolean.TRUE);
                searchDto.setStaffId(staff.getId());
                for (Position position : staff.getCurrentPositions()) {
                    if (position.getDepartment().getPositionManager().getId().equals(position.getId())) {
                        searchDto.setStaffDivision(position.getDepartment().getId());
                    }
                }
            } else {
                searchDto.setStaffId(staff.getId());
            }
        }

        return new ApiResponse<>(HttpStatus.SC_OK, "Ok", evaluationFormRepository.paging(
                searchDto.getStaffId(),
                searchDto.getKeyword(),
                searchDto.getStatus(),
                searchDto.getContractType(),
                searchDto.getDirectManagerId(),
                searchDto.getDepartment() != null ? searchDto.getDepartment().getId() : null,
                searchDto.getStaffDivision(),
                searchDto.getPosition() != null ? searchDto.getPosition().getId() : null,
                searchDto.getIsManager(),
                pageRequest));
    }

    /**
     * Thiết lập các entity liên quan từ DTO
     *
     * @param dto    DTO Biểu mẫu đánh giá
     * @param entity Entity Biểu mẫu đánh giá
     */
    private void setRelatedEntities(EvaluationFormDto dto, EvaluationForm entity) {
        // Fetch và thiết lập các entity liên quan nếu có ID trong DTO
        if (dto.getStaffId() != null) {
            staffRepository.findById(dto.getStaffId()).ifPresent(staff -> {
                entity.setStaff(staff);
                entity.setStaffCode(staff.getStaffCode());
                entity.setStaffName(staff.getDisplayName());
            });
        }

        if (dto.getDirectManagerId() != null) {
            staffRepository.findById(dto.getDirectManagerId()).ifPresent(staff -> {
                entity.setDirectManager(staff);
                entity.setDirectManagerName(staff.getDisplayName());
            });
        }

        if (dto.getStaffTeamId() != null) {
            departmentRepository.findById(dto.getStaffTeamId()).ifPresent(department -> {
                entity.setStaffTeam(department);
                entity.setTeam(department.getName());
            });
        }

        if (dto.getStaffDepartmentId() != null) {
            departmentRepository.findById(dto.getStaffDepartmentId()).ifPresent(department -> {
                entity.setStaffDepartment(department);
                entity.setDepartment(department.getName());
            });
        }

        if (dto.getContractTypeId() != null) {
            contractTypeRepository.findById(dto.getContractTypeId()).ifPresent(contractType -> {
                entity.setContractType(contractType);
                entity.setContractTypeName(contractType.getName());
            });
        }

        if (dto.getStaffPositionId() != null) {
            positionTitleRepository.findById(dto.getStaffPositionId()).ifPresent(position -> {
                entity.setStaffPosition(position);
                entity.setPosition(position.getName());
            });
        }
        if (dto.getStaffDivisionId() != null) {
            departmentRepository.findById(dto.getStaffDivisionId()).ifPresent(department -> {
                entity.setStaffDivision(department);
                entity.setDivision(department.getName());
            });
        }
        if (dto.getContractRecommendation() != null) {
            if (dto.getContractRecommendation()) {
                if (dto.getPositionTitleId() != null) {
                    positionTitleRepository.findById(dto.getPositionTitleId()).ifPresent(positionTitle -> {
                        entity.setPositionTitle(positionTitle);
                        entity.setPositionTitleName(positionTitle.getName());
                    });
                }
                if (dto.getRankTitleId() != null) {
                    rankTitleRepository.findById(dto.getRankTitleId()).ifPresent(rankTitle -> {
                        entity.setRankTitle(rankTitle);
                        entity.setRankTitleName(rankTitle.getName());
                    });
                }
            } else {
                entity.setPositionTitle(null);
                entity.setPositionTitleName(null);
                entity.setRankTitle(null);
                entity.setRankTitleName(null);
            }
        }

        if (dto.getCooperationStatus() != null) {
            if (!dto.getCooperationStatus()) {
                if (dto.getNewPositionId() != null) {
                    positionRepository.findById(dto.getNewPositionId()).ifPresent(position -> {
                        entity.setNewPosition(position);
                        entity.setNewPositionName(position.getName());
                    });
                } else {
                    entity.setNewPosition(null);
                    entity.setNewPositionName(null);
                }
            }
        } else {
            entity.setNewPosition(null);
            entity.setNewPositionName(null);
        }
    }

    /**
     * Thiết lập các entity liên quan từ DTO
     *
     * @param items DTO tiêu chí đánh giá của Biểu mẫu đánh giá
     * @param form  Entity Biểu mẫu đánh giá
     */
    @Transactional
    void saveStaffEvaluation(EvaluationForm form, List<StaffEvaluationDto> items) {
        staffEvaluationRepository.deleteByFormId(form.getId());
        List<StaffEvaluation> staffEvaluations = new ArrayList<>();
        if (!CollectionUtils.isEmpty(items)) {
            for (StaffEvaluationDto item : items) {
                StaffEvaluation staffEvaluation = new StaffEvaluation();
                staffEvaluation.setForm(form);
                staffEvaluation.setSelfEvaluate(item.getSelfEvaluate());
                staffEvaluation.setManagementEvaluate(item.getManagementEvaluate());
                if (item.getItemId() != null) {
                    evaluationItemRepository.findById(item.getItemId()).ifPresent(staffEvaluation::setItem);
                }
                staffEvaluations.add(staffEvaluation);
            }
        }
        if (!CollectionUtils.isEmpty(staffEvaluations)) {
            long countFail = staffEvaluations.stream()
                    .filter(s -> s.getManagementEvaluate() != null && s.getManagementEvaluate().equals(Const.EVALUATION.FAIL))
                    .count();
            long countPass = staffEvaluations.stream()
                    .filter(s -> s.getManagementEvaluate() != null && s.getManagementEvaluate().equals(Const.EVALUATION.PASS))
                    .count();
            if (countFail != 0 || countPass != 0) {
                if (countFail >= countPass) {
                    form.setStatus(Const.EVALUATION.FAIL);
                } else {
                    form.setStatus(Const.EVALUATION.PASS);
                }
            } else {
                form.setStatus(null);
            }
            evaluationFormRepository.save(form);
        }
        staffEvaluationRepository.saveAll(staffEvaluations);
    }

    /**
     * Chuyển tiếp biểu mẫu
     *
     * @param id của Biểu mẫu đánh giá
     */
    @Override
    public ApiResponse<Boolean> transferEvaluationForm(UUID id) {
        EvaluationForm form = evaluationFormRepository.findById(id).orElse(null);
        if (form == null) {
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy biểu mẫu", false);
        }
        UserDto user = userExtService.getCurrentUser();
        Staff staff = staffRepository.findByUserId(user.getId());
        String message = "OK";
        if (staff == null) {
            message = "Tài khoản hiện tại không ứng với nhân viên nào không thể đánh giá";
            return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, message, true);
        }
        if (form.getEvaluationTransferStatus().equals(Const.EVALUATION_TRANSFER_STATUS_ENUM.STAFF)) {
            if (form.getStaff() != null && form.getStaff().getId().equals(staff.getId())) {
                form.setEvaluationTransferStatus(Const.EVALUATION_TRANSFER_STATUS_ENUM.DIRECT_MANAGER);
                message = "Đã chuyển phiếu đến quản lý trực tiếp thành công";
            } else {
                message = "Bạn không phải nhân viên này không thể đánh giá";
                return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, message, true);
            }
        }
        if (form.getEvaluationTransferStatus().equals(Const.EVALUATION_TRANSFER_STATUS_ENUM.DIRECT_MANAGER)) {
            if (hrRoleService.hasRoleAdmin()) {
                if (form.getDirectManager() != null && form.getDirectManager().getId().equals(staff.getId())) {
                    form.setEvaluationTransferStatus(Const.EVALUATION_TRANSFER_STATUS_ENUM.POSITION_MANAGER);
                    message = "Đã chuyển phiếu đến quản lý phòng thành công";
                } else {
                    if (form.getDirectManager() == null) {
                        message = "Phiếu này được tạo từ khi nhân viên không có quản lý trực tiếp vui lòng tạo phiếu mới";
                        return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, message, true);
                    } else {
                        if (!form.getDirectManager().getId().equals(staff.getId())) {
                            message = "Bạn không phải quản lý trực tiếp của nhân viên này không thể chuyển cho quản lý phòng phê duyệt";
                            return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, message, true);
                        }
                    }
                }
            }
        } else {
            message = "Quản lý của nhân viên này chưa đánh giá không thể chuyển cho quản lý phòng phê duyệt";
            return new ApiResponse<>(HttpStatus.SC_FORBIDDEN, message, true);
        }
        evaluationFormRepository.save(form);
        return new ApiResponse<>(HttpStatus.SC_OK, message, true);
    }

    @Override
    public ApiResponse<Boolean> staffEvaluate(List<StaffEvaluationDto> staffEvaluationDto) {
        if (!CollectionUtils.isEmpty(staffEvaluationDto)) {
            List<StaffEvaluation> staffEvaluations = new ArrayList<>();
            for (StaffEvaluationDto staffEvaluation : staffEvaluationDto) {
                StaffEvaluation staffE = staffEvaluationRepository.findById(staffEvaluation.getId()).orElse(null);
                if (staffE != null) {
                    staffE.setSelfEvaluate(staffEvaluation.getSelfEvaluate());
                    staffEvaluations.add(staffE);
                }
            }
            staffEvaluationRepository.saveAll(staffEvaluations);
        }
        return new ApiResponse<>(HttpStatus.SC_OK, "Thành công", true);
    }

    @Override
    public ApiResponse<Page<EvaluationForm>> pageForExcel(EvaluationFormSearchDto searchDto) {
        int pageIndex = searchDto.getPageIndex() <= 1 ? 0 : searchDto.getPageIndex() - 1;
        int pageSize = searchDto.getPageSize() <= 0 ? 10 : searchDto.getPageSize();
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        return new ApiResponse<>(HttpStatus.SC_OK, "Ok", evaluationFormRepository.getPageExcelEvaluation(
                searchDto.getStaffId(),
                searchDto.getKeyword(),
                searchDto.getStatus(),
                searchDto.getContractType(),
                searchDto.getDirectManagerId(),
                searchDto.getDepartment() != null ? searchDto.getDepartment().getId() : null,
                searchDto.getPosition() != null ? searchDto.getPosition().getId() : null,
                pageRequest));
    }
}
