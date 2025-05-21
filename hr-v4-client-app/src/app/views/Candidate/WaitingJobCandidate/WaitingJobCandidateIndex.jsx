import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import WaitingJobCandidateList from "./WaitingJobCandidateList";
import WaitingJobCandidateIndexToolbar from "./WaitingJobCandidateIndexToolbar";
import WaitingJobCandidateReceivePopup from "./WaitingJobCandidatePopup/WaitingJobCandidateReceivePopup";
import WaitingJobCandidateNotComePopup from "./WaitingJobCandidatePopup/WaitingJobCandidateNotComePopup";

function WaitingJobCandidateIndex() {
  const { waitingJobCandidateStore } = useStore();
  const { t } = useTranslation();

  const {
    pagingWaitingJobCandidates,
    searchObject,
    resetStore,
    openReceiveJobPopup,
    openNotComeToReceivePopup
  } = waitingJobCandidateStore;

  useEffect(() => {
    pagingWaitingJobCandidates();

    return resetStore;
  }, []);

  return (
    <div className="content-index">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: "Nhân viên" },
            { name: "Tiếp nhận ứng viên" },
            { name: "Ứng viên chờ nhận việc" }
          ]} />
      </div>

      <Grid container spacing={2}>
        <Grid item xs={12} className="index-card">
          <WaitingJobCandidateIndexToolbar />
        </Grid>

        <Grid item xs={12} className="index-card">
          <WaitingJobCandidateList />
        </Grid>
      </Grid>

      {openReceiveJobPopup && (
        <WaitingJobCandidateReceivePopup />
      )}

      {openNotComeToReceivePopup && (
        <WaitingJobCandidateNotComePopup />
      )}

    </div >
  );
}

export default memo(observer(WaitingJobCandidateIndex));