package com.globits.budget.repository;

import com.globits.budget.domain.VoucherItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoucherItemRepository extends JpaRepository<VoucherItem, UUID> {

	@Query("SELECT SUM(vi.amount * vi.voucherType) " + "FROM VoucherItem vi "
			+ "WHERE vi.voucher.budget.id = :budgetId")
	Double findTotalAmountByBudgetId(@Param("budgetId") UUID budgetId);
}
