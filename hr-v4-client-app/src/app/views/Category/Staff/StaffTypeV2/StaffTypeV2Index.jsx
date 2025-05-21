import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid, Button, IconButton, ButtonGroup } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import StaffTypeV2List from "./StaffTypeV2List";
import StaffTypeV2CUForm from "./StaffTypeV2CUForm";
import StaffTypeV2IndexToolbar from "./StaffTypeV2IndexToolbar";

function StaffTypeV2Index() {
  const { staffTypeStore } = useStore();
  const { t } = useTranslation();

  const {
    pagingStaffType,
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    openCreateEditPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    resetStore,
  } = staffTypeStore;

  useEffect(() => {
    pagingStaffType();

    return resetStore;
  }, []);

  return (
    <div className="content-index">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb routeSegments={
          [
            { name: t("navigation.category.title") },
            { name: t("navigation.category.staff.title") },
            { name: t("navigation.category.staff.employeesType") },
          ]
        } />
      </div>

      <Grid container spacing={2} className="index-card">
        <Grid item xs={12} >
          <StaffTypeV2IndexToolbar />
        </Grid>

        <Grid item xs={12}>
          <StaffTypeV2List />
        </Grid>
      </Grid>

      {openCreateEditPopup && (
        <StaffTypeV2CUForm />
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

export default memo(observer(StaffTypeV2Index));
