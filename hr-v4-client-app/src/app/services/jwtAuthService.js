import axios from "axios";
import localStorageService from "./localStorageService";
import ConstantList from "../appConfig";
import UserService from "../services/UserService";
// import { getCurrentUser } from "app/views/User/UserService"

import history from "history.js";
const config = {
  headers: {
    "Content-Type": "application/x-www-form-urlencoded",
    Authorization: "Basic Y29yZV9jbGllbnQ6c2VjcmV0",
  },
};

class JwtAuthService {

  getCurrentUser = async () => {
    let url = ConstantList.API_ENPOINT + "/api/user-ext/get-current-staff";
    let response = await axios.get(url);

    if (response.data.user) {
      this.setUser(response.data)
      return response.data.user;
    } else {
      let url = ConstantList.API_ENPOINT + "/api/user-ext/get-current-user";
      let response = await axios.get(url);
      let staff = {
        user: response.data
      }
      this.setUser(staff)
      return response.data;
    }

  };

  loginWithUserNameAndPassword = (username, password) => {
    let requestBody =
      "client_id=core_client&grant_type=password&client_secret=secret";
    requestBody =
      requestBody + "&username=" + username + "&password=" + password;
    return axios
      .post(ConstantList.API_ENPOINT + "/oauth/token", requestBody, config)
      .then((response) => {
        console.log(response);
        const tokenExpriredTime = new Date(
          Date.now() + response.data.expires_in * 1000
        );

        localStorageService.setItem("tokenExpiredTime", tokenExpriredTime);

        this.setSession(response.data.access_token);
        this.setLoginUser(response.data);
      });
  };

  loginWithEmailAndPassword = (email, password) => {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        resolve(this.user);
      }, 1000);
    }).then((data) => {
      this.setUser(data);
      this.setSession(data.token);
      return data;
    });
  };

  loginWithToken = () => {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        resolve(this.user);
      }, 100);
    }).then((data) => {
      this.setSession(data.token);
      this.setUser(data);
      return data;
    });
  };

  logout = () => {
    if (ConstantList.AUTH_MODE === "Keycloak") { 
      UserService.doLogout();
      this.setSession(null);
      this.removeUser();
      history.push(ConstantList.HOME_PAGE);
    } else {
      this.setSession(null);
      this.removeUser();
      history.push(ConstantList.LOGIN_PAGE);
    }
  };

  setSession(token) {
    if (token) {
      localStorageService.setItem("jwt_token", token);
      axios.defaults.headers.common["Authorization"] = "Bearer " + token;
    } else {
      localStorage.removeItem("jwt_token");
      localStorage.removeItem("id_project_task");
      delete axios.defaults.headers.common["Authorization"];
    }
  }
  setLoginUser = (data) => {
    let user = {};
    user.token = data.access_token;
    this.setUser(user);
    return user;
  };


  //set token
  setLoginToken = (data) => {
    localStorageService.setItem("auth_token", data);
  };

  setUser = (user) => {
    // console.log("User:", user);
    localStorageService.setItem("auth_user", user);
  };

  // setLoginStaff = (staff) => {
  //   localStorageService.setItem("auth_staff", staff);
  // };


  removeUser = () => {
    localStorage.removeItem("auth_user");
  };
}

export default new JwtAuthService();
