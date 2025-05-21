import {EgretLoadable} from "egret";
import ConstantList from "app/appConfig";
import LocalConstants from "app/LocalConstants";

const systemRole = LocalConstants.SystemRole;

const ROLE_ADMIN = systemRole?.ROLE_ADMIN?.value;
const HR_MANAGER = systemRole?.HR_MANAGER?.value;
const HR_STAFF_VIEW = systemRole?.HR_STAFF_VIEW?.value;
const HR_LEGISLATION = systemRole?.HR_LEGISLATION?.value;

const RenderScreen = EgretLoadable({
    loader: () => import("./StaffLabourManagementBookIndex"),
});

const ViewComponent = RenderScreen;

const StaffLabourManagementBookRoutes = [{
    path: ConstantList.ROOT_PATH + "staff-labour-management-book",
    exact: true,
    component: ViewComponent,
    auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_LEGISLATION],
},];

export default StaffLabourManagementBookRoutes;
