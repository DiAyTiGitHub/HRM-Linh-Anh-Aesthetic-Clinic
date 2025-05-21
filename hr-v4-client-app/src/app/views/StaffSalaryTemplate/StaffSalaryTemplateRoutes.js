import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const StaffSalaryTemplateIndex = EgretLoadable({
  loader: () => import("./StaffSalaryTemplateIndex"),
});
const ViewComponent = StaffSalaryTemplateIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "salary/staff-salary-template",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
