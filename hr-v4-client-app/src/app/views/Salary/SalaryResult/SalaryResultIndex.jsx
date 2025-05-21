import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import SalaryResultCreateBoardPopup from "./SalaryResultCreateBoardPopup";
import SalaryResultIndexToolbar from "./SalaryResultIndexToolbar";
import SalaryResultList from "./SalaryResultList";
import GroupWorkIcon from "@material-ui/icons/GroupWork";
import HourglassEmptyIcon from "@material-ui/icons/HourglassEmpty";
import DoneAll from "@material-ui/icons/DoneAll";
import LockIcon from "@material-ui/icons/Lock";
import CloseIcon from "@material-ui/icons/Close";
import TabsComponent from "../../../common/Tab/TabComponent";
import SalaryResultChangeStatus from "./SalaryResultChangeStatus";
import ExportCommissionPayrollPopup from "./ExportCommissionPayrollPopup";

const tabList = [
    { icon: <GroupWorkIcon fontSize='small' />, label: "Tất cả" },
    { icon: <HourglassEmptyIcon fontSize='small' />, label: "Chưa xét duyệt" },
    { icon: <DoneAll fontSize='small' />, label: "Đã duyệt" },
    { icon: <CloseIcon fontSize='small' />, label: "Không duyệt" },
    // { icon: <LockIcon fontSize='small' />, label: "Đã khóa" },
];

function SalaryResultIndex() {
    const { salaryResultStore } = useStore();
    
    const { t } = useTranslation();

    const {
        pagingSalaryResult,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        openCreateEditPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        resetStore,
        searchObject,
        handleChangeViewByStatus,
        handleSelectListDelete,
        openConfirmChangeStatus,
        openExportCMPPopup
    } = salaryResultStore;

    useEffect(() => {
        pagingSalaryResult();

        return resetStore;
    }, []);

    async function handleChangeTabIndex(tabIndex) {
        await handleChangeViewByStatus(tabIndex);
        handleSelectListDelete([]);
    }

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb
                    routeSegments={[{ name: t("navigation.salary") }, { name: t("navigation.salaryResult.title") }]} />
            </div>

            <Grid container spacing={2} className="index-card">
                <Grid item xs={12}>
                    <SalaryResultIndexToolbar />
                </Grid>

                <Grid item xs={12}>
                    <TabsComponent
                        value={searchObject?.status}
                        handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
                        tabList={tabList}
                    />
                    <SalaryResultList />
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                // <SalaryResultCUForm />
                <SalaryResultCreateBoardPopup />
            )}
            {openConfirmChangeStatus && (<SalaryResultChangeStatus />)}

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

            {openExportCMPPopup && (
                <ExportCommissionPayrollPopup />
            )}
        </div>
    );
}

export default memo(observer(SalaryResultIndex));
