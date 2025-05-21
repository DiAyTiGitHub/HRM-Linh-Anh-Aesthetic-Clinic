import {observer} from "mobx-react";
import React, {memo, useEffect} from "react";
import {useStore} from "../../stores";
import {Grid} from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import {useTranslation} from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import StaffIpKeepingList from "./StaffIpKeepingList";
import StaffIpKeepingForm from "./StaffIpKeepingForm";
import HrDepartmentIpToolbar from "./StaffIpKeepingToolbar";

function StaffIpKeepingIndex() {
    const {staffIpKeepingStore} = useStore();
    const {t} = useTranslation();

    const {
        openCreateEditPopup,
        pagingStaffIpKeeping,
        openConfirmDeletePopup,
        openConfirmDeleteListPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore
    } = staffIpKeepingStore;

    useEffect(() => {
        pagingStaffIpKeeping();
        return resetStore
    }, []);

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb routeSegments={[
                    {name: "Chấm công"},
                    {name: "Danh mục chấm công"},
                    {name: t("navigation.staffIpKeeping.title")}
                ]}/>
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <HrDepartmentIpToolbar/>
                </Grid>

                <Grid item xs={12}>
                    <StaffIpKeepingList/>
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <StaffIpKeepingForm/>
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

export default memo(observer(StaffIpKeepingIndex));
