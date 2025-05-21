package com.globits.budget.repository;

import com.globits.budget.domain.Budget;
import com.globits.budget.dto.budget.BudgetSummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    @Query("SELECT " +
            "    new com.globits.budget.dto.budget.BudgetSummaryDto(" +
            "        SUM(vi.amount), " +             // Tổng số tiền
            "        v.voucherType, " +              // Loại chứng từ
            "        MONTH(v.voucherDate), " +      // Tháng
            "        YEAR(v.voucherDate) " +       // Năm
            "    ) " +
            "FROM " +
            "    VoucherItem vi " +
            "JOIN " +
            "    vi.voucher v " +
            "JOIN " +
            "    v.budget b " +
            "WHERE " +
            "    MONTH(v.voucherDate) = :month " +   // Lọc theo tháng
            "    AND YEAR(v.voucherDate) = :year " + // Lọc theo năm
            "    AND b.id = :budgetId " +            // Lọc theo budgetId
            "GROUP BY " +
            "    MONTH(v.voucherDate), " +        // Nhóm theo tháng
            "    YEAR(v.voucherDate), " +         // Nhóm theo năm
            "    v.voucherType " +                // Nhóm theo loại chứng từ
            "ORDER BY " +
            "    MONTH(v.voucherDate), " +        // Sắp xếp theo tháng
            "    v.voucherType "                // Sắp xếp theo loại chứng từ
    )
    List<BudgetSummaryDto> getTotalAmount(@Param("month") int month,
                                          @Param("year") int year,
                                          @Param("budgetId") UUID budgetId);


    @Query("SELECT " +
            "    new com.globits.budget.dto.budget.BudgetSummaryDto(" +
            "        SUM(vi.amount), " +             // Tổng số tiền
            "        v.voucherType, " +              // Loại chứng từ
            "        MONTH(v.voucherDate), " +      // Tháng
            "        YEAR(v.voucherDate) " +       // Năm
            "    ) " +
            "FROM " +
            "    VoucherItem vi " +
            "JOIN " +
            "    vi.voucher v " +
            "JOIN " +
            "    v.budget b " +
            "WHERE " +
            "    YEAR(v.voucherDate) = :year " +   // Lọc theo năm
            "    AND b.id = :budgetId " +          // Lọc theo budgetId
            "GROUP BY " +
            "    MONTH(v.voucherDate), " +        // Nhóm theo tháng
            "    YEAR(v.voucherDate), " +         // Nhóm theo năm
            "    v.voucherType " +                // Nhóm theo loại chứng từ
            "ORDER BY " +
            "    MONTH(v.voucherDate), " +        // Sắp xếp theo tháng
            "    v.voucherType")
    List<BudgetSummaryDto> findBudgetSummaryByMonthAndType(
            @Param("year") int year,
            @Param("budgetId") UUID budgetId);


    @Query("SELECT " +
            "    new com.globits.budget.dto.budget.BudgetSummaryDto(" +
            "        SUM(vi.amount), " +             // Tổng số tiền
            "        v.voucherType, " +              // Loại chứng từ
            "        CAST(:toDate AS DATE) " +
            "    ) " +
            "FROM " +
            "    VoucherItem vi " +
            "JOIN " +
            "    vi.voucher v " +
            "JOIN " +
            "    v.budget b " +
            "WHERE " +
            "    v.voucherDate <= :toDate " +    // Chỉ lấy trước ngày truyền vào
            "    AND b.id = :budgetId " +      // Lọc theo budgetId
            "GROUP BY " +
            "    v.voucherType "               // Nhóm theo loại chứng từ
    )
    List<BudgetSummaryDto> getTotalAmountBeforeDate(@Param("toDate") Date toDate,
                                                    @Param("budgetId") UUID budgetId);

    @Query("SELECT " +
            "    new com.globits.budget.dto.budget.BudgetSummaryDto(" +
            "        SUM(vi.amount), " +             // Tổng số tiền
            "        v.voucherType, " +              // Loại chứng từ
            "        CAST(:fromDate AS DATE), " +
            "        CAST(:toDate AS DATE) " +
            "    ) " +
            "FROM " +
            "    VoucherItem vi " +
            "JOIN " +
            "    vi.voucher v " +
            "JOIN " +
            "    v.budget b " +
            "WHERE " +
            "    v.voucherDate BETWEEN :fromDate AND :toDate " +  // Lọc theo khoảng thời gian
            "    AND b.id = :budgetId " +   // Lọc theo budgetId
            "GROUP BY " +
            "    v.voucherType "            // Nhóm theo loại chứng từ
    )
    List<BudgetSummaryDto> getTotalAmountBetweenDates(@Param("fromDate") Date fromDate,
                                                      @Param("toDate") Date toDate,
                                                      @Param("budgetId") UUID budgetId);

    @Query("select b FROM Budget b where b.code = ?1")
    List<Budget> findByCode(String code);
}
