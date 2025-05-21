import { EgretLoadable } from "../../../../egret";
import ConstantList from "../../../appConfig";
import EvaluationTicketSaveForm from "./EvaluationTicketCU/EvaluationTicketSaveForm";
import EvaluationTicketView from "./EvaluationTicketView";
const EvaluationTicketIndexView = EgretLoadable({
    loader: () => import("../../Staff/EvaluationTicket/EvaluationTicketIndex"),
});
const EvaluationTicketRoutes = [
    {
        path: ConstantList.ROOT_PATH + "staff-evaluation-ticket",
        exact: true,
        component: EvaluationTicketIndexView,
        auth: ["ROLE_ADMIN", "HR_MANAGER", 'HR_USER'],
    },
    {
        path: ConstantList.ROOT_PATH + "staff-evaluation-ticket",
        exact: true,
        component: EvaluationTicketIndexView,
        auth: ["ROLE_ADMIN", "HR_MANAGER", 'HR_USER'],
    },
    {
        path: ConstantList.ROOT_PATH + "user/staff-evaluation-ticket",
        exact: true,
        component: EvaluationTicketIndexView,
        auth: ["ROLE_ADMIN", "HR_MANAGER", 'HR_USER'],
    },
    {
        path: ConstantList.ROOT_PATH + "staff-evaluation-ticket/save/:id",
        exact: true,
        component: EvaluationTicketSaveForm,
        auth: ["ROLE_ADMIN", "HR_MANAGER", 'HR_USER'],
    },
    {
        path: ConstantList.ROOT_PATH + "staff-evaluation-ticket/save/:id/:templateId",
        exact: true,
        component: EvaluationTicketSaveForm,
        auth: ["ROLE_ADMIN", "HR_MANAGER", 'HR_USER'],
    },
    {
        path: ConstantList.ROOT_PATH + "staff-evaluation-ticket/edit/:id",
        exact: true,
        component: EvaluationTicketSaveForm,
        auth: ["ROLE_ADMIN", "HR_MANAGER", 'HR_USER'],
    },
    {
        path: ConstantList.ROOT_PATH + "staff-evaluation-ticket/view/:id",
        exact: true,
        component: EvaluationTicketView,
        auth: ["ROLE_ADMIN", "HR_MANAGER", 'HR_USER'],
    },
];

export default EvaluationTicketRoutes;