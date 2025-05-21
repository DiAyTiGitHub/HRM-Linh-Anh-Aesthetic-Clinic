import {EgretLoadable} from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
    loader: () => import("./StaffSalaryItemValueIndex"),
});

const ViewComponent = RenderScreen;

const StaffSalaryItemValueRoutes = [
    {
        path: ConstantList.ROOT_PATH + "salary/staff-salary-item-value",
        exact: true,
        component: ViewComponent,
        auth: ["ROLE_ADMIN", "HR_MANAGER"],
    },
];

export default StaffSalaryItemValueRoutes;
