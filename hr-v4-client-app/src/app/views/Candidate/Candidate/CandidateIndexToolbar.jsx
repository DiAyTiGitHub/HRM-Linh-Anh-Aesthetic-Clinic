import {Button, ButtonGroup, Grid, Tooltip} from "@material-ui/core";
import {Mail, RoundedCorner} from "@material-ui/icons";
import AddIcon from "@material-ui/icons/Add";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import DoneAllIcon from "@material-ui/icons/DoneAll";
import FilterListIcon from "@material-ui/icons/FilterList";
import GetAppIcon from "@material-ui/icons/GetApp";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import ScheduleIcon from "@material-ui/icons/Schedule";
import SearchIcon from "@material-ui/icons/Search";
import ThumbDownIcon from "@material-ui/icons/ThumbDown";
import ConstantList from "app/appConfig";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import LocalConstants, {CandidateStatusEnum} from "app/LocalConstants";
import {Form, Formik} from "formik";
import {observer} from "mobx-react";
import React, {memo, useState} from "react";
import {useTranslation} from "react-i18next";
import {useHistory} from "react-router-dom";
import {useStore} from "../../../stores";
import CandidateFilter from "./CandidateFilter";
import CandidatePopupNextRound from "../Candidate/CandidatePopup/CandidateConfirmNextRound"
import AlarmOnIcon from "@material-ui/icons/AlarmOn";
import AlarmOffIcon from "@material-ui/icons/AlarmOff";
import WaitingJobCandidateReceivePopup
    from "../WaitingJobCandidate/WaitingJobCandidatePopup/WaitingJobCandidateReceivePopup";

