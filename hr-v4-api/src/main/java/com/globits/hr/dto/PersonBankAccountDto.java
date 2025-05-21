package com.globits.hr.dto;

import java.util.UUID;

import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.PersonDto;
import com.globits.hr.domain.PersonBankAccount;

public class PersonBankAccountDto extends BaseObjectDto {
	private UUID personId;
	private PersonDto person;
	private BankDto bank; // Ngân hàng nào
	private String bankAccountName; // Tên tài khoản ngân hàng
	private String bankAccountNumber; // Số tài khoản ngân hàng
	private String bankBranch; // Chi nhánh ngân hàng
	private Boolean isMain; // Là tài khoản ngân hàng chính

	public PersonBankAccountDto() {
	}

	public PersonBankAccountDto(PersonBankAccount entity) {
		super(entity);

		if (entity == null)
			return;

		if (entity.getPerson() != null){
			this.personId = entity.getPerson().getId();
			this.person = new PersonDto();
			this.person.setId(entity.getPerson().getId());
			this.person.setDisplayName(entity.getPerson().getDisplayName());
		}

		if (entity.getBank() != null) {
			this.bank = new BankDto(entity.getBank());
		}

		this.bankAccountNumber = entity.getBankAccountNumber();
		this.bankAccountName = entity.getBankAccountName();
		this.bankBranch = entity.getBankBranch();
		this.isMain = entity.getIsMain();

	}

	public PersonBankAccountDto(PersonBankAccount entity, Boolean isDetail) {
		this(entity);

		if (isDetail == null || isDetail.equals(false)) {
			return;
		}

		// other detail...
	}

	public UUID getPersonId() {
		return personId;
	}

	public void setPersonId(UUID personId) {
		this.personId = personId;
	}

	public BankDto getBank() {
		return bank;
	}

	public void setBank(BankDto bank) {
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

	public PersonDto getPerson() {
		return person;
	}

	public void setPerson(PersonDto person) {
		this.person = person;
	}


}
