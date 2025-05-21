import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import CandidateRecruitmentRoundCUForm from "./CandidateRecruitmentRoundCUForm";
import CandidateRecruitmentRoundToolbar from "./CandidateRecruitmentRoundToolbar";
import CandidateRecruitmentRoundList from "./CandidateRecruitmentRoundList";
import CRRUpdateResultPopup from "./CandidateRecruitmentRoundPopup/CRRUpdateResultPopup";
import CRRMoveToNextRoundPopup from "./CandidateRecruitmentRoundPopup/CRRMoveToNextRoundPopup";

function CandidateRecruitmentRoundIndex() {
  const { t } = useTranslation();

  const {
    candidateRecruitmentRoundStore,
    recruitmentStore
  } = useStore();

  const {
    selectedRecruitment
  } = recruitmentStore;

  const {
    pagingCandidateRecruitmentRound,
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    openCreateEditPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    resetStore,

    searchObject,
    handleSetSearchObject,

    openMoveToNextRoundPopup,
    openUpdateResultPopup

  } = candidateRecruitmentRoundStore;

  useEffect(function () {

    // pagingCandidateRecruitmentRound();


    return resetStore;

  }, [selectedRecruitment?.id]);

  return (
    <>
      <Grid container spacing={2} className="index-card">
        <Grid item xs={12} >
          <CandidateRecruitmentRoundToolbar />
        </Grid>

        <Grid item xs={12}>
          <CandidateRecruitmentRoundList />
        </Grid>
      </Grid>

      {openCreateEditPopup && (
        <CandidateRecruitmentRoundCUForm />
      )}

      {openUpdateResultPopup && (
        <CRRUpdateResultPopup />
      )}

      {openMoveToNextRoundPopup && (
        <CRRMoveToNextRoundPopup />
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
    </>
  );
}

export default memo(observer(CandidateRecruitmentRoundIndex));
