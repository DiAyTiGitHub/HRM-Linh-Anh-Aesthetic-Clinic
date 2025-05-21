import {EgretLoadable} from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
    loader: () => import("./RRequestReportIndex"),
});

const ViewComponent = RenderScreen;

const RRequestReportRoutes = [{
    path: ConstantList.ROOT_PATH + "recruitment-request-report",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER", "HR_STAFF_VIEW"],
}, {
    path: ConstantList.ROOT_PATH + "recruitment-request-summary",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./../Summary/RecruitmentRequestSummaryIndex"),
    }),
    auth: ["ROLE_ADMIN", "HR_MANAGER", "HR_STAFF_VIEW"],
},];

export default RRequestReportRoutes;
