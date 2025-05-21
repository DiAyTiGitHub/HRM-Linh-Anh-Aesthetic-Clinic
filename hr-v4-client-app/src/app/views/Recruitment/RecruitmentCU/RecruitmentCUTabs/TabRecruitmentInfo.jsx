import {Grid} from '@material-ui/core';
import GlobitsTextField from 'app/common/form/GlobitsTextField';
import {useFormikContext} from 'formik';
import React, {memo} from 'react'
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker';
import GlobitsEditor from 'app/common/form/GlobitsEditor';
import {useTranslation} from 'react-i18next';
import {pagingPosition} from 'app/views/Position/PositionService';
import {observer} from 'mobx-react';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import {pagingAllDepartments} from 'app/views/Department/DepartmentService';
import GlobitsVNDCurrencyInput from 'app/common/form/GlobitsVNDCurrencyInput';
import {pagingRecruitmentPlan} from '../../RecruitmentPlanV2/RecruitmentPlanV2Service';
import {pagingAllOrg} from 'app/views/Organization/OrganizationService';
import {pagingPositionTitle} from "../../../PositionTitle/PositionTitleService";
import TabRecruitmentInfoItemSection from "./TabRecruitmentInfoItemSection";

const TabRecruitmentInfo = () => {
    const {t} = useTranslation();

    const {values, setFieldValue} = useFormikContext();

    function handleChangeRecruitmentPlan(_, value) {
        setFieldValue("recruitmentPlan", value);
        setFieldValue("organization", value?.organization);
        setFieldValue("department", value?.department);
        setFieldValue("positionTitle", value?.positionTitle);
    }

    return (
        <Grid container spacing={2}>
            <Grid item sm={6} xs={12} md={4}>
                <GlobitsTextField
                    label={"Mã đợt tuyển"}
                    required
                    name="code"
                />
            </Grid>
            <Grid item sm={6} xs={12} md={4}>
                <GlobitsTextField
                    type="text"
                    required
                    label={"Tên đợt tuyển"}
                    name="name"
                />
            </Grid>

            <Grid item sm={6} xs={12} md={4}>
                <GlobitsPagingAutocompleteV2
                    label={"Kế hoạch"}
                    required
                    name="recruitmentPlan"
                    api={pagingRecruitmentPlan}
                    searchObject={{pageIndex: 1, pageSize: 999, keyword: ""}}
                    handleChange={handleChangeRecruitmentPlan}
                />
            </Grid>

            <Grid item sm={6} xs={12} md={4}>
                <GlobitsPagingAutocompleteV2
                    name="organization"
                    label="Đơn vị"
                    api={pagingAllOrg}
                    searchObject={{
                        pageIndex: 1, pageSize: 9999, keyword: ""
                    }}
                    disabled={values?.recruitmentPlan}
                />
            </Grid>

            <Grid item sm={6} xs={12} md={4}>
                <GlobitsPagingAutocompleteV2
                    name="department"
                    label="Phòng ban"
                    api={pagingAllDepartments}
                    searchObject={{
                        pageIndex: 1, pageSize: 9999, keyword: ""
                    }}
                    disabled={values?.recruitmentPlan}
                />
            </Grid>

            {/*<Grid item sm={6} xs={12} md={4}>*/}
            {/*    <GlobitsPagingAutocompleteV2*/}
            {/*        label={"Chức danh cần tuyển"}*/}
            {/*        validate*/}
            {/*        name="positionTitle"*/}
            {/*        api={pagingPositionTitle}*/}
            {/*        disabled={values?.recruitmentPlan}*/}
            {/*    />*/}
            {/*</Grid>*/}

            {/* <Grid item xs={6} sm={12} md={4}>
                <GlobitsAsyncAutocomplete
                    label={"Phòng ban tuyển dụng"}
                    validate
                    name="hrDepartmentCS"
                    api={pagingAllDepartments}
                    searchObject={{ pageIndex: 1, pageSize: 999, keyword: "" }}
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsPagingAutocompleteV2
                    label={"Vị trí tuyển"}
                    required
                    name="position"
                    api={pagingPosition}
                    searchObject={{ pageIndex: 1, pageSize: 9999, keyword: "" }}
                />
            </Grid> */}

            {/*<Grid item sm={6} xs={12} md={4}>*/}
            {/*    <GlobitsVNDCurrencyInput*/}
            {/*        label={"Số lượng"}*/}
            {/*        name="quantity"*/}
            {/*    />*/}
            {/*</Grid>*/}
            <Grid item sm={6} xs={12} md={4}>
                <GlobitsDateTimePicker
                    label={"Ngày bắt đầu"}
                    name="startDate"
                />
            </Grid>
            <Grid item sm={6} xs={12} md={4}>
                <GlobitsDateTimePicker
                    label={"Ngày kết thúc"}
                    name="endDate"
                />
            </Grid>
            <Grid item sm={12} xs={12} md={12}>
                <GlobitsEditor
                    label={"Ghi chú"}
                    name="note"
                />
            </Grid>
            <Grid item sm={12}>
                <TabRecruitmentInfoItemSection/>
            </Grid>
        </Grid>
    )
}

export default memo(observer(TabRecruitmentInfo));
