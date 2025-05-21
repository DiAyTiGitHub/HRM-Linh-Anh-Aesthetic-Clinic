import {Button, ButtonGroup, Collapse, Grid} from '@material-ui/core';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import {observer} from 'mobx-react';
import React, {memo} from 'react';
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker';
import {useFormikContext} from 'formik';
import {useTranslation} from 'react-i18next';
import {useStore} from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import {pagingPosition} from 'app/views/Position/PositionService';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import GlobitsPagingAutocomplete from 'app/common/form/GlobitsPagingAutocomplete';
import {pagingRecruitmentRequest} from '../RecruitmentRequestV2/RecruitmentRequestV2Service';
import {pagingAllOrg} from "app/views/Organization/OrganizationService";
import {pagingAllDepartments} from 'app/views/Department/DepartmentService';
import {pagingPositionTitle} from "../../PositionTitle/PositionTitleService";

function RecruitmentPlanV2Filter(props) {
    const {recruitmentPlanStore} = useStore();
    const {
        searchObject,
        intactSearchObject
    } = recruitmentPlanStore;

    const {
        handleCloseFilter,
        isOpenFilter,
        handleFilter
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
                                                Thông tin kế hoạch tuyển dụng
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name="organization"
                                                label="Đơn vị"
                                                api={pagingAllOrg}
                                                searchObject={{
                                                    pageIndex: 1, pageSize: 9999, keyword: ""
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name="department"
                                                label="Phòng ban"
                                                api={pagingAllDepartments}
                                                searchObject={{
                                                    pageIndex: 1, pageSize: 9999, keyword: ""
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
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
                                    </Grid>
                                </Grid>

                                <Grid item xs={12}>
                                    <Grid container spacing={2} className={"flex flex-end"}>
                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Dự kiến thời gian tuyển
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsDateTimePicker
                                                label="Dự kiến từ"
                                                name="fromDate"
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsDateTimePicker
                                                label="Dự kiến đến"
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

export default memo(observer(RecruitmentPlanV2Filter));