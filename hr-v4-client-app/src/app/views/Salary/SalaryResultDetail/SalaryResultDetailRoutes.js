import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const SalaryResultDetailIndex = EgretLoadable({
  loader: () => import("./UpdateMode/SalaryResultDetailUMIndex"),
});

const UpdateMode = SalaryResultDetailIndex;




const SalaryResultDetailROIndex = EgretLoadable({
  loader: () => import("./ReadOnlyMode/SalaryResultDetailROIndex"),
});

const ReadOnlyMode = SalaryResultDetailROIndex;


// Hiện tại chỉ dùng màn hình này làm màn hình bảng lương
const PayrollIndex = EgretLoadable({
  loader: () => import("../Payroll/PayrollIndex"),
});

const PayrollScreen = PayrollIndex;

const SalaryResultDetailRoutes = [
  {
    path: ConstantList.ROOT_PATH + "payroll/:id",
    exact: true,
    component: PayrollScreen,
    // auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
  {
    path: ConstantList.ROOT_PATH + "salary-result-detail/:id",
    exact: true,
    component: UpdateMode,
    // auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
  {
    path: ConstantList.ROOT_PATH + "salary-result-detail-read-only/:id",
    exact: true,
    component: ReadOnlyMode,
    // auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default SalaryResultDetailRoutes;
