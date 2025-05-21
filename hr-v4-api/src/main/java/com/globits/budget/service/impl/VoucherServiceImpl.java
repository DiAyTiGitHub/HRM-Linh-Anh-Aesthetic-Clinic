package com.globits.budget.service.impl;

import com.globits.budget.BudgetConstants;
import com.globits.budget.dto.budget.BudgetSummaryDto;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.budget.domain.Budget;
import com.globits.budget.domain.BudgetCategory;
import com.globits.budget.domain.Voucher;
import com.globits.budget.domain.VoucherItem;
import com.globits.budget.dto.budget.BudgetSearchDto;
import com.globits.budget.dto.VoucherDto;
import com.globits.budget.dto.VoucherItemDto;
import com.globits.budget.repository.BudgetCategoryRepository;
import com.globits.budget.repository.BudgetRepository;
import com.globits.budget.repository.VoucherItemRepository;
import com.globits.budget.repository.VoucherRepository;
import com.globits.budget.service.VoucherService;
import com.globits.core.utils.SecurityUtils;
import com.globits.hr.service.UserExtService;
import com.globits.security.domain.User;
import com.globits.security.service.UserService;
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
public class VoucherServiceImpl extends GenericServiceImpl<Voucher, UUID> implements VoucherService {
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private VoucherItemRepository voucherItemRepository;

    @Autowired
    private BudgetCategoryRepository budgetCategoryRepository;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private UserService userService;

    @Override
    public Page<VoucherDto> pagingVoucherDto(BudgetSearchDto dto) {
        if (dto == null) return null;
        User currentUser = userExtService.getCurrentUserEntity();
        boolean isAdmin = false;
        if (currentUser != null) {
            isAdmin = SecurityUtils.isUserInRole(currentUser, BudgetConstants.ROLE_ADMIN);
            isAdmin = isAdmin || SecurityUtils.isUserInRole(currentUser, BudgetConstants.ROLE_SUPER_ADMIN);
        } else {
            return null;
        }
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) pageIndex--;
        else pageIndex = 0;

        String sqlCount = "select count(entity.id) from Voucher entity where (1=1) ";
        String sql = "select new com.globits.budget.dto.VoucherDto(entity) from Voucher as entity where (1=1) ";

