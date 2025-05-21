package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.ShiftWorkRepository;
import com.globits.hr.repository.ShiftWorkTimePeriodRepository;
import com.globits.hr.service.ShiftWorkTimePeriodService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.timesheet.dto.search.SearchShiftWorkTimePeriodDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShiftWorkTimePeriodServiceImpl extends GenericServiceImpl<ShiftWorkTimePeriod, UUID> implements ShiftWorkTimePeriodService {
    @Autowired
    private ShiftWorkTimePeriodRepository timePeriodRepository;
    @Autowired
    ShiftWorkRepository shiftWorkRepository;


    @Override
    public Page<ShiftWorkTimePeriodDto> searchByPage(SearchShiftWorkTimePeriodDto dto) {
        if (dto == null) {
            return null;
        }
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        String whereClause = "";

        String orderBy = " ORDER BY entity.name DESC";
        if (dto.getOrderBy() != null && StringUtils.hasLength(dto.getOrderBy().toString()))
            orderBy = " ORDER BY entity." + dto.getOrderBy() + " ASC";

        String sqlCount = "select count(entity.id) from ShiftWorkTimePeriod as entity where (1=1)";
        String sql = "select new com.globits.hr.dto.ShiftWorkTimePeriodDto(entity) from ShiftWorkTimePeriod as entity where (1=1)";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword()))
            whereClause += " AND ( UPPER(entity.name) LIKE UPPER(:text) OR UPPER(entity.code) LIKE UPPER(:text) )";
        if (dto.getShiftWorkId() != null) {
            whereClause += " AND (entity.shiftWork.id =:shiftWorkId) ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, ShiftWorkDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getShiftWorkId() != null) {
            q.setParameter("shiftWorkId", dto.getShiftWorkId());
            qCount.setParameter("shiftWorkId", dto.getShiftWorkId());
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<ShiftWorkTimePeriodDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public ShiftWorkTimePeriodDto saveOne(ShiftWorkTimePeriodDto dto, UUID id) {
//        boolean isUpdate = false;
        if (dto != null) {
            ShiftWorkTimePeriod entity = null;
            if (id != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<ShiftWorkTimePeriod> optional = timePeriodRepository.findById(id);
                if (optional.isPresent()) {
//                    isUpdate = true;
                    entity = optional.get();
                }
                if (entity != null) {
                    entity.setModifyDate(LocalDateTime.now());
                }
            }
            if (entity == null) {
                entity = new ShiftWorkTimePeriod();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            if (dto.getShiftWorkDto() != null && dto.getShiftWorkDto().getId() != null) {
                ShiftWork shiftWork = null;
                Optional<ShiftWork> optional = shiftWorkRepository.findById(dto.getShiftWorkDto().getId());
                if (optional.isPresent()) {
                    shiftWork = optional.get();
//                    double total =  0;
//                    if (shiftWork.getTotalHours() != null) total = shiftWork.getTotalHours();
//                    double totalTimeShiftWork = DateTimeUtil.hoursDifference(dto.getStartTime(), dto.getEndTime());
//                    if (!isUpdate) {
//                        shiftWork.setTotalHours(totalTimeShiftWork + total);
//                        shiftWorkRepository.save(shiftWork);
//                    }else{
//                        double oldTotalTimeShiftWork = DateTimeUtil.hoursDifference(entity.getStartTime(), entity.getEndTime());
//                        double totalHour = total - oldTotalTimeShiftWork + totalTimeShiftWork ;
//                        shiftWork.setTotalHours(totalHour);
//                        shiftWorkRepository.save(shiftWork);
//                    }
                }
                entity.setShiftWork(shiftWork);
            }
            entity.setStartTime(dto.getStartTime());
            entity.setEndTime(dto.getEndTime());
            entity.setCode(dto.getCode());
            entity = timePeriodRepository.save(entity);

            return new ShiftWorkTimePeriodDto(entity);
        }
        return null;
    }

    @Override
    public void remove(UUID id) {
        ShiftWorkTimePeriod entity = null;
        Optional<ShiftWorkTimePeriod> optional = timePeriodRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
//            ShiftWork shiftWork = entity.getShiftWork();
//            double total =  0;
//            if (shiftWork.getTotalHours() != null) total = shiftWork.getTotalHours();
//            double totalHour = total -  DateTimeUtil.hoursDifference(entity.getStartTime(), entity.getEndTime());
//            shiftWork.setTotalHours(totalHour);
//            shiftWorkRepository.save(shiftWork);
        }
        if (entity != null) {
            timePeriodRepository.delete(entity);
        }
    }

    @Override
    public ShiftWorkTimePeriodDto getById(UUID id) {
        if (id != null) {
            Optional<ShiftWorkTimePeriod> religion = timePeriodRepository.findById(id);
            if (religion.isPresent()) {
                ShiftWorkTimePeriod entity = religion.get();
                return new ShiftWorkTimePeriodDto(entity);
            }
            return null;
        }
        return null;
    }

    @Override
    public List<ShiftWorkTimePeriodDto> getAll() {
        List<ShiftWorkTimePeriod> allTimePeriod = timePeriodRepository.findAll();
        List<ShiftWorkTimePeriodDto> models = new ArrayList<>();
        for (ShiftWorkTimePeriod workTimePeriod : allTimePeriod) {
            models.add(new ShiftWorkTimePeriodDto(workTimePeriod));
        }
        return models;
    }
    @Override
    public List<ShiftWorkTimePeriodDto> getShiftWorkTimePeriodByShiftWorkId(UUID id){
        return timePeriodRepository.getShiftWorkTimePeriodByShiftWorkId(id);
    }
}
