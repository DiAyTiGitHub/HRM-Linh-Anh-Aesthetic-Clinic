import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import StaffSalaryTemplateList from "./StaffSalaryTemplateList";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import StaffSalaryTemplateCUForm from "./StaffSalaryTemplateCUForm";
import StaffSalaryTemplateIndexToolbar from "./StaffSalaryTemplateIndexToolbar";
import { Staff } from "app/common/Model/Staff";
import PopupDownloadTemplate from "app/views/StaffSalaryTemplate/PopupDownloadTemplate";
import SalaryValueHistoriesPopup from "./SalaryValueHistories/SalaryValueHistoriesPopup";

function StaffSalaryTemplateIndex() {
    const {
        staffSalaryTemplateStore,
        hrRoleUtilsStore,
        staffSalaryItemValueStore

    } = useStore();

    const { t } = useTranslation();

    const {
        pagingStaffSalaryTemplate,
        openCreateEditPopup,
        openConfirmDeletePopup,
        openConfirmDeleteListPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore,
        openViewPopup,
        openPopupDownloadTemplate,

    } = staffSalaryTemplateStore;

    const {
        openValueHitoriesPopup
    } = staffSalaryItemValueStore;

    useEffect(() => {

        pagingStaffSalaryTemplate();

        return resetStore;
    }, []);

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb routeSegments={[
                    { name: t("navigation.salary") },
                    { name: t("Mẫu bảng lương áp dụng cho nhân viên") }
                ]} />
            </div>

            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <StaffSalaryTemplateIndexToolbar />
                </Grid>

                <Grid item xs={12}>
                    <StaffSalaryTemplateList />
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <StaffSalaryTemplateCUForm />
            )}

            {openPopupDownloadTemplate && (
                <PopupDownloadTemplate />
            )}

            {openViewPopup && (
                <StaffSalaryTemplateCUForm readOnly={true} />
            )}

            {openValueHitoriesPopup && (
                <SalaryValueHistoriesPopup />
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

export default memo(observer(StaffSalaryTemplateIndex));
