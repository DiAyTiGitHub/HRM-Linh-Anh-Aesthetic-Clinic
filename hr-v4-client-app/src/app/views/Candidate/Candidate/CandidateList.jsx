import React, { memo, useMemo, useState } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";
import { useHistory } from "react-router-dom";
import ConstantList from "app/appConfig";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import LocalConstants, {
    CandidateRecruitmentRoundResult,
    CandidateRecruitmentRoundStatusLabel,
    CandidateStatus,
} from "app/LocalConstants";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import CandidateConfirmPopup from "./CandidatePopup/CandidateConfirmPopup";
import HourglassEmptyIcon from "@material-ui/icons/HourglassEmpty";
import DoneAllIcon from "@material-ui/icons/DoneAll";
import ThumbDownIcon from "@material-ui/icons/ThumbDown";
import AssignmentTurnedInIcon from '@material-ui/icons/AssignmentTurnedIn';
function CandidateList(props) {
    const { isFromCandidatesInRecruitment } = props;

    const { candidateStore, evaluationCandidateRoundStore } = useStore();
    const { t } = useTranslation();

    const {
        listCandidates,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        getApprovalStatus,
        handleOpenApprovePopup,
        handleOpenRejectPopup,
        handleOpenNotScreenedPopup,
        handleOpenScreenedPassPopup,
        handleOpenResetApprovalStatus,
    } = candidateStore;

    const { handleOpenFormEvaluationCandidateRound, openFormEvaluationCandidateRound } = evaluationCandidateRoundStore;

    const history = useHistory();

    function handleUpdateApplicant(applicant) {
        history.push(ConstantList.ROOT_PATH + `candidate/` + applicant?.id);
    }

    const [anchorEl, setAnchorEl] = useState();
    const [selectedRow, setSelectedRow] = useState(null);

    let columns = null;
    if (isFromCandidatesInRecruitment) {
        columns = [
            {
                title: t("general.action"),
                minWidth: "100px",
                align: "center",
                render: (rowData) => {
                    return (
                        <div className='flex flex-middle justify-center'>
                            <Tooltip title='Cập nhật' placement='top'>
                                <IconButton
                                    size='small'
                                    onClick={function () {
                                        //link to new page like a staff
                                        history.push(
                                            ConstantList.ROOT_PATH +
                                                `candidate/` +
                                                rowData?.id +
                                                "?isFromCandidatesInRecruitment=true"
                                        );
                                    }}>
                                    <Icon fontSize='small' color='primary'>
                                        edit
                                    </Icon>
                                </IconButton>
                            </Tooltip>

                            <Tooltip title='Xóa' placement='top'>
                                <IconButton className='ml-4' size='small' onClick={() => handleDelete(rowData)}>
                                    <Icon fontSize='small' color='secondary'>
                                        delete
                                    </Icon>
                                </IconButton>
                                
                            </Tooltip>

                            <Tooltip title='Thao tác khác' placement='top'>
                                <IconButton
                                    className='ml-4'
                                    size='small'
                                    onClick={(event) => {
                                        setSelectedRow(rowData);
                                        setAnchorEl(event?.currentTarget);
                                    }}>
                                    <MoreHorizIcon />
                                </IconButton>
                            </Tooltip>
                        </div>
                    );
                },
            },
            {
                title: "Đơn vị tuyển dụng",
                field: "recruitment.organization.name",
                align: "left",
                minWidth: "150px",
            },
            {
                title: "Phòng ban tuyển dụng",
                field: "recruitment.department.name",
                align: "left",
                minWidth: "150px",
            },
            {
                title: "Vị trí tuyển dụng",
                field: "positionTitle",
                align: "left",
                minWidth: "150px",
            },
            {
                title: "Mã ứng viên",
                field: "candidateCode",
                minWidth: "150px",
            },
            {
                title: "Họ tên",
                field: "displayName",
                minWidth: "150px",
            },
            {
                title: "Ngày sinh",
                field: "birthDate",
                minWidth: "150px",
                render: (rowData) => <span>{rowData?.birthDate && formatDate("DD/MM/YYYY", rowData?.birthDate)}</span>,
            },
            {
                title: "Giới tính",
                field: "gender",
                minWidth: "150px",
                render: (rowData) => (
                    <span>{rowData?.gender === "M" ? "Nam" : rowData?.gender === "F" ? "Nữ" : ""}</span>
                ),
            },
            {
                title: "SĐT",
                field: "phoneNumber",
                minWidth: "150px",
                render: (rowData) => (
                    <>{rowData?.phoneNumber && <span className='pr-8'>{rowData?.phoneNumber}</span>}</>
                ),
            },
            {
                title: "Ngày nộp hồ sơ",
                field: "submissionDate",
                minWidth: "150px",
                render: (rowData) => (
                    <span>{rowData?.submissionDate && formatDate("DD/MM/YYYY", rowData?.submissionDate)} </span>
                ),
            },
            {
                title: "Trạng thái hồ sơ",
                field: "status",
                minWidth: "150px",
                render: function (applicant) {
                    return <span>{LocalConstants.CandidateStatus.getNameByValue(applicant?.status)}</span>;
                },
            },
        ];
    } else {
        columns = [
            {
                title: t("general.action"),
                minWidth: "100px",
                align: "center",
                render: (rowData) => {
                    return (
                        <div className='flex flex-middle justify-center'>
                            <Tooltip title='Cập nhật' placement='top'>
                                <IconButton
                                    size='small'
                                    onClick={function () {
                                        handleUpdateApplicant(rowData);
                                    }}>
                                    <Icon fontSize='small' color='primary'>
                                        edit
                                    </Icon>
                                </IconButton>
                            </Tooltip>

                            <Tooltip title='Xóa' placement='top'>
                                <IconButton className='ml-4' size='small' onClick={() => handleDelete(rowData)}>
                                    <Icon fontSize='small' color='secondary'>
                                        delete
                                    </Icon>
                                </IconButton>
                            </Tooltip>

                            <Tooltip title='Thao tác khác' placement='top'>
                                <IconButton
                                    className='ml-4'
                                    size='small'
                                    onClick={(event) => {
                                        setSelectedRow(rowData);
                                        setAnchorEl(event?.currentTarget);
                                    }}>
                                    <MoreHorizIcon />
                                </IconButton>
                            </Tooltip>
                        </div>
                    );
                },
            },
            {
                title: "Mã ứng viên",
                field: "candidateCode",
                minWidth: "150px",
            },
            {
                title: "Ứng viên",
                minWidth: "150px",
                render: (rowData) => (
                    <>
                        {rowData.displayName && (
                            <p className='m-0'>
                                <strong>{rowData.displayName}</strong>
                            </p>
                        )}

                        {rowData.birthDate && (
                            <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY", rowData.birthDate)}</p>
                        )}

                        {rowData.gender && (
                            <p className='m-0'>
                                Giới tính: {rowData.gender === "M" ? "Nam" : rowData.gender === "F" ? "Nữ" : ""}
                            </p>
                        )}

                        {rowData.birthPlace && <p className='m-0'>Nơi sinh: {rowData.birthPlace}</p>}
                    </>
                ),
            },
            {
                title: "Thông tin liên hệ",
                field: "info",
                minWidth: "150px",
                render: (rowData) => (
                    <>
                        {rowData.phoneNumber && <p className='m-0'>SĐT: {rowData.phoneNumber}</p>}
                        {rowData.email && <p className='m-0'>Email: {rowData.email}</p>}
                    </>
                ),
            },
            {
                title: "Ngày nộp hồ sơ",
                field: "submissionDate",
                minWidth: "150px",
                render: (rowData) => (
                    <span>{rowData?.submissionDate && formatDate("DD/MM/YYYY", rowData?.submissionDate)}</span>
                ),
            },
            {
                title: "Kế hoạch tuyển dụng",
                field: "recruitmentPlan.name",
                minWidth: "150px",
                render: (rowData) => (
                    <>{rowData?.recruitmentPlan && <span className='pr-8'>{rowData?.recruitmentPlan?.name}</span>}</>
                ),
            },
            {
                title: "Trạng thái vòng phỏng vấn",
                field: "currentCandidateRound",
                minWidth: "150px",
                render: (rowData) =>
                    rowData?.currentCandidateRound?.status
                        ? CandidateRecruitmentRoundStatusLabel[rowData?.currentCandidateRound?.status]
                        : "",
            },
            {
                title: "Vòng hiện tại",
                field: "currentCandidateRound",
                minWidth: "150px",
                render: (rowData) => <span>{rowData?.currentCandidateRound?.recruitmentRound?.name}</span>,
            },
            {
                title: "Ngày phỏng vấn",
                field: "currentCandidateRound",
                minWidth: "150px",
                render: (rowData) => (
                    <span>
                        {rowData?.currentCandidateRound?.actualTakePlaceDate &&
                            formatDate("DD/MM/YYYY", rowData?.currentCandidateRound?.actualTakePlaceDate)}
                    </span>
                ),
            },
            {
                title: "Kết quả vòng phỏng vấn",
                field: "currentCandidateRound",
                minWidth: "150px",
                render: (rowData) =>
                    rowData?.currentCandidateRound?.resultStatus
                        ? CandidateRecruitmentRoundResult[rowData?.currentCandidateRound?.resultStatus]
                        : "",
            },
            {
                title: "Trạng thái hồ sơ",
                field: "status",
                minWidth: "150px",
                render: function (applicant) {
                    return <span>{LocalConstants.CandidateStatus.getNameByValue(applicant?.status)}</span>;
                },
            },
            {
                title: "Đơn vị tuyển dụng",
                field: "organization.name",
                align: "left",
                minWidth: "150px",
            },
            {
                title: "Phòng ban tuyển dụng",
                field: "department.name",
                align: "left",
                minWidth: "150px",
            },
            {
                title: "Vị trí tuyển dụng",
                field: "positionTitle.name",
                align: "left",
                minWidth: "150px",
            },
        ];
    }

    if (searchObject?.status == LocalConstants.CandidateStatus.REJECTED.value) {
        columns.splice(columns.length - 1, 0, {
            title: "Lý do từ chối",
            field: "refusalReason",
            minWidth: "150px",
        });
    }

    function handleClosePopover() {
        setAnchorEl(null);
        setSelectedRow(null);
    }

    const handleOpenEvaluationCandidateRound = () => {
        handleOpenFormEvaluationCandidateRound(selectedRow?.currentCandidateRound?.id);
    };

    return (
        <>
            <GlobitsTable
                selection
                data={listCandidates}
                handleSelectList={handleSelectListDelete}
                columns={columns}
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                pageSize={searchObject?.pageSize}
                pageSizeOption={[10, 25, 50]}
                totalElements={totalElements}
                page={searchObject?.pageIndex}
            />

            {Boolean(anchorEl) && (
                <Menu
                    id='simple-menu-options'
                    anchorEl={anchorEl}
                    keepMounted
                    open={Boolean(anchorEl)}
                    onClose={handleClosePopover}
                    className='py-0'>
                    {[
                        {
                            isShow: selectedRow?.currentCandidateRound?.id,
                            label: "Đánh giá ứng viên trong vòng tuyển dụng",
                            icon: <AssignmentTurnedInIcon className='pr-6' fontSize='small' />,
                            action: handleOpenEvaluationCandidateRound,
                        },
                        {
                            isShow: selectedRow?.status !== CandidateStatus.NOT_APPROVED_YET.value,
                            label: "Đánh dấu chưa duyệt",
                            icon: <HourglassEmptyIcon className='pr-6' fontSize='small' />,
                            action: handleOpenResetApprovalStatus,
                        },
                        {
                            isShow: selectedRow?.status !== CandidateStatus.SCREENED_PASS.value,
                            label: "Xác nhận qua sơ lọc",
                            icon: <CheckCircleIcon className='pr-6' fontSize='small' style={{ color: "green" }} />,
                            action: handleOpenScreenedPassPopup,
                        },
                        {
                            isShow: selectedRow?.status !== CandidateStatus.NOT_SCREENED.value,
                            label: "Đánh dấu không đạt sơ lọc",
                            icon: <HighlightOffIcon className='pr-6' fontSize='small' style={{ color: "red" }} />,
                            action: handleOpenNotScreenedPopup,
                        },
                        {
                            isShow: selectedRow?.status !== CandidateStatus.REJECTED.value,
                            label: "Từ chối hồ sơ",
                            icon: <ThumbDownIcon className='pr-6' fontSize='small' style={{ color: "red" }} />,
                            action: handleOpenRejectPopup,
                        },
                    ].map(
                        ({ isShow, label, icon, action }, index) =>
                            isShow && (
                                <MenuItem
                                    key={index}
                                    className='flex items-center justify-center'
                                    onClick={() => {
                                        handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                        action();
                                        handleClosePopover();
                                    }}>
                                    {icon}
                                    {label}
                                </MenuItem>
                            )
                    )}
                </Menu>
            )}
        </>
    );
}

export default memo(observer(CandidateList));
