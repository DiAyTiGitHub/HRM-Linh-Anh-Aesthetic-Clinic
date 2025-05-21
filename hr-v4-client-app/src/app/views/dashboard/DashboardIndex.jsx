import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "../../stores";
import { Grid, makeStyles } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { toast } from "react-toastify";
import { Link } from "react-router-dom";
import SupervisorAccountIcon from "@material-ui/icons/SupervisorAccount";
import AdminView from "./AdminView";
import UserView from "./UserView";
import { Button } from "@material-ui/core";
import ListIcon from "@material-ui/icons/List";
import StarIcon from "@material-ui/icons/Star";
import BorderColorIcon from "@material-ui/icons/BorderColor";
import { useTranslation } from "react-i18next";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

const useStyles = makeStyles((theme) => ({
  root: {
    top: "10px",
  },
  card: {
    background: "#ffffff",
    padding: "1.25rem",
  },
  cardBody: {
    display: "flex",
    minHeight: "1px",
  },
  cardInfo: {
    marginLeft: "auto",
    fontSize: "30px",
  },
  cardDevider: {
    height: "6px",
    borderRadius: "0.25rem",
  },
  cardTotal: {
    height: "100%",
    border: "none",
    borderRadius: "0px",
    margin: "0 4px",
  },
}));

function DashboardIndex(props) {
  // const history = useHistory();

  const { dashboardStore } = useStore();
  const { t } = useTranslation();
  const classes = useStyles();

  const {
    isAdmin,
    isUser,
    staffNumber,
    monthTaskNumber,
    projectNumber,
    updatePageData,
  } = dashboardStore;

  useEffect(() => {
    updatePageData();
  }, [updatePageData]);

  return (
    <div className="content-index p-10">
      <GlobitsBreadcrumb routeSegments={[{ name: t("navigation.dashboard.statisticsGeneral") }]} />

      <Grid container spacing={2}>
        <Grid item md={3} xs={12}>
          <Link to="/staff/all">
            <div
              className={classes.card}
              style={{
                boxShadow:
                  "rgb(0 0 0 / 6%) -1px -1px 4px 0px, rgb(0 0 0 / 6%) 4px 4px 4px 2px, rgb(0 0 0 / 6%) 6px 6px 6px 2px",
              }}
            >
              <div className="text-center">
                <div className={classes.cardInfo}>
                  <Button className="rounded-10"
                    style={{
                      backgroundColor: "#E6F3E6",
                      height: "64px",
                      width: "64px",
                    }}
                  >
                    <SupervisorAccountIcon className="w-auto"
                      style={{ fill: "#78B558" }}
                      fontSize="large"
                    />
                  </Button>
                </div>
              </div>
              <div className="text-center">
                <div className={classes.cardInfo}>
                  <span className="text-limegreen">{staffNumber}</span>
                </div>
              </div>
              <div className={classes.cardContent}>
                <p className="text-limegreen text-center font-size-20 m-0">
                  Tổng số nhân viên
                </p>
              </div>
              {/* <div className={`${classes.cardDevider} bgc-limegreen`} /> */}
            </div>
          </Link>
        </Grid>

        <Grid item md={3} xs={12}>
          <Link to="/timesheet/project">
            <div
              className={classes.card}
              style={{
                boxShadow:
                  "rgb(0 0 0 / 6%) -1px -1px 4px 0px, rgb(0 0 0 / 6%) 4px 4px 4px 2px, rgb(0 0 0 / 6%) 6px 6px 6px 2px",
              }}
            >
              <div className="text-center">
                <div className={classes.cardInfo}>
                  <Button className="rounded-10"
                    style={{
                      backgroundColor: "#FAECF2",
                      height: "64px",
                      width: "64px",
                    }}
                  >
                    <ListIcon
                      className="w-auto"
                      style={{ fill: "#DB6296" }}
                      fontSize="large"
                    />
                  </Button>
                </div>
              </div>
              <div className="text-center">
                <div className={classes.cardInfo}>
                  <span className="text-pink">{projectNumber}</span>
                </div>
              </div>
              <div className={classes.cardContent}>
                <p className="text-pink text-center font-size-20 m-0">
                  Tổng dự án
                </p>
              </div>
              {/* <div className={`${classes.cardDevider} bgc-pink`} /> */}
            </div>
          </Link>
        </Grid>

        <Grid item md={3} xs={12}>
          <Link to="/timesheetDetails/list">
            <div
              className={classes.card}
              style={{
                boxShadow:
                  "rgb(0 0 0 / 6%) -1px -1px 4px 0px, rgb(0 0 0 / 6%) 4px 4px 4px 2px, rgb(0 0 0 / 6%) 6px 6px 6px 2px",
              }}
            >
              <div className="text-center">
                <div className={classes.cardInfo}>
                  <Button className="rounded-10"
                    style={{
                      backgroundColor: "#E9F4FC",
                      height: "64px",
                      width: "64px",
                    }}
                  >
                    <BorderColorIcon className="w-auto"
                      style={{ fill: "#4D93CE" }}
                      fontSize="large"
                    />
                  </Button>
                </div>
              </div>
              <div className="text-center">
                <div className={classes.cardInfo}>
                  <span className="text-ocean">{monthTaskNumber}</span>
                </div>
              </div>
              <div className={classes.cardContent}>
                <p className="text-ocean text-center font-size-20 m-0"                >
                  Đầu việc trong tháng
                </p>
              </div>
              {/* <div className={`${classes.cardDevider} bgc-ocean`} /> */}
            </div>
          </Link>
        </Grid>

        <Grid item md={3} xs={12}>
          <Link to="/timesheetDetails/list">
            <div
              className={classes.card}
              style={{
                boxShadow:
                  "rgb(0 0 0 / 6%) -1px -1px 4px 0px, rgb(0 0 0 / 6%) 4px 4px 4px 2px, rgb(0 0 0 / 6%) 6px 6px 6px 2px",
              }}
            >
              <div className="text-center">
                <div className={classes.cardInfo}>
                  <Button className="rounded-10"
                    style={{
                      backgroundColor: "#FCF8DF",
                      height: "64px",
                      width: "64px",
                    }}
                  >
                    <StarIcon className="w-auto"
                      style={{ fill: "#F5DD47" }}
                      fontSize="large"
                    />
                  </Button>
                </div>
              </div>
              <div className="text-center">
                <div className={classes.cardInfo}>
                  <span className="text-orchid">{projectNumber}</span>
                </div>
              </div>
              <div className={classes.cardContent}>
                <p className="text-orchid text-center font-size-20 m-0">
                  Tổng lương trong tháng
                </p>
              </div>
              {/* <div className={`${classes.cardDevider} `} /> */}
            </div>
          </Link>
        </Grid>

        {/* Admin view dashboard is temporary death for unknow reason */}
        <Grid item xs={12}>
          {isAdmin && <AdminView />}
        </Grid>

        <Grid item xs={12}>
          {isUser && <UserView />}
        </Grid>
      </Grid>
    </div>
  );
}

export default memo(observer(DashboardIndex));
