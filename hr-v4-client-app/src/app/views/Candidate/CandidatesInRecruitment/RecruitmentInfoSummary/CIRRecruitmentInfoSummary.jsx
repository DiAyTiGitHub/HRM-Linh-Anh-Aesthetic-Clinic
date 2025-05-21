import { Grid, useTheme } from '@material-ui/core'
import { useStore } from 'app/stores'
import { observer } from 'mobx-react'
import React, { memo, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import * as Yup from "yup";
import { useHistory, useParams } from 'react-router-dom/cjs/react-router-dom'
import { Form, Formik } from 'formik'
import FormikFocusError from 'app/common/FormikFocusError'
import GlobitsTextField from 'app/common/form/GlobitsTextField'
import { pagingRecruitmentPlan } from 'app/views/Recruitment/RecruitmentPlanV2/RecruitmentPlanV2Service'
import { pagingAllDepartments } from 'app/views/Department/DepartmentService'
import { pagingPosition } from 'app/views/Position/PositionService'
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2'
import GlobitsAsyncAutocomplete from 'app/common/form/GlobitsAsyncAutocomplete'
import GlobitsVNDCurrencyInput from 'app/common/form/GlobitsVNDCurrencyInput'
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker'
import GlobitsEditor from 'app/common/form/GlobitsEditor'

function CIRRecruitmentInfoSummary() {
    const { t } = useTranslation()
    const history = useHistory()
    const theme = useTheme();
    const { recruitmentStore } = useStore();

    const {
        resetStore,
        selectedRecruitment,
        handleOpenCreateEdit,
    } = recruitmentStore;

    // const { id: recruitmentId } = useParams();
    const { recruitmentId } = useParams();

    useEffect(function () {
        resetStore();
        
        async function getRecruitmentData(recruitmentId) {
            await handleOpenCreateEdit(recruitmentId);
        }

        if (recruitmentId == "new-recruitment") {
            getRecruitmentData(null);
        }
        else {
            getRecruitmentData(recruitmentId);
        }
    }, [recruitmentId]);

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.code")).nullable(),
        name: Yup.string().required(t("validation.name")).nullable(),
    });


    return (
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={JSON.parse(JSON.stringify(selectedRecruitment))}
            onSubmit={(values) => console.log(values)}
        >
            {({ setFieldValue, values, isSubmitting, errors, touched, resetForm }) => {

                function handleChangeRecruitmentPlan(_, value) {
                    setFieldValue("recruitmentPlan", value);
                    setFieldValue("hrDepartmentCS", value?.recruitmentRequest?.hrDepartment);
                    setFieldValue("position", value?.recruitmentRequest?.position);
                }

                return (
                    <Form autoComplete="off">
                        <FormikFocusError />

                        <Grid container spacing={2}>
                            <Grid item sm={6} xs={12} md={4}>
                                <GlobitsTextField
                                    label={"Mã đợt tuyển"}
                                    required
                                    disabled
                                    name="code"
                                />
                            </Grid>
                            <Grid item sm={6} xs={12} md={4}>
                                <GlobitsTextField
                                    type="text"
                                    required
                                    disabled
                                    label={"Tên đợt tuyển"}
                                    name="name"
                                />
                            </Grid>

                            <Grid item xs={6} sm={12} md={4}>
                                <GlobitsPagingAutocompleteV2
                                    label={"Kế hoạch"}
                                    disabled
                                    required
                                    name="recruitmentPlan"
                                    handleChange={handleChangeRecruitmentPlan}
                                    api={pagingRecruitmentPlan}
                                    searchObject={{ pageIndex: 1, pageSize: 999, keyword: "" }}
                                />
                            </Grid>

                            <Grid item xs={6} sm={12} md={4}>
                                <GlobitsAsyncAutocomplete
                                    label={"Phòng ban tuyển dụng"}
                                    disabled
                                    validate
                                    name="hrDepartmentCS"
                                    api={pagingAllDepartments}
                                    searchObject={{ pageIndex: 1, pageSize: 999, keyword: "" }}
                                />
                            </Grid>

                            <Grid item xs={12} sm={6} md={4}>
                                <GlobitsPagingAutocompleteV2
                                    label={"Vị trí tuyển"}
                                    disabled
                                    required
                                    name="position"
                                    api={pagingPosition}
                                    searchObject={{ pageIndex: 1, pageSize: 9999, keyword: "" }}
                                />
                            </Grid>
                            
                            <Grid item sm={6} xs={12} md={4}>
                                {/* <GlobitsTextField
                                            type="text"
                                            label={"Số lượng"}
                                            name="quantity"
                                        /> */}
                                <GlobitsVNDCurrencyInput
                                    label={"Số lượng"}
                                    disabled
                                    name="quantity"
                                />
                            </Grid>
                            <Grid item sm={6} xs={12} md={4}>
                                <GlobitsDateTimePicker
                                    label={"Ngày bắt đầu"}
                                    disabled
                                    name="startDate"
                                />
                            </Grid>
                            <Grid item sm={6} xs={12} md={4}>
                                <GlobitsDateTimePicker
                                    label={"Ngày kết thúc"}
                                    disabled
                                    name="endDate"
                                />
                            </Grid>
                            <Grid item sm={12} xs={12} md={12}>
                                <GlobitsTextField
                                    label={"Ghi chú"}
                                    disabled
                                    name="note"
                                    multiline
                                    rows={3}
                                />
                            </Grid>
                        </Grid>

                    </Form>
                )
            }}
        </Formik>
    );
}

export default memo(observer(CIRRecruitmentInfoSummary));