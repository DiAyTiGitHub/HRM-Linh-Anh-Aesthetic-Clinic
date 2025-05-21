import { Button, Collapse, Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import FilterListIcon from '@material-ui/icons/FilterList';
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import { Form, Formik } from "formik";
import { FilterOptionKanban } from "app/LocalConstants";
import CheckIcon from '@material-ui/icons/Check';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import GlobitsAutocomplete from "app/common/form/GlobitsAutocomplete";
import { useTranslation } from "react-i18next";

function KanbanFilter() {
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
        reloadKanbanHeaderTotalTasks
    } = taskStore;

    function handleCloseFilter() {
        setIsOpenFilter(false);
    }

    function handleOpenFilter() {
        setIsOpenFilter(true);
    }

    function handleTogglePopupFilter() {
        if (isOpenFilter) handleCloseFilter();
        else handleOpenFilter();
    }

    const [initialFilters, setInitialFilters] = useState(getKanbanFilter());
    function handleFilter(values) {
        //store filter to local storage
        const kanbanFilter = { ...values, staffId: values?.staff?.id };
        setKanbanFilter(kanbanFilter);
        setInitialFilters(kanbanFilter);

        const onViewProjects = getOnViewProjects();

        if (onViewProjects) {
            getListByLimitTask();
        }
        else {
            console.error("NO PROJECT IS CHOOSEN TO DISPLAY");
        }
    }

    const {
        getOnWorkingStaffsOfProject,
        onWorkingStaffList
    } = staffStore; 

    //initialize staff who're joining in selected projects
    useEffect(function () {
        if (needReloadOnJoinStaff) {
            getOnWorkingStaffsOfProject(getOnViewProjectIdList());
            setNeedReloadOnJoinStaff(false);
        }

    }, [needReloadOnJoinStaff]);

    //load kanban data
    useEffect(function () {
        const onViewProjects = getOnViewProjects();

        if (onViewProjects) {
            getListByLimitTask();
        }
        else {
            console.error("NO PROJECT IS CHOOSEN TO DISPLAY");
        }
    }, []);

    //RELOAD NUMS OF TASKS IN KANBAN HEADER WHEN UPDATE KANBAN STATUS
    useEffect(function () {
        if (needReloadKanbanHeaderTotalTasks) {
            reloadKanbanHeaderTotalTasks();
        }
    }, [needReloadKanbanHeaderTotalTasks]);

    return (
        <div className="kanbanFilter">
            <Button className="filterButton py-6 px-8 mr-4" onClick={handleTogglePopupFilter}>
                <FilterListIcon className="mr-4" />
                Bộ lọc
            </Button>

            <Collapse in={isOpenFilter} className="filterPopup">
                <Formik
                    enableReinitialize
                    initialValues={initialFilters}
                    onSubmit={handleFilter}
                >
                    {({ resetForm, values, setFieldValue }) => {
                        // console.log("filter values: ", values);

                        return (
                            <Form autoComplete="off">
                                <Grid container spacing={2}>
                                    <Grid item xs={12} >
                                        <div className="p-8 pb-0">
                                            <Grid container spacing={2}>
                                                <Grid item xs={12}>
                                                    <p className="m-0 p-0 borderThrough">
                                                        Sắp xếp
                                                    </p>
                                                </Grid>

                                                <Grid item xs={12} sm={6} md={5} className="filterFieldWrapper">
                                                    <p className="filterField">
                                                        Độ ưu tiên:
                                                    </p>
                                                </Grid>
                                                <Grid item xs={12} sm={6} md={7} className="filterFieldWrapper">
                                                    <GlobitsSelectInput
                                                        hideNullOption
                                                        name="increasingPriorityOrder"
                                                        keyValue="value"
                                                        options={FilterOptionKanban}
                                                    />
                                                </Grid>


                                                <Grid item xs={12} sm={6} md={5} className="filterFieldWrapper">
                                                    <p className="filterField">
                                                        Mã việc:
                                                    </p>
                                                </Grid>
                                                <Grid item xs={12} sm={6} md={7} className="filterFieldWrapper">
                                                    <GlobitsSelectInput
                                                        hideNullOption
                                                        name="increasingCodeOrder"
                                                        keyValue="value"
                                                        options={FilterOptionKanban}
                                                    />
                                                </Grid>


                                                <Grid item xs={12} sm={6} md={5} className="filterFieldWrapper">
                                                    <p className="filterField">
                                                        Cập nhật cuối:
                                                    </p>
                                                </Grid>

                                                <Grid item xs={12} sm={6} md={7} className="filterFieldWrapper">
                                                    <GlobitsSelectInput
                                                        hideNullOption
                                                        name="increasingLastModifyDate"
                                                        keyValue="value"
                                                        options={FilterOptionKanban}
                                                    />
                                                </Grid>

                                                <Grid item xs={12}>
                                                    <p className="m-0 p-0 borderThrough">
                                                        Chi tiết
                                                    </p>
                                                </Grid>

                                                <Grid item xs={12} sm={6} md={5} className="filterFieldWrapper">
                                                    <p className="filterField">
                                                        Phụ trách:
                                                    </p>
                                                </Grid>

                                                <Grid item xs={12} sm={6} md={7} className="filterFieldWrapper">
                                                    {/* <GlobitsSelectInput
                                                        name="staffId"
                                                        keyValue="id"
                                                        displayvalue={"displayName"}
                                                        options={onWorkingStaffList}
                                                    /> */}

                                                    <GlobitsAutocomplete
                                                        name="staff"
                                                        className="w-100"
                                                        options={onWorkingStaffList}
                                                        displayData='displayName'
                                                    />
                                                </Grid>

                                                <Grid item xs={12} sm={6} md={5} className="filterFieldWrapper">
                                                    <p className="filterField">
                                                        Từ khóa:
                                                    </p>
                                                </Grid>
                                                <Grid item xs={12} sm={6} md={7} className="filterFieldWrapper">
                                                    <GlobitsTextField
                                                        name="keyword"
                                                    />
                                                </Grid>
                                            </Grid>
                                        </div>
                                    </Grid>

                                    <Grid item xs={12} className="pt-0">
                                        <div className="px-8 pb-8 pt-0 flex justify-center">
                                            <Button
                                                className="filterButton px-8 py-4 btn-success"
                                                onClick={handleCloseFilter}
                                                fullWidth
                                                type="submit"
                                            >
                                                <CheckIcon className="mr-4" />
                                                Lưu bộ lọc
                                            </Button>
                                        </div>
                                    </Grid>
                                </Grid>
                            </Form>
                        );
                    }}
                </Formik>
            </Collapse>
        </div >
    );
}

export default memo(observer(KanbanFilter));