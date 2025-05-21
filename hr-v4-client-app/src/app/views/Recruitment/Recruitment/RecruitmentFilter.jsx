import {Button, ButtonGroup, Collapse, Grid} from '@material-ui/core';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import {observer} from 'mobx-react';
import React, {memo, useEffect} from 'react';
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker';
import {useFormikContext} from 'formik';
import {useTranslation} from 'react-i18next';
import {useStore} from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import SearchIcon from '@material-ui/icons/Search';
import {pagingPosition} from 'app/views/Position/PositionService';
import {pagingAllDepartments} from 'app/views/Department/DepartmentService';
import {pagingRecruitmentRequest} from '../RecruitmentRequestV2/RecruitmentRequestV2Service';
import {pagingRecruitmentPlan} from '../RecruitmentPlanV2/RecruitmentPlanV2Service';
import {pagingAllOrg} from "../../Organization/OrganizationService";
import SelectDepartmentComponent from 'app/common/SelectComponent/SelectDepartment/SelectDepartmentComponent';
import {pagingPositionTitle} from "../../PositionTitle/PositionTitleService";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";

function RecruitmentFilter(props) {
    const {recruitmentStore} = useStore();
    const {
        searchObject,
        intactSearchObject
    } = recruitmentStore;

    const {
        isOpenFilter, handleFilter, handleCloseFilter
    } = props;

    const {t} = useTranslation();
    const {values, setFieldValue, setValues, handleReset, handleSubmit} = useFormikContext();

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
            pageIndex: searchObject.pageIndex,
            pageSize: searchObject.pageSize,
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
                                <Grid item xs={12}>
                                    <Grid container spacing={2} className={"flex flex-end"}>
                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Thông tin đợt tuyển dụng
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name="organization"
                                                label="Đơn vị"
                                                api={pagingAllOrg}
                                                searchObject={{
                                                    pageIndex: 1, pageSize: 9999, keyword: ""
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name="department"
                                                label="Phòng ban"
                                                api={pagingAllDepartments}
                                                searchObject={{
                                                    pageIndex: 1, pageSize: 9999, keyword: ""
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={"Chức danh cần tuyển"}
                                                validate
                                                name="positionTitle"
                                                api={pagingPositionTitle}
                                            />
                                        </Grid>


                                        {/* <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name="organization"
                                                label="Đơn vị"
                                                api={pagingAllOrg}
                                                handleChange={(_, value) => {
                                                    setFieldValue("organization", value);
                                                    setFieldValue("department", null);
                                                    setFieldValue("position", null);
                                                }}
                                            />
                                        </Grid>
                    
                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <SelectDepartmentComponent
                                                organizationId={values?.organization?.id} 
                                                name={"department"}
                                                clearFields={["position"]}
                                            />
                                        </Grid>
                    
                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={"Vị trí"}
                                                validate
                                                name="position"
                                                api={pagingPosition}
                                                //disabled={!values?.department?.id}
                                                searchObject={{
                                                    pageIndex: 1, pageSize: 9999, keyword: "",
                                                    departmentId: values?.department?.id
                                                }}
                                            />
                                        </Grid> */}

                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Yêu cầu tuyển dụng")}
                                                name="recruitmentRequest"
                                                api={pagingRecruitmentRequest}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Kế hoạch tuyển dụng")}
                                                name="recruitmentPlan"
                                                api={pagingRecruitmentPlan}
                                                searchObject={{
                                                    recruitmentRequestId: values?.recruitmentRequest?.id
                                                }}
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>

                                <Grid item xs={12}>
                                    <Grid container spacing={2} className={"flex flex-end"}>
                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Thời gian tuyển dụng
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsDateTimePicker
                                                label="Từ ngày"
                                                name="fromDate"
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsDateTimePicker
                                                label="Đến ngày"
                                                name="toDate"
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>

                            <div className="py-8 mt-12 border-bottom-fade border-top-fade">
                                <div className="flex justify-end">
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            onClick={handleResetFilter}
                                            startIcon={<RotateLeftIcon/>}
                                        >
                                            Đặt lại
                                        </Button>
                                        <Button
                                            type="button"
                                            onClick={handleCloseFilter}
                                            startIcon={<HighlightOffIcon/>}
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

export default memo(observer(RecruitmentFilter));