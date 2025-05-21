import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import "@asseinfo/react-kanban/dist/styles.css";
import "./_task.scss";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import PropTypes from "prop-types";
import { Tabs, Box, Tab, AppBar, Grid, makeStyles } from "@material-ui/core";
import GridOnIcon from "@material-ui/icons/GridOn";
import LibraryBooksIcon from "@material-ui/icons/LibraryBooks";
import ByProject from "./Tabs/ByProject";
import TaskTable from "./ViewTask/Table/TaskTable";
import TaskKanban from "./ViewTask/Kanban/TaskKanban";
import TaskGroup from "./ViewTask/Group/TaskGroup";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useParams } from "react-router";
import history from "../../../history";
import { Form, Formik } from "formik";
import ProjectPopupTask from "./TaskPopup/ProjectPopupTask";
import TimelineIcon from '@material-ui/icons/Timeline';
import TaskNewForm from "./TaskNewForm";
import TaskDetailPopup from "./TaskDetail/TaskDetailPopup";
import ChooseMultipleProjectsPopover from "./TaskPopup/ChooseMultipleProject/ChooseMultipleProjectsPopover";
import TaskFilterV4 from "./ViewTask/TaskFilterV4/TaskFilterV4";
import DashboardIcon from '@material-ui/icons/Dashboard';

const useStyles = makeStyles(theme => ({
  customTabIndicator: {
    backgroundColor: "#2f4f4f"
  }
}));

function TaskIndex() {
  const { id } = useParams();
  const { t } = useTranslation();
  const { taskStore } = useStore();

  const {
    tabIndexTask: tabIndex,
    handleChangeTabIndexTask,
    openNewPopup,
    getOnViewProjects,
  } = taskStore;

  const classes = useStyles();

  const initialProjectValues = {
    keyword: null,
    projectList: getOnViewProjects()
  };

  return (
    <>
      <div value={tabIndex} index={0} className="p-8"
        style={{
          marginBottom: "320px"
        }}
      >
        <div className="index-breadcrumb">
          <Formik
            enableReinitialize
            initialValues={initialProjectValues}
            // onSubmit={handleChooseViewingProjects}
            //NOT CATCHED IN ONSUBMIT ACTION OF FORM
            onSubmit={(values) => console.log("on save values: ", values)}
          >
            {({ setFieldValue }) => (
              <Form autoComplete="off" className="project-breadcrumb d-flex">
                {/* <ProjectPopupTask
                  itemProjectDefault
                  title="Công việc trong dự án: "
                  onChangeProject={handleChangeProject}
                /> */}

                <Grid container spacing={2}>
                  <Grid item xs={12} md={8} lg={10}>
                    <ChooseMultipleProjectsPopover />
                  </Grid>

                  <Grid item xs={12} md={4} lg={2}>
                    <GlobitsBreadcrumb
                      noRight
                      // routeSegments={[{ name: screenPath ? "Công việc trong dự án: " + screenPath : "Chưa chọn dự án" }]}
                      routeSegments={[{ name: "Công việc" }, { name: "Phần việc" }]}
                    />
                  </Grid>
                </Grid>
              </Form>
            )}
          </Formik>
        </div>

        <TaskFilterV4 />

        <AppBar position="static" color="#ffff" className="taskAppBar">
          <Tabs
            orientation="horizontal"
            value={tabIndex}
            onChange={(_, newValue) => handleChangeTabIndexTask(newValue)}
            variant="scrollable"
            scrollButtons="on"
            aria-label="staff tabs scrollable"

            classes={{
              indicator: classes.customTabIndicator
            }}
          >
            <Tab
              className="tabStaff text-light-blue"
              icon={<GridOnIcon fontSize="small" className="mr-4 text-light-blue" />}
              label={t("task.table")}
            />
            <Tab
              className="tabStaff text-light-blue"
              icon={<DashboardIcon fontSize="small" className="mr-4 text-light-blue" />}
              label={("Kanban")}
            />

            {/* <Tab className="tabStaff" icon={<LibraryBooksIcon fontSize="small" className="mr-4"/>} label={t("task.byPriority")} /> */}
          </Tabs>

          {/* {tabIndex == 0 && (
            // <TableFilterV2 />
            <TableFilterV3 />
          )}

          {tabIndex == 1 && (
            // <KanbanFilter />
            <KanbanFilterV3 />
          )} */}
        </AppBar>

        <div className="dialog-body" style={{ borderRadius: "unset" }}>
          <TabPanel
            value={tabIndex}
            index={0}
            style={{ height: "auto" }}
            color="#ffffff"
          >
            <TaskTable />
          </TabPanel>

          <TabPanel
            value={tabIndex}
            index={1}
            style={{ height: "auto" }}
            color="#ffffff"
            className="tabPanelNoPaddingOutline"
          >
            <TaskKanban />
          </TabPanel>

          <TabPanel
            value={tabIndex}
            index={2}
            style={{ height: "auto" }}
            color="#ffffff"
          >
            <TaskGroup />
          </TabPanel>

          <TabPanel
            value={tabIndex}
            index={3}
            style={{ height: "auto" }}
            color="#ffffff"
          >
            <ByProject />
          </TabPanel>
        </div>
      </div>

      {/* old task form written by someone */}
      {/* <TaskNewForm /> */}

      {/* new task detail written by diayti */}
      {openNewPopup && (
        <TaskDetailPopup />
      )}
    </>
  );
}

export default memo(observer(TaskIndex));



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
        <Box p={1}>
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