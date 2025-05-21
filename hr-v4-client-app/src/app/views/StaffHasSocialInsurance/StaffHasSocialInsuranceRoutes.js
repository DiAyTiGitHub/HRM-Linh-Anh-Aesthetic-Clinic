import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
    loader: () => import("./StaffHasSocialInsuranceIndex"),
});

const ViewComponent = RenderScreen;

const StaffHasSocialInsuranceRoutes = [
    {
        path: ConstantList.ROOT_PATH + "insurance/staff-has-social-insurance",
        exact: true,
        component: ViewComponent,
        auth: ["ROLE_ADMIN", "HR_MANAGER", "HR_INSURANCE_MANAGER", "HR_COMPENSATION_BENEFIT"],
    },
];

export default StaffHasSocialInsuranceRoutes;
