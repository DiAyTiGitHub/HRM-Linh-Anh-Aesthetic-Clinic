package com.globits.hr.service;


import com.globits.hr.HrConstants;
import com.globits.hr.domain.RecruitmentRequest;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.RecruitmentRequestDto;
import com.globits.hr.dto.importExcel.RecruitmentRequestReportDto;
import com.globits.hr.dto.search.RecruitmentRequestSummarySearch;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.hr.projection.RecruitmentRequestSummary;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface RecruitmentRequestService {
    Page<RecruitmentRequestDto> pagingRecruitmentRequest(SearchRecruitmentDto dto);

    Page<RecruitmentRequest> pagingRecruitmentRequestEntity(SearchRecruitmentDto dto);

    Page<RecruitmentRequestReportDto> pagingRecruitmentRequestReport(SearchRecruitmentDto dto);

    RecruitmentRequestDto getById(UUID id);

    ApiResponse<RecruitmentRequestDto> saveRecruitmentRequest(RecruitmentRequestDto dto);

    Boolean deleteRecruitmentRequest(UUID id);

    Boolean deleteMultipleRecruitmentRequest(List<UUID> ids);

    ApiResponse<Boolean> updateRequestsStatus(SearchRecruitmentDto dto);

    Boolean isValidCode(RecruitmentRequestDto dto);

    ApiResponse<Boolean> approveRecruitmentRequest(UUID recruitmentRequestId, Boolean isApproved);

    XWPFDocument generateDocx(UUID id) throws IOException;

    ApiResponse<List<RecruitmentRequestDto>> personInCharge(SearchRecruitmentDto searchDto);

    Workbook exportExcel(SearchRecruitmentDto dto);

    Integer saveListRecruitmentRequest(List<RecruitmentRequestDto> list);

    // Xuất báo cáo tính hình sử dụng lao động
    Workbook exportRecruitmentRequestReport(SearchRecruitmentDto dto) throws IOException;

    ApiResponse<Boolean> checkNumberIsWithinHeadcount(UUID departmentId, UUID positionTileId, Integer announcementQuantity);

    String autoGenerateCode(String configKey);

    ApiResponse<Boolean> changeStatus(List<UUID> id, HrConstants.RecruitmentRequestStatus status);

    Page<RecruitmentRequestSummary> getRecruitmentRequestSummaries(RecruitmentRequestSummarySearch summary);
}