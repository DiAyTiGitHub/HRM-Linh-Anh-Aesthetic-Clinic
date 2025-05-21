import React, { Component } from "react";
import {
  Icon,
  IconButton,
  Hidden,
  withStyles,
  MuiThemeProvider,
  MenuItem,
} from "@material-ui/core";
import { Link } from "react-router-dom";
import { EgretMenu, EgretToolbarMenu } from "egret";
import { setLayoutSettings } from "app/redux/actions/LayoutActions";
import { PropTypes } from "prop-types";
import { connect } from "react-redux";
import { Helmet } from "react-helmet";
import ConstantList from "app/appConfig";
import LanguageSelect from "../SharedCompoents/LanguageSelect";
import { withTranslation } from "react-i18next";
import { logoutUser } from "app/redux/actions/UserActions";
import localStorageService from "app/services/localStorageService";

const ViewLanguageSelect = withTranslation()(LanguageSelect);

class Layout2Topbar extends Component {
  state = {
    currentUserName: "",
    imagePath: "",
    offset: 0,
  };

  handleSignOut = () => {
    this.props.logoutUser();
  };

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

  handleSidebarToggle = () => {
    let { settings } = this.props;
    let { layout2Settings } = settings;

    let mode =
      layout2Settings.leftSidebar.mode === "close" ? "mobile" : "close";

    this.updateSidebarMode({ mode });
  };

  componentDidUpdate(prevProps, prevState) {
    if (prevState.currentUserName !== this.state.currentUserName) {
      this.setState({
        currentUserName: this.state.currentUserName,
      });
    }
    // const onScroll = () => this.setState({ offset: window.pageYOffset });
    // // clean up code
    // window.removeEventListener("scroll", onScroll);
    // window.addEventListener("scroll", onScroll, { passive: true });
    // return () => window.removeEventListener("scroll", onScroll);
  }

  componentDidMount() {
    let user = localStorageService.getItem("auth_user");
    window.addEventListener("scroll", this.handleScroll, true);
    if (user) {
      this.setState({
        currentUserName: user.user?.displayName,
        imagePath: user.imagePath,
      });
    }
  }
  componentWillUnmount() {
    window.removeEventListener("scroll", this.handleScroll);
  }

  handleScroll = (event) => {
    let scrollTop = event.srcElement.scrollTop;
    this.setState({
      offset: scrollTop,
    });
  };

  getImageNameAndType = (name) => {
    if (name) {
      return name.split(".")[0] + "/" + name.split(".")[1];
    }
    return "";
  };

  render() {
    let { theme, settings } = this.props;
    let { currentUserName } = this.state;

    const topbarTheme =
      settings.themes[settings.layout2Settings.topbar.theme] || theme;

    let topbar = document.querySelector(".topbar");

    if (topbar !== null && this.state.offset > 100) {
      topbar.style.display = "none";
    }
    if (topbar !== null && this.state.offset === 0) {
      topbar.style.display = "block";
    }
    return (
      <MuiThemeProvider theme={topbarTheme}>
        <Helmet>
          <style>
            {`.topbar {
            background-color: ${topbarTheme.palette.primary.main};
            border-color: ${topbarTheme.palette.divider} !important;
          }
          .topbar .brand__text {
            color: ${topbarTheme.palette.primary.contrastText};
          }
          `}
          </style>
        </Helmet>

        <div className="topbar">
          <div className="flex flex-space-between flex-middle container h-100">
            <div className="flex flex-middle brand">
              <img
                src={ConstantList.ROOT_PATH + "assets/images/logo.png"}
                alt="company-logo"
              />
            </div>
            <div className="mx-auto"></div>
            {/* offsetTop="80px" */}
            <div className="flex flex-middle ">
              <EgretToolbarMenu>
                <ViewLanguageSelect />
                <EgretMenu
                  menuButton={
                    <img
                      className="mx-8 text-middle circular-image-small"
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
                    <span className="" style={{ fontSize: "13px" }}>
                      {currentUserName}
                      <Icon
                        style={{ fontSize: "1rem" }}
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
              </EgretToolbarMenu>

              <Hidden mdUp>
                <IconButton onClick={this.handleSidebarToggle}>
                  <Icon>menu</Icon>
                </IconButton>
              </Hidden>
            </div>
          </div>
        </div>
      </MuiThemeProvider>
    );
  }
}

Layout2Topbar.propTypes = {
  setLayoutSettings: PropTypes.func.isRequired,
  logoutUser: PropTypes.func.isRequired,
  settings: PropTypes.object.isRequired,
};

const mapStateToProps = (state) => ({
  setLayoutSettings: PropTypes.func.isRequired,
  logoutUser: PropTypes.func.isRequired,
  settings: state.layout.settings,
});

export default withStyles(
  {},
  { withTheme: true }
)(connect(mapStateToProps, { setLayoutSettings, logoutUser })(Layout2Topbar));
