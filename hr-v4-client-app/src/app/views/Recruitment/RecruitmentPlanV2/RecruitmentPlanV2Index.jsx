import { Grid } from "@material-ui/core";
import { DoneAll, GroupWork, ThumbDown } from "@material-ui/icons";
import CheckBoxIcon from '@material-ui/icons/CheckBox';
import CloseIcon from '@material-ui/icons/Close';
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import TabsComponent from "app/common/Tab/TabComponent";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import RecruitmentPlanListUpdatePopup from "./RecruitmentPlanListUpdatePopup";
import RecruitmentPlanV2Form from "./RecruitmentPlanV2Form";
import RecruitmentPlanV2List from "./RecruitmentPlanV2List";
import RecruitmentPlanV2Toolbar from "./RecruitmentPlanV2Toolbar";
import EvaluationCandiateRoundPopup from "../RecruitmentCU/EvaluationCandidateRound/EvaluationCandiateRoundPopup";

const tabList = [
  { icon: <GroupWork fontSize="small" />, label: "Tất cả" },
  { icon: <CloseIcon fontSize="small" />, label: "Chưa phê duyệt" },
  { icon: <DoneAll fontSize="small" />, label: "Đã phê duyệt" },
  { icon: <ThumbDown fontSize="small" />, label: "Đã từ chối" },
  { icon: <CheckBoxIcon fontSize="small" />, label: "Đã hoàn thành" },
];

function RecruitmentPlanV2Index() {
  const { recruitmentPlanStore } = useStore();
  const { t } = useTranslation();

  const {
    pagingRecruitmentPlan,
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    resetStore,
    handleChangePagingStatus,
    searchObject,
    openCreateEditPopup,
    setPageIndex,
    handleSelectListDelete,
    openConfirmUpdateStatusPopup,
    
  } = recruitmentPlanStore;

  const { evaluationCandidateRoundStore } = useStore();
  const {
      openFormEvaluationCandidateRound,
  } = evaluationCandidateRoundStore;


  useEffect(() => {
    pagingRecruitmentPlan();

    return resetStore;
  }, []);

  async function handleChangeTabIndex(tabIndex) {
    handleChangePagingStatus(tabIndex);
    handleSelectListDelete([]);
    await setPageIndex(1);
  }

  return (
    <div className="content-index">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.recruitment.title") },
            { name: t("navigation.recruitment.plan") },
          ]}
        />
      </div>
      <Grid container spacing={2}>
        <Grid item xs={12} className="index-card">
          <RecruitmentPlanV2Toolbar />
        </Grid>

        <Grid item xs={12} className="index-card">
          {/* <TabsComponent
            value={searchObject?.status}
            handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
            tabList={tabList}
          /> */}

          <RecruitmentPlanV2List />
        </Grid>
      </Grid>
      {openCreateEditPopup && (
        <RecruitmentPlanV2Form />
      )}

      {openFormEvaluationCandidateRound && <EvaluationCandiateRoundPopup />}

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

      {openConfirmUpdateStatusPopup && (
        <RecruitmentPlanListUpdatePopup />
      )}
    </div>
  );
}

export default memo(observer(RecruitmentPlanV2Index));
