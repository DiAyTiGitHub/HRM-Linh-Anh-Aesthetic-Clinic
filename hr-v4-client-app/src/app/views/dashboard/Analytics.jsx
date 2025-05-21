import React, { Component } from "react";
import { Breadcrumb } from "egret";

import { withStyles } from "@material-ui/styles";

class Analytics extends Component {
  render() {
    const { t } = this.props;
    return (
      <div className="analytics m-sm-30">
        <div className="mb-sm-30">
          <Breadcrumb
            routeSegments={[
              { name: "Dashboard", path: "/dashboard" },
              { name: t("Dashboard.analytics.title") },
            ]}
          />
        </div>
        {/* {(org.level == 1) && <Grid container spacing={3}>
          {org.manager && <ManagerDashboard t={t} />}
          <Grid item lg={12} md={12} sm={12} xs={12} className="div-parent-chart">


          </Grid>

        </Grid>}
        {(org.level != 1) && <Grid container spacing={3}>
          {org.manager && <ManagerDashboard t={t} />}
          {org.checking && <CheckingDashboard t={t} />}
          {org.confirmation && <ConfirmationLab t={t} />}
          {org.screening && <ScreeningDashboard t={t} />}
          <Grid item lg={12} md={12} sm={12} xs={12} className="div-parent-chart">


          </Grid>

        </Grid>} */}
      </div>
    );
  }
}

export default withStyles({}, { withTheme: true })(Analytics);
