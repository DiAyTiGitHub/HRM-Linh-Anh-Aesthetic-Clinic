package com.globits.hr.service.impl;

import com.globits.hr.domain.*;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.EvaluationCandidateRoundDto;
import com.globits.hr.dto.EvaluationDto;
import com.globits.hr.dto.search.EvaluationCandidateRoundSearchDto;
import com.globits.hr.repository.CandidateRecruitmentRoundRepository;
import com.globits.hr.repository.EvaluationCandidateRoundRepository;
import com.globits.hr.repository.EvaluationTemplateItemValueRepository;
import com.globits.hr.repository.EvaluationTemplateRepository;
import com.globits.hr.service.EvaluationCandidateRoundService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class EvaluationCandidateRoundServiceImpl implements EvaluationCandidateRoundService {
    @Autowired
    private CandidateRecruitmentRoundRepository candidateRecruitmentRoundRepository;

    @Autowired
    private EvaluationCandidateRoundRepository evaluationCandidateRoundRepository;
    @Autowired
    private EvaluationCandidateRoundService evaluationCandidateRoundService;
    @Autowired
    private EvaluationTemplateRepository evaluationTemplateRepository;
    @Autowired
    private EvaluationTemplateItemValueRepository evaluationTemplateItemValueRepository;

    @Override
    public ApiResponse<Page<EvaluationCandidateRoundDto>> searchByPage(EvaluationCandidateRoundSearchDto dto) {
        return null;
    }

    @Override
    public ApiResponse<EvaluationCandidateRoundDto> saveOrUpdate(EvaluationCandidateRoundDto dto) {
        return null;
    }

    @Override
    public ApiResponse<EvaluationCandidateRoundDto> getOne(UUID id) {
        return null;
    }

    @Override
    public ApiResponse<Boolean> deleteOne(UUID id) {
        return null;
    }

    @Override
    public ApiResponse<Boolean> checkCode(UUID id, String code) {
        return null;
    }

    @Override
    public ApiResponse<EvaluationCandidateRoundDto> getByCandidateRoundId(UUID candidateRoundId) {
        if (candidateRoundId == null) {
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy ứng viên - vòng tuyển dụng", null);
        }

        CandidateRecruitmentRound candidateRecruitmentRound = candidateRecruitmentRoundRepository.findById(candidateRoundId).orElse(null);
        if (candidateRecruitmentRound == null) {
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy ứng viên", null);
        }

        RecruitmentRound round = candidateRecruitmentRound.getRecruitmentRound();
        if (round == null) {
            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy vòng tuyển dụng nào", null);
        }

        EvaluationTemplate template = round.getEvaluationTemplate();
        if (template == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Vòng tuyển dụng chưa chọn mẫu đánh giá", null);
        }
        List<EvaluationCandidateRound> evaluationCandidateRounds = evaluationCandidateRoundRepository.findByCandidateRecruitmentRound(candidateRoundId, template.getId());

        EvaluationCandidateRound evaluationCandidateRound = null;
        if (evaluationCandidateRounds == null || evaluationCandidateRounds.isEmpty()) {
            ApiResponse<EvaluationCandidateRound> responseCreate = this.createEvaluationCandidateByCandidateRound(candidateRecruitmentRound);
            if (responseCreate.getStatus() == HttpStatus.SC_OK) {
                evaluationCandidateRound = responseCreate.getData();
            } else {
                return new ApiResponse<>(responseCreate.getStatus(), responseCreate.getMessage(), null);
            }
        } else {
            evaluationCandidateRound = evaluationCandidateRounds.get(0);
        }


        if (evaluationCandidateRound == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Có lỗi xảy ra vui lòng thử lại", null);
        }

        // ======================== Đồng bộ các item con tại đây =========================
        //        Set<EvaluationTemplateItem> templateItems = template.getEvaluationTemplateItems();
        //        Set<EvaluationTemplateItemValue> currentValues = evaluationCandidateRound.getEvaluationValues();
        //
        //        // Lấy danh sách ID của các EvaluationTemplateItem đã có EvaluationTemplateItemValue
        //        Set<UUID> existingItemIds = currentValues.stream()
        //                .map(value -> value.getEvaluationTemplateItem().getId())
        //                .collect(Collectors.toSet());
        //
        //        boolean needUpdate = false;
        //
        //        for (EvaluationTemplateItem item : templateItems) {
        //            if (!existingItemIds.contains(item.getId())) {
        //                // Nếu item chưa có EvaluationTemplateItemValue thì tạo mới
        //                EvaluationTemplateItemValue newValue = new EvaluationTemplateItemValue();
        //                newValue.setEvaluationCandidateRound(evaluationCandidateRound);
        //                newValue.setEvaluationTemplateItem(item);
        //                currentValues.add(newValue);
        //                needUpdate = true;
        //            }
        //        }
        //
        //        if (needUpdate) {
        //            evaluationCandidateRoundRepository.save(evaluationCandidateRound);
        //        }
        // ======================== Đồng bộ xong ==========================================

        EvaluationCandidateRoundDto res = new EvaluationCandidateRoundDto(evaluationCandidateRound, true);

        return new ApiResponse<>(HttpStatus.SC_OK, "OK", res);
    }

    private ApiResponse<EvaluationCandidateRound> createEvaluationCandidateByCandidateRound(CandidateRecruitmentRound
                                                                                                    candidateRecruitmentRound) {
        // 1. Validate input
        if (candidateRecruitmentRound == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy ứng viên");
        }

        // 2. Validate candidate
        Candidate candidate = candidateRecruitmentRound.getCandidate();
        if (candidate == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy ứng viên");
        }

        // 3. Validate recruitment round and template
        RecruitmentRound recruitmentRound = candidateRecruitmentRound.getRecruitmentRound();
        if (recruitmentRound == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy vòng phỏng vấn của ứng viên");
        }

        EvaluationCandidateRound res = new EvaluationCandidateRound();
        res.setCandidateRecruitmentRound(candidateRecruitmentRound);

        EvaluationTemplate template = recruitmentRound.getEvaluationTemplate();
        if (template != null) {
            res.setTemplate(template);
            // 4. Validate template items
            Set<EvaluationTemplateItem> evaluationTemplateItems = template.getEvaluationTemplateItems();

            // 6. Create template item values
            Set<EvaluationTemplateItemValue> evaluationValues = new HashSet<>();
            if (evaluationTemplateItems != null && !evaluationTemplateItems.isEmpty()) {

                for (EvaluationTemplateItem item : evaluationTemplateItems) {
                    if (item != null && item.getParent() == null) {
                        EvaluationTemplateItemValue valueTree = buildItemValueTree(item, res, null);
                        evaluationValues.add(valueTree); // Chỉ thêm root vào, các con đã nằm trong cây
                    }
                }
            }
            res.setEvaluationValues(evaluationValues);
        }

        // 8. Save and return
        return new ApiResponse<>(HttpStatus.SC_OK, "Ok", evaluationCandidateRoundRepository.save(res));
    }

    private EvaluationTemplateItemValue buildItemValueTree(EvaluationTemplateItem item, EvaluationCandidateRound
            round, EvaluationTemplateItemValue parentValue) {
        EvaluationTemplateItemValue itemValue = new EvaluationTemplateItemValue();
        itemValue.setEvaluationCandidateRound(round);
        itemValue.setEvaluationTemplateItem(item);
        itemValue.setParent(parentValue);

        // Nếu bạn cần, set giá trị mặc định cho `value` và `generalValue` tại đây

        // Duyệt các children và build tiếp
        if (item.getChildren() != null) {
            for (EvaluationTemplateItem child : item.getChildren()) {
                EvaluationTemplateItemValue childValue = buildItemValueTree(child, round, itemValue);
                itemValue.getChildren().add(childValue);
            }
        }

        return itemValue;
    }

    @Override
    @Transactional
    public ApiResponse<EvaluationCandidateRoundDto> saveAndUpdate(EvaluationCandidateRoundDto dto) {
        if (dto == null || dto.getCandidateRecruitmentRound() == null || dto.getCandidateRecruitmentRound().getId() == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy ứng viên tham gia vòng tuyển dụng nào");
        }
        CandidateRecruitmentRound candidateRecruitmentRound = candidateRecruitmentRoundRepository.findById(dto.getCandidateRecruitmentRound().getId()).orElse(null);

        if (candidateRecruitmentRound == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy ứng viên tham gia vòng tuyển dụng nào", null);
        }

        EvaluationCandidateRound entity = null;
        if (dto.getId() != null) {
            entity = evaluationCandidateRoundRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            ApiResponse<EvaluationCandidateRound> responseCreate = this.createEvaluationCandidateByCandidateRound(candidateRecruitmentRound);
            if (responseCreate.getStatus() == HttpStatus.SC_OK) {
                entity = responseCreate.getData();
            } else {
                return new ApiResponse<>(responseCreate.getStatus(), responseCreate.getMessage(), null);
            }
        }

        // trường đơn
        entity.setNote(dto.getNote());
        entity.setResult(dto.getResult());
        entity.setCandidateJobTitle(dto.getCandidateJobTitle());
        entity.setInterviewerJobTitle(dto.getInterviewerJobTitle());
        entity.setCandidateExpectedSalary(dto.getCandidateExpectedSalary());
        entity.setInterviewerExpectedSalary(dto.getInterviewerExpectedSalary());
        entity.setCandidateStartWorkingDate(dto.getCandidateStartWorkingDate());
        entity.setInterviewerStartWorkingDate(dto.getInterviewerStartWorkingDate());
        entity = evaluationCandidateRoundRepository.save(entity);
        // values

        if (dto.getEvaluations() != null && dto.getEvaluations().size() > 0) {
            for (EvaluationDto evaluationDto : dto.getEvaluations()) {
                if (evaluationDto.getEvaluationValueId() == null) {
                    continue;
                }

                EvaluationTemplateItemValue evaluationTemplateItemValue = evaluationTemplateItemValueRepository.findById(evaluationDto.getEvaluationValueId()).orElse(null);
                if (evaluationTemplateItemValue == null) {
                    evaluationTemplateItemValue = new EvaluationTemplateItemValue();
                }
                evaluationTemplateItemValue.setValue(evaluationDto.getValue());
                evaluationTemplateItemValueRepository.save(evaluationTemplateItemValue);
            }

        }

        return new ApiResponse<>(HttpStatus.SC_OK, "OK", new EvaluationCandidateRoundDto(entity));
    }


}
