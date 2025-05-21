import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const SalaryTemplateIndex = EgretLoadable({
  loader: () => import("./SalaryTemplateIndex"),
});

const ViewComponent = SalaryTemplateIndex;

const SalaryTemplateRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/salary-template",
    exact: true,
    component: ViewComponent,
  },
];

export default SalaryTemplateRoutes;
