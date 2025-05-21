package com.globits.hr.service;

import com.globits.core.domain.Department;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.RecruitmentRequest;
import com.globits.hr.domain.RecruitmentRequestPosition;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.utils.Const;

import java.util.UUID;

public interface RecruitmentRequestPositionService {
    ApiResponse<Boolean> save(Position position, RecruitmentRequest recruitmentRequest);
    ApiResponse<Boolean> deleteByRecruitmentRequest(UUID recruitmentRequestId);
}
