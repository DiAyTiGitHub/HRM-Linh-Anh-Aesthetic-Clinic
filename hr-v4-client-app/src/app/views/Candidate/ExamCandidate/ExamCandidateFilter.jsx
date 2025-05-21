import {Button, ButtonGroup, Collapse, Grid} from '@material-ui/core';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import {useStore} from 'app/stores';
import {pagingRecruitment} from 'app/views/Recruitment/Recruitment/RecruitmentService';
import {useFormikContext} from 'formik';
import {observer} from 'mobx-react';
import {memo, useEffect} from 'react';
import {useTranslation} from 'react-i18next';
import {pagingAllDepartments} from "../../Department/DepartmentService";
import {pagingAllOrg} from "../../Organization/OrganizationService";
import {pagingPositionTitle} from "../../PositionTitle/PositionTitleService";

function ExamCandidateFilter(props) {
    const {examCandidateStore} = useStore();
    const {
        searchObject,
        intactSearchObject
    } = examCandidateStore;

    const {
        isOpenFilter,
        handleFilter,
        handleCloseFilter
    } = props;

    const {t} = useTranslation();
    const {values, setFieldValue, setValues, handleReset, handleSubmit} = useFormikContext();

    useEffect(function () {
        if (values?.interviewDateFrom || values?.interviewDateTo) setFieldValue("interviewDate", null);
    }, [values?.interviewDateFrom, values?.interviewDateTo]);

    useEffect(function () {
        if (values?.interviewDate) {
            setFieldValue("interviewDateFrom", null);
            setFieldValue("interviewDateTo", null);
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
                                        <Grid item xs={12} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Đơn vị tuyển dụng")}
                                                name="organization"
                                                api={pagingAllOrg}
                                            />
                                        </Grid>

                                        <Grid item xs={12} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Phòng ban tuyển dụng")}
                                                name="department"
                                                api={pagingAllDepartments}
                                            />
                                        </Grid>
                                        <Grid item xs={12} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={"Chức danh cần tuyển"}
                                                validate
                                                name="positionTitle"
                                                api={pagingPositionTitle}
                                            />
                                        </Grid>
                                        <Grid item xs={12} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Đợt tuyển dụng")}
                                                name="recruitment"
                                                api={pagingRecruitment}
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

                                        <Grid item xs={12} md={6}>
                                            <GlobitsDateTimePicker
                                                label="Nộp hồ sơ từ ngày"
                                                name="submissionDateFrom"
                                            />
                                        </Grid>

                                        <Grid item xs={12} md={6}>
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

                                        <Grid item xs={12} md={6}>
                                            <GlobitsDateTimePicker
                                                label="Ngày phỏng vấn từ"
                                                name="interviewDateFrom"
                                                // placeholder="Ngày thi tuyển từ"
                                            />
                                        </Grid>

                                        <Grid item xs={12} md={6}>
                                            <GlobitsDateTimePicker
                                                label="Đến ngày"
                                                name="interviewDateTo"
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

export default memo(observer(ExamCandidateFilter));