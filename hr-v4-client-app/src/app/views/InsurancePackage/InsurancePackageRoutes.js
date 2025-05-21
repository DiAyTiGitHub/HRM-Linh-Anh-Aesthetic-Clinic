import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
    loader: () => import("./InsurancePackageIndex"),
});

const ViewComponent = RenderScreen;

const HrDocumentTemplateRoutes = [
    {
        path: ConstantList.ROOT_PATH + "insurance-package",
        exact: true,
        component: ViewComponent,
        auth: ["ROLE_ADMIN", "HR_MANAGER"],
    },
];

export default HrDocumentTemplateRoutes;
