import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { useStore } from "../../stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import AllowancePolicyIndexToolbar from "./AllowancePolicyIndexToolbar";
import AllowancePolicyList from "./AllowancePolicyList";
import AllowancePolicyCUForm from "./AllowancePolicyCUForm";

export default observer(function AllowancePolicyIndex() {
  const { allowancePolicyStore } = useStore();
  const { t } = useTranslation();

  const {
    pagingAllowancePolicy,
    openCreateEditPopup,
    openConfirmDeletePopup,
    openConfirmDeleteListPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    resetStore
  } = allowancePolicyStore;

  useEffect(() => {
    pagingAllowancePolicy();
    return resetStore;
  }, []);

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb routeSegments={[
          { name: t("navigation.salary") },
          { name: t("navigation.allowancePolicy.title") }
        ]} />
      </div>

      <Grid className="index-card" container spacing={2}>
        <Grid item xs={12}>
          <AllowancePolicyIndexToolbar />
        </Grid>

        <Grid item xs={12}>
          <AllowancePolicyList />
        </Grid>
      </Grid>

      {openCreateEditPopup && (
        <AllowancePolicyCUForm />
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
