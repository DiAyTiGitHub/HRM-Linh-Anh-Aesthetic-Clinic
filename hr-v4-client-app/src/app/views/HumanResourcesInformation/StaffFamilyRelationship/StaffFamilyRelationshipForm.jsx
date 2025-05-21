import React, { useEffect, useState } from "react";
import { Button, DialogActions, DialogContent, Grid, } from "@material-ui/core";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import SaveIcon from '@material-ui/icons/Save';
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { Form, Formik } from "formik";
import { useStore } from "app/stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import BlockIcon from "@material-ui/icons/Block";
import { observer } from "mobx-react";
import { useParams } from "react-router-dom";
import { pagingProfessions } from "../../Profession/ProfessionService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingFamilyRelationship } from "../../FamilyRelationship/FamilyRelationshipService";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import moment from "moment";

export default observer(function StaffFamilyRelationshipForm(props) {
    const { staffFamilyRelationshipStore } = useStore();
    const { t } = useTranslation();
    const { id } = useParams();
    const {
        handleClose,
        saveOrUpdate,
        selectedStaffFamilyRelationship,
        initialStaffFamilyRelationship,
        shouldOpenEditorDialog
    } = staffFamilyRelationshipStore;
    const validationSchema = Yup.object({
        // profession: Yup.object().required(t("validation.required")).nullable(),
        familyRelationship: Yup.object().required(t("validation.required")).nullable(),
        fullName: Yup.string().required(t("validation.required")).nullable(),
        // workingPlace: Yup.string().required(t("validation.required")).nullable(),
        // address: Yup.string().required(t("validation.required")).nullable(),
        birthDate: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .max(new Date(), "Ngày sinh không được lớn hơn ngày hiện tại") // Không cho phép ngày trong tương lai
            .typeError("Ngày không đúng định dạng")
            // .required(t("validation.required"))
            .nullable(),
        dependentDeductionFromDate: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .typeError("Ngày không đúng định dạng")
            .nullable()
            .test(
                "fromDate-greater-than-birthDate",
                "Ngày bắt đầu giảm trừ phải lớn hơn hoặc bằng ngày sinh",
                function (value) {
                    const { birthDate } = this.parent;
                    if (birthDate && value) {
                        return moment(value).isSameOrAfter(moment(birthDate), "date");
                    }
                    return true;
                }
            ),
        dependentDeductionToDate: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .typeError("Ngày không đúng định dạng")
            .nullable()
            .test(
                "toDate-greater-than-fromDate",
                "Ngày kết thúc giảm trừ phải lớn hơn hoặc bằng ngày bắt đầu giảm trừ",
                function (value) {
                    const { dependentDeductionFromDate } = this.parent;
                    if (dependentDeductionFromDate && value) {
                        return moment(value).isSameOrAfter(moment(dependentDeductionFromDate), "date");
                    }
                    return true;
                }
            )
            .test(
                "toDate-greater-than-birthDate",
                "Ngày kết thúc giảm trừ phải lớn hơn hoặc bằng ngày sinh",
                function (value) {
                    const { birthDate } = this.parent;
                    if (birthDate && value) {
                        return moment(value).isSameOrAfter(moment(birthDate), "date");
                    }
                    return true;
                }
            ),
    });


    const [staffFamilyRelationShip, setStaffFamilyRelationShip] = useState(initialStaffFamilyRelationship);

    useEffect(() => {
        if (selectedStaffFamilyRelationship) setStaffFamilyRelationShip(selectedStaffFamilyRelationship);
        else setStaffFamilyRelationShip(initialStaffFamilyRelationship);
        if (id) {
            setStaffFamilyRelationShip(prev => ({
                ...prev,
                staff: { id: id }
            }));
        }
    }, [selectedStaffFamilyRelationship?.id]);

    async function handleFormSubmit(values) {
        await saveOrUpdate(values);
    }

    return (
        <GlobitsPopupV2
            open={shouldOpenEditorDialog}
            size='sm'
            noDialogContent
            title={(staffFamilyRelationShip?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("quan hệ thân nhân")}
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={staffFamilyRelationShip}
                onSubmit={(values) => handleFormSubmit(values)}>
                {({ isSubmitting, setFieldValue, values }) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='o-hidden p-12'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsTextField
                                            required
                                            validate
                                            name="fullName"
                                            label={t("relatives.name")} />
                                    </Grid>

                                    <Grid item xs={12} md={6}>
                                        <GlobitsPagingAutocompleteV2
                                            name="profession"
                                            api={pagingProfessions}
                                            label={t("relatives.job")} />
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsDateTimePicker
                                            name="birthDate"
                                            disableFuture
                                            label={t("relatives.dob")} />
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsPagingAutocompleteV2
                                            required
                                            name="familyRelationship"
                                            api={pagingFamilyRelationship}
                                            label={t("relatives.relative")} />

                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsTextField
                                            name="address"
                                            label={t("relatives.address")} />
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsTextField name="workingPlace" label={t("relatives.workingPlace")} />
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsNumberInput
                                            label={"Mã số thuế"}
                                            name="taxCode"
                                        />
                                    </Grid>
                                    <Grid item xs={12} className={"flex align-end"}>
                                        <GlobitsCheckBox
                                            label={t("Là người phụ thuộc")}
                                            name="isDependent"
                                            handleChange={(_, value) => {
                                                setFieldValue("isDependent", value); // Cập nhật giá trị của isDependent
                                                if (!value) {
                                                    // Nếu bỏ chọn "Là người phụ thuộc", đặt lại các giá trị liên quan
                                                    setFieldValue("dependentDeductionFromDate", null);
                                                    setFieldValue("dependentDeductionToDate", null);
                                                }
                                            }}
                                        />
                                    </Grid>

                                    {values?.isDependent === true && (
                                        <Grid item xs={12} md={6}>
                                            <GlobitsDateTimePicker
                                                name="dependentDeductionFromDate"
                                                label={t("relatives.dependentDeductionFromDate")}
                                            />
                                        </Grid>
                                    )}
                                    {values?.isDependent === true && (
                                        <Grid item xs={12} md={6}>
                                            <GlobitsDateTimePicker
                                                name="dependentDeductionToDate"
                                                label={t("relatives.dependentDeductionToDate")}
                                            />
                                        </Grid>
                                    )}

                                </Grid>
                            </DialogContent>
                        </div>
                        <div className='dialog-footer'>
                            <DialogActions className='p-4'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button startIcon={<BlockIcon />} variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex' color='secondary'
                                        onClick={() => handleClose()}>
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button startIcon={<SaveIcon />} className='mr-0 btn btn-primary d-inline-flex'
                                        variant='contained' color='primary' type='submit' disabled={isSubmitting}>
                                        {t("general.button.save")}
                                    </Button>
                                </div>
                            </DialogActions>
                        </div>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    )
})