import { Staff } from "../Staff";

export class Candidate extends Staff {
  candidateCode = null;
  // tab 1
  permanentResidence = null; // Hộ khẩu thường trú
  currentResidence = null; // Noi o hien tai
  administrativeUnit = null; // xa phuong, tinh thanh, que quan
  district = null;
  province = null;
  nativeVillage = null;

  // tab 2 - Thông tin tuyển dụng
  recruitment = null; // dot tuyen dung
  position = null; // vi tri ung tuyen
  submissionDate = null; // Ngày nop ho so
  interviewDate = null; // Ngày phong van = Ngày gap mat ung vien
  desiredPay = null; // muc luong mong muon
  possibleWorkingDate = null; // Ngày co the lam viec
  onboardDate = null; // Ngày tiếp nhận nhân viên
  organization = null;
  department = null;
  introducer = null;
  recruitmentPlan = null;
  recruitmentRequest = null;
  recruitmentNew = false;
  replacementRecruitment = false;

  // tab 3 - Trình độ học vấn/quá trình đào tạo
  candidateEducationalHistories = []; // Quá trình đào tạo của ứng viên

  // tab 4 - Chứng chỉ (hiện có của nhân viên)
  candidateCertificates = []; // Bang cap, Chứng chỉ (hiện có của nhân viên)

  // tab 5 - Kinh nghiệm làm việc
  candidateWorkingExperiences = []; // Kinh nghiệm làm việc

  // tab 6 - Tệp đính kèm
  candidateAttachments = []; // Tệp đính kèm

  // Các trường khác
  // list status of candidate: Trang thai ho so ung vien
  approvalStatus = null; // trang thai ho so ung vien da duoc duyet hay chua
  // Xem status: HrConstants.CandidateApprovalStatus
  examStatus = null; // trang thai ung vien co PASS bai test cua dot phong van/thi tuyen hay khong
  // Xem status: HrConstants.CandidateExamStatus
  receptionStatus = null; // trạng thái của ứng viên sau khi đã PASS bài phỏng vấn/thi tuyển, trạng thái này chỉ ứng viên có được nhận việc hay không
  // Xem status: HrConstants.CandidateReceptionStatus
  onboardStatus = null; // trạng thái chỉ tình trạng nhận việc của ứng viên (không đến nhận việc, đã nhận việc,...)
  // Xem status: HrConstants.CandidateOnboardStatus
  refusalReason = null; // lý do từ chối
  staff = null; // nhan vien duoc tao sau khi ung vien da nhan viec thanh cong
  positionTitle = null;
}

