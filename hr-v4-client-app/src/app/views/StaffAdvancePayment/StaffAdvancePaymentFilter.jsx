import { Button, ButtonGroup, Collapse, Grid } from '@material-ui/core';
import { observer } from 'mobx-react';
import React, { memo, useEffect } from 'react';
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker';
import { useFormikContext } from 'formik';
import { useTranslation } from 'react-i18next';
import { useStore } from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import { pagingSalaryPeriod } from '../Salary/SalaryPeriod/SalaryPeriodService';
import { pagingSalaryResult } from '../Salary/SalaryResult/SalaryResultService';
import { pagingStaff } from '../HumanResourcesInformation/StaffService';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { pagingPositionTitle } from '../PositionTitle/PositionTitleService';
import { pagingAllDepartments } from '../Department/DepartmentService';
import { pagingAllOrg } from '../Organization/OrganizationService';

function StaffSocialInsuranceFilter(props) {
    const {
        staffSocialInsuranceStore,
        hrRoleUtilsStore
    } = useStore();

    const {
        searchObject,
        intactSearchObject
    } = staffSocialInsuranceStore;

    const {
        handleCloseFilter,
        isOpenFilter,
        handleFilter
    } = props;

    const { t } = useTranslation();

    const {
        values,
        setFieldValue,
        setValues,
        handleReset,
        handleSubmit

    } = useFormikContext();

    // useEffect(function () {
    //     if (values?.onboardDateFrom || values?.onboardDateTo) setFieldValue("onboardDate", null);
    // }, [values?.onboardDateFrom, values?.onboardDateTo]);

    // useEffect(function () {
    //     if (values?.interviewDate) {
    //         setFieldValue("onboardDateFrom", null);
    //         setFieldValue("onboardDateTo", null);
    //     }
    // }, [values?.interviewDate]);

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
            pageIndex: searchObject.pageIndex,
            pageSize: searchObject.pageSize,
            salaryPeriod: null,
            salaryResult: null,
            staff: null,
        };
        handleFilter(newSearchObject);
    }

    const {
        isAdmin,
        isPositionManager,
        isCompensationBenifit,
        isManager
    } = hrRoleUtilsStore;

    let canChangeFilter = false;
    if (isAdmin || isPositionManager || isCompensationBenifit || isManager) {
        canChangeFilter = true;
    }

    // console.log("checking: isAdmin", isAdmin);
    // console.log("checking: isPositionManager", isPositionManager);
    // console.log("checking: isCompensationBenifit", isCompensationBenifit);
    // console.log("checking: isManager", isManager);

    return (
        <Collapse in={isOpenFilter} className="filterPopup">
            <div className="flex flex-column">
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className="filterContent pt-8">
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <p className="m-0 p-0 borderThrough2">Bộ lọc</p>
                                </Grid>

                                <Grid item xs={12}>
                                    <Grid container spacing={2} className="flex">
                                        <Grid item xs={12} sm={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name="organization"
                                                label="Đơn vị"
                                                api={pagingAllOrg}
                                                readOnly={!canChangeFilter}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={"Phòng ban"}
                                                name="department"
                                                api={pagingAllDepartments}
                                                searchObject={{
                                                    pageIndex: 1, pageSize: 9999, keyword: "",
                                                    organizationId: values?.organization?.id,
                                                }}
                                                getOptionLabel={(option) =>
                                                    [option?.name, option?.code].filter(Boolean).join(' - ') || ''
                                                }
                                                readOnly={!canChangeFilter}

                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name="positionTitle"
                                                label="Chức danh"
                                                api={pagingPositionTitle}
                                                searchObject={{
                                                    departmentId: values?.department?.id,
                                                }}
                                                readOnly={!canChangeFilter}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label="Nhân viên"
                                                name="staff"
                                                api={pagingStaff}
                                                readOnly={!canChangeFilter}
                                                getOptionLabel={(option) =>
                                                    [option?.displayName, option?.staffCode].filter(Boolean).join(' - ') || ''
                                                }
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name='salaryPeriod'
                                                label='Kỳ lương ứng viền'
                                                api={pagingSalaryPeriod}
                                            // handleChange={(_, value) => {
                                            //     setFieldValue("salaryPeriod", value);
                                            //     // setFieldValue("salaryPeriodId", value?.id);
                                            //     setFieldValue("fromDate", value?.fromDate);
                                            //     setFieldValue("toDate", value?.toDate);
                                            // }}
                                            />
                                        </Grid>

                                    </Grid>
                                </Grid>


                            </Grid>

                            <div className="py-8 mt-12 border-bottom-fade border-top-fade">
                                <div className="flex justify-end" >
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
                </Grid >
            </div >

        </Collapse >
    );
}

export default memo(observer(StaffSocialInsuranceFilter));