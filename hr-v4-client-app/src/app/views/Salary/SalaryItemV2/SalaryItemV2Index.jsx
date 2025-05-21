import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import SalaryItemV2List from "./SalaryItemV2List";
import SalaryItemV2CUForm from "./SalaryItemV2CUForm";
import SalaryItemIndexToolbar from "./SalaryItemIndexToolbar";

function SalaryItemV2Index() {
    const { t } = useTranslation();
    const { salaryItemStore, hrRoleUtilsStore } = useStore();

    const {
        pagingSalaryItem,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        openCreateEditPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore,
        openViewPopup,
    } = salaryItemStore;

    const {
        checkAllUserRoles
    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
        pagingSalaryItem();
        return resetStore;
    }, []);

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb routeSegments={
                    [
                        { name: t("navigation.salary") },
                        { name: t("navigation.salaryitem") }
                    ]
                } />
            </div>

            <Grid container spacing={2} className="index-card">
                <Grid item xs={12}>
                    <SalaryItemIndexToolbar />
                </Grid>

                <Grid item xs={12}>
                    <SalaryItemV2List />
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <SalaryItemV2CUForm />
            )}
            {openViewPopup && (
                <SalaryItemV2CUForm readOnly={true} />
            )}

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDelete}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}

            {openConfirmDeleteListPopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeleteListPopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeleteList}
                    title={t("confirm_dialog.delete_list.title")}
                    text={t("confirm_dialog.delete_list.text")}
                    agree={t("confirm_dialog.delete_list.agree")}
                    cancel={t("confirm_dialog.delete_list.cancel")}
                />
            )}
        </div>
    );
}

export default memo(observer(SalaryItemV2Index));
