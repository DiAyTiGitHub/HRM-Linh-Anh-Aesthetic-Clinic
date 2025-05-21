package com.globits.hr.service.impl;

import com.globits.core.domain.Department;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.RecruitmentRequest;
import com.globits.hr.domain.RecruitmentRequestPosition;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.repository.RecruitmentRequestPositionRepository;
import com.globits.hr.service.RecruitmentRequestPositionService;
import com.globits.hr.service.StaffHierarchyService;
import com.globits.hr.utils.Const;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RecruitmentRequestPositionServiceImpl implements RecruitmentRequestPositionService {
    private static final Logger logger = LoggerFactory.getLogger(RecruitmentRequestPositionServiceImpl.class);

    @Autowired
    private RecruitmentRequestPositionRepository recruitmentRequestPositionRepository;

    @Override
    public ApiResponse<Boolean> save(Position position, RecruitmentRequest recruitmentRequest) {
        if(position != null) {
            RecruitmentRequestPosition recruitmentRequestPosition = new RecruitmentRequestPosition();
            recruitmentRequestPosition.setRecruitment(recruitmentRequest);
            recruitmentRequestPosition.setPosition(position);
            recruitmentRequestPositionRepository.save(recruitmentRequestPosition);
            return new ApiResponse<>(HttpStatus.SC_OK, "Lưu thành công", Boolean.TRUE);
        }else {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không có vị trí", Boolean.TRUE);

        }
    }

    @Override
    public ApiResponse<Boolean> deleteByRecruitmentRequest(UUID recruitmentRequestId) {
        try {
            recruitmentRequestPositionRepository.deleteByRecruitment(recruitmentRequestId);
            return new ApiResponse<>(HttpStatus.SC_OK, "Xoá thành công", Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Xoá thất bại", Boolean.FALSE);
        }
    }
}
