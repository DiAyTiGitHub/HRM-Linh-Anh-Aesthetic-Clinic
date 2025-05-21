import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const DepartmentIndex = EgretLoadable({
    loader: () => import("./DepartmentIndex"),
});
const ViewComponent = DepartmentIndex;

const DepartmentDiagram = EgretLoadable({
    loader: () => import("./Diagram/DepartmentDiagramIndex"),
});
const ViewDiagram = DepartmentDiagram;

const Routes = [
    {
        path: ConstantList.ROOT_PATH + "category/staff/departments",
        exact: true,
        component: ViewComponent,
    },

    {
        path: ConstantList.ROOT_PATH + "category/department/diagram/:departmentId",
        exact: true,
        component: ViewDiagram,
        auth: ["ROLE_ADMIN", "HR_MANAGER"],
        settings: { layout1Settings: { leftSidebar: { mode: "close" } } },
    },
];

export default Routes;
