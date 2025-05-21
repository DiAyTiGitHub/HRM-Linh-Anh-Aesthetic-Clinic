import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./DepartmentTypeIndex"),
});

const ViewComponent = RenderScreen;

const DepartmentTypeRoutes = [
  {
    path: ConstantList.ROOT_PATH + "organization/department-type",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default DepartmentTypeRoutes;
