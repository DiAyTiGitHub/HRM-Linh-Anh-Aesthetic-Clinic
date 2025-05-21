import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Button, ButtonGroup, Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import TabsComponent from "app/common/Tab/TabComponent";
import CandidatesInRecruitmentToolbar from "./CandidatesInRecruitmentToolbar";
import CIRRecruitmentInfoSummary from "./RecruitmentInfoSummary/CIRRecruitmentInfoSummary";
import TabAccordion from "app/common/Accordion/TabAccordion";
import { candidateTabList } from "../Candidate/CandidateIndex";
import CandidateList from "../Candidate/CandidateList";
import CandidateConfirmApprovePopup from "../Candidate/CandidatePopup/CandidateConfirmApprovePopup";
import CandidateConfirmRejectPopup from "../Candidate/CandidatePopup/CandidateConfirmRejectPopup";
import { useHistory, useParams } from 'react-router-dom/cjs/react-router-dom'
import CIRRecruitmentRoundSummary from "./RecruitmentInfoSummary/CIRRecruitmentRoundSummary";
import ArrowBackIcon from '@material-ui/icons/ArrowBack';
import RateReviewIcon from '@material-ui/icons/RateReview';
import HourglassEmptyIcon from '@material-ui/icons/HourglassEmpty';

function CandidatesInRecruitmentIndex() {
  const { t } = useTranslation();
  const history = useHistory();

  const {
    candidateStore,
    recruitmentStore
  } = useStore();

  const {
    selectedRecruitment,
  } = recruitmentStore;

  const {
    pagingCandidates,
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    searchObject,
    resetStore,
    handleChangeApprovalStatus,
    setPageIndex,
    handleSelectListDelete,
    openApprovePopup,
    openRejectPopup,
    openResetApprovalStatus,
    handleConfirmResetApprovalStatus,
    listOnDelete,
    handleSetSearchObject
  } = candidateStore;


  const { recruitmentId } = useParams();

  useEffect(() => {
    if (recruitmentId) {
      const payloadSO = {
        ...searchObject,
        recruitmentId: recruitmentId
      };
      handleSetSearchObject(payloadSO);

      pagingCandidates();
    }

    return resetStore;
  }, [recruitmentId]);

  async function handleChangeTabIndex(tabIndex) {
    handleChangeApprovalStatus(tabIndex);
    handleSelectListDelete([]);
    await setPageIndex(1);
  }


  return (
    <div className="content-index">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb routeSegments={[
          { name: t("navigation.recruitment.title") },
          { name: selectedRecruitment?.id ? "Đợt tuyển dụng " + selectedRecruitment?.code : "Đợt tuyển dụng mới" },
          { name: "Danh sách ứng tuyển" }
        ]} />

      </div>

      <Grid container spacing={2}>

        <Grid item xs={12} className="index-card mb-8">
          <ButtonGroup
            color="container"
            aria-label="outlined primary button group"
          >
            <Button
              type="button"
              onClick={() => history.goBack()}
            >
              <ArrowBackIcon className="mr-6" />
              Quay lại
            </Button>

            {
              selectedRecruitment?.id && (
                <Button
                  type="button"
                  onClick={() => history.push("/recruitment/" + selectedRecruitment?.id)}
                >
                  <RateReviewIcon className="mr-6" />
                  Cập nhật thông tin đợt tuyển
                </Button>
              )
            }

            {
              selectedRecruitment?.id && (
                <Button
                  type="button"
                  onClick={() => history.push("/recruitment-process/" + selectedRecruitment?.id)}
                >
                  <HourglassEmptyIcon className="mr-6" />
                  Quá trình tuyển dụng
                </Button>
              )
            }

          </ButtonGroup>
        </Grid>

        <Grid item xs={12} className="p-0">
          <TabAccordion
            className="pb-0 mb-0"
            title='Thông tin đợt tuyển dụng'
          >
            <CIRRecruitmentInfoSummary />
          </TabAccordion>
        </Grid>

        <Grid item xs={12} className="p-0">
          <TabAccordion
            className="pb-0 mb-0"
            title='Danh sách vòng thi tuyển'
          >
            <CIRRecruitmentRoundSummary />
          </TabAccordion>
        </Grid>

        <Grid item xs={12} className="index-card">
          <CandidatesInRecruitmentToolbar />
        </Grid>

        <Grid item xs={12} className="index-card">
          <TabsComponent
            value={searchObject?.status}
            handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
            tabList={candidateTabList}
          />

          <CandidateList
            isFromCandidatesInRecruitment={true}
          />
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
      }

      {
        openResetApprovalStatus && (
          <GlobitsConfirmationDialog
            open={openResetApprovalStatus}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmResetApprovalStatus}
            title={"XÁC NHẬN CÀI LẠI TRẠNG THÁI"}
            text={<>
              Bạn có chắc muốn cập nhật trạng thái hồ sơ của ứng viên {` `}
              <span className="text-red">
                {listOnDelete?.map(function (candidate) {
                  return candidate?.displayName;
                }).join(", ")}
              </span> thành
              <span className="text-red">
                {` `} Chưa phê duyệt
              </span>
              ?
            </>}
            agree={t("confirm_dialog.delete.agree")}
            cancel={t("confirm_dialog.delete.cancel")}
          />
        )
      }

      {openApprovePopup && (
        <CandidateConfirmApprovePopup />
      )}

      {openRejectPopup && (
        <CandidateConfirmRejectPopup />
      )}


    </div >
  );
}

export default memo(observer(CandidatesInRecruitmentIndex));