function CandidateIndexToolbar() {
    const history = useHistory();
    const {candidateStore, waitingJobCandidateStore} = useStore();
    const {t} = useTranslation();
    const {
        handleOpenReceiveJobPopup,
        listChosen,
        openReceiveJobPopup,
        handleSelectListChosen
    } = waitingJobCandidateStore;
    const {
        handleDeleteList,
        pagingCandidates,
        searchObject,
        listOnDelete,
        handleSetSearchObject,
        handleOpenRejectPopup,
        handleOpenApprovePopup,
        handleOpenNotScreenedPopup,
        handleOpenScreenedPassPopup,
        handleDownloadCandidateTemplate,
        uploadFileExcel,
        handleOpenChooseTemplatePopup,
        approveCV,
        setOpenPopupExportExcelRecruitmentReports,
    } = candidateStore;
    const {
        doActionAssignment,
        resetStore
    } = useStore().candidateRecruitmentRoundStore;
    const {handleOpenCreateEdit} = useStore().candidateRecruitmentRoundStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            departmentId: values?.department?.id,
            organizationId: values?.organization?.id,
            positionTitleId: values?.positionTitle?.id,
            recruitmentPlanId: values?.recruitmentPlan?.id,
            pageIndex: 1,
        };
        let recruitmentRoundIds = [];
        if (values?.recruitmentRound) {
            recruitmentRoundIds = values?.recruitmentRound?.map((i) => i.id);
        }
        newSearchObject.recruitmentRoundIds = recruitmentRoundIds;

        handleSetSearchObject(newSearchObject);
        await pagingCandidates();
    }

    function handleApproveCandidate() {
        handleOpenApprovePopup();
    }

    function handleDeclineCandidate() {
        handleOpenRejectPopup();
    }

    const [isOpenFilter, setIsOpenFilter] = useState(false);

    function handleCloseFilter() {
        if (isOpenFilter) {
            setIsOpenFilter(false);
        }
    }

    function handleOpenFilter() {
        if (!isOpenFilter) {
            setIsOpenFilter(true);
        }
    }

    function handleTogglePopupFilter() {
        if (isOpenFilter) handleCloseFilter();
        else handleOpenFilter();
    }

    const buttonList = [
        {
            label: "Thêm ứng viên",
            icon: <AddIcon/>,
            onClick: () => history.push(ConstantList.ROOT_PATH + `candidate/new-candidate`),
            show: true,
        },
        {
            label: "Xác nhận qua sơ lọc",
            icon: <CheckCircleIcon/>,
            onClick: handleOpenScreenedPassPopup,
            disabled: listOnDelete?.length <= 0,
            show: [LocalConstants.CandidateStatus.NOT_APPROVED_YET.value].includes(searchObject?.status),
        },
        {
            label: "Đánh dấu không đạt sơ lọc",
            icon: <HighlightOffIcon/>,
            onClick: handleOpenNotScreenedPopup,
            disabled: listOnDelete?.length <= 0,
            show: [LocalConstants.CandidateStatus.NOT_APPROVED_YET.value].includes(searchObject?.status),
        },
        {
            label: "Gửi duyệt CV",
            icon: <HighlightOffIcon/>,
            onClick: async () => {
                await approveCV({candidateIds: listOnDelete?.map((item) => item.id)})
                pagingCandidates();
            },
            show: [LocalConstants.CandidateStatus.SCREENED_PASS.value].includes(searchObject?.status),
        },
        {
            label: "Nhận việc",
            icon: <AlarmOnIcon/>,
            onClick: () => {
                handleSelectListChosen(listOnDelete)
                handleOpenReceiveJobPopup(true)
            },
            show: [LocalConstants.CandidateStatus.SEND_OFFER.value].includes(searchObject?.status),
        },
        {
            label: "Từ chối hồ sơ",
            icon: <ThumbDownIcon/>,
            onClick: handleOpenRejectPopup,
            disabled: listOnDelete?.length <= 0,
            show: searchObject?.status !== LocalConstants.CandidateStatus.REJECTED.value,
        },
        {
            label: "Gửi Mail",
            icon: <Mail/>,
            onClick: handleOpenChooseTemplatePopup,
            show: !!searchObject?.status,
        },
        {
            label: "Lên lịch phỏng vấn",
            icon: <ScheduleIcon/>,
            onClick: () => handleOpenCreateEdit(listOnDelete?.[0]?.currentCandidateRound),
            show: searchObject?.status === LocalConstants.CandidateStatus.APPROVE_CV.value && listOnDelete?.length === 1,
        },
        {
            label: "Chuyển sang vòng kế tiếp",
            icon: <RoundedCorner/>,
            onClick: async () => {
                candidateStore.handleOpenPopupNextRound();
                console.log("openPopupNextRound:", candidateStore.openPopupNextRound);
                // await passToNextRound(listOnDelete?.[0]?.currentCandidateRound?.id);
                // pagingCandidates();
            },
            show: searchObject?.status === LocalConstants.CandidateStatus.APPROVE_CV.value && listOnDelete?.length > 0,
        },

        {
            label: "Xác nhận tuyển",
            icon: <AddIcon/>,
            onClick: async () => {
                await doActionAssignment(listOnDelete[0]?.currentCandidateRound?.id, CandidateStatusEnum.PENDING_ASSIGNMENT)
                resetStore()
                pagingCandidates()
            },
            show: listOnDelete?.length === 1 && listOnDelete[0]?.currentCandidateRound?.id,
        },
        {
            label: t("general.button.importExcel"),
            icon: <CloudUploadIcon/>,
            onClick: () => document.getElementById("fileExcel").click(),
            tooltip: "Import hồ sơ ứng viên",
            show: true,
        },
        {
            label: "Tải mẫu nhập",
            icon: <GetAppIcon/>,
            onClick: handleDownloadCandidateTemplate,
            tooltip: "Tải mẫu import hồ sơ ứng viên",
            show: true,
        },
        {
            label: "Báo cáo tuyển dụng",
            icon: <GetAppIcon/>,
            onClick: () => setOpenPopupExportExcelRecruitmentReports(true),
            tooltip: "Tải báo cáo theo kế hoạch",
            show: true,
        },
        {
            label: t("general.button.delete"),
            icon: <DeleteOutlineIcon/>,
            onClick: handleDeleteList,
            disabled: listOnDelete?.length <= 0,
            show: true,
        },
    ];

    return (
        <Formik enableReinitialize initialValues={JSON.parse(JSON.stringify(searchObject))} onSubmit={handleFilter}>
            {({resetForm, values, setFieldValue, setValues}) => {
                return (
                    <Form autoComplete='off'>
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                                        {buttonList.map((btn, index) => {
                                            if (!btn.show) return null;

                                            const buttonElement = (
                                                <Button
                                                    key={index}
                                                    startIcon={btn.icon}
                                                    onClick={btn.onClick}
                                                    disabled={btn.disabled}>
                                                    {btn.label}
                                                </Button>
                                            );

                                            return btn.tooltip ? (
                                                <Tooltip key={index} title={btn.tooltip} placement='top' arrow>
                                                    {buttonElement}
                                                </Tooltip>
                                            ) : (
                                                buttonElement
                                            );
                                        })}
                                    </ButtonGroup>
                                    <input
                                        type='file'
                                        id='fileExcel'
                                        style={{display: "none"}}
                                        onChange={uploadFileExcel}
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <div className='flex justify-between align-center'>
                                        <GlobitsTextField
                                            placeholder='Tìm kiếm theo mã, tên ứng viên...'
                                            name='keyword'
                                            variant='outlined'
                                            notDelay
                                        />

                                        <ButtonGroup
                                            className='filterButtonV4'
                                            color='container'
                                            aria-label='outlined primary button group'>
                                            <Button
                                                startIcon={<SearchIcon className={``}/>}
                                                className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                                                type='submit'>
                                                Tìm kiếm
                                            </Button>
                                            <Button
                                                startIcon={
                                                    <FilterListIcon
                                                        className={` filterRotateIcon ${isOpenFilter && "onRotate"}`}
                                                    />
                                                }
                                                className=' d-inline-flex py-2 px-8 btnHrStyle'
                                                onClick={handleTogglePopupFilter}>
                                                Bộ lọc
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>

                            <CandidateFilter
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                                isOpenFilter={isOpenFilter}
                            />
                            {candidateStore.openPopupNextRound && <CandidatePopupNextRound/>}
                        </Grid>
                        {openReceiveJobPopup && (
                            <WaitingJobCandidateReceivePopup/>
                        )}
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(CandidateIndexToolbar));
