import { Button, ButtonGroup, Collapse, Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import FilterListIcon from '@material-ui/icons/FilterList';
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import { Form, Formik, useFormikContext } from "formik";
import { FilterOptionKanban } from "app/LocalConstants";
import CheckIcon from '@material-ui/icons/Check';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import GlobitsAutocomplete from "app/common/form/GlobitsAutocomplete";
import { useTranslation } from "react-i18next";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import Search from "@material-ui/icons/Search";
import AddIcon from "@material-ui/icons/Add";
import PostAddIcon from "@material-ui/icons/PostAdd";
import BackupIcon from '@material-ui/icons/Backup';
import FilterActivitySectionV2 from "./FilterActivitySectionV2";
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import ErrorIcon from '@material-ui/icons/Error';
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import AutorenewIcon from '@material-ui/icons/Autorenew';
import LocalConstants from "app/LocalConstants";

function TaskFilterV4(props) {
    const { t } = useTranslation();
    const [isOpenFilter, setIsOpenFilter] = useState(false);
    const { taskStore, staffStore } = useStore();
    const {
        getKanbanFilter,
        setKanbanFilter,
        getListByLimitTask,
        getOnViewProjects,
        getOnViewProjectIdList,
        needReloadOnJoinStaff,
        setNeedReloadOnJoinStaff,
        needReloadKanbanHeaderTotalTasks,
        reloadKanbanHeaderTotalTasks,
        canExportExcel,
        handleExportExcel,
        handleOpenTaskPopup,
        tabIndexTask,
        pagingTaskTable,
        handleChangeTablePageIndex,
        initialKanbanFitler,
        isFilterChanged
    } = taskStore;

    const [initialFilters, setInitialFilters] = useState(getKanbanFilter());

    function handleCloseFilter() {
        if (isOpenFilter) {
            setIsOpenFilter(false);
        }
    }

    function handleOpenFilter() {
        if (!isOpenFilter) {
            setInitialFilters(getKanbanFilter());
            setIsOpenFilter(true);
        }
    }

    function handleTogglePopupFilter() {
        if (isOpenFilter) handleCloseFilter();
        else handleOpenFilter();
    }

    async function handleFilter(values) {
        //store filter to local storage
        const kanbanFilter = { ...values, staffId: values?.staff?.id, modifierId: values?.modifier?.id };
        setKanbanFilter(kanbanFilter);
        setInitialFilters(kanbanFilter);

        const onViewProjects = getOnViewProjects();

        if (onViewProjects) {
            if (tabIndexTask == 0) {
                //viewing tab TABLE
                await handleChangeTablePageIndex(null, 1);
            }
            else if (tabIndexTask == 1) {
                //viewing tab KANBAN
                await getListByLimitTask();
            }
        }
        else {
            console.error("NO PROJECT IS CHOOSEN TO DISPLAY");
        }

        handleCloseFilter();
    }

    const {
        getOnWorkingStaffsOfProject,
        onWorkingStaffList,
        onModifierList
    } = staffStore;

    //initialize staff who're joining in selected projects
    useEffect(function () {
        if (needReloadOnJoinStaff) {
            getOnWorkingStaffsOfProject(getOnViewProjectIdList());
            setNeedReloadOnJoinStaff(false);
        }

    }, [needReloadOnJoinStaff]);

    //RELOAD NUMS OF TASKS IN KANBAN HEADER WHEN UPDATE KANBAN STATUS
    useEffect(function () {
        if (needReloadKanbanHeaderTotalTasks) {
            reloadKanbanHeaderTotalTasks();
        }
    }, [needReloadKanbanHeaderTotalTasks]);

    function handleOpenFormCreateNewTask() {
        handleOpenTaskPopup(null, null, 2);
    }

    //load kanban data/table data
    async function handleLoadViewingData() {
        if (tabIndexTask == 0) {
            //viewing tab TABLE
            await pagingTaskTable();

        }
        else if (tabIndexTask == 1) {
            //viewing tab KANBAN
            const onViewProjects = getOnViewProjects();

            if (onViewProjects) {
                await getListByLimitTask();
            }
            else {
                console.error("NO PROJECT IS CHOOSEN TO DISPLAY");
            }
        }
    }

    useEffect(function () {
        async function loadInitialData() {
            await handleLoadViewingData();
        }

        loadInitialData();
    }, [tabIndexTask]);


    //handle for popup export excel
    const [openConfirmExportExcel, setOpenConfirmExportExcel] = useState(false);

    return (
        <Formik
            enableReinitialize
            initialValues={initialFilters}
            onSubmit={handleFilter}
        >
            {({ resetForm, values, setFieldValue, setValues }) => {
                function handleResetFilter() {
                    const resetedFilter = {
                        ...initialKanbanFitler,
                        keyword: values?.keyword
                    };

                    setValues(resetedFilter);
                }

                const showAlertIcon = isFilterChanged();

                return (
                    <Form autoComplete="off">
                        <div className="taskFilterV4 mb-10 p-8">
                            <Grid container spacing={2} className="align-center mainBarFilter">
                                <Grid item xs={12} md={6}>
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            startIcon={<AddIcon />}
                                            onClick={handleOpenFormCreateNewTask}
                                            fullWidth
                                        >
                                            {"Thêm mới"}
                                        </Button>

                                        {canExportExcel() && (
                                            <Button
                                                startIcon={<PostAddIcon />}
                                                onClick={() => setOpenConfirmExportExcel(true)}
                                                fullWidth
                                            >
                                                {"Xuất Excel"}
                                            </Button>
                                        )}
                                    </ButtonGroup>
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <div className="flex justify-between align-center">
                                        <GlobitsTextField
                                            placeholder="Tìm kiếm theo mã, tên, từ khóa phần việc..."
                                            name="keyword"
                                            variant="outlined"
                                            timeOut={0}
                                            notDelay
                                            InputProps={{
                                                endAdornment: (
                                                    <IconButton className="py-0 px-4" aria-label="search" type="submit">
                                                        <Search />
                                                    </IconButton>
                                                ),
                                            }}
                                        />

                                        <ButtonGroup
                                            className="filterButtonV4"
                                            color="container"
                                            aria-label="outlined primary button group"
                                        >
                                            <Button
                                                startIcon={<AutorenewIcon
                                                    className={`mr-4`}
                                                />}
                                                className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                // onClick={handleLoadViewingData}
                                                type="submit"
                                            >
                                                Làm mới
                                            </Button>

                                            <Button
                                                startIcon={<FilterListIcon
                                                    className={`mr-4 filterRotateIcon ${isOpenFilter && 'onRotate'}`}
                                                />}
                                                // className="ml-8 d-inline-flex filterButtonV4  py-2 px-8 btn text-white"
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc

                                                <Tooltip title="Có trường lọc được thay đổi gây ảnh hưởng đến kết quả tìm kiếm" placement="top-end" >
                                                    <span className={`${!showAlertIcon ? 'display-none' : "flex"} changedFieldDot`}>
                                                        <ErrorIcon />
                                                    </span>
                                                </Tooltip>
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>


                            <Collapse in={isOpenFilter} className="filterPopup">
                                <div className="flex flex-column">
                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <div className="filterContent w-100 pt-12">
                                                <Grid container spacing={2}>
                                                    <FilterActivitySectionV2
                                                        isOpenFilter={isOpenFilter}
                                                    />

                                                    <Grid item xs={12} sm={6}>
                                                        <Grid container spacing={2}>
                                                            <Grid item xs={12} className="pb-0">
                                                                <p className="m-0 p-0 borderThrough2">
                                                                    Thời gian tạo
                                                                </p>
                                                            </Grid>

                                                            <Grid item xs={12} sm={6}>
                                                                <GlobitsDateTimePicker
                                                                    label="Từ ngày"
                                                                    disableFuture
                                                                    name="fromDate"
                                                                />
                                                            </Grid>

                                                            <Grid item xs={12} sm={6}>
                                                                <GlobitsDateTimePicker
                                                                    label="Đến ngày"
                                                                    disableFuture
                                                                    name="toDate"
                                                                />
                                                            </Grid>
                                                        </Grid>
                                                    </Grid>

                                                    <Grid item xs={12} sm={6}>
                                                        <Grid container spacing={2}>
                                                            <Grid item xs={12} className="pb-0">
                                                                <p className="m-0 p-0 borderThrough2">
                                                                    Thời gian cập nhật
                                                                </p>
                                                            </Grid>

                                                            <Grid item xs={12} sm={6}>
                                                                <GlobitsDateTimePicker
                                                                    label="Từ ngày"
                                                                    disableFuture
                                                                    name="fromDateUpdate"
                                                                />
                                                            </Grid>

                                                            <Grid item xs={12} sm={6}>
                                                                <GlobitsDateTimePicker
                                                                    label="Đến ngày"
                                                                    disableFuture
                                                                    name="toDateUpdate"
                                                                />
                                                            </Grid>
                                                        </Grid>
                                                    </Grid>


                                                    <Grid item xs={12} className="pb-0">
                                                        <p className="m-0 p-0 borderThrough2">
                                                            Thứ tự sắp xếp
                                                        </p>
                                                    </Grid>

                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                        <GlobitsSelectInput
                                                            // hideNullOption
                                                            label={"Độ ưu tiên"}
                                                            name="increasingPriorityOrder"
                                                            keyValue="value"
                                                            options={FilterOptionKanban}
                                                        />
                                                    </Grid>
                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                        <GlobitsSelectInput
                                                            // hideNullOption
                                                            label={"Mã công việc"}
                                                            name="increasingCodeOrder"
                                                            keyValue="value"
                                                            options={FilterOptionKanban}
                                                        />
                                                    </Grid>
                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                        <GlobitsSelectInput
                                                            // hideNullOption
                                                            label={"Thời gian cập nhật cuối"}
                                                            name="increasingLastModifyDate"
                                                            keyValue="value"
                                                            options={FilterOptionKanban}
                                                        />
                                                    </Grid>

                                                    <Grid item xs={12} className="pb-0">
                                                        <p className="m-0 p-0 borderThrough2">
                                                            Tiêu chí khác
                                                        </p>
                                                    </Grid>

                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                        <GlobitsAutocomplete
                                                            name="staff"
                                                            className="w-100"
                                                            label="Người phụ trách"
                                                            options={onWorkingStaffList}
                                                            displayData='displayName'
                                                        />
                                                    </Grid>

                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                        <GlobitsAutocomplete
                                                            name="modifier"
                                                            className="w-100"
                                                            label="Người tạo"
                                                            options={onModifierList}
                                                            displayData='displayName'
                                                        />
                                                    </Grid>

                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                        <GlobitsSelectInput
                                                            label={"Độ ưu tiên"}
                                                            name="priority"
                                                            keyValue="id"
                                                            options={LocalConstants.Priority}
                                                        />
                                                    </Grid>

                                                </Grid>
                                            </div>
                                        </Grid>
                                    </Grid>

                                    <div className="pt-8 mt-12" style={{ borderTop: "1px solid #b3b3b3" }}>
                                        <div className="flex justify-end" >
                                            <Button
                                                className="btn px-8 py-2 bg-light-gray d-inline-flex mr-12"
                                                type="button"
                                                onClick={handleResetFilter}
                                            >
                                                <RotateLeftIcon className="mr-6" />
                                                Đặt lại
                                            </Button>

                                            <Button
                                                className="btn px-8 py-2 btn-danger d-inline-flex"
                                                // fullWidth
                                                type="submit"
                                            >
                                                <BackupIcon className="mr-6" />
                                                Lưu bộ lọc và tìm kiếm
                                            </Button>
                                        </div>
                                    </div>
                                </div>

                            </Collapse>
                        </div>

                        {openConfirmExportExcel && (
                            <GlobitsConfirmationDialog
                                open={openConfirmExportExcel}
                                onConfirmDialogClose={() => setOpenConfirmExportExcel(false)}
                                onYesClick={handleExportExcel}
                                title={t("Xuất Excel?")}
                                text={t("Bạn có chắc muốn xuất danh sách công việc theo bộ lọc hiện tại? Hành động này có thể cần nhiều thời gian?")}
                                agree={t("confirm_dialog.delete.agree")}
                                cancel={t("confirm_dialog.delete.cancel")}
                            />
                        )}
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(TaskFilterV4));