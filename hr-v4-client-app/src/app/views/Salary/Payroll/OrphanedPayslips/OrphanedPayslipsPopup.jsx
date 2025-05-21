import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useHistory } from "react-router-dom";
import SalaryStaffPayslipForm from "../../SalaryStaffPayslip/SalaryStaffPayslipForm";
import SalaryRecalPayslipPopup from "../../SalaryStaffPayslip/SalaryRecalPayslip/SalaryRecalPayslipPopup";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import MergeOrphanedPayslipButton from "./MergeOrphanedPayslipButton";
import OrphanedPayslipList from "./OrphanedPayslipList";

function OrphanedPayslipsPopup() {
    const {
        salaryResultDetailStore,
        salaryStaffPayslipStore,
        payrollStore

    } = useStore();

    const history = useHistory();

    const { t } = useTranslation();

    const {
        openViewOrphanedPayslipsPopup,
        handleCloseViewOrphanedPayslip,
        onViewSalaryResult,
        getAllOrphanedPayslips,
    } = payrollStore;

    const {
        openCreateEditPopup,
        openRecalculatePayslip,
        openConfirmDeletePopup,
        handleClose,
        handleConfirmDelete,
    } = salaryStaffPayslipStore;

    async function handleConfirmDeletePayslip() {
        try {
            await handleConfirmDelete();

            await getAllOrphanedPayslips(onViewSalaryResult?.id);
        }
        catch (error) {
            console.error(error);
        }
    }

    return (
        <>
            <GlobitsPopupV2
                popupId={'openViewOrphanedPayslipsPopup'}
                scroll={"body"}
                size="lg"
                open={openViewOrphanedPayslipsPopup}
                noDialogContent
                title={"Danh sách bản ghi có thể được tổng hợp"}
                onClosePopup={handleCloseViewOrphanedPayslip}
            >
                <div className="dialog-body">
                    <DialogContent className="p-12">

                        <Grid
                            container
                            spacing={2}
                        >
                            <Grid item xs={12}>
                                <OrphanedPayslipList />
                            </Grid>
                        </Grid>

                    </DialogContent>
                </div>

                <div className="dialog-footer dialog-footer-v2 py-8 px-12">
                    <DialogActions className="p-0">
                        <div className="flex flex-space-between flex-middle">
                            <Button
                                startIcon={<BlockIcon />}
                                variant="contained"
                                className="btn btn-secondary d-inline-flex"
                                color="secondary"
                                onClick={handleCloseViewOrphanedPayslip}
                            >
                                {t("general.button.cancel")}
                            </Button>

                            <MergeOrphanedPayslipButton />

                        </div>
                    </DialogActions>
                </div>
            </GlobitsPopupV2>


            {/* {openCreateEditPopup && (
                <SalaryStaffPayslipForm />
            )}

            {openRecalculatePayslip && (
                <SalaryRecalPayslipPopup />
            )} */}

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeletePayslip}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
        </>
    );
}

export default memo(observer(OrphanedPayslipsPopup));