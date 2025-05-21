import React, { Component, Fragment } from "react";
import PropTypes from "prop-types";
import {
  Icon,
  withStyles,
  MenuItem,
  Tooltip,
  IconButton,
  MuiThemeProvider,
} from "@material-ui/core";
import history from "history.js";
import { connect } from "react-redux";
import {
  setLayoutSettings,
  setDefaultSettings,
} from "app/redux/actions/LayoutActions";
import { logoutUser } from "app/redux/actions/UserActions";
import { withRouter } from "react-router-dom";
import { EgretMenu } from "egret";
import Sidenav from "../SharedCompoents/Sidenav";
import SidenavTheme from "../EgretTheme/SidenavTheme";
import { isMdScreen } from "utils";
import ConstantList from "app/appConfig";
import GlobitsAvatar from "app/common/GlobitsAvatar";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { Form, Formik } from "formik";
import SearchIcon from '@material-ui/icons/Search';
import { navigations } from "../../navigations";
import i18n from "i18n";

const styles = (theme) => ({});

const { imagePath, lastName } = JSON.parse(localStorage.getItem('auth_user'));


const IconButtonWhite = withStyles((theme) => ({
  root: {
    // color: theme.palette.getContrastText(purple[500]),
    backgroundColor: "transparent",
    padding: "5px",
  },
}))(IconButton);

const IconSmall = withStyles(() => ({
  root: {
    fontSize: "1rem",
  },
}))(Icon);

class Layout1Sidenav extends Component {
  state = {
    sidenavToggleChecked: false,
    hidden: true,
    onRenderNavigations: JSON.parse(JSON.stringify(navigations))
  };

  componentWillMount() {
    // CLOSE SIDENAV ON ROUTE CHANGE ON MOBILE
    this.unlistenRouteChange = this.props.history.listen((location, action) => {
      if (isMdScreen()) {
        this.updateSidebarMode({ mode: "mobile" });
      }
    });

    setTimeout(() => {
      this.setState({ hidden: false });
    }, 400);
  }

  componentWillUnmount() {
    this.unlistenRouteChange();
  }

  updateSidebarMode = (sidebarSettings) => {
    let { settings, setLayoutSettings, setDefaultSettings } = this.props;
    const updatedSettings = {
      ...settings,
      layout1Settings: {
        ...settings.layout1Settings,
        leftSidebar: {
          ...settings.layout1Settings.leftSidebar,
          ...sidebarSettings,
        },
      },
    };
    setLayoutSettings(updatedSettings);
    setDefaultSettings(updatedSettings);
  };

  handleSidenavToggle = () => {
    let { sidenavToggleChecked } = this.state;
    let mode = sidenavToggleChecked ? "full" : "close";
    this.updateSidebarMode({ mode });
    this.setState({ sidenavToggleChecked: !sidenavToggleChecked });
  };

  handleSignOut = () => {
    this.props.logoutUser();
  };

  

  renderUser = () => {
    let { user } = this.props;

    return (
      <div className="sidenav__user">
        <div className="username-photo">
          <GlobitsAvatar imgPath={imagePath} name={lastName} className='img-avatar border-radius-circle font-weight-600' />
          <span className="username">{user?.displayName}</span>
        </div>

      </div>
    );
  };

  renderSearchNavigationInput = () => {
    const that = this;

    function handleSearchMenu({ navigationKeyword }) {
      const data = JSON.parse(JSON.stringify(navigations));

      if (!navigationKeyword || navigationKeyword == "") {
        //handle case nothing to search
        that.setState({
          onRenderNavigations: data
        });
        return;
      }

      function isContainsKeyword(menu) {
        if (i18n.t(menu?.name)?.toLowerCase()?.includes(navigationKeyword?.toLowerCase())) {
          return true;
        }
        if (menu?.children && menu?.children?.length > 0) {
          for (const child of menu?.children) {
            const isValid = isContainsKeyword(child);
            if (isValid) return true;
          }
        }
        return false;
      }

      const onDisplayMenus = [];

      for (const nav of navigations) {
        const isValid = isContainsKeyword(nav);
        if (isValid) onDisplayMenus.push(nav);
      }

      that.setState({
        onRenderNavigations: onDisplayMenus
      });
    }

    return (
      <div className="px-12 py-12 navigationSearch">
        <Formik
          enableReinitialize
          initialValues={{}}
          onSubmit={function (values) {
            handleSearchMenu(values);
          }}
        >
          {({ resetForm, values, setFieldValue, setValues }) => {

            return (
              <Form autoComplete="off">
                <GlobitsTextField
                  className="pr-0"
                  placeholder="Tìm kiếm..."
                  name="navigationKeyword"
                  variant="outlined"
                  notDelay
                  InputProps={{
                    endAdornment: (
                      <IconButton className="p-0 text-white" aria-label="search" type="submit"

                      >
                        <SearchIcon
                          className="text-white"
                        />
                      </IconButton>
                    ),
                  }}
                />
              </Form>);
          }}
        </Formik>
      </div>
    );
  }

  render() {
    // console.log("this.state after rendering", this.state);
    // console.log("state.onRenderNavigations", this.state.onRenderNavigations);


    let { theme, settings } = this.props;
    const sidenavTheme =
      settings?.themes[settings?.layout1Settings?.leftSidebar?.theme] || theme;

    return (
      <MuiThemeProvider theme={sidenavTheme}>
        <SidenavTheme theme={sidenavTheme} settings={settings} />
        <div className="sidenav">
          <div className="sidenav__hold">
            {!this?.state?.hidden && (
              <Fragment>
                {this.renderSearchNavigationInput()}
                <Sidenav
                  onRenderNavigations={this.state.onRenderNavigations}
                >
                  {/* {this.renderUser()} */}

                </Sidenav>
              </Fragment>
            )}
          </div>
        </div>
      </MuiThemeProvider>
    );
  }
}

Layout1Sidenav.propTypes = {
  setLayoutSettings: PropTypes.func.isRequired,
  setDefaultSettings: PropTypes.func.isRequired,
  logoutUser: PropTypes.func.isRequired,
  user: PropTypes.object.isRequired,
  settings: PropTypes.object.isRequired,
};

const mapStateToProps = (state) => ({
  setDefaultSettings: PropTypes.func.isRequired,
  setLayoutSettings: PropTypes.func.isRequired,
  logoutUser: PropTypes.func.isRequired,
  user: state.user,
  settings: state.layout.settings,
  onRenderNavigations: state.onRenderNavigations
});

export default withStyles(styles, { withTheme: true })(
  withRouter(
    connect(mapStateToProps, {
      setLayoutSettings,
      setDefaultSettings,
      logoutUser,
    })(Layout1Sidenav)
  )
);
