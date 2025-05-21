package com.globits.hr.service.impl;

import com.globits.hr.domain.EvaluationItem;
import com.globits.hr.domain.SystemConfig;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.EvaluationItemDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.EvaluationItemRepository;
import com.globits.hr.service.EvaluationItemService;
import com.globits.hr.service.SystemConfigService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementPermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EvaluationItemServiceImpl implements EvaluationItemService {

    @Autowired
    private EvaluationItemRepository repository;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * Lấy tiêu chí đánh giá theo ID
     *
     * @param id ID của tiêu chí đánh giá cần lấy
     * @return ApiResponse chứa tiêu chí đánh giá hoặc thông báo lỗi nếu không tìm thấy
     * @author anhpdk
     */
    @Override
    public ApiResponse<EvaluationItemDto> getById(UUID id) {
        if (id == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Id is empty", null);
        }
        EvaluationItem entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Can't find with id: " + id, null);
        }
        return new ApiResponse<>(HttpStatus.SC_OK, "Success", toDto(entity));
    }

    /**
     * Lấy tất cả các tiêu chí đánh giá
     *
     * @return ApiResponse chứa danh sách tiêu chí đánh giá
     * @author anhpdk
     */
    @Override
    public ApiResponse<List<EvaluationItemDto>> getAll() {
        List<EvaluationItem> entities = repository.findAll();
        List<EvaluationItemDto> dtos = entities.stream().map(this::toDto).collect(Collectors.toList());
        return new ApiResponse<>(HttpStatus.SC_OK, "Success", dtos);
    }

    /**
     * Lưu hoặc cập nhật tiêu chí đánh giá
     *
     * @param dto Dữ liệu tiêu chí đánh giá cần lưu hoặc cập nhật
     * @return ApiResponse chứa tiêu chí đánh giá đã lưu hoặc cập nhật
     * @author anhpdk
     */
    @Override
    public ApiResponse<EvaluationItemDto> save(EvaluationItemDto dto) {
        if (dto == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "DTO rỗng", null);
        }

        EvaluationItem entity = dto.getId() != null ?
                repository.findById(dto.getId()).orElse(new EvaluationItem()) :
                new EvaluationItem();

        // Cập nhật các trường của entity từ DTO
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        // Lưu entity vào cơ sở dữ liệu
        EvaluationItem saved = repository.save(entity);
        return new ApiResponse<>(HttpStatus.SC_OK, "Lưu thành công", toDto(saved));
    }

    /**
     * Xóa tiêu chí đánh giá theo ID
     *
     * @param id ID của tiêu chí đánh giá cần xóa
     * @author anhpdk
     */
    @Override
    public void delete(UUID id) {
        if (id != null) {
            repository.deleteById(id);
        }
    }

    /**
     * Đánh dấu xóa tiêu chí đánh giá theo ID
     *
     * @param id ID của tiêu chí đánh giá cần xóa
     * @author anhpdk
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
     * Phân trang tất cả các tiêu chí đánh giá
     *
     * @return ApiResponse chứa danh sách tiêu chí đánh giá
     * @author anhpdk
     */
    @Override
    public ApiResponse<Page<EvaluationItemDto>> paging(SearchDto searchDto) {
        int pageIndex = searchDto.getPageIndex() <= 1 ? 0 : searchDto.getPageIndex() - 1;
        int pageSize = searchDto.getPageSize();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        String keyword = null;
        if (searchDto.getKeyword() != null) {
            keyword = searchDto.getKeyword();
        }
        Page<EvaluationItemDto> paging = repository.paging(keyword, searchDto.getRemoveId(), pageable);
        return new ApiResponse<>(HttpStatus.SC_OK, "OK", paging);
    }
    @Override
    public Integer saveList(List<EvaluationItemDto> list) {
        List<EvaluationItem> listAll = repository.findAll();
        Map<String, EvaluationItem> evaluationItemMap = listAll.stream()
                .filter(e -> e.getCode() != null && !e.getCode().isEmpty())
                .collect(Collectors.toMap(EvaluationItem::getCode, Function.identity()));

        List<EvaluationItem> toSave = new ArrayList<>();

        for (EvaluationItemDto dto : list) {
            if (dto.getCode() == null || dto.getCode().isEmpty()) {
                continue; // Bỏ qua nếu thiếu mã
            }

            EvaluationItem entity;
            if (evaluationItemMap.containsKey(dto.getCode())) {
                // Đã tồn tại → cập nhật
                entity = evaluationItemMap.get(dto.getCode());
            } else {
                // Mới → tạo đối tượng mới
                entity = new EvaluationItem();
            }

            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());

            toSave.add(entity);
        }

        repository.saveAll(toSave);
        return toSave.size();
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = repository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }


    /**
     * Chuyển đổi từ Entity sang DTO
     *
     * @param entity tiêu chí đánh giá Entity
     * @return DTO tiêu chí đánh giá
     * @author anhpdk
     */
    private EvaluationItemDto toDto(EvaluationItem entity) {
        return new EvaluationItemDto(entity);
    }
}
