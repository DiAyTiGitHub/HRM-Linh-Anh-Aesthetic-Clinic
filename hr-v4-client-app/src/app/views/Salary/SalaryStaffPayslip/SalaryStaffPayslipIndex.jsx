import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import SalaryStaffPayslipList from "./SalaryStaffPayslipList";
import GroupWorkIcon from "@material-ui/icons/GroupWork";
import TabsComponent from "app/common/Tab/TabComponent";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import SalaryStaffPayslipForm from "./SalaryStaffPayslipForm";
import SalaryStaffPayslipChangeStatusPopup from "./SalaryStaffPayslipChangeStatusPopup";
import SalaryStaffPayslipToolbar from "./SalaryStaffPayslipToolbar";
import SalaryStaffPayslipSalaryPopup from "./SalaryStaffPayslipSalaryPopup";
import ChooseAndViewSalaryBoard from "./SalaryBoardFromPaySlip/ChooseAndViewSalaryBoard";
import SalaryRecalPayslipPopup from "./SalaryRecalPayslip/SalaryRecalPayslipPopup";
import MoneyOffIcon from '@material-ui/icons/MoneyOff';
import AttachMoneyIcon from '@material-ui/icons/AttachMoney';
import { getInitialPayslipFilter } from "./SalaryStaffPayslipService";

const tabList = [
    { icon: <GroupWorkIcon fontSize="small" />, label: "Tất cả" },
    { icon: <AttachMoneyIcon fontSize="small" />, label: "Đã chi trả" },
    { icon: <MoneyOffIcon fontSize="small" />, label: "Chưa chi trả" },
];

function SalaryStaffPayslipIndex() {
    const {
        salaryStaffPayslipStore,
        hrRoleUtilsStore

    } = useStore();

    const { t } = useTranslation();

    const {
        pagingSalaryStaffPayslip,
        searchObject,
        resetStore,
        handleChangeViewPaidStatus,
        handleSelectListDelete,
        setPageIndex,
        openConfirmDeletePopup,
        openConfirmDeleteListPopup,
        handleClose,
        handleConfirmDeleteList,
        handleConfirmDelete,
        openConfirmChangeStatus,
        openCreateEditPopup,
        openPopupSalary,
        openViewSalaryBoard,
        openRecalculatePayslip,
        handleSetSearchObject
    } = salaryStaffPayslipStore;

    const { checkAllUserRoles } = hrRoleUtilsStore;


    async function initalizeScreen() {
        try {
            const { data } = await getInitialPayslipFilter();

            handleSetSearchObject(
                {
                    ...searchObject,
                    ...data
                }
            );

            await pagingSalaryStaffPayslip();
        }
        catch (error) {
            console.error(error);
        }
    }

    useEffect(() => {
        resetStore();

        checkAllUserRoles();
        initalizeScreen();

        return resetStore;
    }, []);

    async function handleChangeTabIndex(tabIndex) {
        handleChangeViewPaidStatus(tabIndex);
        handleSelectListDelete([]);
        await setPageIndex(1);
    }

    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: t("navigation.salary") },
                        { name: t("navigation.salaryStaffPayslip.title") },
                    ]}
                />
            </div>

            <Grid container spacing={2}>
                <Grid item xs={12} className='index-card'>
                    <SalaryStaffPayslipToolbar />
                </Grid>

                <Grid item xs={12} className='index-card'>
                    <TabsComponent
                        value={searchObject?.paidStatus}
                        handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
                        tabList={tabList}
                    />

                    <SalaryStaffPayslipList />
                </Grid>
            </Grid>

            {openConfirmChangeStatus && <SalaryStaffPayslipChangeStatusPopup />}

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDelete}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}

            {openConfirmDeleteListPopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeleteListPopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeleteList}
                    title={t("confirm_dialog.delete_list.title")}
                    text={t("confirm_dialog.delete_list.text")}
                    agree={t("confirm_dialog.delete_list.agree")}
                    cancel={t("confirm_dialog.delete_list.cancel")}
                />
            )}

            {openCreateEditPopup && <SalaryStaffPayslipForm />}

            {openPopupSalary && <SalaryStaffPayslipSalaryPopup />}

            {openRecalculatePayslip && <SalaryRecalPayslipPopup />}

            {openViewSalaryBoard && <ChooseAndViewSalaryBoard />}
        </div>
    );
}

export default memo(observer(SalaryStaffPayslipIndex));
