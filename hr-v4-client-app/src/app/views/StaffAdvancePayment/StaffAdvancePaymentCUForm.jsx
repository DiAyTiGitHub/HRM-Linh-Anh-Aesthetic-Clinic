import React, { memo, useEffect, useState } from "react";
import { Form, Formik } from "formik";
import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingSalaryPeriod } from "../Salary/SalaryPeriod/SalaryPeriodService";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";

function StaffAdvancePaymentCUForm({ readOnly }) {
    const { t } = useTranslation();

    const {
        staffAdvancePaymentStore,
        hrRoleUtilsStore

    } = useStore();

    const {
        handleClose,
        saveStaffAdvancePayment,
        pagingStaffAdvancePayment,
        selectedStaffAdvancePayment,
        openCreateEditPopup,
        openViewPopup
    } = staffAdvancePaymentStore;

    const {
        isAdmin,
        isManager,
        isStaffView,
        isCompensationBenifit
    } = hrRoleUtilsStore;

    let canChangeAdvanceStaff = false;
    if (isCompensationBenifit) {
        canChangeAdvanceStaff = true;
    }

    const validationSchema = Yup.object({
        staff: Yup.object().required(t("validation.required")).nullable(),
        advancedAmount: Yup.number().required(t("validation.required")).nullable(),
    });

    async function handleSaveForm(values) {
        await saveStaffAdvancePayment(values);
        await pagingStaffAdvancePayment();
    }

    const [initialValues, setInitialValues] = useState(selectedStaffAdvancePayment);

    useEffect(function () {
        setInitialValues(selectedStaffAdvancePayment);
    }, [selectedStaffAdvancePayment, selectedStaffAdvancePayment?.id]);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="sm"
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={openViewPopup ? (t("Xem chi tiết khoản ứng lương")) : ((selectedStaffAdvancePayment?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + "khoản ứng lương")}
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
                        <Form autoComplete="off" autocomplete="off">
                            <div className="dialog-body">
                                <DialogContent className="p-12">
                                    <FormikFocusError />

                                    <Grid container spacing={2}>
                                        <Grid item xs={12} sm={6}>
                                            <ChooseUsingStaffSection
                                                required
                                                label={"Nhân viên xin ứng trước"}
                                                readOnly={readOnly || !canChangeAdvanceStaff}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsPagingAutocomplete
                                                label={"Kỳ lương ứng tiền"}
                                                name="salaryPeriod"
                                                required
                                                api={pagingSalaryPeriod}
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label={t("Ngày yêu cầu")}
                                                name="requestDate"
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsVNDCurrencyInput
                                                label={"Số tiền ứng trước"}
                                                name="advancedAmount"
                                                required
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsSelectInput
                                                label={"Trạng thái xác nhận"}
                                                name="approvalStatus"
                                                hideNullOption
                                                options={LocalConstants.StaffAdvancePaymentApprovalStatus.getListData()}
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                label="Lý do tạm ứng"
                                                name="requestReason"
                                                multiline
                                                rows={3}
                                                readOnly={readOnly}
                                            />
                                        </Grid>
                                    </Grid>

                                </DialogContent>
                            </div>

                            <div className="dialog-footer dialog-footer-v2 py-8">
                                <DialogActions className="p-0">
                                    <div className="flex flex-space-between flex-middle">
                                        <Button
                                            startIcon={<BlockIcon />}
                                            variant="contained"
                                            className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                            color="secondary"
                                            onClick={handleClose}
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.cancel")}
                                        </Button>

                                        {(!readOnly) && (
                                            <Button
                                                startIcon={<SaveIcon />}
                                                className="mr-0 btn btn-primary d-inline-flex"
                                                variant="contained"
                                                color="primary"
                                                type="submit"
                                                disabled={isSubmitting}
                                            >
                                                {t("general.button.save")}
                                            </Button>
                                        )}
                                    </div>
                                </DialogActions>
                            </div>
                        </Form>
                    );
                }
                }
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(StaffAdvancePaymentCUForm));