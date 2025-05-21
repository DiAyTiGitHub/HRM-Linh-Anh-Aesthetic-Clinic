import {observer} from "mobx-react";
import React, {memo, useState} from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import {useHistory} from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import {Form, Formik} from "formik";
import {Button, ButtonGroup, Grid, Tooltip} from "@material-ui/core";
import {useStore} from "app/stores";
import {useTranslation} from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import RecruitmentFilter from "./RecruitmentFilter";

function RecruitmentIndexToolbar() {
    const history = useHistory();
    const { recruitmentStore } = useStore();
    const { t } = useTranslation();

    const {
        pagingRecruitment,
        searchObject,
        listOnDelete,
        handleSetSearchObject,
        handleDeleteList
    } = recruitmentStore;

    function handlePreSubmit(values) {
        const newValues = {
            ...values,
            departmentId: values?.department?.id,
            positionId: values?.position?.id,
            recruitmentRequestId: values?.recruitmentRequest?.id,
            recruitmentPlanId: values?.recruitmentPlan?.id,
            positionTitleId: values?.positionTitle?.id,
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
        await pagingRecruitment();
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
                                            onClick={() => history.push("/recruitment/new-recruitment")}
                                        >
                                            {t("general.button.add")}
                                        </Button>

                                        <Button
                                            disabled={listOnDelete?.length === 0}
                                            startIcon={<DeleteOutlineIcon />}
                                            onClick={handleDeleteList}
                                        >
                                            {t("general.button.delete")}
                                        </Button>
                                    </ButtonGroup>
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <div className="flex justify-between align-center">
                                        <Tooltip placement="top" title="Tìm kiếm theo mã, tên đợt tuyển dụng">
                                            <GlobitsTextField
                                                placeholder="Tìm kiếm theo mã, tên đợt tuyển dụng..."
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
                                            <Button
                                                startIcon={<FilterListIcon className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
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

export default memo(observer(RecruitmentIndexToolbar));