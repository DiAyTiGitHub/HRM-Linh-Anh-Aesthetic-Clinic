import {Button, ButtonGroup, Grid, Tooltip} from "@material-ui/core";
import {DoneAll, Stop, ThumbDown} from "@material-ui/icons";
import AddIcon from "@material-ui/icons/Add";
import CheckBoxIcon from "@material-ui/icons/CheckBox";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import FilterListIcon from "@material-ui/icons/FilterList";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import LocalConstants, {RECRUITMENT_REQUEST, RecruitmentRequestStatus} from "app/LocalConstants";
import {useStore} from "app/stores";
import {Form, Formik} from "formik";
import {observer} from "mobx-react";
import React, {memo, useState} from "react";
import {useTranslation} from "react-i18next";
import {useHistory} from "react-router-dom";
import RecruitmentFilter from "./RecruitmentRequestV2Filter";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import GetAppIcon from "@material-ui/icons/GetApp";

function RecruitmentRequestV2Toolbar() {
    const history = useHistory();
    const {recruitmentRequestStore} = useStore();
    const {t} = useTranslation();

    const {
        pagingRecruitmentRequest,
        searchObject,
        listChosen,
        handleSetSearchObject,
        handleDeleteList,
        handleOpenCreateEdit,
        handleOpenConfirmUpdateStatusPopup,
        handleOpenChoicePersonInCharge,
        exportExcelByFilter,
        handleDownloadRecruitmentRequestTemplate,
        uploadFileExcel,
        handleStopRecruitment
    } = recruitmentRequestStore;

    function handlePreSubmit(values) {
        const newValues = {
            ...values,
            departmentId: values?.department?.id,
            positionTitleId: values?.positionTitle?.id,
            recruitmentPlanId: values?.recruitmentPlan?.id,
            organizationId: values?.organization?.id,
        };
        return newValues;
    }

    async function handleFilter(values) {
        const newSearchObject = {
            ...handlePreSubmit(values),
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingRecruitmentRequest();
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

    const hasApprovePermission = listChosen?.some((item) => item?.approvePermission === true);
    const hasSentPermission = listChosen?.some((item) => item?.sentPermission);

    const handleExportExcel = async () => {
        await exportExcelByFilter(searchObject)
    };

    return (
        <Formik enableReinitialize initialValues={searchObject} onSubmit={handleFilter}>
            {({resetForm, values, setFieldValue, setValues}) => {
                return (
                    <Form autoComplete='off'>
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                                        <Button startIcon={<AddIcon/>} onClick={() => handleOpenCreateEdit()}>
                                            {t("general.button.add")}
                                        </Button>
                                        <Button startIcon={<CloudUploadIcon/>} onClick={() => handleExportExcel()}>
                                            {t("general.button.exportExcel")}
                                        </Button>

                                        {hasApprovePermission && (
                                            <Button
                                                startIcon={<DoneAll/>}
                                                onClick={() =>
                                                    handleOpenConfirmUpdateStatusPopup(
                                                        LocalConstants.RecruitmentRequestStatus.APPROVED.value
                                                    )
                                                }>
                                                Duyệt
                                            </Button>
                                        )}
                                        {hasSentPermission && (
                                            <Button
                                                startIcon={<DoneAll/>}
                                                onClick={() =>
                                                    handleOpenConfirmUpdateStatusPopup(
                                                        LocalConstants.RecruitmentRequestStatus.APPROVED.value
                                                    )
                                                }>
                                                Gửi lên cấp trên
                                            </Button>
                                        )}
                                        <Button
                                            disabled={listChosen?.length === 0}
                                            startIcon={<ThumbDown/>}
                                            onClick={() =>
                                                handleOpenConfirmUpdateStatusPopup(
                                                    LocalConstants.RecruitmentRequestStatus.REJECTED.value
                                                )
                                            }>
                                            Từ chối
                                        </Button>

                                        <Button
                                            disabled={listChosen?.length === 0}
                                            startIcon={<CheckBoxIcon/>}
                                            onClick={() =>
                                                handleOpenConfirmUpdateStatusPopup(
                                                    LocalConstants.RecruitmentRequestStatus.COMPLETED.value
                                                )
                                            }>
                                            Hoàn thành
                                        </Button>
                                        {[LocalConstants.RecruitmentRequestStatus.HR_LEADER.value, LocalConstants.RecruitmentRequestStatus.START_RECRUITING.value].includes(listChosen[0]?.status) && (
                                            <Button
                                                disabled={listChosen?.length === 0}
                                                startIcon={<CheckBoxIcon/>}
                                                onClick={() => handleOpenChoicePersonInCharge()}>
                                                Gán người phụ trách
                                            </Button>
                                        )}
                                        <Tooltip
                                            placement="top"
                                            title={"Import yêu cầu tuyển dụng"}
                                            arrow
                                        >
                                            <Button
                                                startIcon={<CloudUploadIcon/>}
                                                onClick={() => document.getElementById("fileExcel").click()}
                                            >
                                                {t("general.button.importExcel")}
                                            </Button>
                                        </Tooltip>
                                        <Tooltip
                                            placement="top"
                                            title={"Tải mẫu import yêu cầu tuyển dụng"}
                                            arrow
                                        >
                                            <Button
                                                startIcon={<GetAppIcon/>}
                                                onClick={() => handleDownloadRecruitmentRequestTemplate()}
                                            >
                                                Tải mẫu nhập
                                            </Button>
                                        </Tooltip>
                                        <Button
                                            disabled={listChosen?.length === 0}
                                            startIcon={<DeleteOutlineIcon/>}
                                            onClick={handleDeleteList}>
                                            {t("general.button.delete")}
                                        </Button>
                                        <Button
                                            disabled={listChosen?.length === 0}
                                            startIcon={<Stop/>}
                                            onClick={() => handleStopRecruitment(listChosen.map(item => item.id), RECRUITMENT_REQUEST.STOP)}>
                                            Ngừng tuyển
                                        </Button>
                                    </ButtonGroup>
                                    <input
                                        type="file"
                                        id="fileExcel"
                                        style={{display: "none"}}
                                        onChange={uploadFileExcel}
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <div className='flex justify-between align-center'>
                                        <Tooltip placement='top' title='Tìm kiếm theo mã, tên yêu cầu tuyển dụng'>
                                            <GlobitsTextField
                                                placeholder='Tìm kiếm theo mã, tên yêu cầu tuyển dụng...'
                                                name='keyword'
                                                variant='outlined'
                                                notDelay
                                            />
                                        </Tooltip>

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

                            <RecruitmentFilter
                                isOpenFilter={isOpenFilter}
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                            />
                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(RecruitmentRequestV2Toolbar));
