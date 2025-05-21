import { Button, ButtonGroup, Grid, useMediaQuery, useTheme } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import React from "react";
import { useTranslation } from "react-i18next";
import ListContractList from "./ListContractList";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import DashboardIcon from '@material-ui/icons/Dashboard';
import PublishIcon from "@material-ui/icons/Publish";
import AutorenewIcon from '@material-ui/icons/Autorenew';


const ListContractIndex = () => {
  const { t } = useTranslation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const [anchorEl, setAnchorEl] = React.useState(null);

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.personnel.contractManagement") },
            { name: t("navigation.personnel.listContract") },
          ]}
        />
      </div>

      <Grid className="" container spacing={2}>
        <Grid item lg={6} md={6} sm={4} xs={3}>
          <ButtonGroup
            color="container"
            aria-label="outlined primary button group"
          >
            <Button
              startIcon={<AutorenewIcon />}
              onClick={() => {
              }}
            >
              {!isMobile && "Chuyển thành chính thức"}
            </Button>
            
          </ButtonGroup>
        </Grid>

        <Grid item lg={6} md={6} >
          <GlobitsSearchInput search={""} t={t} />
        </Grid>

        <Grid item xs={12}>
          <ListContractList />
        </Grid>
      </Grid>
    </div>
  );
};

export default ListContractIndex;
