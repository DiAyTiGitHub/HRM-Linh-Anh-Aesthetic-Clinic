import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from "@material-ui/icons/Search";
import { Form, Formik } from "formik";
import { Grid, Button, ButtonGroup, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import FilterListIcon from "@material-ui/icons/FilterList";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { pagingPositionTitle } from "../PositionTitle/PositionTitleService";
import TreeAutocompleteSelector from "app/common/SelectComponent/SelectDepartmentTreeView/TreeAutocompleteSelector";
import { pagingDepartmentHierarchy } from "../Department/DepartmentService";

function HrResourcePlanReportIndexToolbar() {
    const { hrResourcePlanReportStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        tabCU,
        handleDeleteList,
        pagingHrResourcePlan,
        pagingAggregateHrResourcePlan,
        handleOpenCreateEdit,
        searchObject,
        listOnDelete,
        handleSetSearchObject,
        getDepartmentResourcePlan
    } = hrResourcePlanReportStore;
    const {
        isAdmin,
    } = hrRoleUtilsStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await getDepartmentResourcePlan();
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
        <Formik enableReinitialize initialValues={searchObject} onSubmit={handleFilter}>
            {({ resetForm, values, setFieldValue, setValues }) => {
                return (
                    <Form autoComplete='off'>
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={8}>
                                    <Grid container spacing={2}>
                                        <Grid item xs={6} sm={4} md={4} lg={4}>
                                            <GlobitsPagingAutocompleteV2
                                                name='organization'
                                                label='Đơn vị'
                                                api={pagingAllOrg}
                                            />
                                        </Grid>

                                        {/* <Grid item xs={6} sm={6} md={4} lg={3}>
                                        <GlobitsPagingAutocompleteV2
                                            name='positionTitle'
                                            label='Chức danh'
                                            api={pagingPositionTitle}
                                            searchObject={{
                                                departmentId: values?.department?.id,
                                                departmentIds: values?.departmentIds,
                                            }}
                                        />
                                    </Grid> */}
                                        <Grid item xs={6} sm={8} md={8} lg={8}>
                                            <TreeAutocompleteSelector
                                                label={"Phòng ban"}
                                                name='departmentIds'
                                                api={pagingDepartmentHierarchy}
                                                // api={pagingAllDepartments}
                                                hasChild={true}
                                                searchObject={{
                                                    pageIndex: 1,
                                                    pageSize: 9999,
                                                    keyword: "",
                                                    organizationId: values?.organization?.id,
                                                }}
                                                handleChange={(e, value) => {
                                                    let departmentIds = value?.map((item) => item.id);
                                                    setFieldValue("departmentIdList", departmentIds);
                                                }}
                                                placeholder={"Chọn phòng ban"}
                                                getOptionLabel={(option) => {
                                                    return option?.code ? `${option?.name} - ${option?.code}` : option?.name;
                                                }}
                                                multiple
                                            />
                                        </Grid>
                                    </Grid>

                                </Grid>

                                <Grid item xs={12} md={4}>
                                    <div className="pt-8 mt-12">
                                        <div className="flex justify-end">
                                            <ButtonGroup
                                                className='filterButtonV4'
                                                color='container'
                                                aria-label='outlined primary button group'>
                                                <Button
                                                    startIcon={<SearchIcon className={``} />}
                                                    className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                                                    type='submit'>
                                                    Tìm kiếm
                                                </Button>
                                                {/* <Button
                                                    startIcon={
                                                        <FilterListIcon
                                                            className={` filterRotateIcon ${isOpenFilter && "onRotate"
                                                                }`}
                                                        />
                                                    }
                                                    className=' d-inline-flex py-2 px-8 btnHrStyle'
                                                    onClick={handleTogglePopupFilter}>
                                                    Bộ lọc
                                                </Button> */}
                                            </ButtonGroup>
                                        </div>
                                    </div>
                                </Grid>

                            </Grid>

                            {/* <HrResourcePlanFilter
                                isOpenFilter={isOpenFilter}
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                            /> */}
                        </Grid>
                    </Form>
                );
            }}
        </Formik >
    );
}

export default memo(observer(HrResourcePlanReportIndexToolbar));
