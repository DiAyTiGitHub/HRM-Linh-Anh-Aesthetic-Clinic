import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import NotComeCandidateList from "./OnboaredCandidateList";
import OnboaredCandidateIndexToolbar from "./OnboaredCandidateToolbar";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";

function OnboaredCandidateIndex() {
  const { onboaredCandidateStore } = useStore();
  const { t } = useTranslation();

  const {
    pagingOnboardedCandidates,
    searchObject,
    resetStore,
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    handleClose,
    handleConfirmDeleteList,
    handleConfirmDelete,
    openResignListPopup,
    handleOpenResignListPopup,
    handleResignMultiple
  } = onboaredCandidateStore;

  useEffect(() => {
    pagingOnboardedCandidates();

    return resetStore;
  }, []);

  return (
    <div className="content-index">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: "Nhân viên" },
            { name: "Tiếp nhận ứng viên" },
            { name: "Ứng viên đã nhận việc" }
          ]} />
      </div>

      <Grid container spacing={2}>
        <Grid item xs={12} className="index-card">
          <OnboaredCandidateIndexToolbar />
        </Grid>

        <Grid item xs={12} className="index-card">
          <NotComeCandidateList />
        </Grid>
      </Grid>

      {
        openConfirmDeletePopup && (
          <GlobitsConfirmationDialog
            open={openConfirmDeletePopup}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmDelete}
            title={t("confirm_dialog.delete.title")}
            text={t("confirm_dialog.delete.text")}
            agree={t("confirm_dialog.delete.agree")}
            cancel={t("confirm_dialog.delete.cancel")}
          />
        )
      }

      {
        openConfirmDeleteListPopup && (
          <GlobitsConfirmationDialog
            open={openConfirmDeleteListPopup}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmDeleteList}
            title={t("confirm_dialog.delete_list.title")}
            text={t("confirm_dialog.delete_list.text")}
            agree={t("confirm_dialog.delete_list.agree")}
            cancel={t("confirm_dialog.delete_list.cancel")}
          />
        )
      },
      {
        openResignListPopup && (
          <GlobitsConfirmationDialog
            open={openResignListPopup}
            onConfirmDialogClose={()=>handleOpenResignListPopup(false)}
            onYesClick={handleResignMultiple}
            title={"Xác nhận nghỉ việc"}
            text={"Bạn có chắc chắn xác nhận nghỉ việc cho các hồ sơ đã chọn?"}
            agree={t("confirm_dialog.delete_list.agree")}
            cancel={t("confirm_dialog.delete_list.cancel")}
          />
        )
      }
    </div >
  );
}

export default memo(observer(OnboaredCandidateIndex));