import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const EducationalManagementLevelIndex = EgretLoadable({
  loader: () => import("./EducationalManagementLevelIndex"),
});
const ViewComponent = EducationalManagementLevelIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/educationalManagementLevel",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
