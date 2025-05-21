package com.globits.hr.repository;

import com.globits.hr.domain.EvaluationForm;
import com.globits.hr.dto.view.EvaluationFormViewDto;
import com.globits.hr.utils.Const;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface EvaluationFormRepository extends JpaRepository<EvaluationForm, UUID> {
    @Query("SELECT new com.globits.hr.dto.view.EvaluationFormViewDto(form) " +
            "FROM EvaluationForm form " +
            "LEFT JOIN form.staff " +
            "LEFT JOIN form.directManager " +
            "LEFT JOIN form.staffDepartment " +
            "LEFT JOIN form.staffPosition " +
            "LEFT JOIN form.contractType " +
            "WHERE (form.voided IS NULL OR form.voided = false) " +
            "AND ((:isManager = true AND ((form.directManager.id = :staff OR form.staff.id = :staff OR :staff is NULL) OR (form.staffDivision.id = :staffDivision OR :staffDivision IS NULL ))) " +
            "    OR " +
            "    (:isManager = false AND (:staff IS NULL OR form.staff.id = :staff)) " +
            ") " +
            "AND (:department IS NULL OR form.staffDepartment.id = :department) " +
            "AND (:directManager IS NULL OR form.directManager.id = :directManager) " +
            "AND (" +
            ":keyword IS NULL " +
            "OR :keyword = ''" +
            "OR LOWER(form.staff.displayName) LIKE LOWER(CONCAT('%', :keyword, '%'))  " +
            "OR LOWER(form.staff.staffCode) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ")" +
            "AND (:status IS NULL OR form.status = :status) " +
            "AND (:staffPosition IS NULL OR form.staffPosition.id = :staffPosition) " +
            "AND (:contractType IS NULL OR form.contractType.code = :contractType) " +
            "ORDER BY form.modifyDate DESC")
    Page<EvaluationFormViewDto> paging(UUID staff,
                                       String keyword,
                                       Const.EVALUATION status,
                                       String contractType,
                                       UUID directManager,
                                       UUID department,
                                       UUID staffDivision,
                                       UUID staffPosition,
                                       Boolean isManager,
                                       Pageable pageable);

    @Query("SELECT form " +
            "FROM EvaluationForm form " +
            "LEFT JOIN form.staff " +
            "LEFT JOIN form.directManager " +
            "LEFT JOIN form.staffDepartment " +
            "LEFT JOIN form.staffPosition " +
            "LEFT JOIN form.contractType " +
            "WHERE (form.voided is null or form.voided = false) " +
            "AND (:staff is null or form.staff.id = :staff) " +
            "AND (:directManager is null or form.directManager.id = :directManager) " +
            "AND (" +
            ":keyword IS NULL " +
            "OR LOWER(form.staff.displayName) LIKE LOWER(CONCAT('%', :keyword, '%'))  " +
            "OR LOWER(form.staff.staffCode) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ")" +
            "AND (:status is null or form.status = :status) " +
            "AND (:department is null or form.staffDepartment.id = :department) " +
            "AND (:staffPosition is null or form.staffPosition.id = :staffPosition) " +
            "AND (:contractType is null or form.contractType.code = :contractType) " +
            "ORDER BY form.modifyDate DESC")
    Page<EvaluationForm> getPageExcelEvaluation(UUID staff,
                                                String keyword,
                                                Const.EVALUATION status,
                                                String contractType,
                                                UUID directManager,
                                                UUID department,
                                                UUID staffPosition,
                                                Pageable pageable);

}
