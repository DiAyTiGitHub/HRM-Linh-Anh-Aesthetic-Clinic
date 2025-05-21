import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "../../stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import GroupPositionTitleV2List from "./GroupPositionTitleV2List";
import GroupPositionTitleV2CUForm from "./GroupPositionTitleV2CUForm";
import GroupPositionTitleV2Toolbar from "./GroupPositionTitleV2Toolbar";

function GroupPositionTitleV2Index() {
    const { positionTitleV2Store } = useStore();
    const { t } = useTranslation();

    const {
        pagingParentPositionTitle,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        openCreateEditPopup,
        handleClose,
        handleConfirmDeleteParent,
        handleConfirmDeleteListParent,
        resetParentStore,
        openViewPopup
    } = positionTitleV2Store;

    useEffect(() => {
        pagingParentPositionTitle();
        return resetParentStore;
    }, []);

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: t("navigation.organization.title") },
                        { name: t("navigation.organizationalDirectory.title") },
                        { name: t("navigation.groupPositionTitle.title") }
                    ]} />
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <GroupPositionTitleV2Toolbar />
                </Grid>

                <Grid item xs={12}>
                    <GroupPositionTitleV2List />
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <GroupPositionTitleV2CUForm />
            )}

            {openViewPopup && (
                <GroupPositionTitleV2CUForm readOnly={true} />
            )}


            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeleteParent}
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
                    onYesClick={handleConfirmDeleteListParent}
                    title={t("confirm_dialog.delete_list.title")}
                    text={t("confirm_dialog.delete_list.text")}
                    agree={t("confirm_dialog.delete_list.agree")}
                    cancel={t("confirm_dialog.delete_list.cancel")}
                />
            )}
        </div>
    );
}

export default memo(observer(GroupPositionTitleV2Index));
