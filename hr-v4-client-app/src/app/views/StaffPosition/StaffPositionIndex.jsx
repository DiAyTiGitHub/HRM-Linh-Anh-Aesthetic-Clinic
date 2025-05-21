import {observer} from "mobx-react";
import React, {useEffect} from "react";
import {Grid} from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import {useTranslation} from "react-i18next";
import StaffPositionToolbar from "./StaffPositionToolbar";
import StaffPositionList from "./StaffPositionList";
import {useStore} from "../../stores";
import StaffPositionForm from "./StaffPositionForm";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";

export default observer(function StaffPositionIndex() {
    const {t} = useTranslation();
    const {staffPositionStore} = useStore();
    const {
        openCreateEditPopup,
        openConfirmDeletePopup,
        handleClose,
        handleConfirmDelete,
        pagingStaffPosition
    } = staffPositionStore;

    useEffect(() => {
        pagingStaffPosition()
    }, []);
    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                <GlobitsBreadcrumb routeSegments={[
                    {name: t("navigation.staff.title")},
                    {name: t("navigation.staff.position")}
                ]}/>
            </div>

            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <StaffPositionToolbar/>
                </Grid>

                <Grid item xs={12}>
                    <StaffPositionList/>
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <StaffPositionForm/>
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


        </div>
    );
});
