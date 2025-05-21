package com.globits.timesheet.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.globits.timesheet.domain.Label;
import com.globits.timesheet.domain.Project;
import com.globits.timesheet.dto.LabelDto;
import com.globits.timesheet.repository.LabelRepository;
import com.globits.timesheet.repository.ProjectRepository;
import com.globits.timesheet.service.LabelService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LabelServiceImpl implements LabelService {
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public LabelDto getOneLabel(UUID id) {
        return labelRepository.getLabelDtoById(id);
    }

    @Override
    public Label getEntityById(UUID id) {
        Label entity = null;
        Optional<Label> labelOptional = labelRepository.findById(id);
        if (labelOptional.isPresent()) {
            entity = labelOptional.get();
        }
        return entity;
    }

    @Override
    public Boolean deleteOne(UUID id) {
        if (id != null) {
            labelRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Page<LabelDto> searchByPage() {
        return new PageImpl<>(labelRepository.getAllLabel());
    }

    @Override
    public List<LabelDto> saveList(List<LabelDto> dtoList) {
        if (!CollectionUtils.isEmpty(dtoList)) {
            List<LabelDto> listLabel = new ArrayList<>();
            for (LabelDto labelDto : dtoList) {
                listLabel.add(this.saveOrUpdate(labelDto, null));
            }
            return listLabel;
        }
        return null;
    }

    @Override
    public LabelDto saveOrUpdate(LabelDto dto, UUID id) {
        if (dto== null){
            return null;
        }
        if (dto.getColor() == null){
            return null;
        }
        Label entity = null;
        if (dto.getId() != null) {
            if (dto.getId() != null && !dto.getId().equals(id)) {
                return null;
            }
            Optional<Label> labelOptional = labelRepository.findById(id);
            if (labelOptional.isPresent()) {
                entity = labelOptional.get();
            }
            if (entity != null) {
                entity.setModifyDate(LocalDateTime.now());
            }
        }
        if (entity == null) {
            entity = new Label();
            entity.setCreateDate(LocalDateTime.now());
            entity.setModifyDate(LocalDateTime.now());
        }
        entity.setColor(dto.getColor());
        entity.setName(dto.getName());
        if (dto.getProject() != null){
            Project project = null;
            if (dto.getProject().getId()!=null){
                Optional<Project> projectOptional = projectRepository.findById(dto.getProject().getId());
                if (projectOptional.isPresent()){
                    project = projectOptional.get();
                }
            }
            entity.setProject(project);
        }
        entity = labelRepository.save(entity);
        return new LabelDto(entity);
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Long count = labelRepository.checkCode(code, id);
            return count != 0L;
        }
        return null;
    }

    @Override
    public List<LabelDto> getAllLabel() {
        return labelRepository.getAllLabel();
    }

    @Override
    public List<LabelDto> getAllLabelByProjectId(UUID id) {
        return labelRepository.getAllLabelByProjectId(id);
    }
}
