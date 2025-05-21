import { Button, ButtonGroup, Collapse, Grid } from "@material-ui/core";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { useStore } from "app/stores";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import { pagingHasPermissionDepartments, pagingStaff } from "../HumanResourcesInformation/StaffService";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { pagingPositionTitle } from "../PositionTitle/PositionTitleService";
import { getCurrentStaff } from "../profile/ProfileService";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";

function ShiftRegistrationFilter(props) {
    const { ShiftRegistrationStore, hrRoleUtilsStore } = useStore();
    const { searchObject, intactSearchObject, pagingShiftRegistration } = ShiftRegistrationStore;

    const { handleCloseFilter, isOpenFilter, handleFilter } = props;

    const { t } = useTranslation();
    const { values, setFieldValue, setValues, handleReset, handleSubmit } = useFormikContext();

    const { hasShiftAssignmentPermission, checkHasShiftAssignmentPermission } = hrRoleUtilsStore;

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
            pageIndex: searchObject.pageIndex,
            pageSize: searchObject.pageSize,
        };
        handleFilter(newSearchObject);
    }

    const setUpFilterDate = async () => {
        const { data } = await getCurrentStaff();
        const staff = data;

        const check = await checkHasShiftAssignmentPermission();
        if (check) {
            if (staff?.department) {
                setFieldValue("department", staff?.department);
                searchObject.department = staff?.department;
                intactSearchObject.department = staff?.department;
            }
            if (staff?.organization) {
                setFieldValue("organization", staff?.organization);
                searchObject.organization = staff?.organization;
                intactSearchObject.organization = staff?.organization;
            }
            setFieldValue("registerStaff", null);
            // searchObject.staff = null;
            intactSearchObject.registerStaff = null;
        } else {
            setFieldValue("registerStaff", staff);
            intactSearchObject.registerStaff = staff;
            searchObject.registerStaff = staff;
            // intactSearchObject.staffId = staff.id;
        }
        await pagingShiftRegistration();
    };

    useEffect(() => {
        setUpFilterDate();
    }, []);
    return (
        <Collapse in={isOpenFilter} className='filterPopup'>
            <div className='flex flex-column'>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className='filterContent pt-8'>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <Grid container spacing={2} className={"flex flex-end"}>
                                        <Grid item xs={12}>
                                            <p className='m-0 p-0 borderThrough2'>Đối tượng đăng ký ca làm việc</p>
                                        </Grid>
                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name='organization'
                                                label='Đơn vị'
                                                api={pagingAllOrg}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={"Phòng ban"}
                                                name='department'
                                                api={pagingHasPermissionDepartments}
                                                searchObject={{
                                                    pageIndex: 1,
                                                    pageSize: 9999,
                                                    keyword: "",
                                                    organizationId: values?.organization?.id,
                                                }}
                                                getOptionLabel={(option) =>
                                                    [option?.name, option?.code].filter(Boolean).join(" - ") || ""
                                                }
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name='positionTitle'
                                                label='Chức danh'
                                                api={pagingPositionTitle}
                                                searchObject={{
                                                    departmentId: values?.department?.id,
                                                }}
                                            />
                                        </Grid>
                                        <Grid item xs={12} className='pb-0'>
                                            <p className='m-0 p-0 borderThrough2'>Thông tin ca làm việc</p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocomplete
                                                label={t("Nhân viên đăng ký")}
                                                name='registerStaff'
                                                readOnly={!hasShiftAssignmentPermission}
                                                api={pagingStaff}
                                                getOptionLabel={(option) => {
                                                    return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                                                }}
                                                onChange={(_, value) => {
                                                    setFieldValue("registerStaff", value);
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocomplete
                                                name='shiftWork'
                                                label={t("Ca làm việc")}
                                                api={pagingShiftWork}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocomplete
                                                label={t("Nhân viên phê duyệt")}
                                                name='approvalStaff'
                                                api={pagingStaff}
                                                getOptionLabel={(option) => {
                                                    return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                                                }}
                                                onChange={(_, value) => {
                                                    setFieldValue("approvalStaff", value);
                                                }}
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>

                                <Grid item xs={12}>
                                    <Grid container spacing={2} className={"flex flex-end"}>
                                        <Grid item xs={12} className='pb-0'>
                                            <p className='m-0 p-0 borderThrough2'>Khoảng thời gian làm việc</p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsDateTimePicker label='Từ ngày' name='fromDate' />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsDateTimePicker label='Đến ngày' name='toDate' />
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

export default memo(observer(ShiftRegistrationFilter));
