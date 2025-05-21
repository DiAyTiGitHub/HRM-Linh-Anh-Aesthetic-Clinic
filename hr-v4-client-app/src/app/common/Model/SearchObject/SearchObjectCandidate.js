import { SearchObject } from "./SearchObject";

export class SearchObjectCandidate extends SearchObject {
    recruitment = null;
    position = null;
    positionId = null;
    submissionDateFrom = null;
    submissionDateTo = null;
    interviewDateFrom = null;
    interviewDateTo = null;
    onboardDateFrom = null;
    onboardDateTo = null;
    interviewDate = null;
    submissionDate = null;
    onboardDate = null;
    department = null;
    departmentId = null;
    approvalStatus = null; // trang thai ho so ung vien da duoc duyet hay chua
    // Xem status: HrConstants.CandidateApprovalStatus
    examStatus = null; // trang thai ung vien co PASS bai test cua dot phong van/thi tuyen hay khong
    // Xem status: HrConstants.CandidateExamStatus
    receptionStatus = null; // trạng thái của ứng viên sau khi đã PASS bài phỏng vấn/thi tuyển, trạng thái
    // này chỉ ứng viên có được nhận việc hay không
    // Xem status: HrConstants.CandidateReceptionStatus
    onboardStatus = null; // trạng thái chỉ tình trạng nhận việc của ứng viên (không đến nhận việc, đã
    // nhận việc,...)
    // Xem status: HrConstants.CandidateOnboardStatus
    recruitmentPlan = null;
    recruitmentPlanId = null;
    organization = null;
    organizationId = null;
    constructor() {
        super();
    }


}