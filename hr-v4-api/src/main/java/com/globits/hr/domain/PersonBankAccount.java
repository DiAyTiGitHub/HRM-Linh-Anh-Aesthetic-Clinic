package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Person;
import jakarta.persistence.*;

// Tài khoản nhân hàng
@Table(name = "tbl_person_bank_account")
@Entity
public class PersonBankAccount extends BaseObject {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_id")
    private Bank bank; // Ngân hàng nào

    @Column(name = "bank_account_name")
    private String bankAccountName; // Tên tài khoản ngân hàng

    @Column(name = "bank_account_number")
    private String bankAccountNumber; // Số tài khoản ngân hàng

    @Column(name = "bank_branch")
    private String bankBranch; // Chi nhánh ngân hàng

    @Column(name = "is_main")
    private Boolean isMain; // Là tài khoản ngân hàng chính


    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

	public Boolean getIsMain() {
		return isMain;
	}

	public void setIsMain(Boolean isMain) {
		this.isMain = isMain;
	}
    


}
