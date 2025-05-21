package com.globits.timesheet.service;

import com.globits.core.service.GenericService;
import com.globits.timesheet.domain.Journal;
import com.globits.timesheet.dto.JournalDto;
import com.globits.timesheet.dto.search.SearchJournalDto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface JournalService extends GenericService<Journal, UUID> {
    JournalDto getJournal(UUID id);

    Journal getEntityById(UUID id);

    Boolean deleteById(UUID id);

    JournalDto saveOrUpdate(UUID id, JournalDto dto);

    Page<JournalDto> pagingJournals(SearchJournalDto dto);

    List<JournalDto> getListJournal(SearchJournalDto dto);
}
