package com.globits.hr.service.impl;

import com.globits.core.domain.Country;
import com.globits.core.domain.FileDescription;
import com.globits.core.repository.FileDescriptionRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.CandidateAttachmentDto;
import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.CandidateEducationHistoryDto;
import com.globits.hr.repository.CandidateAttachmentRepository;
import com.globits.hr.repository.CandidateRepository;
import com.globits.hr.service.CandidateAttachmentService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Transactional
@Service
public class CandidateAttachmentServiceImpl extends GenericServiceImpl<CandidateAttachment, UUID>
        implements CandidateAttachmentService {
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private CandidateAttachmentRepository candidateAttachmentRepository;
    @Autowired
    private FileDescriptionRepository fileDescriptionRepository;

    @Override
    public void handleSetAttachmentList(CandidateDto dto, Candidate entity) {
        Set<CandidateAttachment> attachmentList = new HashSet<>();
        if (dto.getCandidateAttachments() != null && !dto.getCandidateAttachments().isEmpty()) {
            for (CandidateAttachmentDto attachmentDto : dto.getCandidateAttachments()) {
                CandidateAttachment attachment = null;
                if (attachmentDto.getId() != null) {
                    attachment = candidateAttachmentRepository.findById(attachmentDto.getId()).orElse(null);
                }

                if (attachment == null) {
                    attachment = new CandidateAttachment();
                    attachment.setCandidate(entity);
                }

                if (attachmentDto.getFile() == null) continue;
                FileDescription file = fileDescriptionRepository.findById(attachmentDto.getFile().getId()).orElse(null);
                if (file == null) continue;

                attachment.setFile(file);
                attachment.setAttachmentType(attachmentDto.getAttachmentType());
                attachment.setName(attachmentDto.getName());
                attachment.setNote(attachmentDto.getNote());

                attachmentList.add(attachment);
            }
        }

        if (entity.getCandidateAttachments() == null) {
            entity.setCandidateAttachments(new HashSet<>());
        }
        entity.getCandidateAttachments().clear();
        entity.getCandidateAttachments().addAll(attachmentList);
    }
}
