package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.RecruitmentDto;
import com.globits.hr.dto.search.SearchCandidateDto;
import com.globits.hr.dto.search.SearchTaskDto;
import com.globits.hr.service.ExportCandidateReportService;
import com.globits.hr.service.RecruitmentService;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.task.dto.KanbanDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/export-candidate-report")
public class RestExportCandidateReportController {
	@Autowired
	private ExportCandidateReportService exportCandidateReportService;

	// hiển thị danh sách ứng viên theo bộ lọc
	@Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
	@RequestMapping(path = "/paging-export-candidate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<CandidateDto>> pagingExportCandidate(@RequestBody SearchCandidateDto searchDto) {
		Page<CandidateDto> page = exportCandidateReportService.pagingExportCandidate(searchDto);
		if (page == null)
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@RequestMapping(value = "/export-excel-candidate-by-filter", method = RequestMethod.POST)
	public void exportExcelCandidatesByFilter(HttpSession session, HttpServletResponse response,
			@RequestBody SearchCandidateDto dto) throws IOException {
		List<CandidateDto> dataList = exportCandidateReportService.getListExportCandidatesByFilter(dto);

		ByteArrayResource excelFile;
		if (!dataList.isEmpty()) {
			excelFile = exportCandidateReportService.exportExcelCandidatesByFilter(dataList);
			InputStream ins = null;
			if (excelFile != null) {
				ins = new ByteArrayInputStream(excelFile.getByteArray());
			}
			if (ins != null) {
				org.apache.commons.io.IOUtils.copy(ins, response.getOutputStream());
			}
			
		}
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.addHeader("Content-Disposition", "attachment; filename=Danh_sach_ung_vien_theo_bo_loc.xlsx");
	}
}
