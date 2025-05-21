import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/users/";
const API_PATH_ROLE = ConstantList.API_ENPOINT + "/api/roles/";
const API_PATH_EXT = ConstantList.API_ENPOINT + "/api/user-ext";

// export const pagingUsers = (page, pageSize) => {
//   var params = page + "/" + pageSize;
//   var url = API_PATH + params;
//   return axios.get(url);
// };

export const pagingUsers = (searchObject) => {
  var url = API_PATH_EXT + "/paging-user";
  return axios.post(url, searchObject);
};

export const findUserByUserName = (username, page, pageSize) => {
  var params = "username/" + username + "/" + page + "/" + pageSize;
  var url = API_PATH + params;
  return axios.get(url);
};

export const getAllRoles = () => {
  var url = API_PATH_ROLE + 'all';
  return axios.get(url);
};

export const getFilterRoles = () => {
  var url = API_PATH_EXT + '/get-list-role';
  return axios.get(url);
};

export const getUser = id => {
  var url = API_PATH + id;
  return axios.get(url);
};


export const getUserByUsername = (username) => {
  const config = { params: { username: username } };
  var url = API_PATH;
  return axios.get(url, config);
};

export const saveUser = user => {
  return axios.post(API_PATH_EXT, user);
};

export const updateUser = user => {
  // console.log("updating user: ...", user);
  return axios.post(API_PATH_EXT, user);
};
export const deleteUser = id => {
  return axios.delete(API_PATH + id);
};

export const changePassWord = (obj) => {
  var url = API_PATH_EXT + "/change-password";
  return axios.post(url, obj);
};

export const resetPassWord = (obj) => {
  var url = API_PATH_EXT + "/reset-password";
  return axios.post(url, obj);
};

//new logic: paging user and display staff who is currently using this user account
export const pagingUserWithStaff = (searchObject) => {
  var url = API_PATH_EXT + "/paging-user-with-using-staff";
  return axios.post(url, searchObject);
};

//new logic: save user and choose staff using this account at the same time => this staff can be loggind with this user account
export const saveUserAndChooseUsingStaff = user => {
  var url = API_PATH_EXT + "/save-user-and-choose-using-staff";
  return axios.post(url, user);
};

//new logic: get user and staff who is currently using this user account
export const getUserWithUsingStaff = userId => {
  var url = API_PATH_EXT + "/user-with-using-staff/" + userId;
  return axios.get(url);
};
