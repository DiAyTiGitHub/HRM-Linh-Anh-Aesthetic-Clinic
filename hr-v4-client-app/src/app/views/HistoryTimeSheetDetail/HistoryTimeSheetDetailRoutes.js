import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const StaffWorkingHistoryIndex = EgretLoadable({
    loader:() => import("./HistoryTimeSheetDetailIndex") ,
});
const ViewComponent = StaffWorkingHistoryIndex;

const HistoryTimeSheetDetailRoutes = [
    {
        path:ConstantList.ROOT_PATH + "history-time-sheet-detail" ,
        exact:true ,
        component:ViewComponent ,
    } ,
    {
        path:ConstantList.ROOT_PATH + "history-time-sheet-detail/:staffId" ,
        exact:true ,
        component:ViewComponent ,
    } ,
];

export default HistoryTimeSheetDetailRoutes;