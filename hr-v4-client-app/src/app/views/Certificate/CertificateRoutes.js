import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const CertificateIndex = EgretLoadable({
  loader: () => import("./CertificateIndex"),
});
const ViewComponent = CertificateIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/certificate",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
