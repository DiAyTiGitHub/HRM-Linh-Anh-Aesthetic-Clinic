export class StaffDisciplineHistory {
    id = null;
    disciplineDate = null; // Ngày quyết định kỷ luật
    discipline = null; // Hình thức kỷ luật
    staff = null; // Nhân viên bị kỷ luật
    staffId = null; // Nhân viên bị kỷ luật
    organization = null; // Đơn vị kỷ luật
    department = null; // Phòng ban kỷ luật
    file = null; // Tệp đính kèm

    constructor() {
        this.isMain = true;
    }
}