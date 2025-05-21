import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Button, ButtonGroup, Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import TabAccordion from "app/common/Accordion/TabAccordion";
import { useHistory } from 'react-router-dom/cjs/react-router-dom'
import ArrowBackIcon from '@material-ui/icons/ArrowBack';
import RateReviewIcon from '@material-ui/icons/RateReview';
import CIRRecruitmentInfoSummary from "app/views/Candidate/CandidatesInRecruitment/RecruitmentInfoSummary/CIRRecruitmentInfoSummary";
import CIRRecruitmentRoundSummary from "app/views/Candidate/CandidatesInRecruitment/RecruitmentInfoSummary/CIRRecruitmentRoundSummary";
import RecruitmentProcessContainer from "./RecruitmentProcessContainer";
import CandidateRecruitmentRoundIndex from "app/views/Candidate/CandidateRecruitmentRound/CandidateRecruitmentRoundIndex";
import FormatListNumberedIcon from '@material-ui/icons/FormatListNumbered';

function RecruitmentProcessIndex() {
  const { t } = useTranslation();

  const history = useHistory();

  const { candidateRecruitmentRoundStore, recruitmentStore } = useStore();

  const {
    selectedRecruitment,
  } = recruitmentStore;


  return (
    <div className="content-index">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb routeSegments={[
          { name: t("navigation.recruitment.title") },
          { name: selectedRecruitment?.id ? "Đợt tuyển dụng " + selectedRecruitment?.code : "Đợt tuyển dụng mới" },
          { name: "Quá trình tuyển dụng" }
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
                  onClick={() => history.push("/candidates-in-recruitment/" + selectedRecruitment?.id)}
                >
                  <FormatListNumberedIcon className="mr-6" />
                  Danh sách ứng tuyển
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
          <RecruitmentProcessContainer />
        </Grid>

        <Grid item xs={12} className="index-card">
          <CandidateRecruitmentRoundIndex />
        </Grid>
      </Grid>

    </div>
  );
}

export default memo(observer(RecruitmentProcessIndex));
