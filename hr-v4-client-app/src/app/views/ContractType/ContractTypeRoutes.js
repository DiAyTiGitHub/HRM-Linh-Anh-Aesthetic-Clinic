import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const ContractTypeIndex = EgretLoadable({
  loader: () => import("./ContractTypeIndex"),
});
const ViewComponent = ContractTypeIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "salary/contract-type",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
