package com.globits.hr.repository;


import com.globits.hr.domain.Asset;
import com.globits.hr.dto.AssetDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID> {
    @Query(value = "select new com.globits.hr.dto.AssetDto(e) from Asset e " +
            "where (e.voided is null or e.voided is false) and e.staff.id = :staffId " +
            "order by e.modifyDate")
    List<AssetDto> getListByStaff(UUID staffId);

    @Query(value = "select new com.globits.hr.dto.AssetDto(e) from Asset e " +
            "where (e.voided is null or e.voided is false) and e.product.id = :productId " +
            "order by e.modifyDate")
    List<AssetDto> getListByProduct(UUID productId);

    @Query(value = "select e from Asset e " +
            "where e.product.code = :productCode and e.staff.staffCode = :staffCode ")
    List<Asset> getAssetByProductCodeStaffCode(@Param("productCode") String productCode, @Param("staffCode") String staffCode);

    @Query(value = "select e.product.id from Asset e where (e.voided is null or e.voided is false) and e.endDate is null ")
    List<UUID> getListProducts();

    @Query("SELECT a FROM Asset a WHERE a.product.id = :productId AND (:id IS NULL OR a.id <> :id) AND (a.endDate IS NULL OR a.endDate > CURRENT_TIMESTAMP)")
    List<Asset> findAllActiveByProductIdAndIdNot(@Param("productId") UUID productId, @Param("id") UUID id);

}
