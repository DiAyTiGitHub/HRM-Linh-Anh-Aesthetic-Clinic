import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const StaffWorkScheduleV2Index = EgretLoadable({
    loader:() => import("./StaffWorkScheduleV2Index") ,
});
const ViewComponent = StaffWorkScheduleV2Index;

const Routes = [
    {
        path:ConstantList.ROOT_PATH + "staff-work-schedule" ,
        exact:true ,
        component:ViewComponent ,
    } ,
    {
        path:ConstantList.ROOT_PATH + "staff-work-schedule/staff/:id/:payrollId" ,
        exact:true ,
        component:ViewComponent ,
    } ,
];

export default Routes;
