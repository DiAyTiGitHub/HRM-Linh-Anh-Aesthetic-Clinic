import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable ({
  loader:() => import("./StaffDocumentItemIndex"),
});

const ViewComponent = RenderScreen;

const StaffDocumentItemRoutes = [
  {
    path:ConstantList.ROOT_PATH + "staff-document-item",
    exact:true,
    component:ViewComponent,
    auth:["ROLE_ADMIN", "HR_MANAGER", "HR_COMPENSATION_BENEFIT"],
  },
];

export default StaffDocumentItemRoutes;
