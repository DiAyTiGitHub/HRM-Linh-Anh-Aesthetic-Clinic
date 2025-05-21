import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const SalaryStaffPayslipIndex = EgretLoadable({
  loader: () => import("./SalaryStaffPayslipIndex"),
});
const ViewComponent = SalaryStaffPayslipIndex;

const SalaryStaffPayslipRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/salary-staff-payslip",
    exact: true,
    component: ViewComponent,
  },
];

export default SalaryStaffPayslipRoutes;