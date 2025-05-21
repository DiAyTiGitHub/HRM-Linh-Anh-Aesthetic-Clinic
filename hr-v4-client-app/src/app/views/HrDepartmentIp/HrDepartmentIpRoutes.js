import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./HrDepartmentIpIndex"),
});

const ViewComponent = RenderScreen;

const HrDepartmentIpRoutes = [
  {
    path: ConstantList.ROOT_PATH + "hr-department-ip",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER", "HR_COMPENSATION_BENEFIT"],
  },
];

export default HrDepartmentIpRoutes;
