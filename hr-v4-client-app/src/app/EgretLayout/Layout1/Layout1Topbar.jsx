import React, { Component } from "react";
import { NavLink, withRouter } from "react-router-dom";
import {
  Icon,
  IconButton,
  MenuItem,
  MuiThemeProvider,
  Button,
  Badge
} from "@material-ui/core";
import { connect } from "react-redux";
import AppsIcon from '@material-ui/icons/Apps';
import { setLayoutSettings } from "app/redux/actions/LayoutActions";
import { logoutUser } from "app/redux/actions/UserActions";
import { PropTypes } from "prop-types";
import { EgretMenu } from "egret";
import { isMdScreen } from "utils";
import { Link } from "react-router-dom";
import { withTranslation } from "react-i18next";
import LanguageSelect from "./LanguageSelect";
import Directory from "./Directory";
import ArrowDropUpIcon from "@material-ui/icons/ArrowDropUp";
import AddIcon from "@material-ui/icons/Add";
import FiberManualRecordIcon from "@material-ui/icons/FiberManualRecord";
import localStorageService from "app/services/localStorageService";
import GlobitsAvatar from "app/common/GlobitsAvatar";
import Brand from "../SharedCompoents/Brand";
import ModuleOptionsPopup from "./ModuleOptionsPopup";

const ViewLanguageSelect = withTranslation()(LanguageSelect);

const { imagePath, firstName } = JSON.parse(localStorage.getItem('auth_user'));

class Layout1Topbar extends Component {
  state = {
    checked: false,
    isOpen: false,
    isAdmin: false,
    openSubMenu: false,
  };

  updateSidebarMode = (sidebarSettings) => {
    let { settings, setLayoutSettings } = this.props;

    setLayoutSettings({
      ...settings,
      layout1Settings: {
        ...settings.layout1Settings,
        leftSidebar: {
          ...settings.layout1Settings.leftSidebar,
          ...sidebarSettings,
        },
      },
    });
  };

  handleSidebarToggle = () => {
    let { settings } = this.props;
    let { layout1Settings } = settings;
    const currentMode = layout1Settings.leftSidebar.mode;

    let nextMode;

    // Vòng lặp: full → compact → close → full ...
    switch (currentMode) {
      case "full":
        nextMode = "compact";
        break;
      case "compact":
        nextMode = "close";
        break;
      case "close":
      default:
        nextMode = "full";
        break;
    }

    this.updateSidebarMode({ mode: nextMode });
  };
  handleSignOut = () => {
    this.props.logoutUser();
  };

  componentWillMount() {
    let roles =
      localStorageService
        .getLoginUser()
        ?.user?.roles?.map((item) => item.authority) || [];
    let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
    if (roles.some((role) => auth.indexOf(role) !== -1)) {
      this.setState({ isAdmin: true });
    } else {
      this.setState({ isAdmin: false });
    }
  }

  renderLogoSwitch = () => (
    // Open Brand component file to replace logo and text
    <Brand>
    </Brand>
  );

  render() {
    const styles = {
      notificationItem: {
        width: 275,
        height: 55,
        padding: "16px 15px 18px",
        borderBottom: "1px solid #EFF2F6",
        gap: 8
      },
      notificationTime: {
        backgroundColor: '#f1f1f1',
        float: 'right',
        maxWidth: '75px',
        fontSize: '11px',
        fontWeight: 400,
        opacity: '.7',
        filter: 'alpha(opacity = 70)',
        textAlign: 'right',
        padding: '1px 5px',
      },
      notificationContent: {
        fontWeight: '300',
        lineHeight: '20px',
        whiteSpace: 'normal',
        fontSize: '13px',
        overflow: 'hidden',
        display: '-webkit-box',
        '-webkit-box-orient': 'vertical',
        '-webkit-line-clamp': '1',
      }
    }
    const loginUser = localStorageService.getLoginUser();
    const fullName = loginUser?.user?.username || "Unknown User";
    return (
      <MuiThemeProvider>
        <div className="topbar">
          <div className={`topbar-hold`} style={{ position: "relative" }}>
            <div className="flex flex-space-between flex-middle h-100 topbar-container">
              <div className="flex toggle-btn">
                <div className="flex">
                  <div className="flex toggle-btn">
                    <div className="flex-1">
                      {this.renderLogoSwitch()}
                    </div>

                    <div className="flex align-center jusify-right">
                      <IconButton onClick={this.handleSidebarToggle} className="p-0">
                        <Icon>menu</Icon>
                      </IconButton>
                      {/* <div className="hide-on-mobile"></div> */}
                    </div>
                  </div>
                  <div className="flex align-center jusify-right uppercase text-white text-[18px] ml-4">
                      {fullName}
                  </div>
                </div>
              </div>
              
              <div
                className="flex flex-middle right-btns"
                style={{ height: "100%" }}
              >
                <span
                  className={`redirect-module`}
                  style={{ display: "flex" }}
                >
                  <div className="link-redirect-module flex-center"
                    style={{ zIndex: "999999" }}
                    onClick={() => {
                      // console.log(this.state.openSubMenu);
                      this.setState((prevState) => ({ openSubMenu: !prevState.openSubMenu }));
                    }}
                  >
                    <AppsIcon />
                  </div>
                </span>

                {/* <div className="flex flex-middle nav-item-left item-dropdown">
                  <ViewLanguageSelect horizontalPosition="right" />
                </div> */}

                <div className="flex flex-middle nav-item-left item-dropdown">
                  <EgretMenu
                    menuButton={
                      <Button className="menu-button p-0" disableRipple>
                        <GlobitsAvatar imgPath={imagePath} name={firstName}
                          className="mx-8 text-middle circular-image-small cursor-pointer"
                        />
                      </Button>
                    }
                    horizontalPosition="right"
                  >
                    <MenuItem style={{ minWidth: 185 }}>
                      <Link
                        className="flex flex-middle"
                        to="/page-layouts/user-profile"
                      >
                        <Icon> person </Icon>
                        <span className="pl-16"> Profile </span>
                      </Link>
                    </MenuItem>
                    <MenuItem
                      onClick={this.handleSignOut}
                      className="flex flex-middle"
                      style={{ minWidth: 185 }}
                    >
                      <Icon> power_settings_new </Icon>
                      <span className="pl-16">  Logout </span>
                    </MenuItem>
                  </EgretMenu>
                </div>

                <ModuleOptionsPopup
                  openChooseModulePopup={this.state.openSubMenu}
                  setState={() => this.setState({ openSubMenu: false })}
                />
              </div>
            </div>
          </div>

        </div>
      </MuiThemeProvider>
    );
  }
}

Layout1Topbar.propTypes = {
  setLayoutSettings: PropTypes.func.isRequired,
  logoutUser: PropTypes.func.isRequired,
  settings: PropTypes.object.isRequired,
};

const mapStateToProps = (state) => ({
  setLayoutSettings: PropTypes.func.isRequired,
  logoutUser: PropTypes.func.isRequired,
  settings: state.layout.settings,
});

export default withRouter(connect(mapStateToProps, { setLayoutSettings, logoutUser })(Layout1Topbar))
