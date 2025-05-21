import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { useStore } from "../../stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import StaffAllowanceIndexToolbar from "./StaffAllowanceIndexToolbar";
import StaffAllowanceList from "./StaffAllowanceList";
import StaffAllowanceCUForm from "./StaffAllowanceCUForm";

export default observer(function StaffAllowanceIndex() {
  const { staffAllowanceStore, hrRoleUtilsStore } = useStore();
  const { t } = useTranslation();

  const {
    pagingStaffAllowance,
    openCreateEditPopup,
    openConfirmDeletePopup,
    openConfirmDeleteListPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    checkAdmin,
    resetStore
  } = staffAllowanceStore;

  const {
    checkAllUserRoles
  } = hrRoleUtilsStore;

  useEffect(() => {
    checkAllUserRoles();
    pagingStaffAllowance();
    return resetStore;
  }, []);

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb routeSegments={[
          { name: t("navigation.salary") },
          { name: t("navigation.staffAllowance.title") }
        ]} />
      </div>

      <Grid className="index-card" container spacing={2}>
        <Grid item xs={12}>
          <StaffAllowanceIndexToolbar />
        </Grid>

        <Grid item xs={12}>
          <StaffAllowanceList />
        </Grid>
      </Grid>

      {openCreateEditPopup && (
        <StaffAllowanceCUForm />
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
