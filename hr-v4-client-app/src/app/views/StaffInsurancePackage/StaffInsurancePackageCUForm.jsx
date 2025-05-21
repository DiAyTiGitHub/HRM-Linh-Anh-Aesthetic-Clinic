import React, { memo, useEffect, useState } from "react";
import { Form, Formik } from "formik";
import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import { pagingBank } from "../Bank/BankService";
import GlobitsDateTime from "app/common/form/GlobitsDateTime";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import { pagingInsurancePackage } from "app/views/InsurancePackage/InsurancePackageService";
import { useParams } from "react-router";

function StaffInsurancePackageCUForm(props) {
    const { t } = useTranslation();

    const {
        isDisabled,
        staffId,
        handleAfterSubmit
    } = props;

    const { id } = useParams();

    const { staffInsurancePackageStore } = useStore();

    const {
        handleClose,
        saveStaffInsurancePackage,
        selectedStaffInsurancePackage,
        openCreateEditPopup
    } = staffInsurancePackageStore;

    const validationSchema = Yup.object({

        insurancePackage: Yup.object().required(t("Chưa chọn gói bảo hiểm")).nullable(),
        startDate: Yup.date()
            .transform((castValue, originalValue) => (originalValue ? new Date(originalValue) : castValue))
            .required(t("validation.required"))
            .nullable()
            .typeError("Dữ liệu sai định dạng."),
        endDate: Yup.date()
            .transform((castValue, originalValue) => (originalValue ? new Date(originalValue) : castValue))
            .nullable()
            .typeError("Dữ liệu sai định dạng.")
            .test(
                "endDate-greater-equal-startDate",
                t("Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu"),
                function (value) {
                    const { startDate } = this.parent;
                    if (!value || !startDate) return true;
                    return new Date(value) >= new Date(startDate);
                }
            )

    });

    async function handleSaveForm(values) {
        let newValues = values;
        if (staffId != null) {
            newValues = { ...newValues, staffId, staff: { id: staffId } };
        }

        try {
            const response = await saveStaffInsurancePackage(newValues);

            await handleAfterSubmit(response);
        } catch (error) {
            console.error(error);
        }
    }

    const [initialValues, setInitialValues] = useState(selectedStaffInsurancePackage);

    useEffect(
        function () {
            setInitialValues(selectedStaffInsurancePackage);
        },
        [selectedStaffInsurancePackage, selectedStaffInsurancePackage?.id]
    );

    const updateTotalInsuranceAmount = (values, setFieldValue) => {
        const insuranceAmount = parseFloat(values.insuranceAmount) || 0;
        const staffPercentage = parseFloat(values.staffPercentage) || 0;
        const orgPercentage = parseFloat(values.orgPercentage) || 0;

        const staffAmount = parseFloat(((insuranceAmount * staffPercentage) / 100).toFixed(2));
        const orgAmount = parseFloat(((insuranceAmount * orgPercentage) / 100).toFixed(2));

        setFieldValue("staffAmount", staffAmount);
        setFieldValue("orgAmount", orgAmount);
    };

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='sm'
            open={openCreateEditPopup}
            noDialogContent
            title={(selectedStaffInsurancePackage?.id ? t("general.button.edit") : t("general.button.add")) + " " + "gói bảo hiểm"}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}>
                {({ isSubmitting, values, setFieldValue, initialValues }) => {

                    return (
                        <Form autoComplete='off' autocomplete='off'>
                            <div className='dialog-body'>
                                <DialogContent className='p-12'>
                                    <FormikFocusError />

                                    <Grid container spacing={2}>

                                        <Grid item xs={12}>
                                            <GlobitsPagingAutocompleteV2
                                                label='Gói bảo hiểm'
                                                name='insurancePackage'
                                                api={pagingInsurancePackage}
                                                getOptionLabel={(option) => {
                                                    return option?.code
                                                        ? `${option?.name} - ${option?.code}`
                                                        : option?.name;
                                                }}
                                                required
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label="Ngày bắt đầu đóng bảo hiểm"
                                                name='startDate'
                                                value={values?.startDate}
                                                required
                                            // placeholder="Đến ngày"
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label="Ngày kết thúc đóng bảo hiểm"
                                                name='endDate'
                                                value={values?.endDate}
                                            // placeholder="Đến ngày"
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsVNDCurrencyInput
                                                label={"Mức tham gia bảo hiểm"}
                                                name='insuranceAmount'
                                                onChange={(e) => {
                                                    const value = e?.target?.value;
                                                    setFieldValue("insuranceAmount", value);
                                                    updateTotalInsuranceAmount({ ...values, insuranceAmount: value }, setFieldValue);
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsVNDCurrencyInput
                                                label={"Mức đền bù bảo hiểm"}
                                                name='compensationAmount'
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsVNDCurrencyInput
                                                label={"Tỷ lệ nhân viên đóng bảo hiểm"}
                                                name='staffPercentage'
                                                onChange={(e) => {
                                                    const value = e?.target?.value;
                                                    setFieldValue("staffPercentage", value);
                                                    updateTotalInsuranceAmount({ ...values, staffPercentage: value }, setFieldValue);
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsVNDCurrencyInput
                                                label={"Số tiền nhân viên đóng bảo hiểm"}
                                                name='staffAmount'
                                                disabled
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsVNDCurrencyInput
                                                label={"Tỷ lệ công ty đóng bảo hiểm"}
                                                name='orgPercentage'
                                                onChange={(e) => {
                                                    const value = e?.target?.value;
                                                    setFieldValue("orgPercentage", value);
                                                    updateTotalInsuranceAmount({ ...values, orgPercentage: value }, setFieldValue);
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsVNDCurrencyInput
                                                label={"Số tiền công ty đóng bảo hiểm"}
                                                name='orgAmount'
                                                disabled
                                            />
                                        </Grid>

                                        {/* <Grid item xs={12} sm={6} className="mt-18"> */}
                                        <Grid item xs={12} className="mt-18">
                                            <GlobitsCheckBox
                                                label='Có đóng cho thân nhân người lao động'
                                                name='hasFamilyParticipation'
                                            />
                                        </Grid>


                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className='dialog-footer dialog-footer-v2 py-8'>
                                <DialogActions className='p-0'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button
                                            startIcon={<BlockIcon />}
                                            variant='contained'
                                            className='mr-12 btn btn-secondary d-inline-flex'
                                            color='secondary'
                                            onClick={handleClose}
                                            disabled={isSubmitting || isDisabled}>
                                            {t("general.button.cancel")}
                                        </Button>
                                        <Button
                                            startIcon={<SaveIcon />}
                                            className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained'
                                            color='primary'
                                            type='submit'
                                            disabled={isSubmitting || isDisabled}>
                                            {t("general.button.save")}
                                        </Button>
                                    </div>
                                </DialogActions>
                            </div>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(StaffInsurancePackageCUForm));
