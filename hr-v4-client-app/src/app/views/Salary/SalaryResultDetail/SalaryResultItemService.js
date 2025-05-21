import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/salary-result-item";

export const chooseResultItems = (obj) => {
  const url = API_PATH + "/choose-result-items";
  return axios.post(url, obj);
}; 