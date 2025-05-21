import { Grid } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate } from "app/LocalFunction";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";

function CandidateRecruitmentRounds() {
    const { candidateStore, candidateRecruitmentRoundStore } = useStore();
    const { t } = useTranslation();
    const { openRecruitmentRound, handleClose, selectedCandidate } = candidateStore;
    const { listCandidateRecruitmentRounds, getAllCandidateRoundByCandidateId } = candidateRecruitmentRoundStore;
    const columns = [
        {
            title: "Mã ứng viên",
            field: "candidateCode",
        },
        {
            title: "Họ tên",
            field: "displayName",
        },
        {
            title: "Ngày sinh",
            field: "birthDate",
            render: (rowData) => <span>{rowData?.birthDate && formatDate("DD/MM/YYYY", rowData?.birthDate)}</span>,
        },
        {
            title: "Đợt tuyển dụng",
            field: "recruitment",
            render: (rowData) => (
                <>{rowData?.recruitment && <span className='pr-8'>{rowData?.recruitment?.name}</span>}</>
            ),
        },
        {
            title: "Vị trí ứng tuyển",
            field: "position",
            render: (rowData) => <>{rowData?.position && <span className='pr-8'>{rowData?.position?.name}</span>}</>,
        },
    ];

    useEffect(() => {
        if (selectedCandidate?.id) {
            getAllCandidateRoundByCandidateId(selectedCandidate?.id);
        }
    }, [selectedCandidate?.id]);

    return (
        <GlobitsPopupV2
            open={openRecruitmentRound}
            handleClose={handleClose}
            hideFooter
            size='md'
            title='Quá trình tuyển dụng của ứng viên'
            onConfirm={handleClose}>
            <div className='dialog-body'>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <GlobitsTable data={listCandidateRecruitmentRounds} columns={columns} nonePagination />
                    </Grid>
                </Grid>
            </div>
        </GlobitsPopupV2>
    );
}

export default memo(observer(CandidateRecruitmentRounds));
