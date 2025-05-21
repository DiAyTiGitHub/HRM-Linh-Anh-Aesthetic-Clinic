package com.globits.salary.repository;

import com.globits.salary.domain.StaffSalaryItemValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffSalaryItemValueRepository extends JpaRepository<StaffSalaryItemValue, UUID> {
    @Query("select ssiv from StaffSalaryItemValue ssiv " +
            "where ssiv.staff.id = :staffId " +
            "and ssiv.salaryItem.id = :salaryItemId " +
            "order by ssiv.fromDate desc, ssiv.toDate desc ")
    List<StaffSalaryItemValue> findByStaffIdAndSalaryItemId(@Param("staffId") UUID staffId, @Param("salaryItemId") UUID salaryItemId);

    @Query("select ssiv from StaffSalaryItemValue ssiv " +
            "where ssiv.staff.id = :staffId " +
            "and ssiv.salaryItem.id = :salaryItemId " +
            "and ssiv.isCurrent = true ")
    List<StaffSalaryItemValue> findCurrentByStaffIdAndSalaryItemId(@Param("staffId") UUID staffId, @Param("salaryItemId") UUID salaryItemId);

    @Query("SELECT sv FROM StaffSalaryItemValue sv " +
            "WHERE sv.salaryItem.id IN :salaryItemIds AND sv.staff.id = :staffId")
    List<StaffSalaryItemValue> findSalaryValuesBySalaryItemIdsAndStaffId(
            @Param("salaryItemIds") List<UUID> salaryItemIds,
            @Param("staffId") UUID staffId
    );

    @Query("SELECT sv FROM StaffSalaryItemValue sv " +
            "WHERE sv.staff.id = :staffId AND sv.salaryItem.id = :salaryItemId")
    List<StaffSalaryItemValue> getWithStaffIdAndSalaryItemId(
            @Param("staffId") UUID staffId,
            @Param("salaryItemId") UUID salaryItemId
    );

    @Query("SELECT sv FROM StaffSalaryItemValue sv " +
            "WHERE sv.staff.id = :staffId AND sv.templateItem.salaryTemplate.id = :templateId " +
            "order by sv.templateItem.displayOrder")
    List<StaffSalaryItemValue> findFixTemplateItemsByStaffIdAndTemplateId(
            @Param("staffId") UUID staffId,
            @Param("templateId") UUID templateId
    );

    @Query("SELECT sv FROM StaffSalaryItemValue sv " +
            "WHERE sv.staff.id = :staffId AND sv.salaryItem.code = 'LUONG_THAM_GIA_BAO_HIEM_XA_HOI_THUE' ")
    List<StaffSalaryItemValue> getTaxByStaffId(UUID staffId);

    @Query("select ssiv from StaffSalaryItemValue ssiv " +
            "where ssiv.staff.id = :staffId " +
            "and ssiv.salaryItem.id IN (:salaryItemIds) " +
            "and ssiv.isCurrent = true ")
    List<StaffSalaryItemValue> findCurrentByStaffIdAndSalaryItemIds(
            @Param("staffId") UUID staffId,
            @Param("salaryItemIds") List<UUID> salaryItemIds
    );

    @Query("select ssiv from StaffSalaryItemValue ssiv " +
            "where ssiv.staff.id = :staffId ")
    List<StaffSalaryItemValue> findByStaffId(UUID staffId);
}
