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

const CandidateWorkingTab2 = () => {
    const { t } = useTranslation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const [anchorEl, setAnchorEl] = React.useState(null);

  const open = Boolean(anchorEl);
  const id = open ? "simple-popper" : undefined;
  const handleClick = (event) => {
    setAnchorEl(anchorEl ? null : event.currentTarget);
  };


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

export default CandidateWorkingTab2;
