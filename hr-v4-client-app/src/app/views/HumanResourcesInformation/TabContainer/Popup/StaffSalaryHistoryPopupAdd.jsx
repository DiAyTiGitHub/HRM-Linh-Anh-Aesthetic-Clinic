import React, {useEffect, useState} from "react";
import {Button, DialogActions, DialogContent, Grid,} from "@material-ui/core";
import {Form, Formik, useFormikContext} from "formik";
import {useTranslation} from "react-i18next";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import * as Yup from "yup";
import GlobitsPopupV2 from "../../../../common/GlobitsPopupV2";

export default function StaffSalaryHistoryPopupAdd(props) {
    const {t} = useTranslation();
    const {open, handleClose, item, handleSubmit, editable} = props;
    const {values} = useFormikContext();
    const initialItem = {
        startDate: null,
        endDate: null,
        trainingPlace: "",
        trainingCountry: null,
        certificate: null,
        trainingContent: "",
    };

    const validationSchema = Yup.object({
        decisionDate: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày quyết định tăng lương không đúng định dạng")
            .nullable(),
        positionName: Yup.string().required(t("validation.required")).nullable(),
        staffTypeCode: Yup.string().required(t("validation.required")).nullable(),
        coefficientOverLevel: Yup.string().required(t("validation.required")).nullable(),
        percentage: Yup.string().required(t("validation.required")).nullable(),
        decisionCode: Yup.string().required(t("validation.required")).nullable(),
    });

    const [formValues, setFormValues] = useState(null);

    useEffect(() => {
        if (item) {
            setFormValues({...item});
        } else {

            setFormValues({
                ...initialItem,
                positionName: values?.staffCode || '',
                staffTypeCode: values?.staffType?.code || ''
            });
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [item]);

    return (
        <GlobitsPopupV2
            open={open}
            onClosePopup={handleClose}
            noDialogContent
            title={(editable ? t("general.button.add") : t("general.button.edit")) + " " + t("salaryHistory")}
        >
            <Formik
                initialValues={formValues}
                onSubmit={(values) => handleSubmit(values)}
                validationSchema={validationSchema}
            >
                {({isSubmitting}) => (
                    <Form autoComplete="off">
                        <DialogContent
                            className="dialog-body"
                            style={{maxHeight: "80vh", minWidth: "300px"}}
                        >
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={4}>
                                    <GlobitsTextField
                                        validate
                                        label={t("humanResourcesInformation.positionName")}
                                        name="positionName"
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <GlobitsTextField
                                        validate
                                        label={t("humanResourcesInformation.staffTypeCode")}
                                        name="staffTypeCode"
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <GlobitsTextField
                                        validate
                                        label={t("humanResourcesInformation.coefficientOverLevel")}
                                        name="coefficientOverLevel"
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <GlobitsTextField
                                        validate
                                        label={t("humanResourcesInformation.percentage")}
                                        name="percentage"
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <GlobitsTextField
                                        validate
                                        label={t("humanResourcesInformation.decisionCode")}
                                        name="decisionCode"
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <GlobitsDateTimePicker
                                        required
                                        label={t("humanResourcesInformation.decisionDate")}
                                        name="decisionDate"
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <GlobitsTextField
                                        label={t("humanResourcesInformation.salaryIncrementType")}
                                        name="salaryIncrementType"
                                    />
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <DialogActions className="dialog-footer p-0">
                            <div className="flex flex-space-between flex-middle">
                                <Button
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
    );
}
