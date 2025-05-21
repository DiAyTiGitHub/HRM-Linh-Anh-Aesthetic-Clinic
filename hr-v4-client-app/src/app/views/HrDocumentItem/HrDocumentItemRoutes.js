import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./HrDocumentItemIndex"),
});

const ViewComponent = RenderScreen;

const HrDepartmentItemRoutes = [
  {
    path: ConstantList.ROOT_PATH + "hr-document-item",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default HrDepartmentItemRoutes;
