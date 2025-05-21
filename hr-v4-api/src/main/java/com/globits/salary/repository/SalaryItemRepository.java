package com.globits.salary.repository;

import java.util.List;
import java.util.UUID;

import com.globits.salary.domain.SalaryTemplateItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.Candidate;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.dto.SalaryItemDto;

@Repository
public interface SalaryItemRepository extends JpaRepository<SalaryItem, UUID> {
	@Query("select new com.globits.salary.dto.SalaryItemDto(s) from SalaryItem s")
	Page<SalaryItemDto> getListPage(Pageable pageable);

	@Query("select new com.globits.salary.dto.SalaryItemDto(s) from SalaryItem s where s.name like ?1 or s.code like ?2")
	Page<SalaryItemDto> searchByPage(String name, String code, Pageable pageable);

	@Query("select si from SalaryItem si where si.code = ?1")
	List<SalaryItem> findByCode(String code);

	@Query("SELECT ssiv.salaryItem FROM StaffSalaryItemValue ssiv " +
			"WHERE ssiv.staff.id = ?1 " +
			"AND ssiv.calculationType = 5 " +
			"GROUP BY ssiv.salaryItem " +
			"ORDER BY ssiv.salaryItem.name ASC")
    List<SalaryItem> findByStaffId(UUID staffId);
}
