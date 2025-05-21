import React, { memo, useEffect, useState } from "react";
import { Form, Formik } from "formik";
import { Button, DialogActions, DialogContent, Grid, makeStyles } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingSalaryTemplates } from "../Salary/SalaryTemplate/SalaryTemplateService";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import StaffSalaryItemValue from "./StaffSalaryItemValue";
import { saveStaffSalaryItemValueList } from "../Salary/StaffSalaryItemValue/StaffSalaryItemValueService";
import { toast } from "react-toastify";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { Search } from "@material-ui/icons";
import { downloadTemplate } from "app/views/StaffWorkScheduleV2/StaffWorkScheduleService";

function PopupDownloadTemplate() {
    const { staffSalaryTemplateStore } = useStore();
    const { t } = useTranslation();

    const {
        handleClose,
        openPopupDownloadTemplate,
        searchObject,
        downloadTemplate
    } = staffSalaryTemplateStore;

    const validationSchema = Yup.object({
        salaryTemplate: Yup.object().required(t("validation.required")).nullable(),
    });

    async function handleSaveForm(values) {
        try {
            const updatedSearchObject = {
                ...searchObject,
                salaryTemplate: values.salaryTemplate,
                salaryTemplateId: values.salaryTemplate?.id,
            };
            await downloadTemplate(updatedSearchObject);

            toast.success("Đã tải về mẫu nhập của mẫu bảng lương thành công");

            handleClose();
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại");

            console.error(error);
        }
    }

    const [initialValues, setInitialValues] = useState(searchObject);

    return (<GlobitsPopupV2
        size="xs"
        scroll={"body"}
        open={openPopupDownloadTemplate}
        noDialogContent
        title={t("Tải mẫu nhập theo Mẫu bảng lương")}
        onClosePopup={handleClose}
    >
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={initialValues}
            onSubmit={handleSaveForm}
        >
            {({ isSubmitting, values, setFieldValue, initialValues }) => {

                return (
                    <Form autoComplete="off">
                        <DialogContent className="o-hidden p-12">
                            <Grid container spacing={2}>

                                <Grid item xs={12} sm={12}>
                                    <GlobitsPagingAutocompleteV2
                                        validate
                                        required
                                        //readOnly={values?.id}
                                        label={t("Mẫu bảng lương")}
                                        name="salaryTemplate"
                                        api={pagingSalaryTemplates}
                                        getOptionLabel={(option) => {
                                            return `${option?.name || ""}`
                                        }}
                                        handleChange={(_, value) => {
                                            setFieldValue("salaryTemplate", value);
                                            setFieldValue("salaryTemplateId", value?.id);
                                        }}
                                    />
                                </Grid>

                                {/* <Grid item xs={12} sm={6}>
                                    <GlobitsDateTimePicker
                                        label="Ngày bắt đầu áp dụng"
                                        name="fromDate"
                                        readOnly={readOnly}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6}>
                                    <GlobitsDateTimePicker
                                        label="Ngày kết thúc áp dụng"
                                        name="toDate"
                                        readOnly={readOnly}
                                    />
                                </Grid> */}
                            </Grid>
                        </DialogContent>
                        <DialogActions className="dialog-footer px-12">
                            <div className="flex flex-space-between flex-middle">
                                <Button
                                    variant="contained"
                                    //className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                    className={`mr-12 btn-secondary d-inline-flex`}
                                    startIcon={<BlockIcon />}
                                    color="secondary"
                                    disabled={isSubmitting}
                                    onClick={handleClose}
                                >
                                    {t("general.button.close")}
                                </Button>
                                <Button
                                    className="mr-0 btn btn-primary d-inline-flex"
                                    variant="contained"
                                    color="primary"
                                    startIcon={<SaveIcon />}
                                    type="submit"
                                    disabled={isSubmitting}
                                >
                                    {t("Tải về mẫu nhập")}
                                </Button>
                            </div>
                        </DialogActions>
                    </Form>
                );
            }}
        </Formik>
    </GlobitsPopupV2>);
}

export default memo(observer(PopupDownloadTemplate));
