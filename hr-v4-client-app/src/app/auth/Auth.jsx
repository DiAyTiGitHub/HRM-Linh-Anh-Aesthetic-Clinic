import React, { Component, Fragment } from "react";
import { connect } from "react-redux";
import { PropTypes } from "prop-types";
import { setUserData } from "../redux/actions/UserActions";
import jwtAuthService from "../services/jwtAuthService";
import localStorageService from "../services/localStorageService";
import history from "history.js";
import ConstantList from "../appConfig";

class Auth extends Component {
  state = {
    getUserComplete: false,
  };

  constructor(props) {
    super(props);
    if (ConstantList.AUTH_MODE === "Keycloak") {
      let userToken = localStorageService.getItem("auth_token");
      if (userToken != null) {
        jwtAuthService.setSession(userToken);
        jwtAuthService.getCurrentUser().then((user) => {
          this.props.setUserData(user);
        }).finally(() => {
          this.setState({ getUserComplete: true });
        })
      } else {
        history.push(ConstantList.LOGIN_PAGE);
      }
    } else {
      let user = localStorageService.getItem("auth_user");

      if (user != null) {
        jwtAuthService.setSession(user.token);
        this.props.setUserData(user);
      } else {
        history.push(ConstantList.LOGIN_PAGE)
      }
    }
  }

  checkJwtAuth = () => {
    jwtAuthService.loginWithToken().then(user => {
      this.props.setUserData(user);
    });
  };

  render() {
    const { children } = this.props;
    const { getUserComplete } = this.state;
    
    if (!getUserComplete && ConstantList.AUTH_MODE === "Keycloak") {
      return null;
    }

    // check permissions and roles here

    return <Fragment>{children}</Fragment>;
  }
}

const mapStateToProps = state => ({
  setUserData: PropTypes.func.isRequired,
  login: state.login
});

export default connect(
  mapStateToProps,
  { setUserData }
)(Auth);
