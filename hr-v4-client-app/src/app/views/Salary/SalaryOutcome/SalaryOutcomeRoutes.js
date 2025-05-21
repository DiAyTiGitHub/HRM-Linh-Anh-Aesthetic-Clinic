import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const SalaryOutcomeIndex = EgretLoadable({
  loader: () => import("./SalaryOutcomeIndex"),
});
 
const ViewComponent = SalaryOutcomeIndex;

const SalaryOutcomeRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/salary-outcome",
    exact: true,
    component: ViewComponent,
  },
];

export default SalaryOutcomeRoutes;

