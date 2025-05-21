package com.globits.hr.repository;

import com.globits.hr.domain.EducationalInstitution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EducationalInstitutionRepository extends JpaRepository<EducationalInstitution, UUID> {
	@Query("select t from EducationalInstitution t where t.code = ?1")
	List<EducationalInstitution> findByCode(String code);

	@Query("select t from  EducationalInstitution t where t.name = ?1")
	List<EducationalInstitution> findByName(String name);

	@Query("select count (entity.id)from EducationalInstitution entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null)")
	Long checkCode(String code, UUID id);

}
