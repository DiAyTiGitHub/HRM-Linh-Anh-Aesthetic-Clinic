package com.globits.hr.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.ExistingCandidatesDto;
import com.globits.hr.dto.importExcel.CandidateImport;
import com.globits.hr.dto.search.ExistingCandidatesSearchDto;

import com.globits.hr.dto.search.RecruitmentRequestSummarySearch;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.hr.projection.RecruitmentRequestSummary;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Candidate;
import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchCandidateDto;

public interface CandidateService extends GenericService<Candidate, UUID> {
    public CandidateDto getById(UUID id);

    public CandidateDto deleteCandidate(UUID id);

    public CandidateDto saveCandidate(CandidateDto dto);

    public Boolean deleteMultiple(List<UUID> ids);

    public CandidateDto findByCode(String code);

    public Boolean isValidCode(CandidateDto dto);

    // danh sach ung vien
    public Page<CandidateDto> pagingCandidates(SearchCandidateDto dto);

    // danh sach ung vien duoc tham gia buoi phong van/kiem tra
    public Page<CandidateDto> pagingExamCandidates(SearchCandidateDto dto);

    // danh sach ung vien da PASS bai kiem tra/phong van
    public Page<CandidateDto> pagingPassedCandidates(SearchCandidateDto dto);

    // danh sách ứng viên Chờ nhận việc
    public Page<CandidateDto> pagingWaitingJobCandidates(SearchCandidateDto dto);

    // Danh sách ứng viên Không đến nhận việc
    public Page<CandidateDto> pagingNotComeCandidates(SearchCandidateDto dto);

    // danh sách ứng viên ĐÃ nhận việc
    public Page<CandidateDto> pagingOnboardedCandidates(SearchCandidateDto dto);

    // trang thai ho so cua ung vien
    public Boolean updateApprovalStatus(SearchCandidateDto dto) throws Exception;

    // trang thai bai kiem tra/phong van cua ung vien
    public Boolean updateExamStatus(SearchCandidateDto dto) throws Exception;

    List<CandidateDto> getCandidatesByRecruitmentPlanId(UUID recruitmentPlanId);

    // Cập nhật trạng thái tiếp nhận ứng viên
    // (trạng thái tiếp nhận ứng viên có giá trị sau khi ứng viên đã thi PASS bài
    // phỏng vấn/thi tuyển của đợt tuyển dụng)
    public Boolean updateReceptionStatus(SearchCandidateDto dto) throws Exception;

    // chuyen ung vien sang Chờ nhận việc
    public Boolean convertToWaitingJob(SearchCandidateDto dto) throws Exception;

    // Chuyển ứng viên sang Khong toi nhan viec (ung vien khong toi nhan viec)
    public Boolean convertToNotCome(SearchCandidateDto dto) throws Exception;

    // Chuyển ứng viên sang Đã nhận việc
    public ApiResponse<StaffDto> convertToReceivedJob(List<CandidateDto> dto);

    public XWPFDocument generateDocx(CandidateDto candidateDto) throws IOException;

    ExistingCandidatesDto existingCandidates(ExistingCandidatesSearchDto search);

    List<CandidateDto> getExistCandidateProfileOfStaff(UUID staffId);

    public Boolean updateStatus(SearchCandidateDto dto) throws Exception;

    Integer saveListCandidate(List<CandidateDto> list);

    Workbook exportExcelRecruitmentReports(SearchCandidateDto dto);

    ApiResponse<Boolean> approveCv(SearchCandidateDto dto);

    public String syncCandidateCode();

    String autoGenerateCode(String configKey);

    Boolean resignMultiple(List<UUID> ids);

    Workbook exportExcelCandidateReports(CandidateImport list);

}
