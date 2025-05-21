
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import WorkplaceCUForm from "./WorkplaceCUForm";
import WorkplaceList from "./WorkplaceList";
import WorkplaceToolbar from "./WorkplaceToolbar";
import { Grid } from "@material-ui/core";

function WorkplaceIndex() {
  const { workplaceStore } = useStore();
  const { t } = useTranslation();

  const {
    pagingWorkplace,
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    openCreateEditPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    resetStore,
  } = workplaceStore;

  useEffect(() => {
    pagingWorkplace();
    return resetStore;
  }, []);

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb routeSegments={[{ name: "Cơ cấu tổ chức" }, { name: t("navigation.workplace.title") }]} />
      </div>
      <Grid className="index-card" container spacing={2}>
        <Grid item xs={12}>
          <WorkplaceToolbar />
        </Grid>
        <Grid item xs={12}>
          <WorkplaceList />
        </Grid>
      </Grid>

      {openCreateEditPopup && (
        <WorkplaceCUForm />
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
          onConfirmDialogClose={() => {
            pagingWorkplace();
            handleClose();
          }}
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

export default memo(observer(WorkplaceIndex));
