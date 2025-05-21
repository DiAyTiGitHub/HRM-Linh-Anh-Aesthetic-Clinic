import React, { Component, Fragment } from "react";
import localStorageService from "app/services/localStorageService";
import { Hidden } from "@material-ui/core";
import AppContext from "app/appContext";
import Footer from "../SharedCompoents/Footer";
import Layout2Navbar from "./Layout2Navbar";
import Layout2Sidenav from "./Layout2Sidenav";
import Layout2Topbar from "./Layout2Topbar";
import { PropTypes } from "prop-types";
import { classList } from "utils";
import { connect } from "react-redux";
import { renderRoutes } from "react-router-config";
import { setLayoutSettings } from "app/redux/actions/LayoutActions";
import { withStyles } from "@material-ui/styles";

const styles = (theme) => {
  return {
    layout: {
      backgroundColor: theme.palette.background.default,
      color: theme.palette.text.primary,
    },
  };
};

class Layout2 extends Component {
  constructor(props) {
    super(props);
    this.state = {
      roles:
        localStorageService
          .getLoginUser()
          ?.roles?.map((item) => item.authority) || [],
    };
  }
  updateSidebarMode = (sidebarSettings) => {
    let { settings, setLayoutSettings } = this.props;
    setLayoutSettings({
      ...settings,
      layout2Settings: {
        ...settings.layout2Settings,
        leftSidebar: {
          ...settings.layout2Settings.leftSidebar,
          ...sidebarSettings,
        },
      },
    });
  };

  routesComponent = (routes) => {
    let newRoutes = [];
    routes.map((item, key) => {
      if (
        !item.auth ||
        this.state.roles.some((role) => item.auth.indexOf(role) !== -1)
      ) {
        if (item.children) {
          item.children.map((subItem, subKey) => {
            if (
              !subItem.auth ||
              this.state.roles.some((role) => subItem.auth.indexOf(role) !== -1)
            ) {
              newRoutes.push(subItem);
            }
            return undefined
          });
        } else {
          newRoutes.push(item);
        }
      }
      return undefined
    });

    return renderRoutes(newRoutes);
  };

  componentWillMount() {
    let user = localStorageService.getItem("auth_user");
    if (user && user.user) {
      this.setState({
        roles: user.user?.roles?.map((item) => item.authority) || [],
      });
    } else {
      this.setState({
        roles: user?.roles?.map((item) => item.authority) || [],
      });
    }
  }

  render() {
    let { settings, classes } = this.props;
    let { layout2Settings } = settings;

    let layoutClasses = {
      [classes.layout]: true,
      [settings.activeLayout]: true,
      [`sidenav-${layout2Settings.leftSidebar.mode}`]: true,
      [`layout-${layout2Settings.mode}`]: true,
    };
    return (
      <AppContext.Consumer>
        {({ routes }) => (
          <Fragment>
            <div className={classList(layoutClasses)}>
              {layout2Settings.topbar.show && <Layout2Topbar />}

              <Hidden smDown>
                {layout2Settings.navbar.show && <Layout2Navbar />}
              </Hidden>

              <Hidden mdUp>
                {layout2Settings.leftSidebar.show && <Layout2Sidenav />}
              </Hidden>

              {settings.perfectScrollbar && (
                <>
                  <div
                    options={{ suppressScrollX: true }}
                    className="scrollable-content p-0"
                  >
                    <div className="container p-0">
                      {this.routesComponent(routes)}
                    </div>
                    <div className="my-auto"></div>
                    <Footer />
                  </div>
                </>
              )}

              {!settings.perfectScrollbar && (
                <>
                  <div
                    options={{ suppressScrollX: true }}
                    className="scrollable-content p-0"
                  >
                    <div className="container p-0">
                      {this.routesComponent(routes)}
                    </div>
                    <div className="my-auto"></div>
                    <Footer />
                  </div>
                </>
              )}
            </div>
          </Fragment>
        )}
      </AppContext.Consumer>
    );
  }
}

Layout2.propTypes = {
  settings: PropTypes.object.isRequired,
};

const mapStateToProps = (state) => ({
  setLayoutSettings: PropTypes.func.isRequired,
  settings: state.layout.settings,
});

export default withStyles(styles, { withTheme: true })(
  connect(mapStateToProps, { setLayoutSettings })(Layout2)
);
