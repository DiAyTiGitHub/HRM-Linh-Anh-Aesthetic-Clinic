import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const DepartmentV2Index = EgretLoadable({
  loader: () => import("./DepartmentV2Index"),
});
const ViewComponent = DepartmentV2Index;

const DepartmentV2Routes = [
  {
    path: ConstantList.ROOT_PATH + "hr-department",
    exact: true,
    component: ViewComponent,
  },
];

export default DepartmentV2Routes;