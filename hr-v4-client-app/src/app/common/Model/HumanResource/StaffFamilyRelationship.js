export class StaffFamilyRelationship {
    id = null;
    staff = null;
    familyRelationship = null;
    fullName = null;
    birthDate = null;
    profession = null;
    address = null;
    description = null;
    workingPlace = null;
    isDependent = null; // là người phụ thuộc => Dùng để tính người phụ thuộc trong bảng lương
    taxCode = null; // Mã số thuế
    dependentDeductionFromDate = null; // Ngày bắt đầu được giảm trừ
    dependentDeductionToDate = null
    constructor() {
        this.isDependent = false;
    }
}