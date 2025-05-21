import { Button, ButtonGroup, Collapse, Grid } from "@material-ui/core";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { observer } from "mobx-react";
import React, { memo } from "react";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";
import { useFormikContext } from "formik";
import { pagingPositionTitle } from "../PositionTitle/PositionTitleService";
import { pagingAllDepartments } from "../Department/DepartmentService";
import { pagingAllOrg } from "../Organization/OrganizationService";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsCheckBox from "../../common/form/GlobitsCheckBox";
import SearchIcon from "@material-ui/icons/Search";
import { pagingCertificates } from "../Certificate/CertificateService";

function StaffCertificateFilter(props) {

    const {
        staffCertificateStore,
        hrRoleUtilsStore

    } = useStore();

    const { t } = useTranslation();

    const {
        setFieldValue,
        values

    } = useFormikContext();

    const {
        searchObject,
        intactSearchObject
    } = staffCertificateStore;

    const {
        isOpenFilter,
        handleFilter,
        handleCloseFilter,
        resetForm
    } = props;

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
            pageIndex: 1,
            pageSize: 10,
            staffId: null,
            fromDate: null,
            toDate: null,
            searchKey: null,
        };
        resetForm();
        handleFilter(newSearchObject);
    }

    const {
        isAdmin,
        isManager,
    } = hrRoleUtilsStore;

    let canChangeFilter = false;
    if (isAdmin || isManager) {
        canChangeFilter = true;
    }
    return (
        <Collapse in={isOpenFilter} className='filterPopup'>
            <div className='flex flex-column'>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className='filterContent pt-8'>
                            <Grid
                                container
                                spacing={2}
                            >
                                <Grid item xs={12}>
                                    <p className="m-0 p-0 borderThrough2">Bộ lọc</p>
                                </Grid>

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
                                        readOnly={!canChangeFilter}
                                    />
                                </Grid>
                                
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
                                            return option?.code
                                                ? `${option?.name} - ${option?.code}`
                                                : option?.name;
                                        }}
                                        readOnly={!canChangeFilter}
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
                                        handleChange={(_, value) => {
                                            setFieldValue("positionTitle", value);
                                            setFieldValue("positionTitleId", value?.id);
                                        }}
                                        readOnly={!canChangeFilter}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4} lg={3}>
                                    <GlobitsPagingAutocompleteV2
                                        name='staff'
                                        label={t("Nhân viên")}
                                        api={pagingStaff}
                                        getOptionLabel={(option) =>
                                            option?.displayName && option?.staffCode
                                                ? `${option.displayName} - ${option.staffCode}`
                                                : option?.displayName || option?.staffCode || ""
                                        }
                                        readOnly={!canChangeFilter}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4} lg={3}>
                                    <GlobitsPagingAutocompleteV2
                                        name='certificate'
                                        label={t("Chứng chỉ")}
                                        api={pagingCertificates}
                                        getOptionLabel={(option) => `${option?.name}`}
                                    />
                                </Grid>

                            </Grid>

                            <div className='py-8 mt-12 border-bottom-fade border-top-fade'>
                                <div className='flex justify-end'>
                                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                                        <Button
                                            startIcon={<SearchIcon className={``} />}
                                            className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                            type="submit"
                                        >
                                            Tìm kiếm
                                        </Button>

                                        <Button
                                            onClick={handleResetFilter}
                                            startIcon={<RotateLeftIcon />}
                                        >
                                            Đặt lại
                                        </Button>

                                        <Button
                                            type='button'
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

export default memo(observer(StaffCertificateFilter));
