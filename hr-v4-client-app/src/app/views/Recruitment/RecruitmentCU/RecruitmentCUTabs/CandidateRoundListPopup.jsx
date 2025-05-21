import { Button, DialogActions, DialogContent, Grid, Icon, IconButton, Tooltip } from "@material-ui/core";
import { t } from "app/common/CommonFunctions";
import { useStore } from "app/stores";
import React, { memo, useCallback, useEffect } from "react";
import GlobitsPopupV2 from "../../../../common/GlobitsPopupV2";
import GlobitsTable from "../../../../common/GlobitsTable";
import {
    CandidateRecruitmentRoundResult,
    CandidateRecruitmentRoundStatusLabel,
    CandidateStatus,
    GENDER,
    HttpStatus,
} from "../../../../LocalConstants";
import { formatDate } from "../../../../LocalFunction";
import { getByIdRecruitmentRound } from "./recruitmentRoundService";

import BlockIcon from "@material-ui/icons/Block";
import { observer } from "mobx-react";
import CandidateRecruitmentRoundPopup from "../CandidateRecruitmentRound/CandidateRecruitmentRoundPopup";
import CandidateRoundListPopupIndexToolbar from "../CandidateRecruitmentRound/CandidateRoundListPopupIndexToolbar";

const CandidateRoundListPopup = (prop) => {
    const [data, setData] = React.useState([]);

    const { handleOpenCreateEdit, handleSelectRound, handleSelectListDelete } =
        useStore().candidateRecruitmentRoundStore;
    const { evaluationCandidateRoundStore } = useStore();
    const { handleOpenFormEvaluationCandidateRound } = evaluationCandidateRoundStore;

    const handleByIdRecruitment = useCallback(async () => {
        const response = await getByIdRecruitmentRound(prop?.value?.id);
        if (response?.status === HttpStatus.OK) {
            if (response?.data?.status === HttpStatus.OK) {
                setData(response?.data.data);
            }
        }
    }, [prop?.value?.id]);

    const columns = [
        {
            title: t("general.action"),
            minWidth: "100px",
            align: "center",
            render: (rowData) => (
                <div className='flex align-center justify-center'>
                    <Tooltip arrow placement='top' title={"Chỉnh sửa Ứng viên vòng phỏng vấn"}>
                        <IconButton
                            className='ml-4'
                            size='small'
                            onClick={() => {
                                handleSelectRound(rowData);
                                handleOpenCreateEdit(rowData?.id);
                            }}>
                            <Icon fontSize='small' style={{ color: "#3f51b5" }}>
                                edit
                            </Icon>
                        </IconButton>
                    </Tooltip>
                    <Tooltip arrow placement='top' title={"Đánh giá Ứng viên vòng phỏng vấn"}>
                        <IconButton
                            className='ml-4'
                            size='small'
                            onClick={() => {
                                handleSelectRound(rowData);
                                handleOpenFormEvaluationCandidateRound(rowData?.id);
                            }}>
                            <Icon fontSize='small' style={{ color: "#3f51b5" }}>
                                assignment_turned_in
                            </Icon>
                        </IconButton>
                    </Tooltip>
                </div>
            ),
        },
        {
            title: "Mã ứng viên",
            field: "candidate.candidateCode",
            minWidth: "150px",
        },
        {
            title: "Ứng viên",
            minWidth: "150px",
            render: (rowData) => (
                <>
                    {rowData.candidate.displayName && (
                        <p className='m-0'>
                            <strong>{rowData.candidate.displayName}</strong>
                        </p>
                    )}

                    {rowData.candidate.birthDate && (
                        <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY", rowData.candidate.birthDate)}</p>
                    )}

                    {rowData.candidate.gender && (
                        <p className='m-0'>
                            Giới tính:{" "}
                            {rowData?.candidate?.gender ? GENDER.getNameByValue(rowData.candidate.gender) : ""}
                        </p>
                    )}

                    {rowData.candidate.birthPlace && <p className='m-0'>Nơi sinh: {rowData.candidate.birthPlace}</p>}
                </>
            ),
        },
        {
            title: "Thông tin liên hệ",
            field: "info",
            minWidth: "150px",
            render: (rowData) => (
                <>
                    {rowData.candidate.phoneNumber && <p className='m-0'>SĐT: {rowData.candidate.phoneNumber}</p>}
                    {rowData.candidate.email && <p className='m-0'>Email: {rowData.candidate.email}</p>}
                </>
            ),
        },
        {
            title: "Trạng thái hồ sơ",
            field: "info",
            minWidth: "150px",
            render: (rowData) => (
                <>
                    {
                        <p className='m-0'>
                            {rowData?.candidate?.status
                                ? CandidateStatus.getNameByValue(rowData?.candidate?.status)
                                : ""}
                        </p>
                    }
                </>
            ),
        },
        {
            title: "Trạng thái",
            field: "info",
            minWidth: "150px",
            render: (rowData) => (
                <>
                    {
                        <p className='m-0'>
                            {rowData?.status ? CandidateRecruitmentRoundStatusLabel[rowData.status] : ""}
                        </p>
                    }
                </>
            ),
        },
        {
            title: "Kết quả vòng phỏng vấn",
            field: "resultStatus",
            minWidth: "150px",
            render: (rowData) => (
                <span>
                    {
                        <p className='m-0'>
                            {rowData?.resultStatus ? CandidateRecruitmentRoundResult[rowData.resultStatus] : ""}
                        </p>
                    }
                </span>
            ),
        },
        {
            title: "Thời gian phỏng vấn",
            field: "actualTakePlaceDate",
            minWidth: "150px",
            render: (rowData) => (
                <span>
                    {rowData.candidate?.submissionDate && formatDate("DD/MM/YYYY", rowData.candidate?.submissionDate)}
                </span>
            ),
        },
        {
            title: "Địa điểm phỏng vấn",
            field: "examPosition",
            minWidth: "150px",
        },
        {
            title: "Đơn vị tuyển dụng",
            field: "candidate.organization.name",
            align: "left",
            minWidth: "150px",
        },
        {
            title: "Phòng ban tuyển dụng",
            field: "candidate.department.name",
            align: "left",
            minWidth: "150px",
        },
        {
            title: "Vị trí tuyển dụng",
            field: "candidate.positionTitle.name",
            align: "left",
            minWidth: "150px",
        },
    ];

    useEffect(() => {
        handleByIdRecruitment();
    }, []);
    return (
        <GlobitsPopupV2
            open={prop.open}
            onClosePopup={prop.handleClose}
            scroll={"paper"}
            size='lg'
            noDialogContent
            title={"Ứng viên tham gia"}>
            <div className='dialog-body'>
                <DialogContent className='o-hidden p-12'>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <CandidateRoundListPopupIndexToolbar handleByIdRecruitment={handleByIdRecruitment} />
                        </Grid>
                        <Grid item xs={12}>
                            <GlobitsTable
                                selection
                                columns={columns}
                                data={data}
                                nonePagination
                                pageSizeOption={[10, 25, 50]}
                                handleSelectList={handleSelectListDelete}
                            />
                        </Grid>
                    </Grid>
                </DialogContent>
            </div>
            <div className='dialog-footer'>
                <DialogActions className='p-0'>
                    <div className='flex flex-space-between flex-middle'>
                        <Button
                            startIcon={<BlockIcon />}
                            variant='contained'
                            className='mr-12 btn btn-gray d-inline-flex'
                            onClick={() => prop.handleClose()}>
                            {t("general.button.cancel")}
                        </Button>
                    </div>
                </DialogActions>
            </div>
            <CandidateRecruitmentRoundPopup handleAfterSubmit={handleByIdRecruitment} />
        </GlobitsPopupV2>
    );
};

export default memo(observer(CandidateRoundListPopup));
