import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { useStore } from "../../stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import StaffWorkingHistoryToolbar from "./StaffWorkingHistoryToolbar";
import StaffWorkingHistoryList from "./StaffWorkingHistoryList";
import StaffWorkingHistoryForm from "./StaffWorkingHistoryForm";
export default observer(function StaffWorkingHistoryIndex() {
  const { staffWorkingHistoryStore } = useStore();
  const { t } = useTranslation();

  const {
    pagingStaffWorkingHistory,
    openCreateEditPopup,
    openConfirmDeletePopup,
    handleClose,
    handleConfirmDelete,
    resetStore
  } = staffWorkingHistoryStore;

  useEffect(() => {
    pagingStaffWorkingHistory();
    return resetStore;
  }, []);

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb routeSegments={[
          { name: t("navigation.staff.title") },
          { name: t("navigation.staff.staffWorkingHistory") }
        ]} />
      </div>

      <Grid className="index-card" container spacing={2}>
        <Grid item xs={12}>
          <StaffWorkingHistoryToolbar />
        </Grid>

        <Grid item xs={12}>
          <StaffWorkingHistoryList />
        </Grid>
      </Grid>

      {openCreateEditPopup && (
        <StaffWorkingHistoryForm />
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


      {/* {openConfirmDeleteListPopup && (
        <GlobitsConfirmationDialog
          open={openConfirmDeleteListPopup}
          onConfirmDialogClose={handleClose}
          onYesClick={handleConfirmDeleteList}
          title={t("confirm_dialog.delete_list.title")}
          text={t("confirm_dialog.delete_list.text")}
          agree={t("confirm_dialog.delete_list.agree")}
          cancel={t("confirm_dialog.delete_list.cancel")}
        />
      )} */}
    </div>
  );
});
