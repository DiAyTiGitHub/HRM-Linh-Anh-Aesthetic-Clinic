import {Button, ButtonGroup, Collapse, Grid} from '@material-ui/core';
import GlobitsPagingAutocomplete from 'app/common/form/GlobitsPagingAutocomplete';
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
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import {pagingRecruitmentRequest} from 'app/views/Recruitment/RecruitmentRequestV2/RecruitmentRequestV2Service';
import {pagingRecruitmentPlan} from 'app/views/Recruitment/RecruitmentPlanV2/RecruitmentPlanV2Service';
import {pagingRecruitment} from 'app/views/Recruitment/Recruitment/RecruitmentService';
import GlobitsPagingAutocompleteV2 from "../../../common/form/GlobitsPagingAutocompleteV2";
import {pagingAllOrg} from "../../Organization/OrganizationService";
import {pagingPositionTitle} from "../../PositionTitle/PositionTitleService";

function WaitingJobCandidateFilter(props) {
    const {waitingJobCandidateStore} = useStore();
    const {
        searchObject,
        intactSearchObject
    } = waitingJobCandidateStore;

    const {
        handleCloseFilter,
        isOpenFilter,
        handleFilter
    } = props;

    const {t} = useTranslation();
    const {values, setFieldValue, setValues, handleReset, handleSubmit} = useFormikContext();

    useEffect(function () {
        if (values?.onboardDateFrom || values?.onboardDateTo) setFieldValue("onboardDate", null);
    }, [values?.onboardDateFrom, values?.onboardDateTo]);

    useEffect(function () {
        if (values?.interviewDate) {
            setFieldValue("onboardDateFrom", null);
            setFieldValue("onboardDateTo", null);
        }
    }, [values?.interviewDate]);

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
                                                Thông tin tuyển dụng
                                            </p>
                                        </Grid>
                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Đơn vị tuyển dụng")}
                                                name="organization"
                                                api={pagingAllOrg}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocomplete
                                                label={t("Phòng ban tuyển dụng")}
                                                name="department"
                                                api={pagingAllDepartments}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            {/*<GlobitsPagingAutocomplete*/}
                                            {/*    label={t("Vị trí tuyển dụng")}*/}
                                            {/*    name="position"*/}
                                            {/*    api={pagingPosition}*/}
                                            {/*/>*/}
                                            <GlobitsPagingAutocompleteV2
                                                label={"Chức danh cần tuyển"}
                                                validate
                                                name="positionTitle"
                                                api={pagingPositionTitle}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocomplete
                                                label={t("Yêu cầu tuyển dụng")}
                                                name="recruitmentRequest"
                                                api={pagingRecruitmentRequest}
                                                searchObject={{
                                                    positionId: values?.position?.id,
                                                    departmentId: values?.department?.id
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocomplete
                                                label={t("Kế hoạch tuyển dụng")}
                                                name="recruitmentPlan"
                                                api={pagingRecruitmentPlan}
                                                searchObject={{
                                                    recruitmentRequestId: values?.recruitmentRequest?.id,
                                                    positionId: values?.position?.id,
                                                    departmentId: values?.department?.id
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocomplete
                                                label={t("Đợt tuyển dụng")}
                                                name="recruitment"
                                                api={pagingRecruitment}
                                                searchObject={{
                                                    recruitmentRequestId: values?.recruitmentRequest?.id,
                                                    recruitmentPlanId: values?.recruitmentPlan?.id,
                                                    positionId: values?.position?.id,
                                                    departmentId: values?.department?.id
                                                }}
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>

                                <Grid item xs={12} lg={6}>
                                    <Grid container spacing={2} className={"flex flex-end"}>
                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Ngày nộp hồ sơ trong khoảng
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={6}>
                                            <GlobitsDateTimePicker
                                                label="Nộp hồ sơ từ ngày"
                                                name="submissionDateFrom"
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={6}>
                                            <GlobitsDateTimePicker
                                                label="Đến ngày"
                                                name="submissionDateTo"
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>

                                <Grid item xs={12} lg={6}>
                                    <Grid container spacing={2} className={"flex flex-end"}>
                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Ngày phỏng vấn/thi tuyển trong khoảng
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={6}>
                                            <GlobitsDateTimePicker
                                                label="Ngày phỏng vấn từ"
                                                name="interviewDateFrom"
                                                // placeholder="Ngày thi tuyển từ"
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={6}>
                                            <GlobitsDateTimePicker
                                                label="Đến ngày"
                                                name="interviewDateTo"
                                                // placeholder="Đến ngày"
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>

                                <Grid item xs={12}>
                                    <Grid container spacing={2} className={"flex flex-end"}>
                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Ngày nhận việc trong khoảng
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsDateTimePicker
                                                label="Ngày nhận việc từ"
                                                name="onboardDateFrom"
                                                // placeholder="Ngày thi tuyển từ"
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsDateTimePicker
                                                label="Đến ngày"
                                                name="onboardDateTo"
                                                // placeholder="Đến ngày"
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

export default memo(observer(WaitingJobCandidateFilter));