import { DialogContent, Grid } from "@material-ui/core";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import SalaryStaffPayslipSection from "./SalaryStaffPayslipSection";
import SalaryStaffPayslipUpdateSection from "./SalaryStaffPayslipUpdateSection";
import SelectSignatureForm from "./SelectSignatureForm";
import VerticalSalaryStaffPayslipSectionPrint from "./VerticalSalaryStaffPayslipSectionPrint";

function SalaryStaffPayslipForm() {
    const { salaryStaffPayslipStore } = useStore();
    const { t } = useTranslation();
    const {
        handleClose,
        saveSalaryStaffPayslip,
        pagingSalaryStaffPayslip,
        selectedStaffPayslip,
        openCreateEditPopup,
        openSelectSignature,
    } = salaryStaffPayslipStore;

    const validationSchema = Yup.object({
        staff: Yup.object().required(t("validation.required")).nullable(),
    });

    async function handleSaveForm(values) {
        await saveSalaryStaffPayslip(values);
        await pagingSalaryStaffPayslip();
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
            size='md'
            open={openCreateEditPopup}
            noDialogContent
            title={"Phiếu lương nhân viên"}
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
                                    <FormikFocusError />

                                    <Grid container spacing={2}>
                                        <Grid item xs={12} sm={8} lg={9}>
                                            <div className='dialogScrollContent pr-12'>
                                                <SalaryStaffPayslipSection />
                                            </div>
                                            <VerticalSalaryStaffPayslipSectionPrint
                                                values={values}
                                                // style={{ display: "none" }} // để ngang
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={4} lg={3}>
                                            <SalaryStaffPayslipUpdateSection />
                                        </Grid>
                                    </Grid>

                                    {openSelectSignature && <SelectSignatureForm />}
                                </DialogContent>
                            </div>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(SalaryStaffPayslipForm));
