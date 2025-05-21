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
import moment from "moment";
import { toast } from "react-toastify";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";

function StaffSalaryTemplateCUForm(props) {
    const { hasStaff = false, readOnly } = props;
    const { staffSalaryTemplateStore } = useStore();
    const { t } = useTranslation();

    const {
        handleClose,
        saveStaffSalaryTemplate,
        pagingStaffSalaryTemplate,
        selectedStaffSalaryTemplate,
        openCreateEditPopup,
        openViewPopup,
        handleSetOpenCreateEditPopup,
        handleSetOpenViewPopup
    } = staffSalaryTemplateStore;

    const validationSchema = Yup.object({
        staff: Yup.object().required(t("validation.required")).nullable(),
        salaryTemplate: Yup.object().required(t("validation.required")).nullable(),
        // fromDate: Yup.date()
        //     .transform(function transformDate(castValue, originalValue) {
        //         return originalValue ? new Date(originalValue) : castValue;
        //     })
        //     .typeError("Ngày bắt đầu không đúng định dạng")
        //     .nullable(),

        // toDate: Yup.date()
        //     .test("is-greater", "Ngày kết thúc phải lớn hơn ngày bắt đầu", function (value) {
        //         const { fromDate } = this.parent;
        //         if (fromDate && value) {
        //             return moment(value).isAfter(moment(fromDate), "date");
        //         }
        //         return true;
        //     })
        //     .transform(function transformDate(castValue, originalValue) {
        //         return originalValue ? new Date(originalValue) : castValue;
        //     })
        //     .typeError("Ngày kết thúc không đúng định dạng")
        //     .nullable(),
    });

    // async function handleSaveForm(values) {
    //   await saveStaffSalaryTemplate(values);
    //   await pagingStaffSalaryTemplate();
    // }

    async function handleSaveForm(values) {
        try {
            const payloadLabourAgreement = {
                ...values
            };

            await saveStaffSalaryTemplate(payloadLabourAgreement);

            // save staff salary item values
            const payloadStaffSalaryItemValueList = {
                staff: values?.staff,
                salaryTemplate: values?.salaryTemplate,
                staffSalaryItemValue: values?.staffSalaryItemValue
            };

            if (payloadStaffSalaryItemValueList.staff && payloadStaffSalaryItemValueList.salaryTemplate && Array.isArray(payloadStaffSalaryItemValueList.staffSalaryItemValue) && payloadStaffSalaryItemValueList.staffSalaryItemValue.length > 0) {
                await saveStaffSalaryItemValueList(payloadStaffSalaryItemValueList);
            }

            await pagingStaffSalaryTemplate();

            toast.success("Thông tin mẫu bảng lương áp dụng cho nhân viên đã được lưu");
            handleSetOpenCreateEditPopup(false)
            handleSetOpenViewPopup(true)

        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại");

            console.error(error);
        }
    }

    const [initialValues, setInitialValues] = useState(selectedStaffSalaryTemplate);

    useEffect(function () {
        setInitialValues({
            ...selectedStaffSalaryTemplate
        });
    }, [selectedStaffSalaryTemplate, selectedStaffSalaryTemplate?.id]);

    return (<GlobitsPopupV2
        size="sm"
        scroll={"body"}
        open={openCreateEditPopup || openViewPopup}
        noDialogContent
        title={openViewPopup ? (t("Xem chi tiết ") + t("Mẫu bảng lương nhân viên")) : (selectedStaffSalaryTemplate?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("Mẫu bảng lương nhân viên")}
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

                                <Grid item xs={12} className="pb-0">
                                    <p className="m-0 p-0 borderThrough2">
                                        Đối tượng áp dụng
                                    </p>
                                </Grid>

                                {!hasStaff && (
                                    <Grid item xs={12} sm={6}>
                                        <ChooseUsingStaffSection
                                            label="Nhân viên"
                                            name="staff"
                                            required
                                            readOnly={values?.id || readOnly}
                                        />
                                    </Grid>
                                )}

                                <Grid item xs={12} sm={6}>
                                    <GlobitsPagingAutocompleteV2
                                        required
                                        readOnly={values?.id || readOnly}
                                        label={t("Mẫu bảng lương")}
                                        name="salaryTemplate"
                                        api={pagingSalaryTemplates}
                                        getOptionLabel={(option) => {
                                            return `${option?.name || ""}`
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

                                <StaffSalaryItemValue readOnly={readOnly} />
                            </Grid>
                        </DialogContent>

                        <DialogActions className="dialog-footer px-12">
                            <div className="flex flex-space-between flex-middle">
                                <Button
                                    variant="contained"
                                    className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                    startIcon={<BlockIcon />}
                                    color="secondary"
                                    disabled={isSubmitting}
                                    onClick={handleClose}
                                >
                                    {t("general.button.close")}
                                </Button>
                                {!readOnly && (
                                    <Button
                                        className="mr-0 btn btn-primary d-inline-flex"
                                        variant="contained"
                                        color="primary"
                                        startIcon={<SaveIcon />}
                                        type="submit"
                                        disabled={isSubmitting}
                                    >
                                        {t("general.button.save")}
                                    </Button>)}
                            </div>
                        </DialogActions>
                    </Form>
                );
            }}
        </Formik>
    </GlobitsPopupV2>);
}

export default memo(observer(StaffSalaryTemplateCUForm));
