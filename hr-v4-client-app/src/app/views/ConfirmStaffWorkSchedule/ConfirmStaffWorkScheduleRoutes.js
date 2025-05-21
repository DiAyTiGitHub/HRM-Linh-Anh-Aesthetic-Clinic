import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const ConfirmStaffWorkScheduleIndex = EgretLoadable({
  loader: () => import("./ConfirmStaffWorkScheduleIndex"),
});

const ViewComponent = ConfirmStaffWorkScheduleIndex;

const ConfirmStaffWorkSchedule = [
  {
    path: ConstantList.ROOT_PATH + "category/confirm-staff-work-schedule",
    exact: true,
    component: ViewComponent
  },
];

export default ConfirmStaffWorkSchedule;
