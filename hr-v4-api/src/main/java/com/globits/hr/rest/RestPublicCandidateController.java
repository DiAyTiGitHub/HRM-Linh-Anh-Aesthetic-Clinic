package com.globits.hr.rest;

import com.globits.hr.HrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.globits.core.dto.CountryDto;
import com.globits.core.dto.EthnicsDto;
import com.globits.core.dto.ReligionDto;
import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.CertificateDto;
import com.globits.hr.dto.EducationDegreeDto;
import com.globits.hr.dto.EducationalInstitutionDto;
import com.globits.hr.dto.HrAdministrativeUnitDto;
import com.globits.hr.dto.HrEducationTypeDto;
import com.globits.hr.dto.HrSpecialityDto;
import com.globits.hr.dto.PositionDto;
import com.globits.hr.dto.search.SearchAdministrativeUnitDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchPositionDto;
import com.globits.hr.service.CandidateService;
import com.globits.hr.service.CertificateService;
import com.globits.hr.service.EducationDegreeService;
import com.globits.hr.service.EducationalInstitutionService;
import com.globits.hr.service.HrAdministrativeUnitService;
import com.globits.hr.service.HrCountryService;
import com.globits.hr.service.HrEducationTypeService;
import com.globits.hr.service.HrEthinicsService;
import com.globits.hr.service.HrReligionService;
import com.globits.hr.service.HrSpecialityService;
import com.globits.hr.service.PositionService;

@RestController
@RequestMapping("/public/candidate")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestPublicCandidateController {
	@Autowired
	private CandidateService candidateService;

	@Autowired
	private HrAdministrativeUnitService hrAdministrativeUnitService;
	@Autowired
	private HrEthinicsService hrEthinicsService;
	@Autowired
	private HrReligionService hrReligionService;
	@Autowired
	private CertificateService certificateService;
	@Autowired
	private HrCountryService hrCountryService;
	@Autowired
	EducationalInstitutionService educationalInstitutionService;
	@Autowired
	private HrSpecialityService hrSpecialityService;
	@Autowired
	private HrEducationTypeService hrEducationTypeService;
	@Autowired
	private EducationDegreeService educationDegreeService;
	@Autowired
	private PositionService positionService;


//	@Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
	@RequestMapping(method = RequestMethod.POST, path = "/saveCandidate")
	public ResponseEntity<CandidateDto> saveCandidate(@RequestBody CandidateDto dto) {
		// candidate's code is duplicated
//		Boolean isValidCode = candidateService.isValidCode(dto);
//		if (isValidCode == null || isValidCode.equals(false)) {
//			return new ResponseEntity<CandidateDto>(dto, HttpStatus.CONFLICT);
//		}//bo doan nay di ma set ma tu sinh
		dto.setIsEnterdCandidateProfile(true);// danh dau la ung vien tu nhap tren link ngoai va ma tu sinh
		String code = candidateService.autoGenerateCode(HrConstants.CodePrefix.UNG_VIEN.getConfigKey());
		dto.setCandidateCode(code);
		CandidateDto response = candidateService.saveCandidate(dto);

		if (response == null)
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

//	@PostMapping("/hrAdministrativeUnit/searchByPage")
	@RequestMapping(method = RequestMethod.POST, path = "/hrAdministrativeUnit/searchByPage")
	public ResponseEntity<Page<HrAdministrativeUnitDto>> searchByPageAdministrativeUnit(
			@RequestBody SearchAdministrativeUnitDto dto) {
		Page<HrAdministrativeUnitDto> page = hrAdministrativeUnitService.searchByPage(dto);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

//	@PostMapping("/hrEthnics/searchByPage")
	@RequestMapping(method = RequestMethod.POST, path = "/hrEthnics/searchByPage")
	public ResponseEntity<Page<EthnicsDto>> searchByPageEthnics(@RequestBody SearchDto dto) {
		Page<EthnicsDto> page = hrEthinicsService.searchByPage(dto);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

//	 @PostMapping("/hrRelegion/searchByPage")
	@RequestMapping(method = RequestMethod.POST, path = "/hrRelegion/searchByPage")
	public ResponseEntity<Page<ReligionDto>> searchByPageRelegion(@RequestBody SearchDto dto) {
		Page<ReligionDto> page = hrReligionService.searchByPage(dto);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/certificate/searchByPage")
	public ResponseEntity<Page<CertificateDto>> searchByPageCertificate(@RequestBody SearchDto searchDto) {
		Page<CertificateDto> page = certificateService.searchByPage(searchDto);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/hrCountry/searchByPage")
	public ResponseEntity<Page<CountryDto>> searchByPageCountry(@RequestBody SearchDto dto) {
		Page<CountryDto> page = hrCountryService.searchByPage(dto);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/educationalInstitution/searchByPage")
	public Page<EducationalInstitutionDto> searchByPageEducationalInstitution(@RequestBody SearchDto dto) {
		return educationalInstitutionService.searchByPage(dto);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/hrSpeciality/searchByPage")
	public ResponseEntity<Page<HrSpecialityDto>> searchByPageSpeciality(@RequestBody SearchDto searchDto) {
		Page<HrSpecialityDto> page = this.hrSpecialityService.searchByPage(searchDto);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/hrEducationType/searchByPage")
	public Page<HrEducationTypeDto> searchByPageEducationType(@RequestBody SearchDto dto) {
		return hrEducationTypeService.searchByPage(dto);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/educationDegree/searchByPage")
	public ResponseEntity<Page<EducationDegreeDto>> searchByPageEducationDegree(@RequestBody SearchDto searchDto) {
		Page<EducationDegreeDto> page = this.educationDegreeService.searchByPage(searchDto);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/position/searchByPage")
	public ResponseEntity<Page<PositionDto>> pagingPosition(@RequestBody SearchPositionDto searchDto) {
		searchDto.setIsPublic(true);
		Page<PositionDto> page = positionService.pagingPosition(searchDto);
		if (page == null)
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}
}
