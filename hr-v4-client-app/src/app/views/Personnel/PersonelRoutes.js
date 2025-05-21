import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "personnel/candidate_working",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./EmployeeOnboarding/CandidateWorking/CandidateWorkingIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "personnel/list_contract",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./ContractManagement/ListContract/ListContractIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "personnel/addendum_contract",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./ContractManagement/AddendumContract/AddendumContractIndex"),
      }),
  },
];

export default Routes;
