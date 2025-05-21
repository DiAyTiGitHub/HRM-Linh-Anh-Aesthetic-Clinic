import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/global-property";

export const getAllGlobalProperty = () => {
  var url = API_PATH;
  return axios.get(url);
};

export const getGlobalProperty = (property) => {
  let url = API_PATH + "/" + property;
  return axios.get(url);
};


export const saveGlobalProperty = (obj, edit) => {
  if (edit) {
    let url = API_PATH + "/" + obj.property;
    return axios.put(url, obj);
  } else {
    let url = API_PATH;
    return axios.post(url, obj);
  }
};

export const deleteGlobalProperty = (property) => {
  let url = API_PATH + "/" + property;
  return axios.delete(url);
};