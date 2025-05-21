import LocalConstants from "app/LocalConstants";

export class CandidateRecruitmentRound {
  id = null;
  candidate = null;
  recruitmentRound = null;
  note = null;
  result = null;
  status = null;
  resultStatus = null;
  actualTakePlaceDate = null;
  examPosition = null;
  recruitmentType = null;
  workplace = null;

  constructor (entity = null) {
    if (entity) {
      this.id = entity.id ?? null;
      this.candidate = entity.candidate ?? null;
      this.recruitmentRound = entity.recruitmentRound ?? null;
      this.note = entity.note ?? null;
      this.status = entity.status ?? null;
      this.resultStatus = entity.resultStatus ?? null;
      this.actualTakePlaceDate = entity.actualTakePlaceDate ?? null;
      this.examPosition = entity.examPosition ?? null;
      this.recruitmentType = entity.recruitmentType ?? null;
    } else {
      this.result = LocalConstants.CandidateExamStatus.NOT_TESTED_YET.value;
      this.status = LocalConstants.CandidateRecruitmentRoundStatus.WAIT_RESPONSE

    }
  }
}
