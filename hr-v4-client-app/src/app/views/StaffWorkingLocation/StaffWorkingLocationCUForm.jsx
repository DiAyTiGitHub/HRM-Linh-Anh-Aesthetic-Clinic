import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "../../stores";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingWorkplace } from "app/views/Workplace/WorkplaceService";

function StaffWorkingLocationCUForm(props) {
    const { t } = useTranslation();
    const { staffId = null, onSaved } = props;
    const { staffWorkingLocationStore } = useStore();

    const {
        handleClose,
        saveStaffWorkingLocation,
        pagingStaffWorkingLocation,
        selectedWorkingLocation,
        openCreateEditPopup,
    } = staffWorkingLocationStore;

    const validationSchema = Yup.object({
        staff: Yup.object().required(t("validation.required")).nullable(),
        workplace: Yup.object().required(t("validation.required")).nullable(),
    });

    async function handleSaveForm(values) {
        try {
            const response = await saveStaffWorkingLocation(values);

            if (onSaved) {
                onSaved();
            } else {
                await pagingStaffWorkingLocation();
            }
        } catch (error) {
            console.error(error);
        }
    }

    const [initialValues, setInitialValues] = useState(selectedWorkingLocation);

    useEffect(
        function () {
            setInitialValues({
                ...selectedWorkingLocation,
            });
        },
        [selectedWorkingLocation, selectedWorkingLocation?.id]
    );

    return (
        <GlobitsPopupV2
            size='xs'
            scroll={"body"}
            open={openCreateEditPopup}
            noDialogContent
            title={
                (selectedWorkingLocation?.id ? t("general.button.edit") : t("general.button.add")) +
                " " +
                "Địa điểm làm việc"
            }
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}>
                {({ isSubmitting, values, setFieldValue, initialValues }) => {
                    return (
                        <Form autoComplete='off'>
                            <DialogContent className='dialog-body p-12'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <ChooseUsingStaffSection
                                            required
                                            label={"Nhân viên"}
                                            placeholder={""}
                                            disabled
                                        />
                                    </Grid>

                                    {/* <Grid item xs={12}>
                                        <GlobitsTextField
                                            label='Địa điểm làm việc'
                                            name='workingLocation'
                                            multiline
                                            rows={3}
                                        />
                                    </Grid> */}

                                    <Grid item xs={12}>
                                        <GlobitsPagingAutocompleteV2
                                            label={"Địa điểm làm việc"}
                                            name='workplace'
                                            api={pagingWorkplace}
                                            required
                                        />
                                    </Grid>


                                    <Grid item xs={12} className='mt-8'>
                                        <GlobitsCheckBox
                                            style={{
                                                marginLeft: "0",
                                            }}
                                            label='Là địa điểm làm việc chính'
                                            name='isMainLocation'
                                        />
                                    </Grid>
                                </Grid>
                            </DialogContent>

                            <DialogActions className='dialog-footer px-12'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        color='secondary'
                                        disabled={isSubmitting}
                                        onClick={handleClose}>
                                        {t("general.button.close")}
                                    </Button>
                                    <Button
                                        className='mr-0 btn btn-primary d-inline-flex'
                                        variant='contained'
                                        color='primary'
                                        type='submit'
                                        disabled={isSubmitting}>
                                        {t("general.button.save")}
                                    </Button>
                                </div>
                            </DialogActions>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(StaffWorkingLocationCUForm));
