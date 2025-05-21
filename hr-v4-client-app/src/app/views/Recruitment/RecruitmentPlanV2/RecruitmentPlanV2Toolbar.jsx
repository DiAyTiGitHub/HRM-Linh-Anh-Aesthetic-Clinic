import {observer} from "mobx-react";
import React, {memo, useEffect, useState} from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import {useHistory} from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import {Form, Formik} from "formik";
import {Grid, Button, ButtonGroup, Tooltip} from "@material-ui/core";
import {useStore} from "app/stores";
import {useTranslation} from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import RecruitmentPlanV2Filter from "./RecruitmentPlanV2Filter";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import GetAppIcon from "@material-ui/icons/GetApp";
import GlobitsQrLink from "../../../common/GlobitsQrLink";

function RecruitmentPlanV2Toolbar() {
    const history = useHistory();
    const {recruitmentPlanStore} = useStore();
    const {t} = useTranslation();

    const {
        pagingRecruitmentPlan,
        searchObject,
        listChosen,
        handleSetSearchObject,
        handleDeleteList,
        handleOpenCreateEdit,
        handleDownloadRecruitmentTemplate,
        uploadFileExcel,
        handleDownloadRecruitmentRoundTemplate,
        uploadFileRecruitmentRoundExcel,
        handleOpenCreateLink,
        openCreateLink,
    } = recruitmentPlanStore;

    function handlePreSubmit(values) {
        const newValues = {
            ...values,
            departmentId: values?.department?.id,
            positionTitleId: values?.positionTitle?.id,
            recruitmentRequestId: values?.recruitmentRequest?.id,
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
        await pagingRecruitmentPlan();
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

    const {isManager, isAdmin, checkAllUserRoles} = useStore().hrRoleUtilsStore;
    useEffect(() => {
        checkAllUserRoles()
    }, []);
    return (
        <Formik
            enableReinitialize
            initialValues={searchObject}
            onSubmit={handleFilter}
        >
            {({resetForm, values, setFieldValue, setValues}) => {

                return (
                    <Form autoComplete="off">
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    {(isManager || isAdmin) && (
                                        <ButtonGroup
                                            color="container"
                                            aria-label="outlined primary button group"
                                        >
                                            <Button
                                                startIcon={<AddIcon/>}
                                                onClick={() => handleOpenCreateEdit()}
                                            >
                                                {t("general.button.add")}
                                            </Button>
                                            {/*
                            <Button
                                disabled={listChosen?.length === 0}
                                startIcon={<DoneAll/>}
                                onClick={() => handleOpenConfirmUpdateStatusPopup (LocalConstants.RecruitmentPlanStatus.APPROVED.value)}
                            >
                              Duyệt
                            </Button>

                            <Button
                                disabled={listChosen?.length === 0}
                                startIcon={<ThumbDown/>}
                                onClick={() => handleOpenConfirmUpdateStatusPopup (LocalConstants.RecruitmentPlanStatus.REJECTED.value)}
                            >
                              Từ chối
                            </Button>

                            <Button
                                disabled={listChosen?.length === 0}
                                startIcon={<CheckBoxIcon/>}
                                onClick={() => handleOpenConfirmUpdateStatusPopup (LocalConstants.RecruitmentPlanStatus.COMPLETED.value)}
                            >
                              Hoàn thành
                            </Button> */}
                                            <Tooltip
                                                placement="top"
                                                title={"Import kế hoạch tuyển dụng"}
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
                                                title={"Tải mẫu import kế hoạch tuyển dụng"}
                                                arrow
                                            >
                                                <Button
                                                    startIcon={<GetAppIcon/>}
                                                    onClick={() => handleDownloadRecruitmentTemplate()}
                                                >
                                                    Tải mẫu nhập
                                                </Button>
                                            </Tooltip>

                                            <Tooltip
                                                placement="top"
                                                title={"Import vòng tuyển dụng"}
                                                arrow
                                            >
                                                <Button
                                                    startIcon={<CloudUploadIcon/>}
                                                    onClick={() => document.getElementById("fileExcelRecruitmentRound").click()}
                                                >
                                                    {t("general.button.importExcel")} Vòng tuyển dụng
                                                </Button>
                                            </Tooltip>

                                            <Tooltip
                                                placement="top"
                                                title={"Tải mẫu nhập vòng tuyển dụng"}
                                                arrow
                                            >
                                                <Button
                                                    startIcon={<GetAppIcon/>}
                                                    onClick={() => handleDownloadRecruitmentRoundTemplate()}
                                                >
                                                    Tải mẫu nhập vòng tuyển dụng
                                                </Button>
                                            </Tooltip>
                                            <Button
                                                disabled={listChosen?.length === 0}
                                                startIcon={<DeleteOutlineIcon/>}
                                                onClick={handleDeleteList}
                                            >
                                                {t("general.button.delete")}
                                            </Button>
                                            <Button
                                                disabled={listChosen?.length !== 1}
                                                startIcon={<GetAppIcon/>}
                                                onClick={handleOpenCreateLink}
                                            >
                                                Tạo link tuyển dụng
                                            </Button>
                                        </ButtonGroup>
                                    )}
                                    {(isManager || isAdmin) && (
                                        <input
                                            type="file"
                                            id="fileExcel"
                                            style={{display: "none"}}
                                            onChange={uploadFileExcel}
                                        />)}
                                    {(isManager || isAdmin) && (
                                        <input
                                            type="file"
                                            id="fileExcelRecruitmentRound"
                                            style={{display: "none"}}
                                            onChange={uploadFileRecruitmentRoundExcel}
                                        />)}
                                </Grid>
                                <Grid item xs={12}>
                                    <div className="flex justify-between align-center">
                                        <Tooltip placement="top" title="Tìm kiếm theo mã, tên kế hoạch tuyển dụng">
                                            <GlobitsTextField
                                                placeholder="Tìm kiếm theo mã, tên kế hoạch tuyển dụng..."
                                                name="keyword"
                                                variant="outlined"
                                                notDelay
                                            />
                                        </Tooltip>

                                        <ButtonGroup
                                            className="filterButtonV4"
                                            color="container"
                                            aria-label="outlined primary button group"
                                        >
                                            <Button
                                                startIcon={<SearchIcon className={``}/>}
                                                className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                type="submit"
                                            >
                                                Tìm kiếm
                                            </Button>
                                            <Button
                                                startIcon={<FilterListIcon
                                                    className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`}/>}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>

                            <RecruitmentPlanV2Filter
                                isOpenFilter={isOpenFilter}
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                            />
                        </Grid>
                        {openCreateLink &&
                            <GlobitsQrLink
                                open={openCreateLink}
                                maxWidth={'xs'}
                                onConfirmDialogClose={() => handleOpenCreateLink(false)}
                                onYesClick={()=>handleOpenCreateLink(false)}
                                title={"Tạo link tuyển dụng"}
                                text={`${window.location.origin}/public-router/interview-public-link/${listChosen?.[0].id}`}
                                type={'link'}
                                agree={t("confirm_dialog.delete.agree")}
                                cancel={t("confirm_dialog.delete.cancel")}
                            />}
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(RecruitmentPlanV2Toolbar));