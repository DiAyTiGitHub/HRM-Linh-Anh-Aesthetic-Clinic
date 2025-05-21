package com.globits.hr.service.impl;

import com.globits.hr.domain.EvaluationTemplate;
import com.globits.hr.domain.EvaluationTemplateItem;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.EvaluationItemDto;
import com.globits.hr.dto.EvaluationTemplateDto;
import com.globits.hr.dto.EvaluationTemplateItemDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.EvaluationItemRepository;
import com.globits.hr.repository.EvaluationTemplateItemRepository;
import com.globits.hr.repository.EvaluationTemplateRepository;
import com.globits.hr.service.EvaluationTemplateService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EvaluationTemplateServiceImpl implements EvaluationTemplateService {

    @Autowired
    private EvaluationTemplateRepository evaluationTemplateRepository;

    @Autowired
    private EvaluationTemplateItemRepository evaluationTemplateItemRepository;

    @Autowired
    private EvaluationItemRepository evaluationItemRepository;

    @Override
    public ApiResponse<Boolean> save(EvaluationTemplateDto dto) {
        try {
            EvaluationTemplate entity;

            if (dto.getId() != null) {
                // Cập nhật
                entity = evaluationTemplateRepository.findById(dto.getId()).orElse(null);
                if (entity == null) {
                    return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy EvaluationTemplate với ID này", false);
                }
            } else {
                // Thêm mới
                entity = new EvaluationTemplate();
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            if (!CollectionUtils.isEmpty(entity.getEvaluationTemplateItems())) {
                entity.getEvaluationTemplateItems().clear();
            }

            evaluationTemplateRepository.saveAndFlush(entity);
            saveTemplateItems(dto.getItems(), entity, null);
            // Lưu vào database
            return new ApiResponse<>(HttpStatus.SC_OK, "Lưu thành công", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Lưu thất bại", false);
        }
    }

    @Override
    public ApiResponse<EvaluationTemplateDto> getById(UUID id) {
        EvaluationTemplate entity = evaluationTemplateRepository.findById(id).orElse(null);
        if (entity != null) {
            return new ApiResponse<>(HttpStatus.SC_OK, "OK", new EvaluationTemplateDto(entity, true));
        }
        return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy", null);
    }

    @Override
    public ApiResponse<Page<EvaluationTemplateDto>> paging(SearchDto searchDto) {
        int pageIndex = searchDto.getPageIndex();
        int pageSize = searchDto.getPageSize();
        if (pageIndex > 0) pageIndex--;
        else pageIndex = 0;
        return new ApiResponse<>(HttpStatus.SC_OK, "OK", evaluationTemplateRepository.paging(PageRequest.of(pageIndex, pageSize)));
    }

    private void saveTemplateItems(List<EvaluationTemplateItemDto> items, EvaluationTemplate template, EvaluationTemplateItem parent) {
        if (items == null || items.isEmpty()) return;

        for (EvaluationTemplateItemDto itemDto : items) {
            EvaluationTemplateItem itemEntity = null;

            if (itemDto.getId() != null) {
                itemEntity = evaluationTemplateItemRepository.findById(itemDto.getId()).orElse(null);
            }
            if (itemEntity == null) {
                itemEntity = new EvaluationTemplateItem();
            }

            if (itemDto.getItem() != null && itemDto.getItem().getId() != null) {
                itemEntity.setItem(evaluationItemRepository.findById(itemDto.getItem().getId()).orElse(null));
            }

            itemEntity.setTemplate(template);
            itemEntity.setContentType(itemDto.getContentType());
            itemEntity.setParent(parent); // set cha nếu có
            itemEntity.setOrder(itemDto.getNumberOrder());

            // Lưu entity
            evaluationTemplateItemRepository.saveAndFlush(itemEntity);

            // Đệ quy xử lý các subItems
            saveTemplateItems(itemDto.getItems(), template, itemEntity);
        }
    }

}

