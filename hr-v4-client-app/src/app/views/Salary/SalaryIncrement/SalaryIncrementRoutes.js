import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const SalaryIncrement = EgretLoadable({
  loader: () => import("./SalaryIncrementIndex"),
});
const ViewComponent = SalaryIncrement;

const SalaryIncrementRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/salary-increment",
    exact: true,
    component: ViewComponent,
  },
];

export default SalaryIncrementRoutes;