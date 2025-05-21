import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const HrResourcePlanReportIndex = EgretLoadable({
  loader: () => import("./HrResourcePlanReportIndex"),
});
const ViewComponent = HrResourcePlanReportIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "organization/hr-resource-plan-report",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
