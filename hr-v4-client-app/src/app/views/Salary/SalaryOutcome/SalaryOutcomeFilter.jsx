import { Button, ButtonGroup, Grid, Tooltip } from '@material-ui/core';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import GlobitsPagingAutocomplete from 'app/common/form/GlobitsPagingAutocomplete';
import { useStore } from 'app/stores';
import { pagingAllDepartments } from 'app/views/Department/DepartmentService';
import { pagingAllOrg } from 'app/views/Organization/OrganizationService';
import { pagingPosition } from 'app/views/Position/PositionService';
import { useFormikContext } from 'formik';
import { observer } from 'mobx-react';
import React, { memo, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { pagingSalaryPeriod } from '../SalaryPeriod/SalaryPeriodService';
import { pagingSalaryTemplates } from '../SalaryTemplate/SalaryTemplateService';
import MonetizationOnIcon from '@material-ui/icons/MonetizationOn';

import AssignmentIcon from '@material-ui/icons/Assignment';
import { pagingPositionTitle } from 'app/views/PositionTitle/PositionTitleService';
import { pagingLowerLevelStaff, pagingStaff } from 'app/views/HumanResourcesInformation/StaffService';
import { formatDate } from 'app/LocalFunction';

function SalaryOutcomeFilter(props) {
    const {
        salaryOutcomeStore,
        hrRoleUtilsStore
    } = useStore();

    const {
        isAdmin,
    } = hrRoleUtilsStore;

    const { t } = useTranslation();

    const {
        searchObject,
        intactSearchObject,
        listChosenPayslip,
        handleSetSearchObject,
        handleExportPopup,
        handlePdfPopup,
        handleCalculateSalaryStaffs
    } = salaryOutcomeStore;

    const {
        handleFilter
    } = props;

    const { values } = useFormikContext();

    function handleResetFilter() {
        const newSearchObject = {
            ...intactSearchObject,
            pageIndex: searchObject.pageIndex,
            pageSize: searchObject.pageSize,
        };
        handleSetSearchObject(newSearchObject);
    }

    function handleViewSalaryBoard() {
        handleSetSearchObject(values);
        handleCalculateSalaryStaffs();
    }

    return (
        // <Collapse in={isOpenFilter} className="filterPopup">
        <div className="flex flex-column">
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <div className="filterContent pt-8">
                        <Grid container spacing={2}>
                            <Grid item xs={12} className="pb-0">
                                <p className="m-0 p-0 borderThrough2">
                                    Lọc kết quả lương theo mẫu và kỳ lương
                                </p>
                            </Grid>

                            <Grid item xs={12}>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <Grid container spacing={2}>
                                            <Grid item xs={12} sm={6} md={4}>
                                                <GlobitsPagingAutocomplete
                                                    label={"Kỳ lương"}
                                                    name="salaryPeriod"
                                                    api={pagingSalaryPeriod}
                                                    getOptionLabel={(option) =>
                                                        option?.name && option?.code
                                                            ? `${option.name} - ${option.code} (${formatDate("DD/MM/YYYY", option.fromDate)} - ${formatDate("DD/MM/YYYY", option.toDate)})`
                                                            : `${option?.name || option?.code || ""}`
                                                    }
                                                />
                                            </Grid>

                                            <Grid item xs={12} sm={6} md={4}>
                                                <GlobitsPagingAutocomplete
                                                    label={"Mẫu bảng lương"}
                                                    name="salaryTemplate"
                                                    api={pagingSalaryTemplates}
                                                />
                                            </Grid>

                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>

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

                                    {/* <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsPagingAutocomplete
                                            label={"Vị trí"}
                                            name="position"
                                            api={pagingPosition}
                                            getOptionLabel={(option) => {
                                                return option?.name ? `${option?.name} - ${option?.code}` : '';
                                            }}
                                            disabled={!values?.positionTitle?.id}
                                        />
                                    </Grid> */}

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
                                        title="Xem danh sách lương đã tính theo bộ lọc"
                                    >
                                        <Button
                                            disabled={!values?.salaryPeriod?.id || !values?.salaryTemplate?.id}
                                            startIcon={<AssignmentIcon />}
                                            onClick={handleViewSalaryBoard}
                                            className="d-inline-flex py-2 px-8 btnHrStyle"
                                        >
                                            Xem DS đã tính
                                        </Button>
                                    </Tooltip>

                                    <Tooltip
                                        arrow
                                        placement="top"
                                        title="Tính lương cho nhân viên theo bộ lọc"
                                    >
                                        <Button
                                            disabled={!values?.salaryPeriod?.id || !values?.salaryTemplate?.id}
                                            startIcon={<MonetizationOnIcon />}
                                            type="submit"
                                            className="d-inline-flex py-2 px-8 btnHrStyle"
                                        >
                                            Tính lương
                                        </Button>
                                    </Tooltip>

                                </ButtonGroup>
                            </div>
                        </div>
                    </div>
                </Grid>
            </Grid>
        </div>

        // </Collapse >
    );
}

export default memo(observer(SalaryOutcomeFilter));


