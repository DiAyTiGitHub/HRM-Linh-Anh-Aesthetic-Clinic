import React, { Component, Fragment } from "react";
import { withRouter } from "react-router-dom";
import { connect } from "react-redux";
import AppContext from "app/appContext";
import ConstantList from "../appConfig";
import localStorageService from "app/services/localStorageService";
import axios from "axios";
import jwtAuthService from '../services/jwtAuthService';
import { toast } from "react-toastify";
import history from "../../history";

let timeout;

class AuthGuard extends Component {
  constructor(props, context) {
    super(props);
    let { routes } = context;

    this.state = {
      authenticated: true,
      getUserComplete: false,
      // roles: localStorageService.getLoginUser()?.roles?.map((item) => item.authority) || [],
      routes
    };

    axios.interceptors.response.use(
      response => {
        if (timeout) {
          clearTimeout(timeout);
        }
        timeout = setTimeout(() => {
          toast.warning('Phiên đăng nhập hết hạn, vui lòng đăng nhập lại.');
          jwtAuthService.setSession(null);
          jwtAuthService.removeUser();
          setTimeout(() => {
            jwtAuthService.logout();
          }, 3000)
        }, 7 * 24 * 60 * 60 * 1000);

        if (response?.data?.status === 403) {
          // chuyển sang màn hình không có quyền
        }


        return response
      },
      error => {
        if (401 === error?.response?.status) {
          toast.warning('Phiên đăng nhập hết hạn, vui lòng đăng nhập lại.');
          jwtAuthService.setSession(null);
          jwtAuthService.removeUser();
          setTimeout(() => {
            jwtAuthService.logout();
          }, 3000)
          return Promise.reject(error);
        }
        return Promise.reject(error);
      }
    );

    // if (ConstantList.AUTH_MODE === "Keycloak") {
    //   let userToken = localStorageService.getItem("auth_token");
    //   if (userToken != null) {
    //     jwtAuthService.setSession(userToken);
    //     jwtAuthService.getCurrentUser().then((user) => {
    //       // this.props.setUserData(user);
    //     }).finally(() => {
    //       this.setState({ ...this.state, getUserComplete: true });
    //     })
    //   } else {
    //     history.push(ConstantList.LOGIN_PAGE)
    //   }
    // } else {
    //   let user = localStorageService.getItem("auth_user");

    //   if (user != null) {
    //     jwtAuthService.setSession(user.token);
    //     // this.props.setUserData(user);
    //   } else {
    //     history.push(ConstantList.LOGIN_PAGE)
    //   }
    // }
  }


  // componentDidMount() {
  //   // if (!this.state.authenticated) {
  //   //   this.redirectRoute(this.props);
  //   // }
  //   // console.log(this.state.routes)
  //   let roles = localStorageService.getLoginUser()?.roles?.map((item) => item.authority) || [];
  //   let newRoutes = [];
  //   this.state.routes.map((item, key) => {
  //     if (!item.auth || roles.some((role) => (item.auth).indexOf(role) !== -1)) {
  //       newRoutes.push(item)
  //       this.setState({ routes: newRoutes });
  //       if (item.children) {
  //         item.children.map((subItem, subKey) => {
  //           newRoutes.push(subItem)
  //           this.setState({ routes: newRoutes });
  //         })
  //       }
  //     }
  //   })

  // }

  // componentDidUpdate() {
  //   if (!this.state.routes) {
  //     this.redirectRoute(this.props);
  //   }
  // }

  shouldComponentUpdate(nextProps, nextState) {
    return nextState.routes !== this.state.routes;
  }

  // static getDerivedStateFromProps(props, state) {
  //   const { location, user } = props;
  //   const { pathname } = location;
  //   const matched = state.routes.find(r => r.path === pathname);
  //   const authenticated =
  //     matched && matched.auth && matched.auth.length
  //       ? matched.auth.includes(user.role)
  //       : true;

  //   return {
  //     authenticated
  //   };
  // }

  // redirectRoute(props) {
  //   const { location, history } = props;
  //   const { pathname } = location;

  //   history.push({
  //     //pathname: ConstantList.ROOT_PATH + "session/signin",
  //     pathname: ConstantList.HOME_PAGE,
  //     state: { redirectUrl: pathname }
  //   });
  // }

  render() {
    let { children } = this.props;
    // const { authenticated } = this.state;

    return <Fragment>{children}</Fragment>;

    //return authenticated ? <Fragment>{children}</Fragment> : null;
  }
}

AuthGuard.contextType = AppContext;

const mapStateToProps = state => ({
  user: state.user
});

export default withRouter(connect(mapStateToProps)(AuthGuard));
