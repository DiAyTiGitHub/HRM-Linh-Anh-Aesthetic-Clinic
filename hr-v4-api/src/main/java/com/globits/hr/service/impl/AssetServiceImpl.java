package com.globits.hr.service.impl;

import com.globits.hr.domain.Asset;
import com.globits.hr.domain.Product;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.AssetDto;
import com.globits.hr.dto.TransferAssetDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.AssetRepository;
import com.globits.hr.repository.ProductRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.AssetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssetServiceImpl implements AssetService {
    @Resource
    private AssetRepository assetRepository;

    @Resource
    private StaffRepository staffRepository;

    @Resource
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager manager;

    @Override
    public List<AssetDto> getAll() {
        List<Asset> assets = assetRepository.findAll();
        return assets.stream()
                .map(AssetDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public AssetDto getAsset(UUID id) {
        Asset asset = assetRepository.findById(id).orElse(null);
        if (asset != null) {
            return new AssetDto(asset);
        }
        return null;
    }


    public boolean isProductAssignedToAnotherStaff(UUID productId, UUID currentAssetId) {
        if (productId == null) {
            return false;
        }

        List<Asset> existingAssets = assetRepository.findAllActiveByProductIdAndIdNot(productId, currentAssetId);
        return existingAssets != null && !existingAssets.isEmpty();
    }

    @Override
    public AssetDto saveAsset(AssetDto dto) {
        if (dto == null) {
            return null;
        }

        UUID currentAssetId = dto.getId();
        UUID productId = dto.getProduct() != null ? dto.getProduct().getId() : null;

        // Kiểm tra xem product đã được gán cho nhân viên khác chưa
//        if (isProductAssignedToAnotherStaff(productId, currentAssetId)) {
//            return null;
//        }

        Asset asset = null;
        if (currentAssetId != null) {
            asset = assetRepository.findById(currentAssetId).orElse(null);
        }
        if (asset == null) {
            asset = new Asset();
        }

        // Gán thông tin staff
        Staff staff = null;
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }

        // Gán thông tin product
        Product product = null;
        if (dto.getProduct() != null && dto.getProduct().getId() != null) {
            product = productRepository.findById(dto.getProduct().getId()).orElse(null);
        }

        asset.setStaff(staff);
        asset.setProduct(product);
        asset.setStartDate(dto.getStartDate());
        asset.setEndDate(dto.getEndDate());
        asset.setNote(dto.getNote());

        asset = assetRepository.save(asset);
        return new AssetDto(asset);
    }


    @Override
    public Boolean deleteAsset(UUID id) {
        Asset asset = assetRepository.findById(id).orElse(null);
        if (asset != null) {
            assetRepository.delete(asset);
            return true;
        }
        return false;
    }

    @Override
    public Page<AssetDto> paging(SearchDto dto) {
        if (dto == null) {
            return null;
        }
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String hqlSelect = "select new com.globits.hr.dto.AssetDto(e) from Asset e left join e.product p ";
        String hqlCount = "select count(e.id) from Asset e left join e.product p ";
        String whereClause = " where (1=1) ";
        String orderBy = " order by e.modifyDate desc";

        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty() && !dto.getKeyword().isBlank()) {
            whereClause += " and (e.note like :keyword or p.name like :keyword)";
        }
        if (dto.getFromDate() != null) {
            whereClause += " and (e.startDate >= :fromDate)";
        }
        if (dto.getToDate() != null) {
            whereClause += " and (e.endDate <= :endDate)";
        }

        hqlSelect += whereClause + orderBy;
        hqlCount += whereClause;

        Query q = manager.createQuery(hqlSelect, AssetDto.class);
        Query qCount = manager.createQuery(hqlCount);

        if (dto.getKeyword() != null && !dto.getKeyword().isBlank()) {
            q.setParameter("keyword", '%' + dto.getKeyword() + '%');
            qCount.setParameter("keyword", '%' + dto.getKeyword() + '%');
        }
        if (dto.getFromDate() != null) {
            q.setParameter("fromDate", dto.getFromDate());
            qCount.setParameter("fromDate", dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            q.setParameter("endDate", dto.getToDate());
            qCount.setParameter("endDate", dto.getToDate());
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);

        List<AssetDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }


    @Override
    public List<AssetDto> getListByStaff(UUID staffId) {
        return assetRepository.getListByStaff(staffId);
    }

    @Override
    public List<AssetDto> getListByProduct(UUID productId) {
        return assetRepository.getListByProduct(productId);
    }

    @Override
    public AssetDto transferAsset(TransferAssetDto dto) {
        if (dto == null) {
            return null;
        }
        if (dto.getAsset() == null || dto.getAsset().getId() == null) {
            return null;
        }
        if (dto.getStaff() == null || dto.getStaff().getId() == null) {
            return null;
        }
        Asset asset = assetRepository.findById(dto.getAsset().getId()).orElse(null);
        Staff newStaff = staffRepository.findById(dto.getStaff().getId()).orElse(null);

        if (asset == null) {
            return null;
        }
        if (newStaff == null) {
            return null;
        }

        if (asset.getStaff().getId().equals(newStaff.getId())) {
            return null;
        }

        asset.setStaff(newStaff);
        asset.setStartDate(dto.getStartDate());
        asset.setEndDate(dto.getEndDate());
        asset = assetRepository.save(asset);
        AssetDto res = new AssetDto(asset);
        return res;
    }

    @Override
    public AssetDto returnAsset(UUID id) {
        if (id == null) {
            return null;
        }

        Asset asset = assetRepository.findById(id).orElse(null);
        if (asset == null) {
            return null;
        }
        if (asset.getStaff() == null) {
            return null;
        }
        asset.setStaff(null);

        asset = assetRepository.save(asset);
        return new AssetDto(asset);
    }

    @Override
    public Integer saveListAsset(List<AssetDto> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<Asset> assetList = new ArrayList<>();
        for (AssetDto dto : list) {
            if (dto.getStaff() == null || dto.getStaff().getStaffCode() == null || dto.getProduct() == null || dto.getProduct().getCode() == null) {
                continue;
            }
            Asset asset = null;
            List<Asset> existingAssets = assetRepository.getAssetByProductCodeStaffCode(dto.getProduct().getCode(), dto.getStaff().getStaffCode());
            if (existingAssets != null && !existingAssets.isEmpty()) {
                asset = existingAssets.get(0);
            }
            if (asset == null) {
                asset = new Asset();
            }
            Staff staff = null;
            List<Staff> staffList = staffRepository.getByCode(dto.getStaff().getStaffCode());
            if (staffList != null && !staffList.isEmpty()) {
                staff = staffList.get(0);
            }
            if (staff == null) {
                continue;
            }
            asset.setStaff(staff);
            Product product = null;
            List<Product> productList = productRepository.findByCode(dto.getProduct().getCode());
            if (productList != null && !productList.isEmpty()) {
                product = productList.get(0);
            }
            if (product == null) {
                continue;
            }
            asset.setProduct(product);
            asset.setStartDate(dto.getStartDate());
            asset.setEndDate(dto.getEndDate());
            asset.setNote(dto.getNote());

            assetList.add(asset);
        }

        assetList = assetRepository.saveAll(assetList);
        return assetList.size();
    }
}
