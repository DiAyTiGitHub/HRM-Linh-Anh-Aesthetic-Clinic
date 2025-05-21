import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const GlobalPropertyIndex = EgretLoadable({
    loader: () => import("./GlobalPropertyIndex"),
});

const Routes = [
    {
        path: ConstantList.ROOT_PATH + "setting/global-property",
        exact: true,
        component: GlobalPropertyIndex,
    },
];

export default Routes;
