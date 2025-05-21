import {EgretLoadable} from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
    loader: () => import("./StaffCertificateIndex"),
});

const ViewComponent = RenderScreen;

const StaffCertificateRoutes = [{
    path: ConstantList.ROOT_PATH + "staff-certificate",
    exact: true,
    component: ViewComponent,
},];

export default StaffCertificateRoutes;
