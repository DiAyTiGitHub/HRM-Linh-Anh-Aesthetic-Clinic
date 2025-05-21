import React, { useEffect, useState } from "react";
import {
  Grid,
  makeStyles,
  Button,
  Box,
  AppBar,
  Tab,
  Tabs,
} from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { useStore } from "../../stores";
import { useParams } from "react-router";
import PropTypes from "prop-types";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import ConstantList from "../../appConfig";
import { useHistory } from "react-router-dom";
import KeyboardReturnIcon from "@material-ui/icons/KeyboardReturn";
import "./Project.scss";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import ProjectActivityInProjectIndex from "./ProjectActivity/ProjectActivityInProjectIndex";
import ProjectInfomation from "./ProjectInfomation";
import ListTaskByActivity from "./ProjectActivity/ListTaskByActivity";

const useStyles = makeStyles((theme) => ({
  root: {
    background: "#fff",
    padding: "8px",
    borderRadius: "5px",
  },
  groupContainer: {
    width: "100%",
    "& .MuiOutlinedInput-root": {
      borderRadius: "0!important",
    },
  },
  tableContainer: {
    marginTop: "16px",
    overflowX: "auto",
    overflowY: "hidden",
    "& .MuiTableCell-body": {
      border: "1px solid #e9ecef",
      padding: 0,
    },
    "& .MuiTableCell-head": {
      minWidth: "150px",
      padding: "10px",
      border: "1px solid #e9ecef",
    },
  },
  tableHeader: {
    "& >table": {
      width: "100%",
      borderBottom: "1px solid #E3F2FD",
      marginBottom: "8px",
      "padding-right": "27px",
      "& th": {
        width: "calc(100vw / 4)",
      },
    },
  },
}));
function TabPanel(props) {
  const { children, value, index, ...other } = props;
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`scrollable-force-tabpanel-${index}`}
      aria-labelledby={`scrollable-force-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box p={3}>
          <div>{children}</div>
        </Box>
      )}
    </div>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.any.isRequired,
  value: PropTypes.any.isRequired,
};

export default observer(function ProjectDetail() {
  const classes = useStyles();
  const { t } = useTranslation();
  const { id } = useParams();
  const history = useHistory();
  const [tabIndexActivity, setTabIndexActivity] = useState(0);

  const { projectStore } = useStore();

  const {
    handleClosePopup,
    getDataProject,
    dataEditProject,
    openPopupConfirmDelete,
    handleConfirmDeleteActivity,
    shouldOpenTaskListByActivity,
    
  } = projectStore;

  useEffect(() => {
    getDataProject(id !== "create" ? id : null);
  }, [getDataProject, id]);

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb routeSegments={[{ name: t("project.title") }]} />
      </div>
      <Grid container className={`index-card tab ${classes.root}`}>
        <Grid className="tab-item" item xs={12}>
          <h3
            style={{
              marginTop: "5px",
              marginBottom: "15px",
              textTransform: "uppercase",
              display: "inline-block",
              marginLeft: "10px",
            }}
          >
            {dataEditProject?.id ? dataEditProject?.name : "Thêm mới dự án"}
          </h3>
          <Button
            className="mr-16 btn btn-info d-inline-flex"
            startIcon={<KeyboardReturnIcon />}
            variant="contained"
            style={{ float: "right" }}
            onClick={() =>
              history.push(ConstantList.ROOT_PATH + `timesheet/project`)
            }
          >
            {t("general.button.back")}
          </Button>
        </Grid>

        {/* <ProjectTab /> */}
        <Grid className="tab-item" item xs={12}>
          <div className="tab-container">
            <Grid item xs={12} className={classes.root}>
              <div value={tabIndexActivity} index={0}>
                <AppBar
                  className={classes.tabHeader}
                  position="static"
                  color="#ffff"
                >
                  <Tabs
                    orientation="horizontal"
                    value={tabIndexActivity}
                    onChange={(event, newValue) =>
                      setTabIndexActivity(newValue)
                    }
                    variant="scrollable"
                    scrollButtons="on"
                    aria-label="staff tabs scrollable"
                    classes={{ indicator: classes.indicator }}
                  >
                    <Tab className="tabStaff" label={t("task.info")} />
                    {id !== "create" && (
                      <Tab className="tabStaff" label={t("task.acitivity")} />
                    )}
                  </Tabs>
                </AppBar>

                <div
                  className="dialog-body"
                  style={{
                    borderRadius: "unset",
                    boxShadow:
                      "0px 2px 4px -1px rgb(0 0 0 / 6%), 0px 4px 5px 0px rgb(0 0 0 / 4%), 0px 1px 10px 0px rgb(0 0 0 / 4%)",
                  }}
                >
                  <TabPanel
                    value={tabIndexActivity}
                    index={0}
                    style={{ height: "auto" }}
                    color="#ffffff"
                  >
                    <ProjectInfomation />
                  </TabPanel>

                  <TabPanel
                    value={tabIndexActivity}
                    index={1}
                    style={{ height: "auto" }}
                    color="#ffffff"
                  >
                    <ProjectActivityInProjectIndex />
                  </TabPanel>
                </div>
              </div>

              {shouldOpenTaskListByActivity && <ListTaskByActivity />}
            </Grid>
          </div>
        </Grid>

        <GlobitsConfirmationDialog
          open={openPopupConfirmDelete}
          onConfirmDialogClose={handleClosePopup}
          onYesClick={handleConfirmDeleteActivity}
          title={t("confirm_dialog.delete.title")}
          text={t("confirm_dialog.delete.text")}
          agree={t("confirm_dialog.delete.agree")}
          cancel={t("confirm_dialog.delete.cancel")}
        />
      </Grid>
    </div>
  );
});
