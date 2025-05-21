import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const PositionRouter = [
    {
        path: ConstantList.ROOT_PATH + "category/staff/position",
        exact: true,
        component: EgretLoadable({
            loader: () => import("./PositionIndex"),
        }),
    },
];

export default PositionRouter;
