import { EgretLoadable } from "egret";

import ConstantList from "app/appConfig";

const VoucherComponent = EgretLoadable({ loader: () => import("./VoucherIndex") });

const VoucherRoutes = [
  {
    path: ConstantList.ROOT_PATH + "budget/voucher",
    exact: true,
    component: VoucherComponent,
  }
]

export default VoucherRoutes;
