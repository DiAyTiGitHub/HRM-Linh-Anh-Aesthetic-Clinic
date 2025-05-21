import { makeAutoObservable } from "mobx";

import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { hasShiftAssignmentPermission, hasShiftAssignmentPermissionStaff, hasPositionManager } from "./HrRoleUtilsService";
import localStorageService from "app/services/localStorageService";
import LocalConstants from "app/LocalConstants";
import { hasRoleManageHCNS } from "../profile/ProfileService";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class HrRoleUtilsStore {
    hasShiftAssignmentPermission = false; // Biến này có thể được sử dụng để kiểm tra quyền phân ca
    isManager = false;
    isAdmin = false;
    isUser = false;
    isCompensationBenifit = false;
    hasRoleManageHCNS = false; // Biến này có thể được sử dụng để kiểm tra quyền phân ca
    isPositionManager = false;
    isGeneralDirector = false;
    isDeputyGeneralDirector = false;
    isStaffView = false;

    constructor() {
        makeAutoObservable(this);
    }

    getCurrentLoginUser = () => {
        return localStorageService.getLoginUser();
    }

    checkAllUserRoles = async () => {
        try {
            this.checkManager();
            this.checkAdmin();
            this.checkUser();
            this.checkCompensationBenifit();
            this.checkPositionManager();
            this.checkGeneralDirector();
            this.checkDeputyGeneralDirector();
            this.checkStaffView();
        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi kiểm tra quyền người dùng");
        }
    };


    checkRoleManageHCNS = async () => {
        try {
            const { data } = await hasRoleManageHCNS();
            this.hasRoleManageHCNS = data;
            return this.hasRoleManageHCNS;
        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi kiểm tra quyền phân ca");
        }
    }

    checkHasShiftAssignmentPermission = async (staffId) => {
        try {
            if (staffId) {
                const { data } = await hasShiftAssignmentPermissionStaff(staffId);
                this.hasShiftAssignmentPermission = data;
            } else {
                const { data } = await hasShiftAssignmentPermission();
                this.hasShiftAssignmentPermission = data;
            }

            return this.hasShiftAssignmentPermission;
        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi kiểm tra quyền phân ca");
        }
    };

    checkRole = (roleName, stateKey) => {
        const roles = localStorageService.getLoginUser()?.user?.roles?.map(item => item.authority) || [];
        const hasRole = roles.includes(roleName);
        this[stateKey] = hasRole;
        return hasRole;
    };

    // Các hàm cụ thể dùng lại checkRole
    checkAdmin = () => this.checkRole(LocalConstants.SystemRole.ROLE_ADMIN.value, 'isAdmin');
    checkStaffView = () => this.checkRole(LocalConstants.SystemRole.HR_STAFF_VIEW.value, 'isStaffView');
    checkManager = () => this.checkRole(LocalConstants.SystemRole.HR_MANAGER.value, 'isManager');
    checkCompensationBenifit = () => this.checkRole(LocalConstants.SystemRole.HR_COMPENSATION_BENEFIT.value, 'isCompensationBenifit');
    checkUser = () => this.checkRole(LocalConstants.SystemRole.HR_USER.value, 'isUser');
    checkPositionManager = () => this.checkRole(LocalConstants.SystemRole.IS_POSITION_MANAGER.value, 'isPositionManager');
    checkGeneralDirector = () => this.checkRole(LocalConstants.SystemRole.IS_GENERAL_DIRECTOR.value, 'isGeneralDirector');
    checkDeputyGeneralDirector = () => this.checkRole(LocalConstants.SystemRole.IS_DEPUTY_GENERAL_DIRECTOR.value, 'isDeputyGeneralDirector');



}
