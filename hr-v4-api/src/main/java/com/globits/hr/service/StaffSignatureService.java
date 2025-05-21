package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.PositionStaff;
import com.globits.hr.domain.StaffSignature;
import com.globits.hr.dto.PositionStaffDto;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.StaffSignatureDto;
import com.globits.hr.dto.function.PositionTitleStaffDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffSignatureDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface StaffSignatureService extends GenericService<StaffSignature, UUID> {
    StaffSignatureDto saveOrUpdate(StaffSignatureDto dto);

    Page<StaffSignatureDto> searchByPage(SearchStaffSignatureDto dto);

    Boolean deleteById(UUID id);

    StaffSignatureDto getById(UUID id);

    List<StaffSignatureDto> getAll();

    String insertImageIntoPDFFile(String base64Image, File filePDF);

    Boolean validateCode(StaffSignatureDto dto);

    String generateUniqueSignatureCode();
}
