import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const SalaryTemplateCUIndex = EgretLoadable({
    loader:() => import("./SalaryTemplateCUIndex") ,
});

const ViewComponent = SalaryTemplateCUIndex;

const SalaryTemplateCURoutes = [
    {
        path:ConstantList.ROOT_PATH + "salary-template/:id" ,
        exact:true ,
        component:ViewComponent ,
        // auth:["ROLE_ADMIN" , "HR_MANAGER"] ,
    } ,
    {
        path:ConstantList.ROOT_PATH + "salary-template/view/:id" ,
        exact:true ,
        component:ViewComponent ,
        // auth:["ROLE_ADMIN" , "HR_MANAGER"] ,
    } ,
];

export default SalaryTemplateCURoutes;
