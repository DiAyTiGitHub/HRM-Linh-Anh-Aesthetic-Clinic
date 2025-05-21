import React, { memo } from "react";
import { observer } from "mobx-react";
import LocalConstants from "app/LocalConstants";

const statusStyles = {
    [LocalConstants.CandidateExamStatus.NOT_TESTED_YET.value]: { color: 'purple', label: 'Chưa dự thi' },
    [LocalConstants.CandidateExamStatus.PASSED.value]: { color: 'green', label: 'Đạt' },
    [LocalConstants.CandidateExamStatus.FAILED.value]: { color: 'red', label: 'Không đạt' },
    [LocalConstants.CandidateExamStatus.REJECTED.value]: { color: '#d58a00', label: 'Đã từ chối' },
    [LocalConstants.CandidateExamStatus.RECRUITING.value]: { color: 'blue', label: 'Đang dự tuyển' },
};

function ExamResultInEachRecruitmentRound(props) {
    const { recruitmentRoundResult } = props;

    const name = `${recruitmentRoundResult?.recruitmentRound?.name}: `;
    const displayResult = statusStyles[recruitmentRoundResult?.result];

    if (displayResult) {
        return (
            <React.Fragment>
                <b style={{ color: displayResult.color }}>{name}{displayResult.label}</b> <br />
            </React.Fragment>
        );
    }

    return (
        <React.Fragment>
            <b>{name}Chưa có kết quả</b> <br />
        </React.Fragment>
    );
}

export default memo(observer(ExamResultInEachRecruitmentRound));