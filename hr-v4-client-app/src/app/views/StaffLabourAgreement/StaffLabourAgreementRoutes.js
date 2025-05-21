import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./StaffLabourAgreementIndex"),
}); 

const ViewComponent = RenderScreen;

const StaffLabourAgreementRoutes = [
  {
    path: ConstantList.ROOT_PATH + "staff-labour-agreement",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default StaffLabourAgreementRoutes;
