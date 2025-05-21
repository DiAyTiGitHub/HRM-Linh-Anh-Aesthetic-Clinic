export class PersonBankAccount {
    id = null;
    personId = null;
    person = null;
    bank = null; // Ngân hàng nào
    bankAccountName = null; // Tên tài khoản ngân hàng
    bankAccountNumber = null; // Số tài khoản ngân hàng
    bankBranch = null; // Chi nhánh ngân hàng
    isMain = null; // Là tài khoản ngân hàng chính

    constructor() {
        this.isMain = true;
    }
}