package com.globits.salary.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.salary.domain.SalaryResultStaffItem;

@Repository
public interface SalaryResultStaffItemRepository extends JpaRepository<SalaryResultStaffItem, UUID> {
    @Query("select srsi from SalaryResultStaffItem srsi " +
            "where srsi.salaryResultStaff.id = :salaryResultStaffId and srsi.salaryResultItem.id = :salaryResultItemId")
    List<SalaryResultStaffItem> findBySalaryResultStaffIdAndSalaryResultItemId(@Param("salaryResultStaffId") UUID salaryResultStaffId, @Param("salaryResultItemId") UUID salaryResultItemId);

    @Query("select srsi from SalaryResultStaffItem srsi " +
            "where srsi.salaryResultStaff.id = :salaryResultStaffId order by srsi.referenceDisplayOrder")
    List<SalaryResultStaffItem> findBySalaryResultStaffId(@Param("salaryResultStaffId") UUID salaryResultStaffId);

    @Query("select srsi from SalaryResultStaffItem srsi " +
            "where srsi.salaryResultStaff.staff.id = :staffId " +
            "and srsi.salaryResultStaff.salaryPeriod.id = :salaryPeriodId " +
            "and srsi.salaryTemplateItem.id = :salaryTemplateItemId ")
    List<SalaryResultStaffItem> findByStaffIdSalaryPeriodIdAndSalaryTemplateItemId(@Param("staffId") UUID staffId,
                                                                                   @Param("salaryPeriodId") UUID salaryPeriodId,
                                                                                   @Param("salaryTemplateItemId") UUID salaryTemplateItemId);

    @Query("select srsi from SalaryResultStaffItem srsi " +
            "where srsi.salaryResultStaff.id = :salaryResultStaffId " +
            "and trim(srsi.referenceCode) like trim(:referenceCode)")
    List<SalaryResultStaffItem> findBySalaryResultStaffIdAndReferenceCode(@Param("salaryResultStaffId") UUID salaryResultStaffId,
                                                                          @Param("referenceCode") String referenceCode);

    @Query("select srsi from SalaryResultStaffItem srsi " +
            "where srsi.salaryResultStaff.salaryPeriod.id = :salaryPeriodId " +
            "and srsi.salaryResultStaff.staff.id = :staffId " +
            "and trim(srsi.referenceCode) in :referenceCodeList " +
            "order by srsi.salaryResultStaff.id ")
    List<SalaryResultStaffItem> findByPeriodIdStaffIdAndReferenceCodeList(@Param("salaryPeriodId") UUID salaryPeriodId,
                                                                          @Param("staffId") UUID staffId,
                                                                          @Param("referenceCodeList") List<String> referenceCodeList);

    @Query(value = """
            SELECT COALESCE(SUM(CAST(srsi.value AS DECIMAL(18,4))), 0)
            FROM tbl_salary_result_staff_item srsi
            JOIN tbl_salary_result_staff srs ON srsi.salary_result_staff_id = srs.id
            WHERE srs.salary_period_id = :salaryPeriodId
              AND srs.staff_id = :staffId
              AND TRIM(srsi.reference_code) IN (:referenceCodeList)
              AND srsi.value RLIKE '^[0-9]+(\\.[0-9]+)?$'
            """, nativeQuery = true)
    Double sumValidDecimalValues(
            @Param("salaryPeriodId") UUID salaryPeriodId,
            @Param("staffId") UUID staffId,
            @Param("referenceCodeList") List<String> referenceCodeList
    );

    @Query("""
                SELECT sri.value
                FROM SalaryResultStaffItem sri
                JOIN sri.salaryResultStaff srs
                WHERE srs.salaryPeriod.id = :salaryPeriodId
                  AND srs.staff.id = :staffId
                  AND sri.referenceCode IN :referenceCodes
                order by srs.id
            """)
    List<String> findValidDecimalValues(
            @Param("salaryPeriodId") UUID salaryPeriodId,
            @Param("staffId") UUID staffId,
            @Param("referenceCodes") List<String> referenceCodes
    );


    @Query(value = """
            SELECT COALESCE(SUM(CAST(NULLIF(srsi.value, '') AS DECIMAL(18, 4))), 0)
            FROM tbl_salary_result_staff_item srsi
            JOIN tbl_salary_result_staff srs ON srsi.salary_result_staff_id = srs.id
            WHERE srs.salary_period_id = :salaryPeriodId
              AND srs.staff_id = :staffId
              AND TRIM(srsi.reference_code) IN ( :referenceCodeListStr )
              AND srsi.value REGEXP '^[0-9]+(\\.[0-9]+)?$'
            """, nativeQuery = true)
    BigDecimal sumValidDecimalValueByPeriodIdAndStaffIdAndReferenceCodes(
            @Param("salaryPeriodId") UUID salaryPeriodId,
            @Param("staffId") UUID staffId,
            @Param("referenceCodeListStr") String referenceCodeListStr
    );


}

