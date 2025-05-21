import {EgretLoadable} from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
    loader: () => import("./BankIndex"),
});

const ViewComponent = RenderScreen;

const BankRoutes = [
    {
        path: ConstantList.ROOT_PATH + "category/bank",
        exact: true,
        component: ViewComponent,
        auth: ["ROLE_ADMIN", "HR_MANAGER"],
    },
];

export default BankRoutes;
