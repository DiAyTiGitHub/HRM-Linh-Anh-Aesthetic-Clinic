import {Button, DialogActions, DialogContent, Grid} from "@material-ui/core";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import {Form, Formik} from "formik";
import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import * as Yup from "yup";

import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import {useStore} from "app/stores";
import {pagingCountry} from "app/views/Country/CountryService";
import {pagingEducationDegrees} from "app/views/EducationDegree/EducationDegreeService";
import {pagingEducationTypes} from "app/views/EducationType/EducationTypeService";
import {pagingSpecialities} from "app/views/Speciality/SpecialityService";
import {pagingTrainingBases} from "app/views/TrainingBase/TrainingBaseService";
import {useParams} from "react-router-dom/cjs/react-router-dom.min";
import moment from "moment";
import FormikFocusError from "../../../../common/FormikFocusError";

export default function StaffEducationHistoryPopupAdd(props) {
    const {t} = useTranslation();
    // const { open, handleClose, item, handleSubmit, editable } = props;
    const {staffEducationHistoryStore} = useStore();

    const {
        shouldOpenEditorDialog,
        handleClose,
        saveOrUpdateEducationHistory,
        selectedEducationHistory,
    } = staffEducationHistoryStore;
    const initialItem = {
        startDate: null,
        endDate: null,

        actualGraduationYear: null,
        returnDate: null,

        educationalInstitution: null,
        country: null,

        decisionCode: null,
        decisionDate: null,

        fundingSource: null,

        extendDateByDecision: null,
        extendDecisionCode: null,
        extendDecisionDate: null,

        speciality: null,
        major: null,
        educationType: null,

        educationDegree: null,
        isConfirmation: false,

        basis: "",
        description: "",

        isCurrent: false,
        isCountedForSeniority: false,
        isExtended: false,
        notFinish: false,
    };

    const validationSchema = Yup.object({
        startDate: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày nhập học không đúng định dạng")
            .nullable(),

        endDate: Yup.date()
            .test("is-greater", "Ngày tốt nghiệp phải lớn ngày nhập học", function (value) {
                const {startDate} = this.parent;
                if (startDate && value) {
                    return moment(value).isAfter(moment(startDate), "date");
                }
                return true;
            })
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày tốt nghiệp không đúng định dạng")
            .nullable(),

        educationalInstitution: Yup.object().required(t("validation.required")).nullable(),
        country: Yup.object().required(t("validation.required")).nullable(),
        speciality: Yup.object().required(t("validation.required")).nullable(),
        major: Yup.object().required(t("validation.required")).nullable(),
        educationType: Yup.object().required(t("validation.required")).nullable(),
        educationDegree: Yup.object().required(t("validation.required")).nullable(),

        extendDateByDecision: Yup.date()
            .test("is-greater", "Ngày gia hạn phải lớn ngày quyết định số", function (value) {
                const {decisionDate} = this.parent;
                if (decisionDate && value) {
                    return moment(value).isAfter(moment(decisionDate), "date");
                }
                return true;
            })
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .typeError("Ngày không đúng định dạng")
            .nullable(true),

        extendDecisionDate: Yup.date()
            .test("is-greater", "Ngày quyết định gia hạn phải lớn ngày gia hạn", function (value) {
                const {decisionDate} = this.parent;
                if (decisionDate && value) {
                    return moment(value).isAfter(moment(decisionDate), "date");
                }
                return true;
            })
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .typeError("Ngày không đúng định dạng")
            .nullable(true),
    });

    const [formValues, setFormValues] = useState(null);

    const {id: staffId} = useParams();

    const handleSubmit = (values) => {
        const dto = {
            ...values,
            staff: {
                id: staffId,
            },
        };

        console.log(dto);
        saveOrUpdateEducationHistory(dto)
            .then((result) => {
                console.log(result);
            })
            .catch((err) => {
                console.log(err);
            });
    };

    useEffect(() => {
        if (selectedEducationHistory) {
            setFormValues({...selectedEducationHistory});
        } else {
            setFormValues({...initialItem});
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [selectedEducationHistory, selectedEducationHistory?.id]);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            open={shouldOpenEditorDialog}
            onClosePopup={handleClose}
            title={
                (selectedEducationHistory?.id ? t("general.button.add") : t("general.button.edit")) +
                " " +
                t("educationHistory.title")
            }
            noDialogContent>
            <Formik
                initialValues={formValues}
                onSubmit={(values) => handleSubmit(values)}
                validationSchema={validationSchema}
                enableReinitialize
            >
                {({isSubmitting}) => (
                    <Form autoComplete='off'>
                        <FormikFocusError/>
                        <DialogContent
                            className='dialog-body'
                            // style={{ maxHeight: "80vh", minWidth: "300px" }}
                        >
                            <Grid container spacing={3}>
                                <Grid item container spacing={1} xs={12} md={6}>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsDateTimePicker
                                            required
                                            label={t("educationHistory.startDateOfQD")}
                                            name='startDate'
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsDateTimePicker
                                            required
                                            label={t("educationHistory.endDateOfQD")}
                                            name='endDate'
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsDateTimePicker
                                            label={t("educationHistory.endDate")}
                                            name='actualGraduationYear'
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsDateTimePicker
                                            label={t("educationHistory.returnDate")}
                                            name='returnDate'
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={12}>
                                        <GlobitsPagingAutocomplete
                                            label={t("educationHistory.educationalInstitution")}
                                            name='educationalInstitution'
                                            required
                                            api={pagingTrainingBases}
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={12}>
                                        <GlobitsPagingAutocomplete
                                            label={t("educationHistory.country")}
                                            name='country'
                                            required
                                            api={pagingCountry}
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsTextField
                                            label={t("educationHistory.decisionNumber")}
                                            name='decisionCode'
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsDateTimePicker
                                            label={t("educationHistory.date")}
                                            name='decisionDate'
                                            disableFuture={true}
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={12}>
                                        <GlobitsTextField
                                            label={t("educationHistory.fundingSource")}
                                            name='fundingSource'
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={12}>
                                        <GlobitsDateTimePicker
                                            label={t("educationHistory.extendDateByDecision")}
                                            name='extendDateByDecision'
                                            disableFuture={true}
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsTextField
                                            label={t("educationHistory.decisionNumber")}
                                            name='extendDecisionCode'
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsDateTimePicker
                                            label={t("educationHistory.date")}
                                            name='extendDecisionDate'
                                            disableFuture={true}
                                        />
                                    </Grid>
                                </Grid>
                                <Grid item container spacing={1} xs={12} md={6}>
                                    <Grid item xs={12} md={12}>
                                        <GlobitsPagingAutocomplete
                                            label={t("educationHistory.speciality")}
                                            name='speciality'
                                            required
                                            api={pagingSpecialities}
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={12}>
                                        <GlobitsPagingAutocomplete
                                            label={t("educationHistory.major")}
                                            name='major'
                                            required
                                            api={pagingSpecialities}
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={12}>
                                        <GlobitsPagingAutocomplete
                                            required
                                            label={t("educationHistory.formsOfTraining")}
                                            name='educationType'
                                            api={pagingEducationTypes}
                                        />
                                    </Grid>
                                    <Grid item xs={12} sm={8}>
                                        <GlobitsPagingAutocomplete
                                            label={t("educationHistory.degree")}
                                            name='educationDegree'
                                            required
                                            api={pagingEducationDegrees}
                                        />
                                    </Grid>
                                    <Grid item xs={12} sm={4} style={{paddingTop: "25px", paddingLeft: "20px"}}>
                                        <GlobitsCheckBox
                                            label={t("educationHistory.isConfirmation")}
                                            name='isConfirmation'
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={12}>
                                        <GlobitsTextField
                                            label={t("educationHistory.basis")}
                                            name='basis'
                                            multiline
                                            rows={4}
                                        />
                                    </Grid>
                                    <Grid item xs={12} md={12}>
                                        <GlobitsTextField
                                            label={t("educationHistory.note")}
                                            name='description'
                                            multiline
                                            rows={4}
                                        />
                                    </Grid>
                                    <Grid item container xs={12} md={12} spacing={1}>
                                        <Grid item xs={6} sm={3} md={6}>
                                            <GlobitsCheckBox label={t("educationHistory.isCurrent")} name='isCurrent'/>
                                        </Grid>
                                        <Grid item xs={6} sm={3} md={6}>
                                            <GlobitsCheckBox
                                                label={t("educationHistory.isCountedForSeniority")}
                                                name='isCountedForSeniority'
                                            />
                                        </Grid>
                                        <Grid item xs={6} sm={3} md={6}>
                                            <GlobitsCheckBox
                                                label={t("educationHistory.isExtended")}
                                                name='isExtended'
                                            />
                                        </Grid>
                                        <Grid item xs={6} sm={3} md={6}>
                                            <GlobitsCheckBox label={t("educationHistory.notFinish")} name='notFinish'/>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <div className='dialog-footer dialog-footer-v2 py-8'>
                            <DialogActions className='p-0'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        startIcon={<BlockIcon/>}
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        color='secondary'
                                        onClick={handleClose}
                                        disabled={isSubmitting}>
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<SaveIcon/>}
                                        className='mr-0 btn btn-primary d-inline-flex'
                                        variant='contained'
                                        color='primary'
                                        type='submit'
                                        disabled={isSubmitting}>
                                        {t("general.button.save")}
                                    </Button>
                                </div>
                            </DialogActions>
                        </div>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
}
