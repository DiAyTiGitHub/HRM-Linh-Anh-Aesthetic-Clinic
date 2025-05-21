import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import PositionTitleV2CUForm from "./PositionTitleV2CUForm";
import PositionTitleV2List from "./PositionTitleV2List";
import PositionTitleV2Toolbar from "./PositionTitleV2Toolbar";

function PositionTitleV2Index() {
    const { positionTitleV2Store, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        pagingPositionTitle,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        openCreateEditPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore,
        openViewPopup
    } = positionTitleV2Store;

    const {
        checkAllUserRoles
    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
        pagingPositionTitle();
        return resetStore;
    }, []);

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: t("navigation.organization.title") },
                        { name: t("navigation.organizationalDirectory.title") },
                        { name: t("navigation.positionTitleV2.title") }
                    ]}
                />
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <PositionTitleV2Toolbar />
                </Grid>

                <Grid item xs={12}>
                    <PositionTitleV2List />
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <PositionTitleV2CUForm />
            )}
            {openViewPopup
                && (
                    <PositionTitleV2CUForm readOnly={true} />
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

export default memo(observer(PositionTitleV2Index));
