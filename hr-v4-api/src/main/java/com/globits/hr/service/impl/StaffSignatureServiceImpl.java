package com.globits.hr.service.impl;

import com.globits.core.domain.FileDescription;
import com.globits.core.repository.FileDescriptionRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffSignature;
import com.globits.hr.dto.StaffSignatureDto;
import com.globits.hr.dto.search.SearchStaffSignatureDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffSignatureRepository;
import com.globits.hr.service.StaffSignatureService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaffSignatureServiceImpl extends GenericServiceImpl<StaffSignature, UUID> implements StaffSignatureService {

    @Autowired
    StaffRepository staffRepository;
    @Autowired
    private Environment env;
    @Autowired
    private FileDescriptionRepository fileDescriptionRepository;

    @Autowired
    private StaffSignatureRepository staffSignatureRepository;

    @Override
    public StaffSignatureDto saveOrUpdate(StaffSignatureDto dto) {
        StaffSignature staffSignature = null;
        if (dto == null) {
            return null;
        }

        if (dto.getId() != null) {
            staffSignature = repository.findById(dto.getId()).orElse(null);
        }

        if (staffSignature == null) {
            staffSignature = new StaffSignature();
        }

        Staff staff = null;
        if (dto.getStaff() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }
        if (staff == null) {
            return null;
        }

        staffSignature.setStaff(staff);
        staffSignature.setName(dto.getName());
        staffSignature.setCode(dto.getCode());
        staffSignature.setDescription(dto.getDescription());
//        staffSignature.setSignature(dto.getSignature());

        if (dto.getFile() != null) {
            FileDescription fileDescription = fileDescriptionRepository.findById(dto.getFile().getId()).orElse(null);
            staffSignature.setFile(fileDescription);
        } else {
            staffSignature.setFile(null);
        }

        staffSignature = repository.save(staffSignature);

        return new StaffSignatureDto(staffSignature);
    }

    @Override
    public Page<StaffSignatureDto> searchByPage(SearchStaffSignatureDto dto) {
        if (dto == null) {
            return Page.empty();
        }
        int pageIndex = dto.getPageIndex() > 0 ? dto.getPageIndex() - 1 : 0;
        int pageSize = dto.getPageSize();

        String whereClause = "";
        String orderBy = " ORDER BY entity.id DESC";
        String sqlCount = "SELECT COUNT(entity.id) FROM StaffSignature AS entity WHERE (1=1)";
        String sql = "SELECT new com.globits.hr.dto.StaffSignatureDto(entity) FROM StaffSignature AS entity WHERE (1=1) ";

        if (StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (UPPER(entity.name) LIKE UPPER(:text) OR UPPER(entity.code) LIKE UPPER(:text))";
        }

        if (dto.getStaffId() != null) {
            whereClause += " AND entity.staff.id = :staffId";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, StaffSignatureDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        if (dto.getStaffId() != null) {
            q.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }

        q.setFirstResult(pageIndex * pageSize);
        q.setMaxResults(pageSize);
        List<StaffSignatureDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        return new PageImpl<>(entities, PageRequest.of(pageIndex, pageSize), count);
    }


    @Override
    public Boolean deleteById(UUID id) {
        if (id != null) {
            StaffSignature entity = repository.findById(id).orElse(null);
            if (entity != null) {
                repository.deleteById(id);
                return true;
            }
        }
        return false;
    }


    @Override
    public StaffSignatureDto getById(UUID id) {
        if (id != null) {
            Optional<StaffSignature> religion = repository.findById(id);
            if (religion.isPresent()) {
                StaffSignature entity = religion.get();
                return new StaffSignatureDto(entity);
            }
            return null;
        }
        return null;
    }

    @Override
    public List<StaffSignatureDto> getAll() {
        List<StaffSignature> allTimePeriod = repository.findAll();
        List<StaffSignatureDto> models = new ArrayList<>();
        for (StaffSignature workTimePeriod : allTimePeriod) {
            models.add(new StaffSignatureDto(workTimePeriod));
        }
        return models;
    }

    @Override
    public String insertImageIntoPDFFile(String base64Image, File filePDF) {
        if (base64Image != null && filePDF != null) {
            try {
                String path = "";
                if (env.getProperty("hrm.file.folder") != null) {
                    path = env.getProperty("hrm.file.folder");
                }
                path += "filePDFImage.pdf";
                PdfDocument pdfDoc = new PdfDocument(new PdfReader(filePDF), new PdfWriter(path));
                Document doc = new Document(pdfDoc);
                // Thêm ảnh vào trang đầu tiên
                ImageData imageData = ImageDataFactory.create(base64Image); // Đường dẫn ảnh
                Image img = new Image(imageData);
                img.setFixedPosition(400, 100); // (x, y) vị trí ảnh
                img.scaleAbsolute(150, 50); // Kích thước ảnh

                doc.add(img);
                doc.close();
                pdfDoc.close();
                return path;
            } catch (Exception e) {

            }

        }
        return null;

    }

    @Override
    public Boolean validateCode(StaffSignatureDto dto) {
        if (dto == null) return false;

        // ID of Staff is null => Create new Staff
        // => Assure that there's no other Staffs using this code of new Staff
        // if there was any Staff using new Staff code, then this new code is
        // invalid => return False
        // else return true
        if (dto.getId() == null) {
            List<StaffSignature> entities = staffSignatureRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        }
        // ID of Staff is NOT null => Staff is modified
        // => Assure that the modified code is not same to OTHER any Staff's code
        // if there was any Staff using new Staff code, then this new code is
        // invalid => return False
        // else return true
        else {
            List<StaffSignature> entities = staffSignatureRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (StaffSignature entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }

    @Override
    public String generateUniqueSignatureCode() {
        long count = staffSignatureRepository.count(); // Đếm tổng số mã hiện tại
        long nextNumber = count + 1;
        String code;

        do {
            code = String.format("CK_%06d", nextNumber);
            List<StaffSignature> exists = staffSignatureRepository.findByCode(code);
            if (exists == null || exists.isEmpty()) {
                return code;
            }
            nextNumber++;
        } while (true); // Dừng khi tìm được mã không trùng
    }

}
