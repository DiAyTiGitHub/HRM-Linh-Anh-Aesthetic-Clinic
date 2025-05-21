import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
import { withTranslation } from "react-i18next";

const TimeKeeping = EgretLoadable({
    loader: () => import("./TimeKeepingIndex"),
});

// const ViewComponentUser = TimeKeeping;
const ViewComponent = withTranslation()(TimeKeeping);

const TimeKeepingRoutes = [
    {
        path: ConstantList.ROOT_PATH + "timeKeeping/:id",
        exact: true,
        component: ViewComponent,
    },
    {
        path: ConstantList.ROOT_PATH + "timeKeeping",
        exact: true,
        auth: ["HR_MANAGER", "HR_USER", "HR_TESTER", "ADMIN"],
        component: ViewComponent,
    },
];

export default TimeKeepingRoutes;
