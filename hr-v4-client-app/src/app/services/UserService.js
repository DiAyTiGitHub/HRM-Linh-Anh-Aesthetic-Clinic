import Keycloak from "keycloak-js";
import ConstantsList from '../appConfig';
import JwtAuthService from './jwtAuthService';
// import { getCurrentUser } from "app/views/User/UserService"
const _kc = new Keycloak(ConstantsList.ROOT_PATH + 'keycloak.json');

/**
 * Initializes Keycloak instance and calls the provided callback function if successfully authenticated.
 *
 * @param onAuthenticatedCallback
 */

const initKeycloak = (onAuthenticatedCallback) => {
  // console.log("login")
  // // JwtAuthService.getCurrentUser();
  // JwtAuthService.getCurrentUser().then(res => {
  //   console.log(res);
  //   // this.setLoginUser(res.data);
  // });

  _kc.init({
    onLoad: 'check-sso',
    promiseType: 'native',
    silentCheckSsoRedirectUri: window.location.origin + '/' + ConstantsList.ROOT_PATH + '/silent-check-sso.html',
    pkceMethod: 'S256',
    checkLoginIframe: false,
  })
    .then((authenticated) => {
      if (authenticated) {
        console.log("authenticated info _kc: ", _kc)
        JwtAuthService.setLoginToken(_kc.token);//Xem lại hàm này để làm chuẩn xác hơn
        onAuthenticatedCallback();
      } else {
        console.warn("not authenticated!");
        doLogin();
      }
    })
};

const doLogin = _kc.login;

const doLogout = () => {
  let logoutOptions = {
    redirectUri: window.location.origin
  }
  _kc.logout(logoutOptions)
};

const getToken = () => _kc.token;

const updateToken = (successCallback) => {
  alert("update")
  return _kc.updateToken(5)
    .then(successCallback)
    .catch(doLogin)
};

const getUsername = () => _kc.tokenParsed.preferred_username;

export default {
  initKeycloak,
  doLogin,
  doLogout,
  getToken,
  updateToken,
  getUsername,
}
