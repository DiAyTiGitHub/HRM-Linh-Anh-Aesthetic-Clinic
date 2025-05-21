package com.globits.hr.service;


import com.globits.hr.dto.AssetDto;
import com.globits.hr.dto.TransferAssetDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface AssetService {
    List<AssetDto> getAll();

    AssetDto getAsset(UUID id);

    AssetDto saveAsset(AssetDto dto);

    Boolean deleteAsset(UUID id);

    Page<AssetDto> paging(SearchDto dto);

    List<AssetDto> getListByStaff(UUID staffId);

    List<AssetDto> getListByProduct(UUID productId);

    AssetDto transferAsset(TransferAssetDto dto);

    boolean isProductAssignedToAnotherStaff(UUID productId, UUID currentAssetId);

    AssetDto returnAsset(UUID id);

    Integer saveListAsset(List<AssetDto> list);
}
