import { Button, ButtonGroup, Collapse, Grid } from "@material-ui/core";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { useStore } from "app/stores";
import { pagingHasPermissionDepartments, pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import { pagingAllOrg } from "app/views/Organization/OrganizationService";
import { pagingPositionTitle } from "app/views/PositionTitle/PositionTitleService";
import { getCurrentStaff } from "app/views/profile/ProfileService";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { pagingSalaryPeriod } from "../SalaryPeriod/SalaryPeriodService";
import { pagingSalaryResult } from "../SalaryResult/SalaryResultService";
import { pagingSalaryTemplates } from "../SalaryTemplate/SalaryTemplateService";
import { pagingAllDepartments } from "app/views/Department/DepartmentService";

function SalaryStaffPayslipFilter(props) {
    const {
        salaryStaffPayslipStore,
        hrRoleUtilsStore
    } = useStore();

    const {
        searchObject,
        intactSearchObject,
        pagingSalaryStaffPayslip
    } = salaryStaffPayslipStore;

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

    const {
        hasShiftAssignmentPermission,
        isAdmin,
        isManager,
        isPositionManager,
        isCompensationBenifit,
        checkHasShiftAssignmentPermission
    } = hrRoleUtilsStore;

    let canChangeFilter = false;
    if (isAdmin || isPositionManager || isCompensationBenifit || isManager) {
        canChangeFilter = true;
    }

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
            pageIndex: searchObject.pageIndex,
            pageSize: searchObject.pageSize,
            salaryPeriod: null,
            salaryResult: null,
        };
        handleFilter(newSearchObject);
    }
    return (
        <Collapse in={isOpenFilter} className='filterPopup'>
            <div className='flex flex-column'>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className='filterContent pt-8'>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <p className='m-0 p-0 borderThrough2'>Đối tượng</p>
                                </Grid>

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

                                <Grid item xs={12} className='pb-0'>
                                    <p className='m-0 p-0 borderThrough2'>
                                        {/* Thông tin bảo hiểm */}
                                        Thông tin tính lương
                                    </p>
                                </Grid>

                                <Grid item xs={12}>
                                    <Grid
                                        container
                                        spacing={2}
                                    // className='justify-end'
                                    >
                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Kỳ lương")}
                                                name='salaryPeriod'
                                                api={pagingSalaryPeriod}
                                                getOptionLabel={(option) =>
                                                    [option?.name, option?.code].filter(Boolean).join(" - ") || ""
                                                }
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Mẫu bảng lương (có tạo phiếu lương)")}
                                                name='salaryTemplate'
                                                api={pagingSalaryTemplates}
                                                searchObject={{
                                                    isCreatePayslip: true
                                                }}
                                                getOptionLabel={(option) =>
                                                    [option?.name, option?.code].filter(Boolean).join(" - ") || ""
                                                }
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Được tổng hợp trong bảng lương")}
                                                name='salaryResult'
                                                api={pagingSalaryResult}
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>

                            <div className='py-8 mt-12 border-bottom-fade border-top-fade'>
                                <div className='flex justify-end'>
                                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                                        <Button onClick={handleResetFilter} startIcon={<RotateLeftIcon />}>
                                            Đặt lại
                                        </Button>
                                        <Button
                                            type='button'
                                            onClick={handleCloseFilter}
                                            startIcon={<HighlightOffIcon />}>
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

export default memo(observer(SalaryStaffPayslipFilter));
