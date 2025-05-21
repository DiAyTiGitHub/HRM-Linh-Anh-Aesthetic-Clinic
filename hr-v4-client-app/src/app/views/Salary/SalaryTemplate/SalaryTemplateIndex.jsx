import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import SalaryTemplateList from "./SalaryTemplateList";
import SalaryTemplateIndexToolbar from "./SalaryTemplateIndexToolbar";
import SalaryTemplateCreateForm from "../SalaryTemplateCU/SalaryTemplateCreateForm";

function SalaryTemplateIndex() {
    const { t } = useTranslation();
    const {
        salaryTemplateStore,
        hrRoleUtilsStore
    } = useStore();

    const {
        checkAllUserRoles
    } = hrRoleUtilsStore;

    const {
        pagingSalaryTemplates,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        shouldOpenCreateForm,
        resetStore,
    } = salaryTemplateStore;

    useEffect(() => {
        checkAllUserRoles();

        pagingSalaryTemplates();

        return resetStore;
    }, []);


    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb
                    routeSegments={[{ name: t("navigation.salary") }, { name: t("navigation.salaryTemplate.title") }]} />
            </div>

            <Grid container spacing={2} className="index-card">
                <Grid item xs={12}>
                    <SalaryTemplateIndexToolbar />
                </Grid>

                <Grid item xs={12}>
                    <SalaryTemplateList />
                </Grid>
            </Grid>
            {shouldOpenCreateForm && (
                <SalaryTemplateCreateForm />
            )}
            {
                openConfirmDeletePopup && (
                    <GlobitsConfirmationDialog
                        open={openConfirmDeletePopup}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDelete}
                        title={t("confirm_dialog.delete.title")}
                        text={t("confirm_dialog.delete.text")}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />
                )
            }

            {
                openConfirmDeleteListPopup && (
                    <GlobitsConfirmationDialog
                        open={openConfirmDeleteListPopup}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDeleteList}
                        title={t("confirm_dialog.delete_list.title")}
                        text={t("confirm_dialog.delete_list.text")}
                        agree={t("confirm_dialog.delete_list.agree")}
                        cancel={t("confirm_dialog.delete_list.cancel")}
                    />
                )
            }
        </div>
    );
}

export default memo(observer(SalaryTemplateIndex));