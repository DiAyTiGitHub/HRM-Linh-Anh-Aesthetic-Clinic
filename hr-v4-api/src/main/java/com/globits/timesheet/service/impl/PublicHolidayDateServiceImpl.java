package com.globits.timesheet.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffAllowanceHistoryDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.timesheet.domain.PublicHolidayDate;
import com.globits.timesheet.dto.PublicHolidayDateDto;
import com.globits.timesheet.dto.search.SearchPublicHolidayDateDto;
import com.globits.timesheet.repository.PublicHolidayDateRepository;
import com.globits.timesheet.service.PublicHolidayDateService;

import jakarta.persistence.Entity;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PublicHolidayDateServiceImpl extends GenericServiceImpl<PublicHolidayDate, UUID> implements PublicHolidayDateService {

    private static final Logger logger = LoggerFactory.getLogger(PublicHolidayDateDto.class);

    @Autowired
    PublicHolidayDateRepository repository;

    @Override
    public PublicHolidayDateDto getPublicHolidayDateById(UUID id) {
        PublicHolidayDate entity = repository.findById(id).orElse(null);
        return new PublicHolidayDateDto(entity);
    }

    @Override
    public Boolean deleteById(UUID id) {
        try {
            repository.deleteById(id);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public PublicHolidayDateDto saveOrUpdate(PublicHolidayDateDto dto) {
        if (dto == null || dto.getHolidayDate() == null) {
            return null;
        }
        PublicHolidayDate entity = null;

        if (dto.getId() != null) {
            entity = repository.findById(dto.getId()).orElse(null);
        } else {
            entity = repository.findByHolidayDate(dto.getHolidayDate()).orElse(null);
        }

        if (entity == null) {
            entity = new PublicHolidayDate();
        }

        entity.setHolidayDate(dto.getHolidayDate());
        entity.setHolidayType(dto.getHolidayType());
        entity.setSalaryCoefficient(dto.getSalaryCoefficient());
        entity.setDescription(dto.getDescription());
        entity.setIsHalfDayOff(dto.getIsHalfDayOff());
        entity.setLeaveHours(dto.getLeaveHours());

        entity = repository.save(entity);
        return new PublicHolidayDateDto(entity);
    }

    @Override
    public Page<PublicHolidayDateDto> pagingPublicHolidayDate(SearchPublicHolidayDateDto searchDto) {
        if (searchDto == null) {
            return null;
        }
        int pageIndex = searchDto.getPageIndex();
        int pageSize = searchDto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        String whereClause = "";
        String sql = "select new com.globits.timesheet.dto.PublicHolidayDateDto(entity) from PublicHolidayDate entity where (1=1) ";
        String sqlCount = "select count(entity.id) from PublicHolidayDate as entity where (1=1) ";
        String orderBy = " ORDER BY entity.holidayDate DESC ";
        whereClause += " and (entity.voided is null or entity.voided = false) ";

        if (searchDto.getFromDate() != null && searchDto.getToDate() != null) {
            whereClause += " and DATE(entity.holidayDate) BETWEEN DATE(:fromDate) AND DATE(:toDate) ";
        } else if (searchDto.getFromDate() != null && searchDto.getToDate() == null) {
            whereClause += " and DATE(entity.holidayDate) >= DATE(:fromDate) ";
        } else if (searchDto.getToDate() != null && searchDto.getFromDate() == null) {
            whereClause += " and DATE(entity.holidayDate) <= DATE(:toDate) ";
        }

        if (searchDto.getHolidayType() != null) {
            if (searchDto.getHolidayType().equals(HrConstants.HolidayLeaveType.WEEKEND.getValue())) {
                whereClause += " and entity.holidayType = 1 ";
            } else if (searchDto.getHolidayType().equals(HrConstants.HolidayLeaveType.PULBIC_HOLIDAY.getValue())) {
                whereClause += " and entity.holidayType = 2 ";
            } else if (searchDto.getHolidayType().equals(HrConstants.HolidayLeaveType.OTHERS.getValue())) {
                whereClause += " and entity.holidayType = 3 ";
            }
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;
        Query query = manager.createQuery(sql, PublicHolidayDateDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getFromDate() != null && searchDto.getToDate() != null) {
            query.setParameter("fromDate", searchDto.getFromDate());
            qCount.setParameter("fromDate", searchDto.getFromDate());
            query.setParameter("toDate", searchDto.getToDate());
            qCount.setParameter("toDate", searchDto.getToDate());
        } else if (searchDto.getFromDate() != null) {
            query.setParameter("fromDate", searchDto.getFromDate());
            qCount.setParameter("fromDate", searchDto.getFromDate());
        } else if (searchDto.getToDate() != null) {
            query.setParameter("toDate", searchDto.getToDate());
            qCount.setParameter("toDate", searchDto.getToDate());
        }

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        List<PublicHolidayDateDto> entities = query.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    public Map<Date, String> getFixedHolidays(Integer year) throws ParseException {
        if (year == null) {
            //throw new IllegalArgumentException("Input year is not null");
            return null;
        }

        Map<Date, String> annualHolidays = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Map.Entry<String, String> entry : HrConstants.SPECIAL_HOLIDAYS.entrySet()) {
            String dateString = year + "-" + entry.getKey();
            Date date = dateFormat.parse(dateString);
            annualHolidays.put(date, entry.getValue());
        }

        return annualHolidays;
    }


    @Override
    public Boolean createPublicHolidayDateAutomatic(SearchDto dto) {
        if (dto == null || dto.getFromDate() == null || dto.getToDate() == null) {
            return false;
        }

//        List<Date> listDateSunday = new ArrayList<>();
        List<Date> listDateAnnualHolidays = new ArrayList<>();
        int fromYear = getYear(dto.getFromDate());
        int toYear = getYear(dto.getToDate());
        Map<Date, String> annualHolidays = null;
        for (int year = fromYear; year <= toYear; year++) {
            try {
                annualHolidays = getFixedHolidays(year);
                for (Date holiday : annualHolidays.keySet()) {
                    if ((!resetTime(holiday).before(resetTime(dto.getFromDate())) &&
                            !resetTime(holiday).after(resetTime(dto.getToDate()))) ||
                            resetTime(holiday).equals(resetTime(dto.getFromDate())) ||
                            resetTime(holiday).equals(resetTime(dto.getToDate()))) {
                        listDateAnnualHolidays.add(holiday);
                    }
                }
            } catch (ParseException e) {
                logger.error("Lỗi khi lấy ngày nghỉ năm " + year + ": " + e.getMessage());
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(dto.getFromDate());

//        while (!cal.getTime().after(dto.getToDate())) {
//            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
//                listDateSunday.add(cal.getTime());
//            }
//            cal.add(Calendar.DAY_OF_MONTH, 1);
//        }

//        if (listDateSunday != null && !listDateSunday.isEmpty()) {
//            for (Date date : listDateSunday) {
//                updateHoliday(date, HrConstants.HolidayLeaveType.WEEKEND.getValue(), 2.0, "Nghỉ CN");
//            }
//        }

        if (listDateAnnualHolidays != null && !listDateAnnualHolidays.isEmpty()) {
            for (Date date : listDateAnnualHolidays) {
                String description = "";
                if (annualHolidays != null) {
                    description = annualHolidays.getOrDefault(date, "Nghỉ lễ hàng năm");
                }
                updateHoliday(date, HrConstants.HolidayLeaveType.PULBIC_HOLIDAY.getValue(), 4.0, description);
            }
        }
        return true;
    }

    private void updateHoliday(Date date, Integer holidayType, double salaryCoefficient, String description) {
        PublicHolidayDate entity = repository.findByHolidayDate(date).orElse(new PublicHolidayDate());

        entity.setHolidayDate(date);
        entity.setHolidayType(holidayType);
        entity.setSalaryCoefficient(salaryCoefficient);
        entity.setDescription(description);
        entity.setLeaveHours(8.0);

        repository.save(entity);
    }

    private int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    private Date resetTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    @Transactional
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        for (UUID id : ids) {
            this.deleteById(id);
        }
        return true;
    }


    @Override
    public Set<Date> getHolidaysInRangeTime(Date fromDate, Date toDate) {
        Set<Date> response = new HashSet<>();

        if (fromDate == null || toDate == null) {
            return response;
        }

        List<PublicHolidayDate> holidays = repository.getInRangeTime(fromDate, toDate);
        if (holidays == null || holidays.isEmpty()) return response;

        for (PublicHolidayDate holiday : holidays) {
            response.add(holiday.getHolidayDate());
        }

        return response;
    }
}