import { Button, ButtonGroup, Collapse, Grid } from '@material-ui/core';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { observer } from 'mobx-react';
import React, { memo } from 'react';
import { useTranslation } from 'react-i18next';
import { useStore } from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import { pagingStaff } from '../HumanResourcesInformation/StaffService';
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker';
import { pagingPositionTitle } from '../PositionTitle/PositionTitleService';
import { pagingAllDepartments } from '../Department/DepartmentService';
import { pagingAllOrg } from '../Organization/OrganizationService';
import { useFormikContext } from 'formik';
import { pagingSalaryTemplates } from '../Salary/SalaryTemplate/SalaryTemplateService';

function StaffSalaryTemplateFilter(props) {
    const {
        staffSalaryTemplateStore,
        hrRoleUtilsStore
    } = useStore();
    const { t } = useTranslation();

    const {
        searchObject,
        intactSearchObject
    } = staffSalaryTemplateStore;

    const {
        isAdmin,
        isManager
    } = hrRoleUtilsStore;

    const {
        isOpenFilter,
        handleFilter,
        handleCloseFilter
    } = props;

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
            pageIndex: searchObject.pageIndex,
            pageSize: searchObject.pageSize,
        };
        handleFilter(newSearchObject);
    }

    const {
        setFieldValue,
        values
    } = useFormikContext();

    return (
        <Collapse in={isOpenFilter} className="filterPopup">
            <div className="flex flex-column">
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className="filterContent pt-8">
                            <Grid container spacing={2}>

                                <Grid item xs={12}>
                                    <p className='m-0 p-0 borderThrough2'>
                                        Đối tượng lọc
                                    </p>
                                </Grid>


                                {(isAdmin || isManager) && (
                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsPagingAutocompleteV2
                                            name='organization'
                                            label='Đơn vị'
                                            api={pagingAllOrg}
                                            handleChange={(_, value) => {
                                                setFieldValue("organization", value);
                                                setFieldValue("organizationId", value?.id);
                                                setFieldValue("department", null);
                                                setFieldValue("positionTitle", null);
                                                setFieldValue("position", null);

                                            }}
                                        />
                                    </Grid>
                                )}

                                {(isAdmin || isManager) && (
                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsPagingAutocompleteV2
                                            label={"Phòng ban"}
                                            name='department'
                                            api={pagingAllDepartments}
                                            searchObject={{
                                                pageIndex: 1,
                                                pageSize: 9999,
                                                keyword: "",
                                                organizationId: values?.organization?.id,
                                            }}
                                            handleChange={(_, value) => {
                                                setFieldValue("department", value);
                                                setFieldValue("departmentId", value?.id);
                                                setFieldValue("positionTitle", null);
                                                setFieldValue("position", null);

                                            }}
                                            getOptionLabel={(option) => {
                                                return option?.code ? `${option?.name} - ${option?.code}` : option?.name;
                                            }}
                                        />
                                    </Grid>
                                )}

                                {(isAdmin || isManager) && (
                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                        <GlobitsPagingAutocompleteV2
                                            name='positionTitle'
                                            label='Chức danh'
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
                                )}

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

                                <Grid item xs={12}>
                                    <p className='m-0 p-0 borderThrough2'>
                                        Tiêu chí khác
                                    </p>
                                </Grid>

                                <Grid item xs={12} sm={6} md={4} lg={3}>
                                    <GlobitsPagingAutocompleteV2
                                        label={t("Mẫu bảng lương")}
                                        name="salaryTemplate"
                                        api={pagingSalaryTemplates}
                                        // getOptionLabel={(option) => {
                                        //     return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                                        // }}
                                    />
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
                </Grid>
            </div>

        </Collapse>
    );
}

export default memo(observer(StaffSalaryTemplateFilter)); 