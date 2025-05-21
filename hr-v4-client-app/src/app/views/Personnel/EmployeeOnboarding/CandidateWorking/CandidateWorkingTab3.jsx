import { Button, ButtonGroup, Grid, Popover, useMediaQuery, useTheme } from "@material-ui/core";
import React from "react";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import DashboardIcon from '@material-ui/icons/Dashboard';
import PublishIcon from "@material-ui/icons/Publish";
import AutorenewIcon from '@material-ui/icons/Autorenew';
import { useTranslation } from "react-i18next";
import CandidateWorkingList from "./CandidateWorkingList";
import PersonAddDisabledIcon from '@material-ui/icons/PersonAddDisabled';
import GlobitsSearchInput from "app/common/GlobitsSearchInput";

const CandidateWorkingTab3 = () => {
    const { t } = useTranslation();
  const theme = useTheme();

  return (
    <div>
      <Grid className="" container spacing={2}>
        <Grid item lg={6} md={6} sm={4} xs={3}>
        </Grid>
        <Grid item lg={6} md={6} sm={8} xs={9} className="mb-10">
          <GlobitsSearchInput search={""} t={t} />
        </Grid>
        <Grid item xs={12}>
          <CandidateWorkingList />
        </Grid>
      </Grid>
    </div>
  );
};

export default CandidateWorkingTab3;
