import { observer } from "mobx-react";
import React , { memo , useEffect } from "react";
import { useStore } from "../../stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import PublicHolidayDateCUForm from "./PublicHolidayDateCUForm";
import PublicHolidayDateIndexToolbar from "./PublicHolidayDateIndexToolbar";
import PublicHolidayDateList from "./PublicHolidayDateList";
import PublicHolidayDateAutomaticForm from "./PublicHolidayDateAutomaticForm";
import { getFirstDateOfMonth , getLastDateOfMonth } from "app/LocalFunction";

function PublicHolidayDateIndex() {
    const {publicHolidayDateStore} = useStore();
    const {t} = useTranslation();

    const {
        pagingPublicHolidayDate ,
        openCreateEditPopup ,
        openConfirmDeletePopup ,
        openConfirmDeleteListPopup ,
        handleClose ,
        handleConfirmDelete ,
        handleConfirmDeleteList ,
        openPopupAutomatic ,
        searchObject ,
        handleSetSearchObject ,
        resetStore ,
        openViewPopup
    } = publicHolidayDateStore;

    useEffect(() => {
        handleSetSearchObject({
            ... searchObject ,
            fromDate:getFirstDateOfMonth() ,
            toDate:getLastDateOfMonth() ,
        });
        pagingPublicHolidayDate();
        return resetStore;
    } , []);

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb routeSegments={[
                    {name:t("navigation.timeKeeping.title")} ,
                    {name:t("navigation.publicHolidayDate.title")}
                ]}/>
            </div>

            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <PublicHolidayDateIndexToolbar/>
                </Grid>

                <Grid item xs={12}>
                    <PublicHolidayDateList/>
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <PublicHolidayDateCUForm/>
            )}
            {openViewPopup && (
                <PublicHolidayDateCUForm readOnly={true}/>
            )}

            {openPopupAutomatic && (
                <PublicHolidayDateAutomaticForm/>
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

export default memo(observer(PublicHolidayDateIndex));
