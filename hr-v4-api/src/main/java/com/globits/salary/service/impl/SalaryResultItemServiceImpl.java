package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.repository.StaffRepository;
import com.globits.salary.domain.*;
import com.globits.salary.dto.*;
import com.globits.salary.dto.search.ChooseSalaryResultItemDto;
import com.globits.salary.repository.*;
import com.globits.salary.service.SalaryResultItemService;
import com.globits.salary.service.SalaryResultStaffItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SalaryResultItemServiceImpl extends GenericServiceImpl<SalaryResultItem, UUID>
        implements SalaryResultItemService {
    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    @Autowired
    private SalaryResultRepository salaryResultRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SalaryResultStaffRepository salaryResultStaffRepository;

    @Autowired
    private SalaryResultStaffItemRepository salaryResultStaffItemRepository;

    @Autowired
    private SalaryTemplateRepository salaryTemplateRepository;

    @Autowired
    private SalaryTemplateItemRepository salaryTemplateItemRepository;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Autowired
    private SalaryResultItemRepository salaryResultItemRepository;

    @Autowired
    private SalaryResultItemGroupRepository salaryResultItemGroupRepository;

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Autowired
    private SalaryTemplateItemGroupRepository salaryTemplateItemGroupRepository;

    @Override
    public void autoGenerateSpecialFormulaForResultItem(SalaryResult entity) {
        for (SalaryResultItem column : entity.getResultItems()) {
            if (column.getCode().equals(HrConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_SYSTEM.getValue())) {
                generateSpecialFormulaForSRITongThuNhap(column, entity);
            } else if (column.getCode()
                    .equals(HrConstants.SalaryItemCodeSystemDefault.TONG_KHAU_TRU_SYSTEM.getValue())) {
                generateSpecialFormulaForSRITongKhauTru(column, entity);
            } else if (column.getCode()
                    .equals(HrConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_MIEN_THUE_SYSTEM.getValue())) {
                generateSpecialFormulaForSRITongThuNhapMienThue(column, entity);
            } else if (column.getCode()
                    .equals(HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_CHIU_THUE_SYSTEM.getValue())) {
                generateSpecialFormulaForSRIThuNhapChiuThue(column, entity);
            } else if (column.getCode()
                    .equals(HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_TINH_THUE_SYSTEM.getValue())) {
                generateSpecialFormulaForSRIThuNhapTinhThue(column, entity);
            } else if (column.getCode()
                    .equals(HrConstants.SalaryItemCodeSystemDefault.CAC_KHOAN_GIAM_TRU_SYSTEM.getValue())) {
                generateSpecialFormulaForSRICacKhoanGiamTru(column, entity);
            }
        }
    }

    private void generateSpecialFormulaForSRICacKhoanGiamTru(SalaryResultItem resultItem, SalaryResult salaryResult) {
        List<SalaryResultItem> listResultItems = this.getListSortedResultItems(salaryResult.getResultItems());
        // Cac khoan giam tru
        String formula = getSpecialFormulaCacKhoanGiamTru(listResultItems);

        resultItem.setUsingFormula(formula);
    }

    private void generateSpecialFormulaForSRIThuNhapTinhThue(SalaryResultItem resultItem, SalaryResult salaryResult) {
        List<SalaryResultItem> listResultItems = this.getListSortedResultItems(salaryResult.getResultItems());
        // Thu nhap Tinh thue = Thu nhap Chiu Thue - Cac khoan giam tru
        String formula = getSpecialFormulaThuNhapTinhThue(listResultItems);

        resultItem.setUsingFormula(formula);
    }

    private String getSpecialFormulaThuNhapTinhThue(List<SalaryResultItem> listResultItems) {
        Set<String> setResultItemCode = new HashSet<>();
        for (SalaryResultItem sri : listResultItems) {
            setResultItemCode.add(sri.getCode());
        }

        // Thu nhap Tinh thue = Thu nhap Chiu Thue - Cac khoan giam tru

        // handle for Thu nhap Chiu thue
        String thuNhapChiuThueFormula = null;
        // find existed var first
        if (setResultItemCode.contains(HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_CHIU_THUE_SYSTEM.getValue()))
            thuNhapChiuThueFormula = getCurrentUsingFormulaOfResultItem(listResultItems,
                    HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_CHIU_THUE_SYSTEM.getValue());
        // if var is not existed => generate formula
        if (thuNhapChiuThueFormula == null) {
            thuNhapChiuThueFormula = getSpecialFormulaThuNhapChiuThue(listResultItems);
        }

        // handle for Cac khoan giam tru
        String cacKhoanGiamTru = null;
        // find existed var first
        if (setResultItemCode.contains(HrConstants.SalaryItemCodeSystemDefault.CAC_KHOAN_GIAM_TRU_SYSTEM.getValue()))
            cacKhoanGiamTru = getCurrentUsingFormulaOfResultItem(listResultItems,
                    HrConstants.SalaryItemCodeSystemDefault.CAC_KHOAN_GIAM_TRU_SYSTEM.getValue());
        // if var is not existed => generate formula
        if (cacKhoanGiamTru == null) {
            cacKhoanGiamTru = getSpecialFormulaCacKhoanGiamTru(listResultItems);
        }

        String thuNhapTinhThueFormula = null;
        if (!cacKhoanGiamTru.isEmpty()) {
            thuNhapTinhThueFormula = thuNhapChiuThueFormula + " - " + cacKhoanGiamTru;
        } else {
            thuNhapTinhThueFormula = thuNhapChiuThueFormula;
        }

        return thuNhapTinhThueFormula;
    }

    private String getSpecialFormulaCacKhoanGiamTru(List<SalaryResultItem> listResultItems) {
        Set<String> setResultItemCode = new HashSet<>();
        for (SalaryResultItem sri : listResultItems) {
            setResultItemCode.add(sri.getCode());
        }

        // Cac khoan giam tru = Giam tru gia canh + Cac khoan BHXH bat buoc
        // (BHXH,BHYT,BHTN,...) + Cac khoan tu thien
        // handle for Giam tru gia canh
        String giamTruGiaCanhFormula = null;
        // find existed var first
        if (setResultItemCode.contains(HrConstants.SalaryItemCodeSystemDefault.GIAM_TRU_GIA_CANH_SYSTEM.getValue()))
            giamTruGiaCanhFormula = getCurrentUsingFormulaOfResultItem(listResultItems,
                    HrConstants.SalaryItemCodeSystemDefault.GIAM_TRU_GIA_CANH_SYSTEM.getValue());
        // if var is not existed => generate formula
        if (giamTruGiaCanhFormula == null || giamTruGiaCanhFormula.isEmpty()) {
            if (setResultItemCode
                    .contains(HrConstants.SalaryItemCodeSystemDefault.SO_NGUOI_PHU_THUOC_SYSTEM.getValue())) {
                giamTruGiaCanhFormula = "(11000000 + SO_NGUOI_PHU_THUOC_SYSTEM * 4400000)";
            } else {
                giamTruGiaCanhFormula = "11000000";
            }
        }

        List<String> cacKhoanGiamTruVars = new ArrayList<>();
        cacKhoanGiamTruVars.add(giamTruGiaCanhFormula);
        // handle for Cac khoan BHXH bat buoc (BHXH,BHYT,BHTN,...)

        String bhxhFormula = null;
        if (setResultItemCode.contains(HrConstants.SalaryItemCodeSystemDefault.BH_XA_HOI_SYSTEM.getValue())) {
            bhxhFormula = getCurrentUsingFormulaOfResultItem(listResultItems,
                    HrConstants.SalaryItemCodeSystemDefault.BH_XA_HOI_SYSTEM.getValue());
        }
        if (bhxhFormula != null) {
            cacKhoanGiamTruVars.add(bhxhFormula);
        }

        String bhytFormula = null;
        if (setResultItemCode.contains(HrConstants.SalaryItemCodeSystemDefault.BH_Y_TE_SYSTEM.getValue())) {
            bhytFormula = getCurrentUsingFormulaOfResultItem(listResultItems,
                    HrConstants.SalaryItemCodeSystemDefault.BH_Y_TE_SYSTEM.getValue());
        }
        if (bhytFormula != null) {
            cacKhoanGiamTruVars.add(bhytFormula);
        }

        String bhtnFormula = null;
        if (setResultItemCode.contains(HrConstants.SalaryItemCodeSystemDefault.BH_THAT_NGHIEP_SYSTEM.getValue())) {
            bhtnFormula = getCurrentUsingFormulaOfResultItem(listResultItems,
                    HrConstants.SalaryItemCodeSystemDefault.BH_THAT_NGHIEP_SYSTEM.getValue());
        }
        if (bhtnFormula != null) {
            cacKhoanGiamTruVars.add(bhtnFormula);
        }

        String cacKhoanGiamTruFormula = String.join(" + ", cacKhoanGiamTruVars);
        if (!cacKhoanGiamTruFormula.isEmpty()) {
            cacKhoanGiamTruFormula = " (" + cacKhoanGiamTruFormula + ") ";
        }

        return cacKhoanGiamTruFormula;
    }

    private String getCurrentUsingFormulaOfResultItem(List<SalaryResultItem> listResultItems, String itemCode) {
        for (SalaryResultItem sri : listResultItems) {
            if (sri.getCode().equals(itemCode)) {
                return sri.getUsingFormula();
            }
        }

        return null;
    }

    private void generateSpecialFormulaForSRIThuNhapChiuThue(SalaryResultItem resultItem, SalaryResult salaryResult) {
        List<SalaryResultItem> listResultItems = this.getListSortedResultItems(salaryResult.getResultItems());

        String formula = getSpecialFormulaThuNhapChiuThue(listResultItems);

        resultItem.setUsingFormula(formula);
    }

    private List<SalaryResultItem> getListSortedResultItems(Set<SalaryResultItem> resultItemsSet) {
        List<SalaryResultItem> listResultItems = new ArrayList<>();
        listResultItems.addAll(resultItemsSet);

        Collections.sort(listResultItems, new Comparator<SalaryResultItem>() {
            @Override
            public int compare(SalaryResultItem o1, SalaryResultItem o2) {
                // First, compare by displayOrder
                if (o1.getDisplayOrder() == null && o2.getDisplayOrder() == null)
                    return 0;
                if (o1.getDisplayOrder() == null)
                    return 1;
                if (o2.getDisplayOrder() == null)
                    return -1;

                int orderComparison = o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
                if (orderComparison != 0) {
                    return orderComparison;
                }

                // If displayOrder is the same, compare by displayName (handling nulls)
                if (o1.getDisplayName() == null && o2.getDisplayName() == null)
                    return 0;
                if (o1.getDisplayName() == null)
                    return 1;
                if (o2.getDisplayName() == null)
                    return -1;
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        return listResultItems;
    }

    private void generateSpecialFormulaForSRITongKhauTru(SalaryResultItem resultItem, SalaryResult salaryResult) {
        List<SalaryResultItem> listResultItems = this.getListSortedResultItems(salaryResult.getResultItems());

        String formula = getSpecialFormulaTongThuKhauTru(listResultItems);

        resultItem.setUsingFormula(formula);
    }

    private void generateSpecialFormulaForSRITongThuNhapMienThue(SalaryResultItem resultItem,
                                                                 SalaryResult salaryResult) {
        List<SalaryResultItem> listResultItems = this.getListSortedResultItems(salaryResult.getResultItems());

        String formula = getSpecialFormulaTongThuNhapMienThue(listResultItems);

        resultItem.setUsingFormula(formula);
    }

    private void generateSpecialFormulaForSRITongThuNhap(SalaryResultItem resultItem, SalaryResult salaryResult) {
        List<SalaryResultItem> listResultItems = this.getListSortedResultItems(salaryResult.getResultItems());

        String formula = getSpecialFormulaTongThuNhap(listResultItems);

        resultItem.setUsingFormula(formula);
    }

    private String getSpecialFormulaThuNhapChiuThue(List<SalaryResultItem> listResultItems) {
        Set<String> setResultItemCode = new HashSet<>();
        for (SalaryResultItem sri : listResultItems) {
            setResultItemCode.add(sri.getCode());
        }

        // Thu nhap chiu thue = Tong thu nhap - Tong thu nhap mien thue

        // handle for Tong thu nhap
        String tongThuNhapFormula = null;
        // find existed var first
        if (setResultItemCode.contains(HrConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_SYSTEM.getValue())) {
            tongThuNhapFormula = getCurrentUsingFormulaOfResultItem(listResultItems,
                    HrConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_SYSTEM.getValue());
        }
        // if var is not existed => generate formula
        if (tongThuNhapFormula == null || tongThuNhapFormula.isEmpty()) {
            tongThuNhapFormula = getSpecialFormulaTongThuNhap(listResultItems);
        }

        // handle for Tong thu nhap mien thue
        String tongThuNhapMienThueFormula = null;
        // find existed var first
        if (setResultItemCode
                .contains(HrConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_MIEN_THUE_SYSTEM.getValue())) {
            tongThuNhapMienThueFormula = getCurrentUsingFormulaOfResultItem(listResultItems,
                    HrConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_MIEN_THUE_SYSTEM.getValue());
        }
        // if var is not existed => generate formula
        if (tongThuNhapMienThueFormula == null || tongThuNhapMienThueFormula.isEmpty()) {
            tongThuNhapMienThueFormula = getSpecialFormulaTongThuNhapMienThue(listResultItems);
        }

        // Thu nhap chiu thue = Tong thu nhap - Tong thu nhap mien thue
        String formula = "(" + tongThuNhapFormula + ") - (" + tongThuNhapMienThueFormula + ")";
        return formula;
    }

    private String getSpecialFormulaTongThuKhauTru(List<SalaryResultItem> listResultItems) {
        List<String> vars = new ArrayList<>();
        for (SalaryResultItem sri : listResultItems) {
            if (sri.getType() != null && sri.getType().equals(HrConstants.SalaryItemType.DEDUCTION.getValue())) {
                vars.add(sri.getCode());
            }
        }

        String formula = String.join(" + ", vars);
        return formula;
    }

    private String getSpecialFormulaTongThuNhapMienThue(List<SalaryResultItem> listResultItems) {
        List<String> vars = new ArrayList<>();
        for (SalaryResultItem sri : listResultItems) {
            if (sri.getIsTaxable() != null && sri.getIsTaxable().equals(false) && sri.getType() != null
                    && sri.getType().equals(HrConstants.SalaryItemType.ADDITION.getValue())) {
                vars.add(sri.getCode());
            }
        }

        String formula = String.join(" + ", vars);
        return formula;
    }

    private String getSpecialFormulaTongThuNhap(List<SalaryResultItem> listResultItems) {
        List<String> vars = new ArrayList<>();
        for (SalaryResultItem sri : listResultItems) {
            if (sri.getType() != null && sri.getType().equals(HrConstants.SalaryItemType.ADDITION.getValue())) {
                vars.add(sri.getCode());
            }
        }

        String formula = String.join(" + ", vars);
        return formula;
    }

    private SalaryResultItemDto getExistedResultItemInCurrentFEList(List<SalaryResultItemDto> currentResultItems,
                                                                    UUID findingItem) {
        if (currentResultItems == null || currentResultItems.size() == 0)
            return null;

        for (SalaryResultItemDto currentRI : currentResultItems) {
            if (currentRI.getSalaryItem() != null && currentRI.getSalaryItem().getId() != null
                    && currentRI.getSalaryItem().getId().equals(findingItem)) {
                return currentRI;
            }
        }

        return null;
    }

    private SalaryResultItemDto getExistedResultItemSavedInBE(UUID salaryResultId, UUID salaryItemId) {
        if (salaryItemId == null || salaryResultId == null)
            return null;

        List<SalaryResultItem> availableRIs = salaryResultItemRepository
                .getBySalaryResultIdAndSalaryItemId(salaryResultId, salaryItemId);
        if (availableRIs == null || availableRIs.size() == 0)
            return null;

        SalaryResultItemDto resultItem = new SalaryResultItemDto(availableRIs.get(0), true);
        return resultItem;
    }

    private SalaryResultItemDto createNewResultItemDto(UUID salaryResultId, UUID salaryItemId) {
        SalaryItem salaryItem = salaryItemRepository.findById(salaryItemId).orElse(null);
        if (salaryItem == null)
            return null;

        SalaryResultItemDto resultItem = new SalaryResultItemDto();

        resultItem.setId((new SalaryResultItem()).getId());
        resultItem.setSalaryResultId(salaryResultId);
        resultItem.setSalaryItem(new SalaryItemDto(salaryItem));
        resultItem.setDescription(salaryItem.getDescription());
        resultItem.setDisplayName(salaryItem.getName());

        // properties of salary result item which are copied from original salary item
        SalaryItem originalItem = salaryItem;
        resultItem.setCode(originalItem.getCode());
        resultItem.setType(originalItem.getType());
        resultItem.setCalculationType(originalItem.getCalculationType());
        resultItem.setIsInsurable(originalItem.getIsInsurable());
        resultItem.setIsTaxable(originalItem.getIsTaxable());
        resultItem.setUsingFormula(originalItem.getFormula());
        resultItem.setMaxValue(originalItem.getMaxValue());
        resultItem.setValueType(originalItem.getValueType());
        resultItem.setDefaultValue(originalItem.getDefaultValue());

        return resultItem;
    }

    @Override
    public List<SalaryResultItemDto> handleChooseResultItems(ChooseSalaryResultItemDto dto) {
        List<SalaryResultItemDto> response = new ArrayList<>();

        // add to response the previous saved first, then add the new ones later
        List<UUID> unhandledItemIds = new ArrayList<>();
        for (UUID chosenItemId : dto.getChosenItemIds()) {
            SalaryResultItemDto chosenResultItem = null;

            // find in current result items in Frontend first
            chosenResultItem = getExistedResultItemInCurrentFEList(dto.getCurrentResultItems(), chosenItemId);

            if (chosenResultItem == null) {
                // find in current result items in Backend then
                chosenResultItem = getExistedResultItemSavedInBE(dto.getSalaryResultId(), chosenItemId);
            }

            if (chosenResultItem == null) {
                // mark that this salary item has this id is unhandled to response
                unhandledItemIds.add(chosenItemId);
                continue;
            }

            chosenResultItem.setDisplayOrder(response.size() + 1);
            response.add(chosenResultItem);
        }

        // add the remaining to response, these are new result items
        for (UUID chosenItemId : unhandledItemIds) {
            SalaryResultItemDto newResultItem = createNewResultItemDto(dto.getSalaryResultId(), chosenItemId);

            if (newResultItem == null)
                continue;

            newResultItem.setDisplayOrder(response.size() + 1);
            response.add(newResultItem);
        }

        Collections.sort(response, new Comparator<SalaryResultItemDto>() {
            @Override
            public int compare(SalaryResultItemDto o1, SalaryResultItemDto o2) {
                // First, compare by displayOrder
                if (o1.getDisplayOrder() == null && o2.getDisplayOrder() == null)
                    return 0;
                if (o1.getDisplayOrder() == null)
                    return 1;
                if (o2.getDisplayOrder() == null)
                    return -1;

                int orderComparison = o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
                if (orderComparison != 0) {
                    return orderComparison;
                }

                // If displayOrder is the same, compare by displayName (handling nulls)
                if (o1.getDisplayName() == null && o2.getDisplayName() == null)
                    return 0;
                if (o1.getDisplayName() == null)
                    return 1;
                if (o2.getDisplayName() == null)
                    return -1;
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        return response;
    }

    @Override
    public void handleSetSalaryResultItemsFromConfigV2(SalaryResult entity, SalaryResultDto dto) {
        if (entity.getResultItems() == null)
            entity.setResultItems(new HashSet<SalaryResultItem>());

        Set<SalaryResultItem> resultItems = new HashSet<>();
        if (dto.getResultItems() != null && !dto.getResultItems().isEmpty()) {

            for (SalaryResultItemDto itemDto : dto.getResultItems()) {
                SalaryResultItem item = null;
                if (itemDto.getId() != null)
                    item = salaryResultItemRepository.findById(itemDto.getId()).orElse(null);
                if (item == null) {
                    item = new SalaryResultItem();
                    if (itemDto.getId() != null)
                        item.setId(itemDto.getId());
                }

                item.setDisplayOrder(itemDto.getDisplayOrder());
                item.setDisplayName(itemDto.getDisplayName());
                item.setDescription(itemDto.getDescription());
                item.setUsingFormula(itemDto.getUsingFormula());
                item.setHiddenOnPayslip(itemDto.getHiddenOnPayslip());
                item.setValueType(itemDto.getValueType());

                item.setSalaryResult(entity);

                if (itemDto.getResultItemGroupId() != null) {
                    SalaryResultItemGroup group = salaryResultItemGroupRepository
                            .findById(itemDto.getResultItemGroupId()).orElse(null);
                    item.setSalaryResultItemGroup(group);
                } else {
                    item.setSalaryResultItemGroup(null);
                }

                if (itemDto.getSalaryItem() != null) {
                    SalaryItem salaryItem = salaryItemRepository.findById(itemDto.getSalaryItem().getId()).orElse(null);
                    if (salaryItem != null) {
                        item.setSalaryItem(salaryItem);
                        item.setCode(salaryItem.getCode());

                        item.setAllowance(salaryItem.getAllowance());
                    }

                } else {
                    item.setSalaryItem(null);
                }


                if (item.getDisplayOrder() == null && item.getDisplayName() == null && item.getDescription() == null
                        && item.getSalaryItem() == null
                        && (item.getUsingFormula() == null || item.getUsingFormula().length() == 0))
                    continue;

                item = salaryResultItemRepository.save(item);

                resultItems.add(item);
            }
        }

        entity.getResultItems().clear();
        entity.getResultItems().addAll(resultItems);
    }

    @Override
    public void copyFromSalaryTemplateItem(SalaryResult result) {
        SalaryTemplate template = result.getSalaryTemplate();

        if (result.getResultItems() == null) {
            result.setResultItems(new HashSet<SalaryResultItem>());
        }

        Set<SalaryResultItem> resultItems = new HashSet<SalaryResultItem>();
        if (template.getTemplateItems() != null && !template.getTemplateItems().isEmpty()) {
            for (SalaryTemplateItem templateItem : template.getTemplateItems()) {
                SalaryResultItem resultItem = null;
                List<SalaryResultItem> availableItems = salaryResultItemRepository
                        .getBySalaryResultIdAndTemplateItemId(result.getId(), templateItem.getId());

                if (availableItems != null && !availableItems.isEmpty()) {
                    resultItem = availableItems.get(0);
                }

                if (resultItem == null) {
                    resultItem = new SalaryResultItem();
                    resultItem.setCopiedTemplateItem(templateItem);
                    resultItem.setSalaryResult(result);
                }

                resultItem.setSalaryItem(templateItem.getSalaryItem());
                resultItem.setDescription(templateItem.getDescription());
                resultItem.setDisplayName(templateItem.getDisplayName());
                resultItem.setDisplayOrder(templateItem.getDisplayOrder());
                resultItem.setHiddenOnPayslip(templateItem.getHiddenOnPayslip());

                resultItem.setCode(templateItem.getCode());
                resultItem.setType(templateItem.getType());
                resultItem.setCalculationType(templateItem.getCalculationType());
                resultItem.setIsInsurable(templateItem.getIsInsurable());
                resultItem.setIsTaxable(templateItem.getIsTaxable());
                resultItem.setUsingFormula(templateItem.getFormula());
                resultItem.setMaxValue(templateItem.getMaxValue());
                resultItem.setValueType(templateItem.getValueType());
                resultItem.setDefaultValue(templateItem.getDefaultValue());

                if (templateItem.getTemplateItemGroup() != null) {
                    SalaryResultItemGroup group = null;

                    List<SalaryResultItemGroup> availableGroups = salaryResultItemGroupRepository
                            .getBySalaryResultIdAndTemplateItemGroupId(result.getId(),
                                    templateItem.getTemplateItemGroup().getId());
                    if (availableGroups != null && !availableGroups.isEmpty()) {
                        group = availableGroups.get(0);
                    }

                    resultItem.setSalaryResultItemGroup(group);
                }

                resultItem = salaryResultItemRepository.save(resultItem);

                resultItems.add(resultItem);
            }
        }

        result.getResultItems().clear();
        result.getResultItems().addAll(resultItems);
    }
}
