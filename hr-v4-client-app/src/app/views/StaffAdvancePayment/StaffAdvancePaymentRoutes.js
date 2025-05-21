import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./StaffAdvancePaymentIndex"),
});

const ViewComponent = RenderScreen;

const StaffAdvancePaymentRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/staff-advance-payment",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER","HR_COMPENSATION_BENEFIT", "HR_USER"],
  },
];

export default StaffAdvancePaymentRoutes;
