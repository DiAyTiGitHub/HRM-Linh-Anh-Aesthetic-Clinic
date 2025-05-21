import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/staff/disciplinary_reason",
    exact: true,
    component: EgretLoadable({
      loader: () =>
        import("./Staff/DisciplinaryReason/DisciplinaryReasonIndex"),
    }),
  },
  {
    path: ConstantList.ROOT_PATH + "category/staff/refusal-reason",
    exact: true,
    component: EgretLoadable({
      loader: () =>
        import("./Staff/RefusalReason/RefusalReasonIndex"),
    }),
  },
  {
    path: ConstantList.ROOT_PATH + "category/staff/deferred-type",
    exact: true,
    component: EgretLoadable({
      loader: () =>
        import("./Staff/DeferredType/DeferredTypeIndex"),
    }),
  },
  {
    path: ConstantList.ROOT_PATH + "category/staff/transfer_type",
    exact: true,
    component: EgretLoadable({
      loader: () =>
        import("./Staff/TransferType/TransferTypeIndex"),
    }),
  },
  {
    path: ConstantList.ROOT_PATH + "category/staff/staff-type",
    exact: true,
    component: EgretLoadable({
      loader: () =>
        import("./Staff/StaffTypeV2/StaffTypeV2Index"),
    }),
  },
  {
    path: ConstantList.ROOT_PATH + "category/staff/contract-type",
    exact: true,
    component: EgretLoadable({
      loader: () =>
        import("./Staff/ContractType/ContractTypeIndex"),
    }),
  },
  {
    path: ConstantList.ROOT_PATH + "category/staff/addendum-type",
    exact: true,
    component: EgretLoadable({
      loader: () =>
        import("./Staff/AddendumType/AddendumTypeIndex"),
    }),
  },
  {
    path: ConstantList.ROOT_PATH + "category/staff/leaving-job-reason",
    exact: true,
    component: EgretLoadable({
      loader: () =>
        import("./Staff/LeavingJobReason/LeavingJobReasonIndex"),
    }),
  },
];

export default Routes;
