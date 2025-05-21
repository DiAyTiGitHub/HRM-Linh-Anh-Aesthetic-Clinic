import React, { memo, useEffect, useState } from "react";
import { Button, DialogActions, DialogContent, Grid, } from "@material-ui/core";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { Form, Formik } from "formik";
import { useStore } from "app/stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { observer } from "mobx-react";
import { useParams } from "react-router-dom";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import moment from "moment";

function StaffInsuranceHistoryCUForm(props) {
    const { staffInsuranceHistoryStore } = useStore();
    const { t } = useTranslation();
    const { id } = useParams();
    const {
        handleClose,
        saveStaffInsuranceHistory,
        selectedInsuranceHistory,
        shouldOpenEditorDialog
    } = staffInsuranceHistoryStore;

    const validationSchema = Yup.object({
        staff: Yup.object().required(t("validation.required")).nullable(),
        startDate: Yup.date()
            .test(
                "is-greater-or-equal",
                "Ngày bắt đầu hiệu lực phải lớn hơn hoặc bằng ngày thiết lập",
                function (value) {
                    const { signedDate } = this.parent;
                    if (signedDate && value) {
                        return moment(value).isSameOrAfter(moment(signedDate), "date");
                    }
                    return true;
                }
            )
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày bắt đầu không đúng định dạng")
            .nullable(),

        endDate: Yup.date()
            .test("is-greater", "Ngày kết thúc phải lớn ngày bắt đầu", function (value) {
                const { startDate } = this.parent;
                if (startDate && value) {
                    return moment(value).isAfter(moment(startDate), "date");
                }
                return true;
            })
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            // .required(t("validation.required"))
            .typeError("Ngày kết thúc không đúng định dạng")
            .nullable(),
    });

    const [staffInsuranceHistory, setStaffInsuranceHistory] = useState(selectedInsuranceHistory);

    useEffect(() => {
        if (selectedInsuranceHistory) setStaffInsuranceHistory(selectedInsuranceHistory);
        else setStaffInsuranceHistory(selectedInsuranceHistory);
        if (id) {
            setStaffInsuranceHistory(prev => ({
                ...prev,
                staff: { id: id }
            }));
        }

    }, [selectedInsuranceHistory?.id]);

    async function handleSubmit(values) {
        await saveStaffInsuranceHistory(values);
    }

    const updateTotalInsuranceAmount = (values, setFieldValue) => {
        const insuranceSalary = values.insuranceSalary || 0;

        // Cá nhân đóng
        const staffInsuranceAmount = (insuranceSalary * (values.staffPercentage || 0)) / 100;

        // Công ty đóng
        const orgInsuranceAmount = (insuranceSalary * (values.orgPercentage || 0)) / 100;

        setFieldValue("staffInsuranceAmount", staffInsuranceAmount);
        setFieldValue("orgInsuranceAmount", orgInsuranceAmount);
    };

    return (
        <GlobitsPopupV2
            size={"sm"}
            open={shouldOpenEditorDialog}
            onClosePopup={handleClose}
            noDialogContent
            title={(staffInsuranceHistory?.id?.length > 0 ? t("general.button.add") : t("general.button.edit")) + " " + "quá trình đóng BHXH"}
        >
            <Formik
                initialValues={staffInsuranceHistory}
                onSubmit={(values) => handleSubmit(values)}
                validationSchema={validationSchema}
            >
                {({ isSubmitting, values, setFieldValue }) => (
                    <Form autoComplete="off">
                        <DialogContent className='dialog-body p-12'>
                            <Grid container spacing={2}>
                                <Grid item xs={12} >
                                    <p className='m-0 p-0 borderThrough2'>Thông tin quá trình</p>
                                </Grid>

                                <Grid item xs={12} sm={6} >
                                    <GlobitsDateTimePicker
                                        label={"Ngày bắt đầu"}
                                        name='startDate'
                                        // value={values.startDate}
                                        required
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} >
                                    <GlobitsDateTimePicker
                                        label={"Ngày kết thúc"}
                                        name='endDate'
                                        // value={values.endDate}
                                        // required
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Mức lương đóng bảo hiểm xã hội (VNĐ)"}
                                        name='insuranceSalary'
                                        onChange={(e) => {
                                            let value = e.target.value;
                                            setFieldValue("insuranceSalary", value);
                                            updateTotalInsuranceAmount({
                                                ...values,
                                                insuranceSalary: value
                                            }, setFieldValue);
                                        }}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Tỷ lệ cá nhân đóng BHXH (%) trong kỳ lương"}
                                        name='staffPercentage'
                                        onChange={(e) => {
                                            let value = e.target.value;
                                            setFieldValue("staffPercentage", value);
                                            updateTotalInsuranceAmount({
                                                ...values,
                                                staffPercentage: value
                                            }, setFieldValue);
                                        }}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Số tiền cá nhân đóng (VNĐ)"}
                                        name='staffInsuranceAmount'
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Tỷ lệ đơn vị đóng BHXH (%) trong kỳ lương"}
                                        name='orgPercentage'
                                        onChange={(e) => {
                                            let value = e.target.value;
                                            setFieldValue("orgPercentage", value);
                                            updateTotalInsuranceAmount({
                                                ...values,
                                                orgPercentage: value
                                            }, setFieldValue);
                                        }}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Số tiền đơn vị đóng (VNĐ)"}
                                        name='orgInsuranceAmount'
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <GlobitsTextField
                                        name='note'
                                        label={t("humanResourcesInformation.note")}
                                        multiline
                                        rows={4}
                                    />
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <DialogActions className='dialog-footer px-12'>
                            <div className="flex flex-space-between flex-middle">
                                <Button
                                    startIcon={<BlockIcon />}
                                    variant="contained"
                                    className="mr-12 btn btn-secondary d-inline-flex"
                                    color="secondary"
                                    onClick={() => {
                                        handleClose();
                                    }}
                                >
                                    {t("general.button.close")}
                                </Button>

                                <Button
                                    startIcon={<SaveIcon />}
                                    className="mr-0 btn btn-primary d-inline-flex"
                                    variant="contained"
                                    color="primary"
                                    type="submit"
                                    disabled={isSubmitting}
                                >
                                    {t("general.button.save")}
                                </Button>
                            </div>
                        </DialogActions>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    )
}

export default memo(observer(StaffInsuranceHistoryCUForm));