package com.globits.hr.service.impl;

import com.globits.hr.domain.OtherIncome;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.InterviewScheduleDto;
import com.globits.hr.dto.OtherIncomeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchOtherIncomeDto;
import com.globits.hr.repository.OtherIncomeRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.OtherIncomeService;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.repository.SalaryPeriodRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OtherIncomeServiceImpl implements OtherIncomeService {

    @PersistenceContext
    EntityManager manager;

    @Autowired
    private OtherIncomeRepository repository;


    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    /**
     * Lấy thu nhập khác theo ID
     *
     * @param id ID của thu nhập khác cần lấy
     * @return ApiResponse chứa thu nhập khác hoặc thông báo lỗi nếu không tìm thấy
     * @author QuangOliu
     */
    @Override
    public ApiResponse<OtherIncomeDto> getById(UUID id) {
        if (id == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Id is empty", null);
        }
        OtherIncome entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Can't find with id: " + id, null);
        }
        return new ApiResponse<>(HttpStatus.SC_OK, "Success", toDto(entity));
    }

    /**
     * Lấy tất cả các bản ghi thu nhập khác
     *
     * @return ApiResponse chứa danh sách thu nhập khác
     * @author QuangOliu
     */
    @Override
    public ApiResponse<List<OtherIncomeDto>> getAll() {
        List<OtherIncome> entities = repository.findAll();
        List<OtherIncomeDto> dtos = entities.stream().map(this::toDto).collect(Collectors.toList());
        return new ApiResponse<>(HttpStatus.SC_OK, "Success", dtos);
    }

    /**
     * Lưu hoặc cập nhật thu nhập khác
     *
     * @param dto Dữ liệu thu nhập khác cần lưu hoặc cập nhật
     * @return ApiResponse chứa bản ghi đã lưu hoặc cập nhật
     * @author QuangOliu
     */
    @Override
    public ApiResponse<OtherIncomeDto> save(OtherIncomeDto dto) {
        if (dto == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "DTO rỗng", null);
        }

        OtherIncome entity = dto.getId() != null ?
                repository.findById(dto.getId()).orElse(new OtherIncome()) :
                new OtherIncome();


        // Cập nhật các trường của entity từ DTO
        Staff staff = null;
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            if (staff == null) {
                return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy nhân viên với ID: " + dto.getStaff().getId(), null);
            }
        }
        if (staff == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Nhân viên không hợp lệ", null);
        }
        entity.setStaff(staff);
        SalaryPeriod salaryPeriod = null;
        if (dto.getSalaryPeriod() != null && dto.getSalaryPeriod().getId() != null) {
            salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriod().getId()).orElse(null);
        }
        entity.setSalaryPeriod(salaryPeriod);

        entity.setIncome(dto.getIncome());
        entity.setNote(dto.getNote());
        entity.setType(dto.getType());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDecisionDate(dto.getDecisionDate());

        // Lưu entity vào cơ sở dữ liệu
        OtherIncome saved = repository.save(entity);
        return new ApiResponse<>(HttpStatus.SC_OK, "Lưu thành công", toDto(saved));
    }

    /**
     * Xóa thu nhập khác theo ID
     *
     * @param id ID của bản ghi cần xóa
     * @author QuangOliu
     */
    @Override
    public void delete(UUID id) {
        if (id != null) {
            repository.deleteById(id);
        }
    }

    /**
     * Đánh dấu xóa thu nhập khác theo ID
     *
     * @param id ID của bản ghi cần xóa
     * @author QuangOliu
     */
    @Override
    public ApiResponse<Boolean> markDeleted(UUID id) {
        if (id != null) {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                return new ApiResponse<>(HttpStatus.SC_OK, "Xoá thành công", true);
            } else {
                return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "ID không hợp lệ " + id, false);
            }
        }
        return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "ID rỗng", null);
    }

    /**
     * Phân trang danh sách thu nhập khác
     *
     * @return ApiResponse chứa danh sách phân trang
     * @author QuangOliu
     */
    @Override
    public ApiResponse<Page<OtherIncomeDto>> paging(SearchOtherIncomeDto dto) {
        if (dto == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Dữ liệu tìm kiếm rỗng", null);
        }

        int pageIndex = dto.getPageIndex() > 0 ? dto.getPageIndex() - 1 : 0;
        int pageSize = dto.getPageSize();

        String whereClause = " WHERE 1=1 ";
        String sqlCount = "SELECT count(entity.id) FROM OtherIncome AS entity ";
        String sql = "SELECT new com.globits.hr.dto.OtherIncomeDto(entity) FROM OtherIncome AS entity ";
        String orderBy = " ORDER BY entity.createDate DESC ";

        if (StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.note LIKE :text) ";
        }

        if (dto.getStaffId() != null) {
            whereClause += " AND (entity.staff.id = :staffId) ";
        }

        if (dto.getType() != null) {
            whereClause += " AND (entity.type = :type) ";
        }
        if (dto.getSalaryPeriodId() != null) {
            whereClause += " AND (entity.salaryPeriod.id = :salaryPeriodId) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, InterviewScheduleDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", "%" + dto.getKeyword().trim() + "%");
            qCount.setParameter("text", "%" + dto.getKeyword().trim() + "%");
        }

        if (dto.getStaffId() != null) {
            q.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }

        if (dto.getType() != null) {
            q.setParameter("type", dto.getType());
            qCount.setParameter("type", dto.getType());
        }
        if (dto.getSalaryPeriodId() != null) {
            q.setParameter("salaryPeriodId", dto.getSalaryPeriodId());
            qCount.setParameter("salaryPeriodId", dto.getSalaryPeriodId());
        }

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);

        List<OtherIncomeDto> results = q.getResultList();
        long total = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<OtherIncomeDto> page = new PageImpl<>(results, pageable, total);

        return new ApiResponse<>(HttpStatus.SC_OK, "OK", page);
    }

    @Override
    @Modifying
    @Transactional
    public ApiResponse<Boolean> deleteLists(List<UUID> ids) {
        if (ids == null) return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Danh sách ID rỗng", false);
        repository.deleteAllById(ids);
        return new ApiResponse<>(HttpStatus.SC_OK, "Xoá thành công", true);
    }

    /**
     * Chuyển đổi từ Entity sang DTO
     *
     * @param entity thực thể thu nhập khác
     * @return DTO thu nhập khác
     * @author QuangOliu
     */
    private OtherIncomeDto toDto(OtherIncome entity) {
        return new OtherIncomeDto(entity);
    }
}
