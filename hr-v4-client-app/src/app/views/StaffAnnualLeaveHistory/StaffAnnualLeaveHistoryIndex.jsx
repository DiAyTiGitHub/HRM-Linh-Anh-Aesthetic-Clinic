import { Grid } from "@material-ui/core";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { getInitialPersonCertificateFilter } from "../HumanResourcesInformation/PersonCertificate/PersonCertificateService";
import StaffAnnualLeaveHistoryCUForm from "./StaffAnnualLeaveHistoryCUForm";
import StaffAnnualLeaveHistoryToolbar from "./StaffAnnualLeaveHistoryToolbar";
import StaffAnnualLeaveHistoryList from "./StaffAnnualLeaveHistoryList";
import { getInitialAnnualLeaveHistoryFilter } from "./StaffAnnualLeaveHistoryService";

function StaffAnnualLeaveHistoryIndex() {
    const {
        staffAnnualLeaveHistoryStore,
        hrRoleUtilsStore

    } = useStore();

    const { t } = useTranslation();

    const {
        handleSetSearchObject,
        handleOpenCreateEdit,
        searchObject,
        openCreateEditPopup,
        openConfirmDeletePopup,
        handleClose,
        handleConfirmDelete,
        listChosen,
        handleDeleteList,
        openConfirmDeleteListPopup,
        handleConfirmDeleteList,
        resetStore,
        pagingStaffAnnualLeaveHistory,
        openViewPopup
    } = staffAnnualLeaveHistoryStore;

    const {
        checkAllUserRoles,
        isAdmin,
        isManager

    } = hrRoleUtilsStore;

    async function initalizeScreen() {
        try {
            const { data } = await getInitialAnnualLeaveHistoryFilter();

            handleSetSearchObject({
                ...searchObject,
                ...data
            });

            await pagingStaffAnnualLeaveHistory();
        } catch (error) {
            console.error(error);
        }
    }

    useEffect(() => {
        resetStore();

        checkAllUserRoles();
        initalizeScreen();

        return resetStore;
    }, []);


    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: "Ngày nghỉ phép theo năm" }
                    ]}
                />
            </div>

            <Grid className='index-card' container spacing={2}>
                <Grid item xs={12}>
                    <StaffAnnualLeaveHistoryToolbar />
                </Grid>

                <Grid item xs={12}>
                    <StaffAnnualLeaveHistoryList />
                </Grid>
            </Grid>

            {openCreateEditPopup && <StaffAnnualLeaveHistoryCUForm />}

            {openViewPopup && <StaffAnnualLeaveHistoryCUForm readOnly={true} />}

            <GlobitsConfirmationDialog
                open={openConfirmDeletePopup}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDelete}
                title={t("confirm_dialog.delete.title")}
                text={t("confirm_dialog.delete.text")}
                agree={t("confirm_dialog.delete.agree")}
                cancel={t("confirm_dialog.delete.cancel")}
            />

            <GlobitsConfirmationDialog
                open={openConfirmDeleteListPopup}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDeleteList}
                title={t("confirm_dialog.delete_list.title")}
                text={t("confirm_dialog.delete_list.text")}
                agree={t("confirm_dialog.delete_list.agree")}
                cancel={t("confirm_dialog.delete_list.cancel")}
            />
        </div>
    );
}

export default memo(observer(StaffAnnualLeaveHistoryIndex));
