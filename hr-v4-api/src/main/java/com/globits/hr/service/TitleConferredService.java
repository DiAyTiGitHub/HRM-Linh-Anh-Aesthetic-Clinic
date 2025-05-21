package com.globits.hr.service;
import java.util.UUID;
import org.springframework.data.domain.Page;
import com.globits.core.service.GenericService;
import com.globits.hr.domain.TitleConferred;
import com.globits.hr.dto.TitleConferredDto;
import com.globits.hr.dto.search.SearchDto;
public interface TitleConferredService extends GenericService<TitleConferred, UUID>{
	Boolean deleteTitleConferred(UUID id);
	public Page<TitleConferredDto> getPage(int pageSize, int pageIndex);
	TitleConferredDto getTitleConferred(UUID id);

	Page<TitleConferredDto> searchByPage(SearchDto dto);

	Boolean checkCode(UUID id, String code);

	TitleConferredDto saveOrUpdate(UUID id, TitleConferredDto dto);
}
