import { Button, ButtonGroup, Collapse, Grid } from '@material-ui/core';
import { observer } from 'mobx-react';
import React, { memo, useState } from 'react';
import { useFormikContext } from 'formik';
import { useTranslation } from 'react-i18next';
import { useStore } from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import { pagingHasPermissionDepartments, pagingStaff } from '../HumanResourcesInformation/StaffService';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { pagingAllOrg } from '../Organization/OrganizationService';
import { pagingPositionTitle } from "app/views/PositionTitle/PositionTitleService";

function StaffHasSocialInsuranceFilter(props) {
    const { staffHasSocialInsuranceStore } = useStore();
    const {
        searchObject,
        intactSearchObject,
        isAdmin,
    } = staffHasSocialInsuranceStore;

    const {
        handleCloseFilter,
        isOpenFilter,
        handleFilter
    } = props;

    const { t } = useTranslation();
    const { values, setFieldValue, setValues, handleReset, handleSubmit } = useFormikContext();

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
            pageIndex: searchObject.pageIndex,
            pageSize: searchObject.pageSize
        };
        handleFilter(newSearchObject);
    }

    return (
        <Collapse in={isOpenFilter} className="filterPopup">
            <div className="flex flex-column">
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className="filterContent pt-8">
                            <Grid container spacing={2}>
                                <Grid item xs={12} className="pb-0">
                                    {/* <p className="m-0 p-0 borderThrough2">
                                        Thông tin ...
                                    </p> */}
                                </Grid>

                                <Grid item xs={12}>
                                    <Grid container spacing={2} className='justify-end'>

                                        {isAdmin &&
                                            <Grid item xs={12} sm={6} md={4} lg={3}>
                                                <GlobitsPagingAutocompleteV2
                                                    label={t("Nhân viên")}
                                                    name="staff"
                                                    api={pagingStaff}
                                                    getOptionLabel={(option) => {
                                                        return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                                                    }}
                                                />
                                            </Grid>
                                        }

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name="organization"
                                                label="Đơn vị"
                                                api={pagingAllOrg}
                                                handleChange={(_, value) => {
                                                    setFieldValue("organization", value);
                                                    setFieldValue("organizationId", value?.id);
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={"Phòng ban"}
                                                name="department"
                                                api={pagingHasPermissionDepartments}
                                                searchObject={{
                                                    pageIndex: 1, pageSize: 9999, keyword: "",
                                                    organizationId: values?.organization?.id,
                                                }}
                                                getOptionLabel={(option) =>
                                                    [option?.name, option?.code].filter(Boolean).join(' - ') || ''
                                                }
                                                handleChange={(_, value) => {
                                                    setFieldValue("department", value);
                                                    setFieldValue("departmentId", value?.id);
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name="positionTitle"
                                                label="Chức danh"
                                                api={pagingPositionTitle}
                                                searchObject={{
                                                    departmentId: values?.department?.id,
                                                }}
                                                handleChange={(_, value) => {
                                                    setFieldValue("positionTitle", value);
                                                    setFieldValue("positionTitleId", value?.id);
                                                }}
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>

                                {/* <Grid item xs={12} lg={6}>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Khoảng thời gian đóng BH
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={6}>
                                            <GlobitsDateTimePicker
                                                label="Ngày đóng từ"
                                                name="onboardDateFrom"
                                            // placeholder="Ngày từ"
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={6}>
                                            <GlobitsDateTimePicker
                                                label="Đến ngày"
                                                name="onboardDateTo"
                                            // placeholder="Đến ngày"
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>*/}
                            </Grid>

                            <div className="py-8 mt-12 border-bottom-fade border-top-fade">
                                <div className="flex  justify-end" >
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            onClick={handleResetFilter}
                                            startIcon={<RotateLeftIcon />}
                                        >
                                            Đặt lại
                                        </Button>
                                        <Button
                                            type="button"
                                            onClick={handleCloseFilter}
                                            startIcon={<HighlightOffIcon />}
                                        >
                                            Đóng bộ lọc
                                        </Button>
                                    </ButtonGroup>
                                </div>
                            </div>
                        </div>
                    </Grid>
                </Grid>
            </div>
        </Collapse>
    );
}

export default memo(observer(StaffHasSocialInsuranceFilter));