import {EgretLoadable} from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
    loader: () => import("./HrDocumentTemplateIndex"),
});

const ViewComponent = RenderScreen;

const HrDocumentTemplateRoutes = [
    {
        path: ConstantList.ROOT_PATH + "hr-document-template",
        exact: true,
        component: ViewComponent,
        auth: ["ROLE_ADMIN", "HR_MANAGER"],
    },
];

export default HrDocumentTemplateRoutes;
