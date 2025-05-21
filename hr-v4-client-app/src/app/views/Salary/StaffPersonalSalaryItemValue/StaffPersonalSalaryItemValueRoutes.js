import {EgretLoadable} from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
    loader: () => import("./StaffPersonalSalaryItemValueIndex"),
});

const ViewComponent = RenderScreen;

const StaffPersonalSalaryItemValueRoutes = [
    {
        path: ConstantList.ROOT_PATH + "salary/staff-personal-salary-item-value",
        exact: true,
        component: ViewComponent,
        auth: ["ROLE_ADMIN", "HR_MANAGER"],
    },
];

export default StaffPersonalSalaryItemValueRoutes;
