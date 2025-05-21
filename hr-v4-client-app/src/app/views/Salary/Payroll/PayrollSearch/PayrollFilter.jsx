import { Button, ButtonGroup, Grid, Tooltip } from '@material-ui/core';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import GlobitsPagingAutocomplete from 'app/common/form/GlobitsPagingAutocomplete';
import { useStore } from 'app/stores';
import { pagingAllDepartments } from 'app/views/Department/DepartmentService';
import { pagingAllOrg } from 'app/views/Organization/OrganizationService';
import { useFormikContext } from 'formik';
import { observer } from 'mobx-react';
import React, { memo, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { pagingSalaryPeriod } from 'app/views/Salary/SalaryPeriod/SalaryPeriodService';
import { pagingSalaryTemplates } from 'app/views/Salary/SalaryTemplate/SalaryTemplateService';
import MonetizationOnIcon from '@material-ui/icons/MonetizationOn';
import PageviewIcon from '@material-ui/icons/Pageview';
import AssignmentIcon from '@material-ui/icons/Assignment';
import { pagingPositionTitle } from 'app/views/PositionTitle/PositionTitleService';
import { pagingLowerLevelStaff, pagingStaff } from 'app/views/HumanResourcesInformation/StaffService';
import { formatDate } from 'app/LocalFunction';

function PayrollFilter(props) {
    const {
        hrRoleUtilsStore,
        payrollStore
    } = useStore();

    const {
        isAdmin,

    } = hrRoleUtilsStore;

    const {
        handleSetSearchObj,
        intactSearchObject,
        searchObject,
        pagingSalaryResultStaff,
        getListSumSalaryResultStaff
    } = payrollStore;


    const { values } = useFormikContext();

    async function handleResetFilter() {
        try {
            const newSearchObject = {
                ...JSON.parse(JSON.stringify(intactSearchObject)),
                salaryResultId: searchObject?.salaryResultId || null,
            };
            handleSetSearchObj(newSearchObject);
            await pagingSalaryResultStaff();
            await getListSumSalaryResultStaff(values);
        }
        catch (error) {
            console.error(error);
        }
    }

    return (
        <div className="flex flex-column">
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <div className="filterContent pt-8">
                        <Grid container spacing={2}>

                            <Grid item xs={12} className="pb-0">
                                <p className="m-0 p-0 borderThrough2">
                                    Đối tượng lọc
                                </p>
                            </Grid>

                            <Grid item xs={12}>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsPagingAutocomplete
                                            label={"Đơn vị"}
                                            name="organization"
                                            api={pagingAllOrg}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsPagingAutocomplete
                                            label={"Phòng ban"}
                                            name="department"
                                            api={pagingAllDepartments}
                                            searchObject={{
                                                organizationId: values?.organization?.id,
                                                // staffId: values?.staff?.id
                                            }}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsPagingAutocomplete
                                            label={"Chức danh"}
                                            name="positionTitle"
                                            api={pagingPositionTitle}
                                            searchObject={{
                                                organizationId: values?.organization?.id,
                                                departmentId: values?.department?.id
                                                // staffId: values?.staff?.id
                                            }}
                                            disabled={!values?.department?.id}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsPagingAutocomplete
                                            label={"Nhân viên"}
                                            name="staff"
                                            api={isAdmin ? pagingStaff : pagingLowerLevelStaff}
                                            displayData="displayName"
                                            searchObject={{
                                                organizationId: values?.organization?.id,
                                                departmentId: values?.department?.id,
                                                positionTitleId: values?.positionTitle?.id
                                            }}
                                            getOptionLabel={(option) => {
                                                return option?.displayName ? `${option?.displayName} - ${option?.staffCode}` : '';
                                            }}
                                        />
                                    </Grid>
                                </Grid>
                            </Grid>

                        </Grid>

                        <div className="pt-8 mt-12 border-top-fade">
                            <div className="flex justify-end">


                                <ButtonGroup
                                    color="container"
                                    aria-label="outlined primary button group"
                                >

                                    <Tooltip
                                        arrow
                                        placement="top"
                                        title="Đặt lại bộ lọc tính lương"
                                    >
                                        <Button
                                            onClick={handleResetFilter}
                                            startIcon={<RotateLeftIcon />}
                                        >
                                            Đặt lại
                                        </Button>
                                    </Tooltip>

                                    <Tooltip
                                        arrow
                                        placement="top"
                                        title="Tìm kiếm bảng lương theo bộ lọc"
                                    >
                                        <Button
                                            // onClick={() => handleSetSearchObj(values)}
                                            type='submit'
                                            startIcon={<PageviewIcon />}
                                        >
                                            Tìm kiếm
                                        </Button>
                                    </Tooltip>
                                </ButtonGroup>
                            </div>
                        </div>
                    </div>
                </Grid>
            </Grid>
        </div>

    );
}

export default memo(observer(PayrollFilter));


