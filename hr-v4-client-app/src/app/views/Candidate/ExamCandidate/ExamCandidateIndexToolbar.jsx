import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import { useHistory } from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import NoteIcon from "@material-ui/icons/Note";
import ConstantList from "app/appConfig";
import { Form, Formik } from "formik";
import { Grid, Button, IconButton, Tabs, Tab, AppBar, makeStyles, ButtonGroup, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import DoneAllIcon from '@material-ui/icons/DoneAll';
import ThumbDownIcon from '@material-ui/icons/ThumbDown';
import CloseIcon from '@material-ui/icons/Close';
import FilterListIcon from '@material-ui/icons/FilterList';
import ExamCandidateFilter from "./ExamCandidateFilter";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";

function ExamCandidateIndexToolbar() {
    const history = useHistory();
    const { examCandidateStore } = useStore();
    const { t } = useTranslation();

    const {
        pagingExamCandidates,
        searchObject,
        listChosen,
        handleSetSearchObject,
        handleOpenRejectPopup,
        handleOpenFailPopup,
        handleOpenPassPopup,
        handleOpenResetPopup
    } = examCandidateStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingExamCandidates();
    }

    function handleRatePass() {
        handleOpenPassPopup();
    }

    function handleRateUnpass() {
        handleOpenFailPopup();
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
                                <Grid item xs={12} lg={4}>
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            disabled={listChosen?.length <= 0}
                                            startIcon={<DoneAllIcon />}
                                            onClick={handleRatePass}
                                        >
                                            Đạt
                                        </Button>

                                        <Button
                                            disabled={listChosen?.length <= 0}
                                            startIcon={<CloseIcon />}
                                            onClick={handleRateUnpass}
                                        >
                                            Không đạt
                                        </Button>

                                        <Button
                                            disabled={listChosen?.length <= 0}
                                            startIcon={<ThumbDownIcon />}
                                            onClick={handleDeclineCandidate}
                                        >
                                            Từ chối
                                        </Button>
                                    </ButtonGroup>
                                </Grid>

                                <Grid item xs={12} lg={8}>
                                    <Grid container spacing={1}>
                                        <Grid item xs={12} sm={3} lg={3}>
                                            <div className="flex justify-end align-center h-100 text-label-horizon">
                                                Ngày phỏng vấn/thi tuyển:
                                            </div>
                                        </Grid>

                                        <Grid item xs={12} sm={3} lg={3}>
                                            <Tooltip title="Ngày phỏng vấn/thi tuyển" placement="top">
                                                <GlobitsDateTimePicker
                                                    // label="Đến ngày"
                                                    name="interviewDate"
                                                    placeholder="Ngày phỏng vấn/thi tuyển"
                                                    disabled={values?.interviewDateFrom || values?.interviewDateTo}
                                                />
                                            </Tooltip>
                                        </Grid>

                                        <Grid item xs={12} sm={6} lg={6}>
                                            <div className="flex justify-between align-center">
                                                <Tooltip placement="top" title="Tìm kiếm theo mã, tên ứng viên">
                                                    <GlobitsTextField
                                                        placeholder="Tìm kiếm theo mã, tên ứng viên..."
                                                        name="keyword"
                                                        variant="outlined"
                                                        notDelay
                                                    // InputProps={{
                                                    //     endAdornment: (
                                                    //         <IconButton className="py-0 px-4" aria-label="search" type="submit">
                                                    //             <SearchIcon />
                                                    //         </IconButton>
                                                    //     ),
                                                    // }}
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
                                </Grid>
                            </Grid>

                            <ExamCandidateFilter
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

export default memo(observer(ExamCandidateIndexToolbar));