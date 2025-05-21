package com.globits.hr.service;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;

import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.search.SearchCandidateDto;

public interface ExportCandidateReportService {
	// hiển thị danh sách ứng viên theo bộ lọc
	Page<CandidateDto> pagingExportCandidate(SearchCandidateDto dto);

	// danh sách ứng viên cần xuất báo cáo Excel theo bộ lọc
	List<CandidateDto> getListExportCandidatesByFilter(SearchCandidateDto dto);

	ByteArrayResource exportExcelCandidatesByFilter(List<CandidateDto> dataList) throws IOException;
}
