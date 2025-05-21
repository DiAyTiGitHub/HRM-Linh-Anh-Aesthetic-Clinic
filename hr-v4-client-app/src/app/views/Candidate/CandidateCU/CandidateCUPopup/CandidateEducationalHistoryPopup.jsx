import React, { memo, useEffect, useState } from "react";
import { Formik, Form } from "formik";
import * as Yup from "yup";
import { useTranslation } from "react-i18next";
import moment from "moment";
import { Grid, DialogActions, Button, DialogContent, } from "@material-ui/core";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";

import { pagingCountry } from "app/views/Country/CountryService";
import { pagingSpecialities } from "app/views/Speciality/SpecialityService";
import { pagingEducationTypes } from "app/views/EducationType/EducationTypeService";
import { pagingEducationDegrees } from "app/views/EducationDegree/EducationDegreeService";
import { pagingTrainingBases } from "app/views/TrainingBase/TrainingBaseService";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { observer } from "mobx-react";
import { EducationalHistory } from "app/common/Model/EducationalHistory";

function CandidateEducationalHistoryPopup(props) {
    const { t } = useTranslation();
    const { open, handleClose, item, handleSubmit, editable } = props;

    const validationSchema = Yup.object({
        startDate: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày nhập học không đúng định dạng")
            .nullable(),

        endDate: Yup.date()
            .test(
                "is-greater",
                "Ngày tốt nghiệp phải lớn ngày nhập học",
                function (value) {
                    const { startDate } = this.parent;
                    if (startDate && value) {
                        return moment(value).isAfter(moment(startDate), "date");
                    }
                    return true;
                }
            )
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày tốt nghiệp không đúng định dạng")
            .nullable(),

        educationalInstitution: Yup.object()
            .required(t("validation.required"))
            .nullable(),
        country: Yup.object().required(t("validation.required")).nullable(),
        speciality: Yup.object().required(t("validation.required")).nullable(),
        major: Yup.object().required(t("validation.required")).nullable(),
        educationType: Yup.object().required(t("validation.required")).nullable(),
        educationDegree: Yup.object().required(t("validation.required")).nullable(),

        extendDateByDecision: Yup.date()
            .test(
                "is-greater",
                "Ngày gia hạn phải lớn ngày quyết định số",
                function (value) {
                    const { decisionDate } = this.parent;
                    if (decisionDate && value) {
                        return moment(value).isAfter(moment(decisionDate), "date");
                    }
                    return true;
                }
            )
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .typeError("Ngày không đúng định dạng")
            .nullable(true),

        extendDecisionDate: Yup.date()
            .test(
                "is-greater",
                "Ngày quyết định gia hạn phải lớn ngày gia hạn",
                function (value) {
                    const { decisionDate } = this.parent;
                    if (decisionDate && value) {
                        return moment(value).isAfter(moment(decisionDate), "date");
                    }
                    return true;
                }
            )
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .typeError("Ngày không đúng định dạng")
            .nullable(true),
    });

    const [formValues, setFormValues] = useState(null);

    useEffect(() => {
        if (item) {
            setFormValues({ ...item });
        } else {
            setFormValues(new EducationalHistory());
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [item]);

    console.log("current item", item);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            open={open}
            onClosePopup={handleClose}
            title={
                <span>
                    Chi tiết {t("educationHistory.title")}
                </span>
            }
            noDialogContent
        >
            <Formik
                initialValues={formValues}
                onSubmit={(values) => handleSubmit(values)}
                validationSchema={validationSchema}
            >
                {({ isSubmitting }) => (
                    <Form autoComplete="off">
                        <DialogContent
                            className="dialog-body px-12"

                        // style={{ maxHeight: "80vh", minWidth: "300px" }}
                        >
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={5}>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                required
                                                label={t("educationHistory.startDateOfQD")}
                                                name="startDate"
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                required
                                                label={t("educationHistory.endDateOfQD")}
                                                name="endDate"
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label={t("educationHistory.endDate")}
                                                name="actualGraduationYear"
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label={t("educationHistory.returnDate")}
                                                name="returnDate"
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsPagingAutocomplete
                                                label={t("educationHistory.educationalInstitution")}
                                                name="educationalInstitution"
                                                required
                                                api={pagingTrainingBases}
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsPagingAutocomplete
                                                label={t("educationHistory.country")}
                                                name="country"
                                                required
                                                api={pagingCountry}
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                label={t("educationHistory.decisionNumber")}
                                                name="decisionCode"
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label={t("educationHistory.date")}
                                                name="decisionDate"
                                                disableFuture={true}
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                label={t("educationHistory.fundingSource")}
                                                name="fundingSource"
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label={t("educationHistory.extendDateByDecision")}
                                                name="extendDateByDecision"
                                                disableFuture={true}
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                label={t("educationHistory.decisionNumber")}
                                                name="extendDecisionCode"
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label={t("educationHistory.date")}
                                                name="extendDecisionDate"
                                                disableFuture={true}
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>

                                <Grid item xs={12} md={7}>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsPagingAutocomplete
                                                label={t("educationHistory.speciality")}
                                                name="speciality"
                                                required
                                                api={pagingSpecialities}
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsPagingAutocomplete
                                                label={t("educationHistory.major")}
                                                name="major"
                                                required
                                                api={pagingSpecialities}
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsPagingAutocomplete
                                                required
                                                label={t("educationHistory.formsOfTraining")}
                                                name="educationType"
                                                api={pagingEducationTypes}
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsPagingAutocomplete
                                                label={t("educationHistory.degree")}
                                                name="educationDegree"
                                                required
                                                api={pagingEducationDegrees}
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={4} className="pt-24 pl-20" >
                                            <GlobitsCheckBox
                                                label={t("educationHistory.isConfirmation")}
                                                name="isConfirmation"
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                label={t("educationHistory.basis")}
                                                name="basis"
                                                multiline
                                                rows={4}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                label={t("educationHistory.note")}
                                                name="description"
                                                multiline
                                                rows={4}
                                            />
                                        </Grid>

                                        <Grid item xs={12}>
                                            <Grid container spacing={2}>
                                                <Grid item xs={6} md={4} lg={3}>
                                                    <GlobitsCheckBox
                                                        label={t("educationHistory.isCurrent")}
                                                        name="isCurrent"
                                                    />
                                                </Grid>
                                                <Grid item xs={6} md={4} lg={3}>
                                                    <GlobitsCheckBox
                                                        label={t("educationHistory.isCountedForSeniority")}
                                                        name="isCountedForSeniority"
                                                    />
                                                </Grid>
                                                <Grid item xs={6} md={4} lg={3}>
                                                    <GlobitsCheckBox
                                                        label={t("educationHistory.isExtended")}
                                                        name="isExtended"
                                                    />
                                                </Grid>
                                                <Grid item xs={6} md={4} lg={3}>
                                                    <GlobitsCheckBox
                                                        label={t("educationHistory.notFinish")}
                                                        name="notFinish"
                                                    />
                                                </Grid>
                                            </Grid>
                                        </Grid>
                                    </Grid>
                                </Grid>

                            </Grid>
                        </DialogContent>

                        <DialogActions className="dialog-footer p-12">
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
    )
}

export default memo(observer(CandidateEducationalHistoryPopup));
