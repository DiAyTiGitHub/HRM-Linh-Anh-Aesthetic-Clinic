package com.globits.budget.service.impl;

import com.globits.budget.dto.budget.*;
import com.globits.budget.utils.Enums;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.budget.domain.Budget;
import com.globits.budget.repository.BudgetRepository;
import com.globits.budget.service.BudgetService;
import com.globits.hr.domain.LeavingJobReason;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.util.*;

@Service
public class BudgetServiceImpl extends GenericServiceImpl<Budget, UUID> implements BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;

    @Override
    public Page<BudgetDto> pagingBudget(BudgetSearchDto dto) {
        if (dto == null) return null;

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) pageIndex--;
        else pageIndex = 0;


        String sqlCount = "select count(entity.id) from Budget entity where (1=1)";
        String sql = "select new com.globits.budget.dto.budget.BudgetDto(entity) from Budget as entity where (1=1)";

        String whereClause = "";
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text  ) ";
        }

        String orderBy = "ORDER BY entity.createDate DESC";

        sql += whereClause + orderBy;
        sqlCount += whereClause;
        Query q = manager.createQuery(sql, BudgetDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<BudgetDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public BudgetDto saveOrUpdate(BudgetDto dto) {
        if (dto != null) {
            Budget entity = null;
            if (dto.getId() != null) {
                entity = budgetRepository.findById(dto.getId()).orElse(null);
            }
            if (entity == null) {
                entity = new Budget();
                entity.setEndingBalance(dto.getOpeningBalance());
            }
            entity.setName(dto.getName());
            entity.setCode(dto.getCode());
            entity.setDescription(dto.getDescription());
            entity.setCurrency(dto.getCurrency());
            entity.setOpeningBalance(dto.getOpeningBalance());

            Budget response = budgetRepository.save(entity);
            return new BudgetDto(response);
        }

        return null;
    }

    @Override
    public BudgetDto getById(UUID id) {
        if (id != null) {
            Budget entity = budgetRepository.findById(id).orElse(null);
            if (entity != null) {
                return new BudgetDto(entity);
            }
        }
        return null;
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id != null) {
            Budget entity = budgetRepository.findById(id).orElse(null);
            if (entity != null) {
                budgetRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Integer deleteMultiple(List<UUID> ids) {
        if (ids == null && ids.isEmpty()) return null;
        int result = 0;
        for (UUID id : ids) {
            this.deleteById(id);
            result++;
        }
        return result;
    }

    @Override
    public List<BudgetSummaryDto> getBudgetSummary(BudgetSummaryDto dto) {
        if (dto != null && dto.getBudget() != null && dto.getBudget().getId() != null) {
            Budget budget = budgetRepository.findById(dto.getBudget().getId()).orElse(null);
            if (budget == null) return null;

            List<BudgetSummaryDto> result = budgetRepository.getTotalAmount(dto.getMonth(), dto.getYear(), dto.getBudget().getId());

            if (result == null || result.isEmpty()) {
                return Arrays.asList(
                        new BudgetSummaryDto(0.0, 1, dto.getMonth(), dto.getYear()),  // Income
                        new BudgetSummaryDto(0.0, -1, dto.getMonth(), dto.getYear())  // Expense
                );
            }

            boolean hasIncome = false;
            boolean hasExpense = false;

            for (BudgetSummaryDto item : result) {
                if (item.getVoucherType() == 1) hasIncome = true;
                if (item.getVoucherType() == -1) hasExpense = true;
            }

            if (!hasIncome) result.add(new BudgetSummaryDto(0.0, 1, dto.getMonth(), dto.getYear()));  // Ensure income entry exists
            if (!hasExpense) result.add(new BudgetSummaryDto(0.0, -1, dto.getMonth(), dto.getYear()));  // Ensure expense entry exists

            return result;
        }
        return null;
    }



    @Override
    public List<BudgetSummaryYearDto> getBudgetSummaryYear(BudgetSummaryDto dto) {
        if (dto != null && dto.getBudget() != null && dto.getBudget().getId() != null) {
            Budget budget = budgetRepository.findById(dto.getBudget().getId()).orElse(null);
            if (budget == null) return null;

            // Get the list of monthly summaries
            List<BudgetSummaryDto> budgetSummaryYearDtos = budgetRepository.findBudgetSummaryByMonthAndType(dto.getYear(), dto.getBudget().getId());

            // If the list is not empty, process the data
            List<BudgetSummaryYearDto> result = new ArrayList<>();

            // Add entries for all 12 months
            for (int month = 1; month <= 12; month++) {
                double totalIncome = 0.0;
                double totalExpenditure = 0.0;
                boolean monthExists = false;

                // Check if the current month exists in the retrieved data
                for (BudgetSummaryDto item : budgetSummaryYearDtos) {
                    if (item.getMonth() == month) {
                        // Initialize the month data if it exists
                        monthExists = true;

                        // Add to income or expenditure based on voucher type
                        if (item.getVoucherType() == Enums.BudgetType.INCOME.getValue()) {
                            totalIncome += item.getTotalAmount(); // Accumulate income
                        } else if (item.getVoucherType() == Enums.BudgetType.EXPENDITURE.getValue()) {
                            totalExpenditure += item.getTotalAmount(); // Accumulate expenditure
                        }
                    }
                }

                // If the month doesn't exist, set income and expenditure to 0.0
                if (!monthExists) {
                    totalIncome = 0.0;
                    totalExpenditure = 0.0;
                }

                // Create the result for the month with accumulated values
                BudgetSummaryYearDto budgetSummaryYear = new BudgetSummaryYearDto();
                budgetSummaryYear.setMonth(month);
                budgetSummaryYear.setIncome(totalIncome);
                budgetSummaryYear.setExpenditure(totalExpenditure);
                result.add(budgetSummaryYear);
            }

            return result;
        }
        return null;
    }

    @Override
    public BudgetSummaryBalanceDto getBudgetSummaryBalance(BudgetSummaryDto dto) {
        if (dto != null && dto.getBudget() != null && dto.getBudget().getId() != null && dto.getFromDate() != null && dto.getToDate() != null) {
            Budget budget = budgetRepository.findById(dto.getBudget().getId()).orElse(null);
            if (budget == null) return null;
            dto.setToDate(endOfDay(dto.getToDate()));
            dto.setFromDate(startOfDay(dto.getFromDate()));
            BudgetSummaryDto summaryUntilToDate = getTotalAmountBeforeDate(dto, budget);
            List<BudgetSummaryDto> summaryFromDateToDate = getTotalAmountBetweenDates(dto);
            if (summaryUntilToDate != null && summaryFromDateToDate != null) {
                if (summaryFromDateToDate.isEmpty()) {
                    BudgetSummaryDto budgetSummary = new BudgetSummaryDto();
                    budgetSummary.setToDate(dto.getToDate());
                    budgetSummary.setFromDate(dto.getFromDate());
                    summaryFromDateToDate.add(budgetSummary);
                }
                BudgetSummaryBalanceDto response = new BudgetSummaryBalanceDto();
                response.setBudget(new BudgetDto(budget));
                response.setSummaryUntilToDate(summaryUntilToDate);
                response.setSummaryFromDateToDate(summaryFromDateToDate);
                return response;
            }
        }
        return null;
    }

    @Override
    public Boolean checkCode(BudgetDto dto) {
        if (dto == null) return false;
        if (dto.getId() == null) {
            List<Budget> entities = budgetRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<Budget> entities = budgetRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (Budget entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }

    private BudgetSummaryDto getTotalAmountBeforeDate(BudgetSummaryDto dto, Budget budget) {
        if (dto != null && dto.getBudget() != null && dto.getBudget().getId() != null && dto.getToDate() != null) {
            List<BudgetSummaryDto> result = budgetRepository.getTotalAmountBeforeDate(dto.getToDate(), dto.getBudget().getId());
            double currentBalance = budget.getOpeningBalance();
            for (BudgetSummaryDto item : result) {
                currentBalance += item.getTotalAmount() * item.getVoucherType();
            }
            BudgetSummaryDto response = new BudgetSummaryDto();
            response.setTotalAmount(currentBalance);
            response.setToDate(dto.getToDate());
            return response;
        }
        return null;
    }

    private List<BudgetSummaryDto> getTotalAmountBetweenDates(BudgetSummaryDto dto) {
        if (dto != null && dto.getBudget() != null && dto.getBudget().getId() != null && dto.getFromDate() != null && dto.getToDate() != null) {
            return budgetRepository.getTotalAmountBetweenDates(dto.getFromDate(), dto.getToDate(), dto.getBudget().getId());
        }
        return null;
    }

    private Date startOfDay(Date date) {
        if (date == null) return null;
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date endOfDay(Date date) {
        if (date == null) return null;
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59, 999999999).atZone(ZoneId.systemDefault()).toInstant());
    }
}
