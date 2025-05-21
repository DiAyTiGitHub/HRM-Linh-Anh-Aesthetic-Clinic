import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";

import BlockIcon from "@material-ui/icons/Block";
import { pagingStaffSignature } from "app/services/StaffSignatureService";
function SelectSignatureForm() {
    const { salaryStaffPayslipStore } = useStore();
    const { t } = useTranslation();
    const { handleClose, selectedStaffPayslip, openSelectSignature , handleDownloadSlip} = salaryStaffPayslipStore;

    const validationSchema = Yup.object({
        staff: Yup.object().required(t("validation.required")).nullable(),
    });

    async function handleSaveForm(values) {
      console.log(values);
      handleDownloadSlip(selectedStaffPayslip?.id, values?.staffSignature?.id)
    }

    const [initialValues, setInitialValues] = useState(selectedStaffPayslip);

    useEffect(
        function () {
            setInitialValues(selectedStaffPayslip);
        },
        [selectedStaffPayslip, selectedStaffPayslip?.id]
    );

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='sm'
            open={openSelectSignature}
            noDialogContent
            title={"Chọn người duyệt và chữ ký"}
            onClosePopup={handleClose}>
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
                                    <Grid container spacing={2}>
                                        <Grid item xs={6}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Người duyệt lương")}
                                                name='approver'
                                                api={pagingStaff}
                                                getOptionLabel={(option) => {
                                                    return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                                                }}
                                                handleChange={(_, value) => {
                                                    setFieldValue("approver", value);
                                                    setFieldValue("staffSignature", null);
                                                }}
                                            />
                                        </Grid>
                                        <Grid item xs={6}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Chọn chữ ký")}
                                                name='staffSignature'
                                                api={pagingStaffSignature}
                                                allowLoadOptions={!!values?.approver?.id}
                                                searchObject={{
                                                    staffId: values?.approver?.id,
                                                }}
                                                clearOptionOnClose
                                                handleChange={(_, value) => {
                                                    setFieldValue("staffSignature", value);
                                                }}
                                            />
                                        </Grid>
                                    </Grid>
                                </DialogContent>
                            </div>
                            <div className='dialog-footer'>
                                <DialogActions className='p-0'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button
                                            startIcon={<BlockIcon />}
                                            variant='contained'
                                            className='mr-12 btn btn-gray d-inline-flex'
                                            // color="secondary"
                                            onClick={() => handleClose()}>
                                            {t("general.button.cancel")}
                                        </Button>
                                        <Button
                                            startIcon={<SaveIcon />}
                                            className='mr-0 btn btn-success d-inline-flex'
                                            variant='contained'
                                            // color="primary"
                                            type='submit'>
                                            {t("Tải phiếu lương")}
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

export default memo(observer(SelectSignatureForm));
