/*
 * TA va Giang làm
 */

package com.globits.salary.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.salary.domain.SalaryIncrementType;
import com.globits.salary.dto.SalaryIncrementTypeDto;

@Repository
public interface SalaryIncrementTypeRepository extends JpaRepository<SalaryIncrementType, UUID>{
	@Query("select new com.globits.salary.dto.SalaryIncrementTypeDto(s) from SalaryIncrementType s")
	Page<SalaryIncrementTypeDto> getListPage( Pageable pageable);

	@Query("select entity from SalaryIncrementType entity where entity.code =?1")
	List<SalaryIncrementType> checkCode(String code);

	@Query("select entity from SalaryIncrementType entity where entity.name =?1")
	List<SalaryIncrementType> findByName(String name);
	
	@Query("select s from SalaryIncrementType s where s.code = ?1")
	List<SalaryIncrementType> findByCode(String code);
}
