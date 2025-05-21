import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { observer } from "mobx-react";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import i18n from "i18n";
import InsurancePackageItemSection from "app/views/InsurancePackage/InsurancePackageItemSection";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

const defaultInsurancePackage = {
    name: "",
    code: "",
    description: "",
    packageItems: []
};

export default observer(function InsurancePackageForm({ handleAfterSubmit, updateListOnClose, open }) {
    const { insurancePackageStore } = useStore();
    const { t } = useTranslation();
    const { handleClose, saveOrUpdate, selectedInsurancePackage } = insurancePackageStore;

    const [hrDocumentTemplate, setInsurancePackage] = useState(defaultInsurancePackage);
    const validationSchema = Yup.object({
        name: Yup.string().trim().required(t("validation.required")),

        code: Yup.string().trim().required(t("validation.required")),

        // description: Yup.string().trim().required(t("validation.required")),

        packageItems: Yup.array().of(
            Yup.object({
                name: Yup.string().trim().required(t("validation.required")),

                // code: Yup.string().trim().required(t("validation.required")),

                description: Yup.string().trim().required(t("validation.required")),

                displayOrder: Yup.number().nullable().required(t("validation.required")),

                // isRequired: Yup.boolean().nullable(),
            })
        ),
    });

    useEffect(() => {
        if (selectedInsurancePackage) setInsurancePackage(selectedInsurancePackage);
        else setInsurancePackage(defaultInsurancePackage);
    }, [selectedInsurancePackage]);

    async function handleFormSubmit(values, { setFieldError }) {
        // if (values?.documentItems?.length > 0) {
        //     const codeMap = values.documentItems.reduce((acc, item, index) => {
        //         if (item?.code) {
        //             acc[item.code] = acc[item.code] || [];
        //             acc[item.code].push(index);
        //         }
        //         return acc;
        //     }, {});

        //     // Lấy các mã bị trùng (xuất hiện nhiều hơn 1 lần)
        //     const duplicateCodes = Object.keys(codeMap).filter(code => codeMap[code].length > 1);

        //     if (duplicateCodes.length > 0) {
        //         toast.error(i18n.t("Mã tài liệu không được trùng"));

        //         // Đặt lỗi ở từng ô input bị trùng
        //         duplicateCodes.forEach(code => {
        //             codeMap[code].forEach(index => {
        //                 setFieldError(`documentItems[${index}].code`, "Mã tài liệu bị trùng");
        //             });
        //         });

        //         return; // Dừng submit nếu có lỗi
        //     }
        // }

        await saveOrUpdate(values);
    }


    return (
        <GlobitsPopupV2
            open={open}
            size='md'
            scroll={"body"}
            noDialogContent
            title={
                (selectedInsurancePackage?.id ? t("general.button.edit") : t("general.button.add")) +
                " " +
                t("Gói bảo hiểm")
            }
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={hrDocumentTemplate}
                onSubmit={handleFormSubmit}>
                {({ isSubmitting }) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='o-hidden p-12'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} md={6}>
                                        <GlobitsTextField label={t("Tên gói bảo hiểm")} name='name' required />
                                    </Grid>

                                    <Grid item xs={12} md={6}>
                                        <GlobitsTextField label={t("Mã gói bảo hiểm")} name='code' required />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsTextField label={"Mô tả"} name="description" multiline rows={4} />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <InsurancePackageItemSection />
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </div>
                        <div className='dialog-footer'>
                            <DialogActions className='p-4'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        startIcon={<BlockIcon />}
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        color='secondary'
                                        onClick={() => handleClose()}>
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<SaveIcon />}
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
});
