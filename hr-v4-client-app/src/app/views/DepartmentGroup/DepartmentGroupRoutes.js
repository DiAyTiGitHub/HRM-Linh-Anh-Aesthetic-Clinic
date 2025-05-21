import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./DepartmentGroupIndex"),
});

const ViewComponent = RenderScreen;

const DepartmentGroupRoutes = [
  {
    path: ConstantList.ROOT_PATH + "category/staff/department-group",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default DepartmentGroupRoutes;
