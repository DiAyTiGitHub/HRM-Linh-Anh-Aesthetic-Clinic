import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import { DoneAll, ThumbDown } from "@material-ui/icons";
import AddIcon from "@material-ui/icons/Add";
import CheckBoxIcon from '@material-ui/icons/CheckBox';
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import SearchIcon from '@material-ui/icons/Search';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import LocalConstants from "app/LocalConstants";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import RecruitmentFilter from "./AbsenceRequestFilter";

function AbsenceRequestToolbar() {
    const history = useHistory();
    const { absenceRequestStore } = useStore();
    const { t } = useTranslation();

    const {
        pagingRecruitmentRequest,
        searchObject,
        listChosen,
        handleSetSearchObject,
        handleDeleteList,
        handleOpenCreateEdit,
        handleOpenConfirmUpdateStatusPopup
    } = absenceRequestStore;

    function handlePreSubmit(values) {
        const newValues = {
            ...values,
            departmentId: values?.department?.id,
            positionId: values?.position?.id,
            recruitmentRequestId: values?.recruitmentRequest?.id,
            recruitmentPlanId: values?.recruitmentPlan?.id,
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

    return (
        <Formik
            enableReinitialize
            initialValues={searchObject}
            onSubmit={handleFilter}
        >
            {({ resetForm, values, setFieldValue, setValues }) => {

                return (
                    <Form autoComplete="off">
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={6}>
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            startIcon={<AddIcon />}
                                            onClick={() => handleOpenCreateEdit()}
                                        >
                                            {t("general.button.add")}
                                        </Button>

                                        <Button
                                            disabled={listChosen?.length === 0}
                                            startIcon={<DoneAll />}
                                            onClick={() => handleOpenConfirmUpdateStatusPopup(LocalConstants.AbsenceRequestApprovalStatus.APPROVED.value)}
                                        >
                                            Duyệt
                                        </Button>

                                        <Button
                                            disabled={listChosen?.length === 0}
                                            startIcon={<ThumbDown />}
                                            onClick={() => handleOpenConfirmUpdateStatusPopup(LocalConstants.AbsenceRequestApprovalStatus.NOT_APPROVED.value)}
                                        >
                                            Không duyệt
                                        </Button>

                                        <Button
                                            disabled={listChosen?.length === 0}
                                            startIcon={<CheckBoxIcon />}
                                            onClick={() => handleOpenConfirmUpdateStatusPopup(LocalConstants.AbsenceRequestApprovalStatus.NOT_APPROVED_YET.value)}
                                        >
                                            Chưa duyệt
                                        </Button>

                                        <Button
                                            disabled={listChosen?.length === 0}
                                            startIcon={<DeleteOutlineIcon />}
                                            onClick={handleDeleteList}
                                        >
                                            {t("general.button.delete")}
                                        </Button>
                                    </ButtonGroup>
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <div className="flex justify-between align-center">
                                        <Tooltip placement="top" title="Tìm kiếm theo mã, tên yêu cầu tuyển dụng">
                                            <GlobitsTextField
                                                placeholder="Tìm kiếm theo mã, tên yêu cầu tuyển dụng..."
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
                                                startIcon={<SearchIcon className={``} />}
                                                className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                type="submit"
                                            >
                                                Tìm kiếm
                                            </Button>
                                            {/* <Button
                                                startIcon={<FilterListIcon className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc
                                            </Button> */}
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

export default memo(observer(AbsenceRequestToolbar));