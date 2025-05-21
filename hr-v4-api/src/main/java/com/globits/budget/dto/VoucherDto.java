package com.globits.budget.dto;

import com.globits.budget.domain.Voucher;
import com.globits.budget.domain.VoucherItem;
import com.globits.budget.dto.budget.BudgetDto;
import com.globits.core.dto.BaseObjectDto;
import com.globits.security.dto.UserDto;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VoucherDto extends BaseObjectDto {
    private Date voucherDate;//Ngày hóa đơn
    private Double totalAmount;//Tổng số tiền thu - chi
    private String voucherCode;//mã hóa đơn
    private Integer voucherType;//1= thu, -1=chi
    private List<VoucherItemDto> voucherItems;//Danh sách khoản thu - chi
    private BudgetDto budget;
    private UserDto spender;
    private UserDto approver;

    public VoucherDto() {
        super();
    }

    public VoucherDto(Voucher entity, Boolean getFull) {
        super(entity);
        if (entity != null) {
            this.voucherDate = entity.getVoucherDate();
            this.totalAmount = entity.getTotalAmount();
            this.voucherCode = entity.getVoucherCode();
            this.voucherType = entity.getVoucherType();
            if (getFull) {
                if (!CollectionUtils.isEmpty(entity.getVoucherItems())) {
                    this.voucherItems = new ArrayList<>();
                    for (VoucherItem item : entity.getVoucherItems()) {
                        this.voucherItems.add(new VoucherItemDto(item, false));
                    }
                }
                if (entity.getBudget() != null) {
                    this.budget = new BudgetDto(entity.getBudget());
                }
                if (entity.getApprover() != null) {
                    this.approver = new UserDto(entity.getApprover());
                }
                if (entity.getSpender() != null) {
                    this.spender = new UserDto(entity.getSpender());
                }
            }
        }
    }

    public VoucherDto(Voucher entity) {
        this(entity, true);
    }

    public List<VoucherItemDto> getVoucherItems() {
        return voucherItems;
    }

    public void setVoucherItems(List<VoucherItemDto> voucherItems) {
        this.voucherItems = voucherItems;
    }

    public Date getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(Date voucherDate) {
        this.voucherDate = voucherDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public Integer getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(Integer voucherType) {
        this.voucherType = voucherType;
    }

    public BudgetDto getBudget() {
        return budget;
    }

    public void setBudget(BudgetDto budget) {
        this.budget = budget;
    }

    public UserDto getSpender() {
        return spender;
    }

    public void setSpender(UserDto spender) {
        this.spender = spender;
    }

    public UserDto getApprover() {
        return approver;
    }

    public void setApprover(UserDto approver) {
        this.approver = approver;
    }
}
