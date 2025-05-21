import LocalConstants from "app/LocalConstants";

export class RecruitmentRequest {
    id = null;
    code = null; // ma yeu cau tuyen dung
    name = null; // ten yeu cau
    organization = null; // cong ty/to chuc
    hrDepartment = null; // phong ban
    description = null; // mo ta cong viec
    request = null; // yeu cau
    positionTitle = null; // Chức danh cần tuyển

    inPlanQuantity = null; // Số lượng trong định biên
    extraQuantity = null; // Số lượng tuyển lọc = Số lượng tuyển ngoài định biên
    totalQuantity = null; // Tổng số lượng đề nghị tuyển = inPlanQuantity + extraQuantity
    announcementQuantity = null; // Số lượng đăng tuyển/Thông báo tuyển
    status = LocalConstants.RecruitmentRequestStatus.CREATED.value; // trang thái: HrConstants.RecruitmentRequestStatus
    proposer = null; // Người đề xuất
    proposalDate = null; // Ngày đề xuất
    proposalReceiptDate = null; // Ngày nhận đề xuất (ngày người phụ trách nhận đề xuất để thực hiện yêu cầu tuyển dụng)

    constructor() {
    }
}