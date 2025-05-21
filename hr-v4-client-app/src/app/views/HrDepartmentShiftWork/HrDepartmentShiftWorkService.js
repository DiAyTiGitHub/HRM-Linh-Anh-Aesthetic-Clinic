import axios from "axios";
import ConstantList from "app/appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/hr-department-shift-work";

export const getShiftWorksOfDepartment = (departmentId) => {
  let url = API_PATH + "/shift-work-of-department/" + departmentId;
  return axios.get(url);
};