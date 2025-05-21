package com.globits.hr.service;
import com.globits.core.service.GenericService;
import com.globits.hr.domain.Language;
import com.globits.hr.dto.LanguageDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;
import java.util.UUID;

public interface LanguageService extends GenericService<Language, UUID> {
    public Page<LanguageDto>getPage(int pageSize, int pageIndex);

    Boolean deleteOtherLanguage(UUID id);

    LanguageDto saveOrUpdate(UUID id,LanguageDto dto);

    LanguageDto getOtherLanguage(UUID id);

    Page<LanguageDto>searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);
}
