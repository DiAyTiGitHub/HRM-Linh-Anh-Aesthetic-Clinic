import { SearchObject } from "./SearchObject";

export class SearchCandidateRecruitmentRound extends SearchObject {
    candidate = null;
    recruitmentRound = null;
    recruitmentRoundId = null;

    chosenRecordIds = [];

    // Kết quả của ứng viên trong từng vòng tuyển dụng, Chi tiết tại: HrConstants.CandidateExamStatus
    result = null;
    // nhận xét ứng viên
    note = null;
    // ngày thực tế ứng viên được sắp xếp tham gia vòng tuyển dụng. VD: Vòng thi diễn ra vào 31/12 nhưng 2/1 ứng viên mới vào vòng tuyển => actualTakePlaceDate = 2/1
    actualTakePlaceDate = null;
    // vị trí ngồi dự thi/phỏng vấn
    examPosition = null;

    constructor() {
        super();
    }


}