        String whereClause = "";
        if (!isAdmin) {
            whereClause += " AND ( entity.spender.id = :spenderId) ";
        }
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.voucherCode LIKE :text OR entity.budget.name LIKE :text OR entity.budget.description LIKE :text) ";
        }
        if (dto.getBudget() != null && dto.getBudget().getId() != null) {
            whereClause += " AND ( entity.budget.id LIKE :budgetId) ";
        }
        if (dto.getVoucherType() != null) {
            whereClause += " and entity.voucherType = :voucherType ";
        }

        if (dto.getFromDate() != null) {
            whereClause += " and entity.voucherDate >= :fromDate ";
        }
        if (dto.getToDate() != null) {
            whereClause += " and entity.voucherDate <= :toDate ";
        }
        String orderBy = "ORDER BY entity.createDate DESC";

        sql += whereClause + orderBy;
        sqlCount += whereClause;
        Query q = manager.createQuery(sql, VoucherDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (!isAdmin) {
            q.setParameter("spenderId", currentUser.getId());
            qCount.setParameter("spenderId", currentUser.getId());
        }

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getBudget() != null && dto.getBudget().getId() != null) {
            q.setParameter("budgetId", dto.getBudget().getId());
            qCount.setParameter("budgetId", dto.getBudget().getId());
        }

        if (dto.getVoucherType() != null) {
            q.setParameter("voucherType", dto.getVoucherType());
            qCount.setParameter("voucherType", dto.getVoucherType());
        }

        if (dto.getFromDate() != null) {
            q.setParameter("fromDate", formatToStartOfDay(dto.getFromDate()));
            qCount.setParameter("fromDate", formatToStartOfDay(dto.getFromDate()));
        }
        if (dto.getToDate() != null) {
            q.setParameter("toDate", endOfDay(dto.getToDate()));
            qCount.setParameter("toDate", endOfDay(dto.getToDate()));
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<VoucherDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    @Transactional
    public VoucherDto saveOrUpdate(VoucherDto dto) {
        if (dto != null) {
            User currentUser = userExtService.getCurrentUserEntity();
            boolean isAdmin = false;
            if (currentUser != null) {
                isAdmin = SecurityUtils.isUserInRole(currentUser, BudgetConstants.ROLE_ADMIN);
                isAdmin = isAdmin || SecurityUtils.isUserInRole(currentUser, BudgetConstants.ROLE_SUPER_ADMIN);
            } else {
                return null;
            }
            Voucher entity = null;
            if (dto.getId() != null) {
                entity = voucherRepository.findById(dto.getId()).orElse(null);
            }
            if (entity == null) {
                entity = new Voucher();
            }
            if (isAdmin) {
                if (dto.getSpender() != null && dto.getSpender().getUsername() != null) {
                    User spender = userService.findEntityByUsername(dto.getSpender().getUsername());
                    if (spender == null) {
                        return null;
                    }
                    entity.setSpender(spender);
                }
            } else {
                entity.setSpender(currentUser);
            }
            Budget budget = null;
            if (dto.getBudget() != null && dto.getBudget().getId() != null) {
                budget = budgetRepository.findById(dto.getBudget().getId()).orElse(null);
            }
            if (budget == null) {
                return null;
            }
            entity.setBudget(budget);
            if (entity.getVoucherItems() != null) {
                entity.getVoucherItems().clear();
            } else {
                entity.setVoucherItems(new HashSet<>());
            }
            double totalAmount = 0;
            if (dto.getVoucherItems() != null && !dto.getVoucherItems().isEmpty()) {
                for (VoucherItemDto item : dto.getVoucherItems()) {
                    VoucherItem voucherItem = null;
                    if (item.getId() != null) {
                        voucherItem = voucherItemRepository.findById(item.getId()).orElse(null);
                    }
                    if (voucherItem == null) {
                        voucherItem = new VoucherItem();
                    }
                    totalAmount += item.getAmount();
                    voucherItem.setAmount(item.getAmount());
                    voucherItem.setNote(item.getNote());
                    voucherItem.setVoucherType(dto.getVoucherType());
                    voucherItem.setVoucher(entity);

                    BudgetCategory budgetCategory = null;
                    if (item.getBudgetCategory() != null && item.getBudgetCategory().getId() != null) {
                        budgetCategory = budgetCategoryRepository.findById(item.getBudgetCategory().getId()).orElse(null);
                    }
                    voucherItem.setBudgetCategory(budgetCategory);
                    entity.getVoucherItems().add(voucherItem);
                }

            }
            entity.setVoucherCode(dto.getVoucherCode());
            entity.setVoucherType(dto.getVoucherType());
            entity.setVoucherDate(dto.getVoucherDate());
            entity.setTotalAmount(totalAmount);
            Voucher response = voucherRepository.save(entity);
            this.handleCalculateBudget(budget);

            return new VoucherDto(response);
        }

        return null;
    }

    private void handleCalculateBudget(Budget budget) {

        Double totalAmoun = voucherItemRepository.findTotalAmountByBudgetId(budget.getId());

        budget.setEndingBalance(budget.getOpeningBalance() + totalAmoun);

        budgetRepository.save(budget);
    }

    @Override
    public VoucherDto getById(UUID id) {
        if (id != null) {
            Voucher entity = voucherRepository.findById(id).orElse(null);
            if (entity != null) {
                return new VoucherDto(entity);
            }
        }
        return null;
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id != null) {
            Voucher entity = voucherRepository.findById(id).orElse(null);
            if (entity != null) {
                voucherRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<VoucherDto> getAll(BudgetSummaryDto dto) {
        if (dto == null) return Collections.emptyList();
        User currentUser = userExtService.getCurrentUserEntity();
        boolean isAdmin = false;

        if (currentUser != null) {
            isAdmin = SecurityUtils.isUserInRole(currentUser, BudgetConstants.ROLE_ADMIN) ||
                    SecurityUtils.isUserInRole(currentUser, BudgetConstants.ROLE_SUPER_ADMIN);
        } else {
            return Collections.emptyList();
        }

        String sql = "select new com.globits.budget.dto.VoucherDto(entity) from Voucher as entity where (1=1) ";
        String whereClause = "";

        if (!isAdmin) {
            whereClause += " AND ( entity.spender.id = :spenderId) ";
        }
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.voucherCode LIKE :text OR entity.budget.name LIKE :text OR entity.budget.description LIKE :text) ";
        }
        if (dto.getBudget() != null && dto.getBudget().getId() != null) {
            whereClause += " AND ( entity.budget.id LIKE :budgetId) ";
        }
        if (dto.getVoucherType() != null) {
            whereClause += " and entity.voucherType = :voucherType ";
        }
        if (dto.getFromDate() != null) {
            whereClause += " and entity.voucherDate >= :fromDate ";
        }
        if (dto.getToDate() != null) {
            whereClause += " and entity.voucherDate <= :toDate ";
        }

        sql += whereClause + " ORDER BY entity.createDate DESC";

        Query q = manager.createQuery(sql, VoucherDto.class);

        if (!isAdmin) {
            q.setParameter("spenderId", currentUser.getId());
        }
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getBudget() != null && dto.getBudget().getId() != null) {
            q.setParameter("budgetId", dto.getBudget().getId());
        }
        if (dto.getVoucherType() != null) {
            q.setParameter("voucherType", dto.getVoucherType());
        }
        if (dto.getFromDate() != null) {
            q.setParameter("fromDate", formatToStartOfDay(dto.getFromDate()));
        }
        if (dto.getToDate() != null) {
            q.setParameter("toDate", endOfDay(dto.getToDate()));
        }

        return q.getResultList();
    }

    @Override
    @Transactional
    public Integer deleteMultiple(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return null;
        int result = 0;
        for (UUID id : ids) {
            this.deleteById(id);
            result++;
        }
        return result;
    }

    private Date formatToStartOfDay(Date date) {
        if (date == null) return null;
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date endOfDay(Date date) {
        if (date == null) return null;
        return Date.from(date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atTime(23, 59, 59, 999999999)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
