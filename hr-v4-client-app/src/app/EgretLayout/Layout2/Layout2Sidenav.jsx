import React, { Component } from "react";
import PropTypes from "prop-types";
import {
  withStyles,
  MuiThemeProvider,
  Icon,
  MenuItem,
} from "@material-ui/core";
import { connect } from "react-redux";
import {
  setLayoutSettings,
  setDefaultSettings,
} from "app/redux/actions/LayoutActions";
import { isMobile } from "utils";
import { Link, withRouter } from "react-router-dom";
import Sidenav from "../SharedCompoents/Sidenav";
import Brand from "../SharedCompoents/Brand";
import SidenavTheme from "../EgretTheme/SidenavTheme";

import { withTranslation } from "react-i18next";
import LanguageSelect from "../SharedCompoents/LanguageSelect";
import { EgretMenu } from "egret";
import { logoutUser } from "app/redux/actions/UserActions";
import ConstantList from "app/appConfig";
import localStorageService from "app/services/localStorageService";

const ViewLanguageSelect = withTranslation()(LanguageSelect);

class Layout2Sidenav extends Component {
  state = {
    currentUserName: "",
    sidenavToggleChecked: false,
  };

  componentWillMount() {
    // CLOSE SIDENAV ON ROUTE CHANGE ON MOBILE
    this.unlistenRouteChange = this.props.history.listen((location, action) => {
      if (isMobile()) {
        this.updateSidebarMode({ mode: "compact" });
      }
    });
  }

  componentWillUnmount() {
    this.unlistenRouteChange();
  }

  componentDidUpdate(prevProps, prevState) {
    if (prevState.currentUserName !== this.state.currentUserName) {
      this.setState({
        currentUserName: this.state.currentUserName,
      });
    }
  }

  componentDidMount() {
    let user = localStorageService.getItem("auth_user");
    if (user) {
      this.setState({
        currentUserName: user.user?.displayName,
        imagePath: user.imagePath,
      });
    }
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

  getImageNameAndType = (name) => {
    if (name) {
      return name.split(".")[0] + "/" + name.split(".")[1];
    }
    return "";
  };

  handleSignOut = () => {
    this.props.logoutUser();
  };


  render() {
    console.log(this)
    let { theme, settings } = this.props;
    let { currentUserName } = this.state;
    const sidenavTheme =
      settings.themes[settings.layout2Settings.leftSidebar.theme] || theme;
    return (
      <MuiThemeProvider theme={sidenavTheme}>
        <SidenavTheme theme={sidenavTheme} settings={settings} />
        <div className="sidenav">
          <div className="sidenav__hold">
            <Brand />

            <EgretMenu
              menuButton={
                <img
                  className="image__user"
                  src={
                    this.state.imagePath
                      ? ConstantList.API_ENPOINT +
                      "/public/hr/file/getImage/" +
                      this.getImageNameAndType(this.state.imagePath)
                      : ConstantList.ROOT_PATH + "assets/images/avatar.jpg"
                  }
                  alt="user"
                />
              }
              menuText={
                <span className="title__user">
                  {currentUserName}
                  <Icon
                    style={{ fontSize: "25px" }}
                    className="text-middle ml-8"
                  >
                    keyboard_arrow_down
                  </Icon>
                </span>
              }
            >
              <MenuItem style={{ minWidth: 185 }}>
                <Link
                  className="flex flex-middle"
                  to={ConstantList.ROOT_PATH + "profile"}
                >
                  <Icon> person </Icon>
                  <span className="pl-16"> Profile</span>
                </Link>
              </MenuItem>
              <MenuItem
                onClick={this.handleSignOut}
                className="flex flex-middle"
                style={{ minWidth: 185 }}
              >
                <Icon> power_settings_new </Icon>
                <span className="pl-16"> Logout </span>
              </MenuItem>
            </EgretMenu>

            <ViewLanguageSelect />

            <Sidenav />
          </div>
        </div>
      </MuiThemeProvider>
    );
  }
}

Layout2Sidenav.propTypes = {
  setDefaultSettings: PropTypes.func.isRequired,
  setLayoutSettings: PropTypes.func.isRequired,
  logoutUser: PropTypes.func.isRequired,
  settings: PropTypes.object.isRequired,
};

const mapStateToProps = (state) => ({
  setDefaultSettings: PropTypes.func.isRequired,
  setLayoutSettings: PropTypes.func.isRequired,
  logoutUser: PropTypes.func.isRequired,
  settings: state.layout.settings,
});

export default withStyles(
  {},
  { withTheme: true }
)(
  withRouter(
    connect(mapStateToProps, {
      setLayoutSettings,
      setDefaultSettings,
      logoutUser
    })(Layout2Sidenav)
  )
);
