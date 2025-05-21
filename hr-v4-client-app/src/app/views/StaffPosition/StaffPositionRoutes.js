import {EgretLoadable} from "egret";
import ConstantList from "../../appConfig";

const StaffPositionIndex = EgretLoadable({
    loader: () => import("./StaffPositionIndex"),
});
const ViewComponent = StaffPositionIndex;

const StaffPositionRoutes = [
    {
        path: ConstantList.ROOT_PATH + "position-staff",
        exact: true,
        component: ViewComponent,
    },
];

export default StaffPositionRoutes;